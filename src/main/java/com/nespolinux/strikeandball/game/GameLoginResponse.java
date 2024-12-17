package com.nespolinux.strikeandball.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameLoginResponse {

  private final String gameId;
  private final String playerId;
}
