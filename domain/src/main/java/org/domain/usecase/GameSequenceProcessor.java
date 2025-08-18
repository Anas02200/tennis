package org.domain.usecase;

import org.domain.model.GameStatus;
import org.domain.model.Player;
import org.domain.model.Score;
import org.domain.model.TennisGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSequenceProcessor {


    public List<Score> processGameSequence(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be null or empty");
        }

        // Extract unique players from sequence
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : sequence.toUpperCase().toCharArray()) {
            if (!Character.isLetter(c)) {
                throw new IllegalArgumentException("Invalid character in sequence: " + c + ". Only letters are allowed.");
            }
            uniqueChars.add(c);
        }

        if (uniqueChars.size() != 2) {
            throw new IllegalArgumentException("Sequence must contain exactly two different letters. Found: " + uniqueChars.size());
        }

        // Convert to Player objects
        Character[] playerChars = uniqueChars.toArray(new Character[0]);
        Player player1 = new Player(playerChars[0]);
        Player player2 = new Player(playerChars[1]);

        //todo remove maybe ??
        // Ensure consistent ordering (first player is the one who appears first in sequence)
        char firstChar = Character.toUpperCase(sequence.charAt(0));
        if (firstChar != player1.getIdentifier()) {
            Player temp = player1;
            player1 = player2;
            player2 = temp;
        }

        TennisGame game = new TennisGame(player1, player2);
        List<Score> intermediateScores = new ArrayList<>();

        for (char c : sequence.toUpperCase().toCharArray()) {
            Player currentPlayer = (c == player1.getIdentifier()) ? player1 : player2;
            Score score = game.addPoint(currentPlayer);
            intermediateScores.add(score);

            if (score.getStatus() == GameStatus.FINISHED) {
                break; // Game is over
            }
        }

        return intermediateScores;
    }

    public String formatScore(Score score) {
        return switch (score.getStatus()) {
            case DEUCE -> "Deuce";
            case ADVANTAGE -> "Advantage Player " + score.getAdvantagePlayer();
            case FINISHED -> "Player " + score.getWinner() + " wins the game";
            default -> "Player " + score.getPlayer1() + " : " + score.getPlayer1ScoreDisplay() +
                    " / Player " + score.getPlayer2() + " : " + score.getPlayer2ScoreDisplay();
        };
    }
}