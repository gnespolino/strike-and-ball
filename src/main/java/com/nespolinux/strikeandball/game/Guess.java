package com.nespolinux.strikeandball.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Guess {

  private final String playerId;
  private final char[] attempt;
  private final int strikes;
  private final int balls;

  public boolean isFromPlayer(Player player) {
    return this.playerId.equals(player.getPlayerId());
  }
}
