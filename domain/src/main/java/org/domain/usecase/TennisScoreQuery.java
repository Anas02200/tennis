package org.domain.usecase;

public class TennisScoreQuery {
    private final GameSequenceProcessor gameSequenceProcessor;


    public TennisScoreQuery(GameSequenceProcessor tennisScoreService) {
        this.gameSequenceProcessor = tennisScoreService;
    }

    public GameResultDto execute(String sequence) {
        var scores = gameSequenceProcessor.processGameSequence(sequence);
        var formattedScores = scores.stream()
                .map(gameSequenceProcessor::formatScore)
                .toList();

        return new GameResultDto(sequence, formattedScores);
    }

}
