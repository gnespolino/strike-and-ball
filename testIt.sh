#!/bin/bash
echo "This is a test script"

CALL_1=$(curl --request POST "http://localhost:8080/game/join?playerName=player1&secretNumber=1234")
CALL_2=$(curl --request POST "http://localhost:8080/game/join?playerName=player2&secretNumber=5678")
GAME_ID=$(echo $CALL_1 | jq -r '.gameId')
PLAYER_1_ID=$(echo $CALL_1 | jq -r '.playerId')
PLAYER_2_ID=$(echo $CALL_2 | jq -r '.playerId')

echo "Game ID: $GAME_ID"
echo "Player 1 ID: $PLAYER_1_ID"
echo "Player 2 ID: $PLAYER_2_ID"

curl --request POST "http://localhost:8080/game/guess?gameId=$GAME_ID&playerId=$PLAYER_1_ID&guess=5678" | jq .
curl --request POST "http://localhost:8080/game/guess?gameId=$GAME_ID&playerId=$PLAYER_2_ID&guess=1234" | jq .
curl "http://localhost:8080/game/status?gameId=$GAME_ID" | jq .