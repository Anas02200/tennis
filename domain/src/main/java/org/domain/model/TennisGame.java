package org.domain.model;
//todo add logs and custom exceptions later
public class TennisGame {
    private Player player1;
    private Player player2;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private Player winner = null;

    public TennisGame(Player player1, Player player2) {
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Players must be different");
        }
        this.player1 = player1;
        this.player2 = player2;
    }

    public Score getCurrentScore() {
        return new Score(player1, player2, status, winner);
    }

    public Score addPoint(Player playerWhoScored) {
        if (status == GameStatus.FINISHED) {
            throw new IllegalStateException("Game is already finished");
        }

        // Update the player who scored
        if (playerWhoScored.equals(player1)) {
            player1 = player1.addPoint();
        } else if (playerWhoScored.equals(player2)) {
            player2 = player2.addPoint();
        } else {
            throw new IllegalArgumentException("Player " + playerWhoScored + " is not part of this game");
        }

        updateGameStatus();
        return getCurrentScore();
    }

    private void updateGameStatus() {
        // Reset advantages first
        player1 = player1.setAdvantage(false);
        player2 = player2.setAdvantage(false);

        // Both players have at least 3 points (40-40 or more)
        if (player1.isAtDeuce() && player2.isAtDeuce()) {
            if (player1.getPoints() == player2.getPoints()) {
                status = GameStatus.DEUCE;
            } else if (Math.abs(player1.getPoints() - player2.getPoints()) == 1) {
                status = GameStatus.ADVANTAGE;
                if (player1.getPoints() > player2.getPoints()) {
                    player1 = player1.setAdvantage(true);
                } else {
                    player2 = player2.setAdvantage(true);
                }
            } else {
                // One player wins by 2+ points after deuce
                status = GameStatus.FINISHED;
                winner = player1.getPoints() > player2.getPoints() ? player1 : player2;
            }
        } else if (player1.canWinGame() || player2.canWinGame()) {
            // Standard win (4 points with opponent having less than 3)
            if ((player1.canWinGame() && !player2.isAtDeuce()) ||
                    (player2.canWinGame() && !player1.isAtDeuce())) {
                status = GameStatus.FINISHED;
                winner = player1.getPoints() > player2.getPoints() ? player1 : player2;
            }
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

//    public void reset() {
//        player1 = player1.reset();
//        player2 = player2.reset();
//        status = GameStatus.IN_PROGRESS;
//        winner = null;
//    }

}
