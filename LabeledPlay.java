package me.antcode.plays;


import me.antcode.TypesOfAction.Actions;


public class LabeledPlay {

    private final Actions action;
    private final int gameID;
    private final int quarter;
    private final double time;
    private final int awayScore;
    private final int homeScore;
    private final int athlete_id_1;
    private final int athlete_id_2;
    private final int athlete_id_3;
    private final String typeText;
    private final String text;
    private final boolean shotMade;
    private final boolean wasSteal;
    private final int distance;
    private final boolean offensive;
    private final boolean defensive;
    private final int gamePlayNumber;
    private final boolean flagrant;
    private final boolean isThreePointer;

    public LabeledPlay(int gameID, int quarter, double time, int awayScore, int homeScore, int athlete_id_1, int athlete_id_2, int athlete_id_3, String typeText, String text, boolean shotMade, boolean wasSteal, int distance, boolean offensive, boolean defensive, int gamePlayNumber, boolean flagrant, boolean isThreePointer, Actions actions) {
        this.gameID = gameID;
        this.quarter = quarter;
        this.time = time;
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.athlete_id_1 = athlete_id_1;
        this.athlete_id_2 = athlete_id_2;
        this.athlete_id_3 = athlete_id_3;
        this.typeText = typeText;
        this.text = text;
        this.shotMade = shotMade;
        this.wasSteal = wasSteal;
        this.distance = distance;
        this.offensive = offensive;
        this.defensive = defensive;
        this.gamePlayNumber = gamePlayNumber;
        this.flagrant = flagrant;
        this.isThreePointer = isThreePointer;
        this.action = actions;
    }


    public Actions getAction() {
        return action;
    }

    public int getGameID() {
        return gameID;
    }

    public int getQuarter() {
        return quarter;
    }

    public double getTime() {
        return time;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAthlete_id_1() {
        return athlete_id_1;
    }

    public int getAthlete_id_2() {
        return athlete_id_2;
    }

    public int getAthlete_id_3() {
        return athlete_id_3;
    }

    public String getTypeText() {
        return typeText;
    }

    public String getText() {
        return text;
    }

    public boolean isShotMade() {
        return shotMade;
    }

    public boolean isWasSteal() {
        return wasSteal;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isOffensive() {
        return offensive;
    }

    public boolean isDefensive() {
        return defensive;
    }

    public int getGamePlayNumber() {
        return gamePlayNumber;
    }

    public boolean isFlagrant() {
        return flagrant;
    }

    public boolean isThreePointer() {
        return isThreePointer;
    }

    @Override
    public String toString(){
        return gameID + " | " + gamePlayNumber + " | " + typeText + " | " + text + " | " + action;
    }
}

