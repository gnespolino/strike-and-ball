package com.nespolinux.strikeandball.game;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStatus {

  private final String gameId;
  private final boolean started;
  private final boolean finished;
  private final String playerName1;
  private final String playerName2;
  private final String winner;
  private final List<Guess> guesses;
}
