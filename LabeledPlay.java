package me.antcode.plays;


import me.antcode.Matchup;
import me.antcode.TypesOfAction.Actions;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LabeledPlay {

    private final Actions action;
    private final int gameID;
    private final int quarter;
    private final double time;
    private final int awayScore;
    private final int homeScore;
    private final String eventTypeText;
    private final String typeText;
    private final String text;
    private final boolean shotMade;
    private final String stealPlayer;
    private final String assistPlayer;
    private final String blockPlayer;
    private final String multUsePlayer;
    private final String opponentColumn;
    private final String awayJumper;
    private final String homeJumper;
    private final int distance;
    private final boolean offensive;
    private final boolean defensive;
    private final int gamePlayNumber;
    private final boolean flagrant;
    private final boolean isThreePointer;
    private final String date;
    private final double playLength;
    private final int freeThrowNum;
    private final int freeThrowOutOf;
    private final String possessionPlayer;
    private Matchup matchup;
    public int getFreeThrowNum() {
        return freeThrowNum;
    }

    public int getFreeThrowOutOf() {
        return freeThrowOutOf;
    }

    private final List<String> homeOnCourt;

    private final List<String> awayOnCourt;


    public LabeledPlay(Actions action, int gameID, int quarter, double time, int awayScore, int homeScore, String eventTypeText, String typeText, String text, boolean shotMade, String stealPlayer, String assistPlayer, String blockPlayer, String multUsePlayer, String fouledPlayer,int distance, boolean offensive, boolean defensive, int gamePlayNumber, boolean flagrant, boolean isThreePointer, String date, double playLength, int freeThrowNum, int freeThrowOutOf, String awayJumper, String homeJumper, String possessionPlayer) {
        this.action = action;
        this.gameID = gameID;
        this.quarter = quarter;
        this.time = time;
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.eventTypeText = eventTypeText;
        this.typeText = typeText;
        this.text = text;
        this.shotMade = shotMade;
        this.stealPlayer = stealPlayer;
        this.assistPlayer = assistPlayer;
        this.blockPlayer = blockPlayer;
        this.multUsePlayer = multUsePlayer;
        this.opponentColumn = fouledPlayer;
        this.distance = distance;
        this.offensive = offensive;
        this.defensive = defensive;
        this.gamePlayNumber = gamePlayNumber;
        this.flagrant = flagrant;
        this.isThreePointer = isThreePointer;
        this.date = convertDateFormat(date);
        this.playLength = playLength;
        this.freeThrowNum = freeThrowNum;
        this.freeThrowOutOf = freeThrowOutOf;
        this.awayJumper = awayJumper;
        this.homeJumper = homeJumper;
        this.possessionPlayer = possessionPlayer;
        this.homeOnCourt = new ArrayList<>();
        this.awayOnCourt = new ArrayList<>();
        matchup = null;
    }

    public Matchup getMatchup() {
        return matchup;
    }

    public void setCourtPlayers(CSVRecord record){
        homeOnCourt.add(record.get("h1"));
        homeOnCourt.add(record.get("h2"));
        homeOnCourt.add(record.get("h3"));
        homeOnCourt.add(record.get("h4"));
        homeOnCourt.add(record.get("h5"));
        awayOnCourt.add(record.get("a1"));
        awayOnCourt.add(record.get("a2"));
        awayOnCourt.add(record.get("a3"));
        awayOnCourt.add(record.get("a4"));
        awayOnCourt.add(record.get("a5"));
        filterPlayersFromListWithDifNames("Enes Kanter", "Enes Freedom");
        filterPlayersFromListWithDifNames("PJ Hairston", "P.J. Hairston");
        filterPlayersFromListWithDifNames("C.J. Wilcox", "CJ Wilcox");
        filterPlayersFromListWithDifNames("Marcelo Huertas", "Marcelinho Huertas");
    }


    /**
     * Use to replace names that don't correlate between matchup data and PBP data
     * @param nameToReplace NAME IN PBP TO REPLACE
     * @param replacingName NAME FROM MATCHUP DATA
     */
    private void filterPlayersFromListWithDifNames(String nameToReplace, String replacingName){
        if (homeOnCourt.contains(nameToReplace)){
            homeOnCourt.set(homeOnCourt.indexOf(nameToReplace), replacingName);
        }else if (awayOnCourt.contains(nameToReplace)){
            awayOnCourt.set(awayOnCourt.indexOf(nameToReplace), replacingName);
        }
    }



    public List<String> getHomeOnCourt() {
        return homeOnCourt;
    }

    public List<String> getAwayOnCourt() {
        return awayOnCourt;
    }

    public String getPossessionPlayer() {
        return possessionPlayer;
    }



    public String getTypeText() {
        return typeText;
    }

    public String getAwayJumper() {
        return awayJumper;
    }

    public String getHomeJumper() {
        return homeJumper;
    }


    public void setMatchup(Matchup matchup) {
        this.matchup = matchup;
    }


    public String getStealPlayer() {
        return stealPlayer;
    }

    public String getAssistPlayer() {
        return assistPlayer;
    }

    public String getBlockPlayer() {
        return blockPlayer;
    }

    public String getMultUsePlayer() {
        return multUsePlayer;
    }


    public String getOpponentColumn() {
        return opponentColumn;
    }


    public String getDate() {
        return date;
    }

    public double getPlayLength() {
        return playLength;
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


    public String getEventTypeText() {
        return eventTypeText;
    }

    public String getText() {
        return text;
    }

    public boolean isShotMade() {
        return shotMade;
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
        return gameID + " | " + gamePlayNumber + " | " + eventTypeText + " | " + text + " | " + action;
    }

    private String convertDateFormat(String dateStr) {
        // Define the date format for MM/DD/YYYY
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        // Define the desired date format for YYYY-MM-DD
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Check if the date is already in the desired format
        try {
            // Parse the date to see if it's already in the YYYY-MM-DD format
            outputFormat.setLenient(false); // Enforce strict parsing
            Date date = outputFormat.parse(dateStr);
            return dateStr; // Date is already in the desired format, return it as is
        } catch (ParseException e) {
            // If parsing fails, continue to try converting from MM/DD/YYYY format
        }

        try {
            // Parse the input date string into a Date object
            Date date = inputFormat.parse(dateStr);
            // Convert it to the new format
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date format";
        }
    }
}

