package org.domain.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Score {
    private final Player player1;
    private final Player player2;
    private final GameStatus status;
    private final Player winner;


    public Score(Player player1, Player player2,
                 GameStatus status, Player winner) {
        this.player1 = player1;
        this.player2 = player2;

        this.status = status;
        this.winner = winner;
    }
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
    public int getPlayer1Points() {
        return player1.getPoints();
    }

    public int getPlayer2Points() {
        return player2.getPoints();
    }

    public Player getPlayerByIdentifier(char identifier) {
        if (player1.getIdentifier() == Character.toUpperCase(identifier)) {
            return player1;
        } else if (player2.getIdentifier() == Character.toUpperCase(identifier)) {
            return player2;
        }
        throw new IllegalArgumentException("Player with identifier " + identifier + " not found");
    }
    public Player getOpponent(Player player) {
        if (player.equals(player1)) {
            return player2;
        } else if (player.equals(player2)) {
            return player1;
        }
        throw new IllegalArgumentException("Player not found in this game");
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

    @Override
    public int hashCode() {
        return Objects.hash(player1, player2, status, winner);
    }
}
