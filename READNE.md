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

- **Endpoint**: `/game/join`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "playerName": "Player1",
    "secretNumber": "1234"
  }
  ```
- **Response**:
  ```json
  {
    "gameId": "game-id"
  }
  ```

### Make a Guess

- **Endpoint**: `/game/guess`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "gameId": "game-id",
    "playerId": "Player1",
    "guess": "5678"
  }
  ```
- **Response**:
  ```json
  {
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