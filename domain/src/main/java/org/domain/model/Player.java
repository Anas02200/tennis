package org.domain.model;


import lombok.Getter;


@Getter
public class Player {

    private final char identifier;
    private final int points;
    private final boolean  advantage;

    public Player(char identifier) {
        this.identifier = Character.toUpperCase(identifier);
        this.points = 0;
        this.advantage = false;
    }
    private Player(char identifier, int points, boolean hasAdvantage) {
        this.identifier = identifier;
        this.points = points;
        this.advantage = hasAdvantage;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return identifier == player.identifier;
    }

    @Override
    public int hashCode() {
        return Character.hashCode(identifier);
    }

    @Override
    public String toString() {
        return String.valueOf(identifier);
    }
    public Player addPoint() {
        return new Player(identifier, this.points + 1, advantage);
    }
    public Player setAdvantage(boolean advantage) {
        return new Player(identifier, points, advantage);
    }
    public boolean canWinGame() {
        return points >= 4;
    }

    public boolean isAtDeuce() {
        return points >= 3;
    }
    public String getScoreDisplay() {
        return switch (points) {
            case 0 -> "0";
            case 1 -> "15";
            case 2 -> "30";
            default -> "40";
        };
    }
}
