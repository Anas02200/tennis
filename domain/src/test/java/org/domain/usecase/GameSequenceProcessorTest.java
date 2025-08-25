package org.domain.usecase;

import org.domain.exception.InvalidSequenceException;
import org.domain.model.GameStatus;
import org.domain.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSequenceProcessorTest {
    private GameSequenceProcessor service;

    @BeforeEach
    void setUp() {
        service = new GameSequenceProcessor();
    }

    @Nested
    @DisplayName("Valid Game Sequences")
    class ValidGameSequences {

        @Test
        @DisplayName("Should process basic game sequence correctly")
        void testBasicGameSequence() {
            // Given
            String sequence = "ABABAA";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(6, scores.size());

            // Verify each score progression
            assertScore(scores.get(0), 'A', 1, 0, 'B', 0, 0, GameStatus.IN_PROGRESS, null);
            assertScore(scores.get(1), 'A', 1, 0, 'B', 1, 0, GameStatus.IN_PROGRESS, null);
            assertScore(scores.get(2), 'A', 2, 0, 'B', 1, 0, GameStatus.IN_PROGRESS, null);
            assertScore(scores.get(3), 'A', 2, 0, 'B', 2, 0, GameStatus.IN_PROGRESS, null);
            assertScore(scores.get(4), 'A', 3, 0, 'B', 2, 0, GameStatus.IN_PROGRESS, null);
            assertScore(scores.get(5), 'A', 4, 0, 'B', 2, 0, GameStatus.FINISHED, 'A');
        }

        @Test
        @DisplayName("Should handle different letter combinations")
        void testDifferentLetterCombinations() {
            // Given
            String sequence = "XYXYXX";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(6, scores.size());
            assertEquals('X', scores.get(0).player1().getIdentifier());
            assertEquals('Y', scores.get(0).player2().getIdentifier());
            assertEquals(GameStatus.FINISHED, scores.get(5).status());
            assertEquals('X', scores.get(5).winner().getIdentifier());
        }
        @Test
        @DisplayName("Should handle minimum valid winning sequence")
        void testMinimumValidWinningSequence() {
            String sequence1 = "ABAAAA"; // A wins 4-1 (valid)
            List<Score> scores1 = service.processGameSequence(sequence1);
            assertEquals(5, scores1.size());
            assertEquals(GameStatus.FINISHED, scores1.get(4).status());
            assertEquals('A', scores1.get(4).winner().getIdentifier());

            // Alternative: B wins 4-0
            String sequence2 = "ABBBB"; // B wins 4-0 after A gets 1 point
            List<Score> scores2 = service.processGameSequence(sequence2);
            assertEquals(5, scores2.size());
            assertEquals(GameStatus.FINISHED, scores2.get(4).status());
            assertEquals('B', scores2.get(4).winner().getIdentifier());
        }

        @ParameterizedTest
        @DisplayName("Should handle case insensitive sequences")
        @ValueSource(strings = {"abab", "ABAB", "AbAb", "aBaB"})
        void testCaseInsensitiveSequences(String sequence) {
            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(4, scores.size());
            assertEquals('A', scores.get(0).player1().getIdentifier());
            assertEquals('B', scores.get(0).player2().getIdentifier());
        }

        @ParameterizedTest
        @DisplayName("Should work with any two different letters")
        @CsvSource({
                "MN, M, N",
                "PQ, P, Q",
                "XY, X, Y",
                "JK, J, K",
                "RS, R, S"
        })
        void testAnyTwoDifferentLetters(String letters, char expectedPlayer1, char expectedPlayer2) {
            // Given
            String sequence = letters.charAt(0) + "" + letters.charAt(1) + "" + letters.charAt(0);

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(expectedPlayer1, scores.get(0).player1().getIdentifier());
            assertEquals(expectedPlayer2, scores.get(0).player2().getIdentifier());
        }
    }

    @Nested
    @DisplayName("Deuce Scenarios")
    class DeuceScenarios {

        @Test
        @DisplayName("Should handle deuce situation")
        void testDeuceScenario() {
            // Given - Both players get to 40 (3 points each)
            String sequence = "ABABAB";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(6, scores.size());
            Score lastScore = scores.get(5);
            assertEquals(GameStatus.DEUCE, lastScore.status());
            assertEquals(3, lastScore.player1().getPoints());
            assertEquals(3, lastScore.player2().getPoints());
            assertFalse(lastScore.player1().isAdvantage());
            assertFalse(lastScore.player2().isAdvantage());
        }

        @Test
        @DisplayName("Should handle advantage after deuce")
        void testAdvantageAfterDeuce() {
            // Given - Deuce then A gets advantage
            String sequence = "ABABABA";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(7, scores.size());
            Score lastScore = scores.get(6);
            assertEquals(GameStatus.ADVANTAGE, lastScore.status());
            assertEquals('A', lastScore.getAdvantagePlayer().getIdentifier());
            assertTrue(lastScore.player1().isAdvantage());
            assertFalse(lastScore.player2().isAdvantage());
        }

        @Test
        @DisplayName("Should handle win from advantage")
        void testWinFromAdvantage() {
            // Given - Deuce, A gets advantage, then A wins
            String sequence = "ABABABAA";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(8, scores.size());
            Score lastScore = scores.get(7);
            assertEquals(GameStatus.FINISHED, lastScore.status());
            assertEquals('A', lastScore.winner().getIdentifier());
            assertEquals(5, lastScore.player1().getPoints());
            assertEquals(3, lastScore.player2().getPoints());
        }

        @Test
        @DisplayName("Should handle long deuce battle")
        void testLongDeuceBattle() {
            // Given - Multiple deuce/advantage cycles
            String sequence = "ABABABABABAB";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(12, scores.size());

            // Check that we go through deuce -> advantage -> deuce -> advantage -> deuce
            assertEquals(GameStatus.DEUCE, scores.get(5).status());      // 3-3
            assertEquals(GameStatus.ADVANTAGE, scores.get(6).status());  // 4-3 A advantage
            assertEquals(GameStatus.DEUCE, scores.get(7).status());      // 4-4
            assertEquals(GameStatus.ADVANTAGE, scores.get(8).status());  // 4-5 B advantage
            assertEquals(GameStatus.DEUCE, scores.get(9).status());      // 5-5
            assertEquals(GameStatus.ADVANTAGE, scores.get(10).status()); // 6-5 A advantage
            assertEquals(GameStatus.DEUCE, scores.get(11).status());     // 6-6
        }
    }

    @Nested
    @DisplayName("Game Termination")
    class GameTermination {

        @Test
        @DisplayName("Should stop processing when game is finished")
        void testStopWhenGameFinished() {
            // Given - Game ends after 6 points but sequence has more
            String sequence = "ABABABAAAAAAA";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then - Should only process until game ends
            assertEquals(8, scores.size()); // Stops when A wins after advantage
            assertEquals(GameStatus.FINISHED, scores.get(7).status());
        }


    }

    @Nested
    @DisplayName("Invalid Input Scenarios")
    class InvalidInputScenarios {


        @ParameterizedTest
        @DisplayName("Should reject sequences with non-alphabetic characters")
        @ValueSource(strings = {"A1", "AB2", "A B", "A-B", "A@B", "A.B"})
        void testNonAlphabeticCharacters(String sequence) {
            // When & Then
            assertThrows(InvalidSequenceException.class, () -> {
                service.processGameSequence(sequence);
            });
        }

        @ParameterizedTest
        @DisplayName("Should reject sequences with wrong number of unique letters")
        @ValueSource(strings = {"AAA", "BBBB", "ABCABC", "XYZXYZ"})
        void testWrongNumberOfUniqueLetters(String sequence) {
            // When & Then
            assertThrows(InvalidSequenceException.class, () -> {
                service.processGameSequence(sequence);
            });
        }

    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle single point sequences")
        void testSinglePointSequences() {
            // Given
            String sequence = "AB";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(2, scores.size());
            assertEquals(1, scores.get(0).player1().getPoints());
            assertEquals(1, scores.get(1).player2().getPoints());
        }


    }

    // Helper method to assert score details
    private void assertScore(Score score, char expectedPlayer1, int expectedP1Points, int expectedP1Advantage,
                             char expectedPlayer2, int expectedP2Points, int expectedP2Advantage,
                             GameStatus expectedStatus, Character expectedWinner) {
        assertEquals(expectedPlayer1, score.player1().getIdentifier());
        assertEquals(expectedPlayer2, score.player2().getIdentifier());
        assertEquals(expectedP1Points, score.player1().getPoints());
        assertEquals(expectedP2Points, score.player2().getPoints());
        assertEquals(expectedP1Advantage == 1, score.player1().isAdvantage());
        assertEquals(expectedP2Advantage == 1, score.player2().isAdvantage());
        assertEquals(expectedStatus, score.status());

        if (expectedWinner != null) {
            assertNotNull(score.winner());
            assertEquals(expectedWinner.charValue(), score.winner().getIdentifier());
        } else {
            assertNull(score.winner());
        }
    }
}

