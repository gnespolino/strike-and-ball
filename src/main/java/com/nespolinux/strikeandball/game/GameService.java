package com.nespolinux.strikeandball.game;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;

  public synchronized GameLoginResponse createOrJoinGame(String playerName, char[] secret) {

    Player player = Player.builder()
        .name(playerName)
        .secret(secret)
        .build();

    GameLoginResponse.GameLoginResponseBuilder gameLoginResponseBuilder = GameLoginResponse.builder()
        .playerId(player.getPlayerId());

    Optional<Game> gameWithoutSecondPlayer = gameRepository.getGameWaitingForPlayer();

    if (gameWithoutSecondPlayer.isPresent()) {
      Game game = gameRepository.addOrUpdateGame(
          gameWithoutSecondPlayer.get().toBuilder()
              .player2(player)
              .build());
      return gameLoginResponseBuilder
          .gameId(game.getGameId())
          .build();
    }
    return gameLoginResponseBuilder
        .gameId(gameRepository.createGame(player, false))
        .build();
  }

  public Guess guess(String gameId, String playerId, char[] guess) {
    Game game = gameRepository.getGame(gameId);
    synchronized (game) {
      checkPlayerCanGuess(game, playerId);
      return gameRepository.makeGuess(gameId, playerId, guess);
    }
  }

  private void checkPlayerCanGuess(Game game, String playerId) {
    checkPlayerIsInGame(game, playerId);
    checkGameIsInProgress(game, playerId);
    checkPlayerTurn(game, playerId);
  }

  private void checkPlayerTurn(Game game, String playerId) {
    if (!game.isPlayersTurn(playerId)) {
      throw new IllegalArgumentException("Not your turn");
    }
  }

  private void checkGameIsInProgress(Game game, String playerId) {
    if (game.isFinished()) {
      throw new IllegalArgumentException("Game is finished");
    }
    if (game.isPlayer1(playerId) && Objects.isNull(game.getPlayer2())) {
      throw new IllegalArgumentException("Game not started, waiting for second player to join");
    }
  }

  private void checkPlayerIsInGame(Game game, String playerId) {
    if (!game.hasPlayer(playerId)) {
      throw new IllegalArgumentException("Player not in this game");
    }
  }

  public GameStatus status(String gameId) {
    Game game = gameRepository.getGame(gameId);
    Map<String, Player> playersById =
        Stream.of(game.getPlayer1(), game.getPlayer2())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Player::getPlayerId,
                Function.identity()));
    return GameStatus.builder()
        .gameId(game.getGameId())
        .started(game.getPlayer2() != null)
        .finished(game.isFinished())
        .winner(game.getWinner())
        .playerName1(game.getPlayer1().getName())
        .playerName2(Optional.ofNullable(game.getPlayer2())
            .map(Player::getName)
            .orElse(null))
        .guesses(game.getGuesses().stream()
            .map(guess -> Guess.builder()
                .playerId(playersById.get(guess.getPlayerId()).getName())
                .guess(guess.getGuess())
                .strikes(guess.getStrikes())
                .balls(guess.getBalls())
                .build())
            .toList())
        .build();
  }

  public GameLoginResponse createGame(String playerName, char[] secretNumber) {
    Player player = Player.builder()
        .name(playerName)
        .secret(secretNumber)
        .build();
    return GameLoginResponse.builder()
        .playerId(player.getPlayerId())
        .gameId(gameRepository.createGame(player, true))
        .build();
  }
}