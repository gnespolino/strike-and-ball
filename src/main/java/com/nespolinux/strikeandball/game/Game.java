package com.nespolinux.strikeandball.game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
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
  private final PlayerSecret playerSecret1;
  private final PlayerSecret playerSecret2;
  @Singular
  private final List<Guess> guesses;

  public boolean hasPlayer(String playerId) {
    return Stream.of(playerSecret1, playerSecret2)
        .map(PlayerSecret::getPlayer)
        .map(Player::getPlayerId)
        .anyMatch(playerId::equals);
  }

  public boolean isFinished() {
    return getLastGuess()
        .map(Guess::getStrikes)
        .orElse(0) == 4;
  }

  public boolean isPlayersTurn(String playerId) {
    Optional<Guess> lastGuess = getLastGuess();
    if (isPlayer1(playerId)) {
      return lastGuess.map(guess -> guess.isFromPlayer(playerSecret2.getPlayer())).orElse(true);
    }
    if (isPlayer2(playerId)) {
      return lastGuess.map(guess -> guess.isFromPlayer(playerSecret1.getPlayer())).orElse(false);
    }
    throw new IllegalArgumentException("Player not in this game");
  }

  public boolean isPlayer1(String playerId) {
    return playerSecret1.getPlayerId().equals(playerId);
  }

  public boolean isPlayer2(String playerId) {
    return playerSecret2.getPlayerId().equals(playerId);
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
        .map(playerId -> isPlayer1(playerId) ? playerSecret1.getPlayer().getName()
            : playerSecret2.getPlayer().getName())
        .orElse(null);
  }

  @Data
  @Builder
  static class PlayerSecret {

    private final Player player;
    private final Secret secret;

    public static PlayerSecret buildPlayerSecret(String playerName, char[] secret) {
      return PlayerSecret.builder()
          .player(Player.builder()
              .name(playerName)
              .build())
          .secret(Secret.of(secret))
          .build();
    }

    public String getPlayerId() {
      return player.getPlayerId();
    }
  }
}
