package com.nespolinux.strikeandball.game;

import com.nespolinux.strikeandball.game.Game.PlayerSecret;
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

    PlayerSecret playerSecret = PlayerSecret.buildPlayerSecret(playerName, secret);

    GameLoginResponse.GameLoginResponseBuilder gameLoginResponseBuilder = GameLoginResponse.builder()
        .playerId(playerSecret.getPlayerId());

    Optional<Game> pendingGame = gameRepository.getFirstPendingGame();

    if (pendingGame.isPresent()) {
      Game game = gameRepository.addOrUpdateGame(
          pendingGame.get().toBuilder()
              .playerSecret2(playerSecret)
              .build());
      return gameLoginResponseBuilder
          .gameId(game.getGameId())
          .build();
    }
    return gameLoginResponseBuilder
        .gameId(gameRepository.createGame(playerSecret, false))
        .build();
  }

  public GameLoginResponse joinGame(String gameId, String playerName, char[] secret) {
    PlayerSecret playerSecret = PlayerSecret.buildPlayerSecret(playerName, secret);

    GameLoginResponse.GameLoginResponseBuilder gameLoginResponseBuilder = GameLoginResponse.builder()
        .playerId(playerSecret.getPlayerId());

    Game existingGame = gameRepository.getGame(gameId);

    if (!existingGame.isPrivateGame()) {
      throw new IllegalArgumentException("Only private games can be joined directly");
    }

    // if both players are already in the game, it is not possible to join
    if (existingGame.getPlayerSecret2() != null) {
      throw new IllegalArgumentException("Game is full");
    }

    return gameLoginResponseBuilder
        .gameId(existingGame.getGameId())
        .build();
  }

  public Guess guess(String gameId, String playerId, char[] guess) {
    Game game = gameRepository.getGame(gameId);
    synchronized (game) {
      if (!game.hasPlayer(playerId)) {
        throw new IllegalArgumentException("Player " + playerId + " is not in this game");
      }
      if (game.isFinished()) {
        throw new IllegalArgumentException("Game " + game + " is finished");
      }
      if (game.isPlayer1(playerId) && Objects.isNull(game.getPlayerSecret2())) {
        throw new IllegalArgumentException(
            "Game " + game + " not started, waiting for second player to join");
      }
      if (!game.isPlayersTurn(playerId)) {
        throw new IllegalArgumentException("Not your turn");
      }
      return gameRepository.makeGuess(gameId, playerId, guess);
    }
  }

  public GameStatus getGameStatus(String gameId) {
    Game game = gameRepository.getGame(gameId);
    Map<String, Player> playersById =
        Stream.of(game.getPlayerSecret1(), game.getPlayerSecret2())
            .filter(Objects::nonNull)
            .map(PlayerSecret::getPlayer)
            .collect(Collectors.toMap(
                Player::getPlayerId,
                Function.identity()));
    return GameStatus.builder()
        .gameId(game.getGameId())
        .started(game.getPlayerSecret2() != null)
        .finished(game.isFinished())
        .winner(game.getWinner())
        .playerName1(game.getPlayerSecret1().getPlayer().getName())
        .playerName2(Optional.ofNullable(game.getPlayerSecret2())
            .map(PlayerSecret::getPlayer)
            .map(Player::getName)
            .orElse(null))
        .guesses(game.getGuesses().stream()
            .map(guess -> Guess.builder()
                .playerId(playersById.get(guess.getPlayerId()).getName())
                .attempt(guess.getAttempt())
                .strikes(guess.getStrikes())
                .balls(guess.getBalls())
                .build())
            .toList())
        .build();
  }

  public GameLoginResponse createGame(String playerName, char[] secretNumber) {
    PlayerSecret player = PlayerSecret.buildPlayerSecret(playerName, secretNumber);
    return GameLoginResponse.builder()
        .playerId(player.getPlayerId())
        .gameId(gameRepository.createGame(player, true))
        .build();
  }

}