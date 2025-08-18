package org.domain.usecase;

import org.domain.model.GameStatus;
import org.domain.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TennisScoregameSequenceProcessorImplTest {

    private GameSequenceProcessor gameSequenceProcessor;

    @BeforeEach
    void setUp() {
        gameSequenceProcessor = new GameSequenceProcessor();
    }

    @Test
    void testBasicScoringWithAB() {
        List<Score> scores = gameSequenceProcessor.processGameSequence("ABABAA");

        assertEquals(6, scores.size());

        // First point: A scores
        assertEquals("Player A : 15 / Player B : 0", gameSequenceProcessor.formatScore(scores.get(0)));

        // Second point: B scores
        assertEquals("Player A : 15 / Player B : 15", gameSequenceProcessor.formatScore(scores.get(1)));

        // Third point: A scores
        assertEquals("Player A : 30 / Player B : 15", gameSequenceProcessor.formatScore(scores.get(2)));

        // Fourth point: B scores
        assertEquals("Player A : 30 / Player B : 30", gameSequenceProcessor.formatScore(scores.get(3)));

        // Fifth point: A scores
        assertEquals("Player A : 40 / Player B : 30", gameSequenceProcessor.formatScore(scores.get(4)));

        // Sixth point: A wins
        assertEquals("Player A wins the game", gameSequenceProcessor.formatScore(scores.get(5)));
        assertEquals(GameStatus.FINISHED, scores.get(5).getStatus());
    }

    @Test
    void testBasicScoringWithXY() {
        List<Score> scores = gameSequenceProcessor.processGameSequence("XYXYXX");

        assertEquals(6, scores.size());

        // First point: X scores (first player in sequence)
        assertEquals("Player X : 15 / Player Y : 0", gameSequenceProcessor.formatScore(scores.get(0)));

        // Second point: Y scores
        assertEquals("Player X : 15 / Player Y : 15", gameSequenceProcessor.formatScore(scores.get(1)));

        // Third point: X scores
        assertEquals("Player X : 30 / Player Y : 15", gameSequenceProcessor.formatScore(scores.get(2)));

        // Fourth point: Y scores
        assertEquals("Player X : 30 / Player Y : 30", gameSequenceProcessor.formatScore(scores.get(3)));

        // Fifth point: X scores
        assertEquals("Player X : 40 / Player Y : 30", gameSequenceProcessor.formatScore(scores.get(4)));

        // Sixth point: X wins
        assertEquals("Player X wins the game", gameSequenceProcessor.formatScore(scores.get(5)));
        assertEquals(GameStatus.FINISHED, scores.get(5).getStatus());
    }
}