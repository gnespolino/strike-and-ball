# Strike and Ball Game

This is a Spring Boot application for the Strike and Ball game. The game allows players to join and make guesses to find the secret number.

## Prerequisites

- Java 17 or higher
- Gradle
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/nespolinux/strikeandball.git
cd strikeandball
```

### Build the Application

```bash
./gradlew build
```

### Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Join Game

- **Endpoint**: `/game/join?playerName=Player1&secretNumber=1234`
- **Method**: `POST`
- **Response**:
  ```json
  {
    "gameId": "game-id",
    "playerId": "player-id"
  }
  ```

### Make a Guess

- **Endpoint**: `/game/guess?gameId=game-id&playerId=player-id&guess=5678`
- **Method**: `POST`
- **Response**:
  ```json
  {
    "playerId": "player-id",
    "guess": "5678",
    "strikes": 1,
    "balls": 2
  }
  ```

## Running Tests

To run the tests, use the following command:

```bash
./gradlew test
```

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
```