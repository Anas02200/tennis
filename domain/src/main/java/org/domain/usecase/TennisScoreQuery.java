package org.domain.usecase;



public class TennisScoreQuery {
    private final GameSequenceProcessor gameSequenceProcessor;


    public TennisScoreQuery(GameSequenceProcessor tennisScoreService) {
        this.gameSequenceProcessor = tennisScoreService;
    }

    public GameResult execute(String sequence) {
        var scores = gameSequenceProcessor.processGameSequence(sequence);
        return new GameResult(sequence, scores);
    }



}
