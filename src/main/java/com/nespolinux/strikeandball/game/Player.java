package com.nespolinux.strikeandball.game;

import java.util.UUID;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class Player {

  @Default
  private final String playerId = UUID.randomUUID().toString();
  private final String name;
}
