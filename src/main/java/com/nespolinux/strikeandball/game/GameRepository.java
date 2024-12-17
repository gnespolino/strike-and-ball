package com.nespolinux.strikeandball.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

  private final Map<String, Game> games = new HashMap<>();

  public String createGame(Player player, boolean privateGame) {
    Game game = addOrUpdateGame(Game.builder()
        .player1(player)
        .privateGame(privateGame)
        .build());
    return game.getGameId();
  }

  public Game getGame(String gameId) {
    return Optional.ofNullable(games.get(gameId))
        .orElseThrow(() -> new IllegalArgumentException("Game not found"));
  }

  public Game addOrUpdateGame(Game game) {
    games.put(game.getGameId(), game);
    return game;
  }

  public Guess makeGuess(String gameId, String playerId, char[] guess) {
    Game game = getGame(gameId);
    Player playerToCheck = game.isPlayer1(playerId) ? game.getPlayer2() : game.getPlayer1();
    int strikes = getStrike(guess, playerToCheck.getSecret());
    int balls = getBall(guess, playerToCheck.getSecret());
    Guess newGuess = Guess.builder()
        .playerId(playerId)
        .attempt(guess)
        .strikes(strikes)
        .balls(balls)
        .build();
    addOrUpdateGame(game.toBuilder()
        .guess(newGuess)
        .build());
    return newGuess;
  }

  private int getStrike(char[] guess, char[] secret) {
    int strikes = 0;
    for (int i = 0; i < 4; i++) {
      if (guess[i] == secret[i]) {
        strikes++;
      }
    }
    return strikes;
  }

  private int getBall(char[] guess, char[] secret) {
    int balls = 0;
    for (int i = 0; i < 4; i++) {
      if (new String(secret).contains(String.valueOf(guess[i])) && guess[i] != secret[i]) {
        balls++;
      }
    }
    return balls;
  }

  public Optional<Game> getGameWaitingForPlayer() {
    return games.values().stream()
        .filter(game -> !game.isPrivateGame())
        .filter(game -> Objects.isNull(game.getPlayer2()))
        .findFirst();
  }

  public Game getGameById(String gameId) {
    Game game = getGame(gameId);
    if (!game.isPrivateGame()) {
      throw new IllegalArgumentException("Only private games can be joined directly");
    }
    // if both players are already in the game, it is not possible to join
    if (game.getPlayer2() != null) {
      throw new IllegalArgumentException("Game is full");
    }
    return game;
  }
}
