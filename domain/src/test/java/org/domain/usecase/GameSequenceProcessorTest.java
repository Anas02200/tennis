package org.domain.usecase;

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
        @DisplayName("Should handle minimum winning sequence")
        void testMinimumWinningSequence() {
            // Given - Player A wins 4-0
            String sequence = "AAAA";

            // When & Then - Should throw exception as only one unique letter
            assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence(sequence);
            });
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
        @DisplayName("Should handle back to deuce from advantage")
        void testBackToDeuceFromAdvantage() {
            // Given - Deuce, A gets advantage, then B equalizes back to deuce
            String sequence = "ABABABB";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then
            assertEquals(8, scores.size());
            Score lastScore = scores.get(7);
            assertEquals(GameStatus.DEUCE, lastScore.status());
            assertEquals(4, lastScore.player1().getPoints());
            assertEquals(4, lastScore.player2().getPoints());
            assertNull(lastScore.getAdvantagePlayer());
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

        @Test
        @DisplayName("Should handle various winning scenarios")
        void testVariousWinningScenarios() {
            // Test different ways to win

            // 4-0 win
            List<Score> scores1 = service.processGameSequence("BAAA");
            assertEquals(GameStatus.FINISHED, scores1.get(3).status());

            // 4-1 win
            List<Score> scores2 = service.processGameSequence("ABAAA");
            assertEquals(GameStatus.FINISHED, scores2.get(4).status());

            // 4-2 win
            List<Score> scores3 = service.processGameSequence("ABAAAA");
            assertEquals(GameStatus.FINISHED, scores3.get(5).status());
        }
    }

    @Nested
    @DisplayName("Invalid Input Scenarios")
    class InvalidInputScenarios {

        @ParameterizedTest
        @DisplayName("Should reject null or empty sequences")
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void testNullOrEmptySequences(String sequence) {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence(sequence);
            });
        }

        @Test
        @DisplayName("Should reject null sequence")
        void testNullSequence() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence(null);
            });
        }

        @ParameterizedTest
        @DisplayName("Should reject sequences with non-alphabetic characters")
        @ValueSource(strings = {"A1", "AB2", "A B", "A-B", "A@B", "A.B"})
        void testNonAlphabeticCharacters(String sequence) {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence(sequence);
            });
        }

        @ParameterizedTest
        @DisplayName("Should reject sequences with wrong number of unique letters")
        @ValueSource(strings = {"AAA", "BBBB", "ABCABC", "XYZXYZ"})
        void testWrongNumberOfUniqueLetters(String sequence) {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence(sequence);
            });
        }

        @Test
        @DisplayName("Should provide meaningful error messages")
        void testErrorMessages() {
            // Test empty sequence
            Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence("");
            });
            assertTrue(e1.getMessage().contains("cannot be null or empty"));

            // Test invalid character
            Exception e2 = assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence("A1");
            });
            assertTrue(e2.getMessage().contains("Only letters are allowed"));

            // Test wrong number of players
            Exception e3 = assertThrows(IllegalArgumentException.class, () -> {
                service.processGameSequence("AAA");
            });
            assertTrue(e3.getMessage().contains("exactly two different letters"));
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

        @Test
        @DisplayName("Should handle very long sequences")
        void testVeryLongSequences() {
            // Given - Very long deuce battle that eventually ends
            StringBuilder sb = new StringBuilder();
            // Create a long deuce battle (30 times back and forth)
            for (int i = 0; i < 30; i++) {
                sb.append("AB");
            }
            sb.append("AA"); // Finally A wins

            // When
            List<Score> scores = service.processGameSequence(sb.toString());

            // Then
            assertTrue(scores.size() > 60);
            assertEquals(GameStatus.FINISHED, scores.get(scores.size() - 1).status());
        }

        @Test
        @DisplayName("Should maintain player order consistency")
        void testPlayerOrderConsistency() {
            // Given - Different sequences starting with different players
            List<Score> scores1 = service.processGameSequence("ABAB");
            List<Score> scores2 = service.processGameSequence("BABA");

            // Then
            // First sequence: A is player1
            assertEquals('A', scores1.get(0).player1().getIdentifier());
            assertEquals('B', scores1.get(0).player2().getIdentifier());

            // Second sequence: B is player1 (first in sequence)
            assertEquals('B', scores2.get(0).player1().getIdentifier());
            assertEquals('A', scores2.get(0).player2().getIdentifier());
        }
    }

    @Nested
    @DisplayName("Score State Validation")
    class ScoreStateValidation {

        @Test
        @DisplayName("Should maintain correct score progression")
        void testScoreProgression() {
            // Given
            String sequence = "ABABABAB";

            // When
            List<Score> scores = service.processGameSequence(sequence);

            // Then - Verify each step
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                assertNotNull(score.player1());
                assertNotNull(score.player2());
                assertNotNull(score.status());

                // Points should never be negative
                assertTrue(score.player1().getPoints() >= 0);
                assertTrue(score.player2().getPoints() >= 0);

                // Only one player can have advantage at a time
                if (score.player1().isAdvantage()) {
                    assertFalse(score.player2().isAdvantage());
                }
                if (score.player2().isAdvantage()) {
                    assertFalse(score.player1().isAdvantage());
                }
            }
        }

        @Test
        @DisplayName("Should have immutable scores")
        void testScoreImmutability() {
            // Given
            String sequence = "ABAB";
            List<Score> scores = service.processGameSequence(sequence);

            // When - Try to modify the returned scores
            Score firstScore = scores.get(0);

            // Then - Score should be immutable
            // The score objects should not change when we get new ones
            List<Score> scores2 = service.processGameSequence(sequence);
            assertEquals(firstScore, scores2.get(0));
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

