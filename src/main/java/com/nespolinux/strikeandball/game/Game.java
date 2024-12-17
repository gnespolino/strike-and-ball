package com.nespolinux.strikeandball.game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
public class Game {

  @Default
  private final String gameId = UUID.randomUUID().toString();
  private final boolean privateGame;
  private final Player player1;
  private final Player player2;
  @Singular
  private final List<Guess> guesses;

  public boolean hasPlayer(String playerId) {
    return player1.getPlayerId().equals(playerId) || player2.getPlayerId().equals(playerId);
  }

  public boolean isFinished() {
    return getLastGuess()
        .map(Guess::getStrikes)
        .orElse(0) == 4;
  }

  public boolean isPlayersTurn(String playerId) {
    Optional<Guess> lastGuess = getLastGuess();
    if (isPlayer1(playerId)) {
      return lastGuess.map(guess -> guess.isFromPlayer(player2)).orElse(true);
    }
    if (isPlayer2(playerId)) {
      return lastGuess.map(guess -> guess.isFromPlayer(player1)).orElse(false);
    }
    throw new IllegalArgumentException("Player not in this game");
  }

  public boolean isPlayer1(String playerId) {
    return player1.getPlayerId().equals(playerId);
  }

  public boolean isPlayer2(String playerId) {
    return player2.getPlayerId().equals(playerId);
  }

  private Optional<Guess> getLastGuess() {
    if (guesses.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(guesses.getLast());
  }

  public String getWinner() {
    return getLastGuess()
        .filter(guess -> guess.getStrikes() == 4)
        .map(Guess::getPlayerId)
        .map(playerId -> isPlayer1(playerId) ? player1.getName() : player2.getName())
        .orElse(null);
  }
}
