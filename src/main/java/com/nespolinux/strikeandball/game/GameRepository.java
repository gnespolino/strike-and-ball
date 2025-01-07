package com.nespolinux.strikeandball.game;

import com.nespolinux.strikeandball.game.Game.PlayerSecret;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

  private final Map<String, Game> games = new HashMap<>();

  public String createGame(PlayerSecret playerSecret, boolean privateGame) {
    Game game = addOrUpdateGame(Game.builder()
        .playerSecret1(playerSecret)
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
    PlayerSecret playerSecretToCheck = game.isPlayer1(playerId) ? game.getPlayerSecret2() : game.getPlayerSecret1();
    int strikes = getStrike(guess, playerSecretToCheck.getSecret().getSecret());
    int balls = getBall(guess, playerSecretToCheck.getSecret().getSecret());
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

  public Optional<Game> getFirstPendingGame() {
    return games.values().stream()
        .filter(game -> !game.isPrivateGame())
        .filter(game -> Objects.isNull(game.getPlayerSecret2()))
        .findFirst();
  }
}
