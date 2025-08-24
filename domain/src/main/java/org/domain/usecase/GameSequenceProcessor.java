package org.domain.usecase;

import lombok.extern.slf4j.Slf4j;
import org.domain.exception.InvalidSequenceException;
import org.domain.model.GameStatus;
import org.domain.model.Player;
import org.domain.model.Score;
import org.domain.model.TennisGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class GameSequenceProcessor {


    public List<Score> processGameSequence(String sequence) {
        log.info("processing sequence : {}" , sequence);
        if (sequence == null || sequence.trim().isEmpty()) {
            throw new InvalidSequenceException("Sequence cannot be null or empty");
        }

        // Extract unique players from sequence
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : sequence.toUpperCase().toCharArray()) {
            if (!Character.isLetter(c)) {
                throw new InvalidSequenceException("Invalid character in sequence: " + c + ". Only letters are allowed.");
            }
            uniqueChars.add(c);
        }

        if (uniqueChars.size() != 2) {
            throw new InvalidSequenceException("Sequence must contain exactly two different letters. Found: " + uniqueChars.size());
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

            if (score.status() == GameStatus.FINISHED) {
                break; // Game is over
            }
        }

        return intermediateScores;
    }


}