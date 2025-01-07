package com.nespolinux.strikeandball.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class Secret {

  private final char[] secret;

}
