package org.infrastructure.web.response;

import org.domain.model.Score;
import org.domain.usecase.GameResult;

import java.util.List;

public record GameResultResponse(String sequence, List<String> scores) {


    public static GameResultResponse of(GameResult gameResult) {


        return new GameResultResponse(gameResult.sequence(), formatScores(gameResult.scores()));


    }

    private static String formatScore(Score score) {
        return switch (score.status()) {
            case DEUCE -> "Deuce";
            case ADVANTAGE -> "Advantage Player " + score.getAdvantagePlayer();
            case FINISHED -> "Player " + score.winner() + " wins the game";
            default -> "Player " + score.player1() + " : " + score.getPlayer1ScoreDisplay() +
                    " / Player " + score.player2() + " : " + score.getPlayer2ScoreDisplay();
        };
    }

    private static List<String> formatScores(List<Score> scores) {
        return scores.stream().map(GameResultResponse::formatScore).toList();
    }
}
