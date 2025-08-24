package org.domain.model;

import lombok.Getter;

import java.util.Objects;


public record Score(Player player1, Player player2, GameStatus status, Player winner) {
    public Player getAdvantagePlayer() {
        if (player1.isAdvantage()) return player1;
        if (player2.isAdvantage()) return player2;
        return null;
    }

    public String getPlayer1ScoreDisplay() {
        return player1.getScoreDisplay();
    }

    public String getPlayer2ScoreDisplay() {
        return player2.getScoreDisplay();
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return Objects.equals(player1, score.player1) &&
                Objects.equals(player2, score.player2) &&
                status == score.status &&
                Objects.equals(winner, score.winner);
    }

}
