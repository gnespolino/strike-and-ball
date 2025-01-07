package com.nespolinux.strikeandball.game;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/game/join")
  public GameLoginResponse joinGame(@RequestParam("playerName") String playerName,
      @RequestParam("secretNumber") char[] secretNumber,
      @RequestParam(value = "gameId", required = false) String gameId,
      @RequestParam(value = "privateGame", defaultValue = "false") boolean privateGame) {
    if (privateGame) {
      return gameService.createGame(playerName, secretNumber);
    }
    if (Objects.nonNull(gameId)) {
      return gameService.joinGame(gameId, playerName, secretNumber);
    }
    return gameService.createOrJoinGame(playerName, secretNumber);
  }

  @PostMapping("/game/guess")
  public Guess guess(@RequestParam("gameId") String gameId,
      @RequestParam("playerId") String playerId,
      @RequestParam("guess") char[] guess) {
    return gameService.guess(gameId, playerId, guess);
  }

  @GetMapping("/game/status")
  public GameStatus status(@RequestParam("gameId") String gameId) {
    return gameService.getGameStatus(gameId);
  }

  @ControllerAdvice
  public static class ExceptionMapper {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public GameErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
      return new GameErrorResponse(e.getMessage(), BAD_REQUEST);
    }
  }

  public record GameErrorResponse(String message, HttpStatus status) {

  }
}
