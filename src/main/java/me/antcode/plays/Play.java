package me.antcode.plays;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVDataGather;
import org.apache.commons.csv.CSVRecord;

/**
 * Top of Hierachy for all plays. Contains crucial information about the plays
 */
public class Play {

    private final int gameID;

    private final String season;

    private final String date;

    private final Matchup matchup;

    private final CSVRecord record;
    //Which play it was. Was it the first play of the game or the 30th?
    private final int playNumber;

    private final int quarter;

    private int playDuration; //How long did the play last since the last play that took place

    private final int remainingTime; //Time left in the quarter once play is done

    private final int homeScore;

    private final int awayScore;


    public Play(Matchup matchup, CSVRecord record){
        this.record = record;
        this.matchup = matchup;
        this.gameID = matchup.getGameID();
        this.season = record.get("season");
        this.date = matchup.getDate();
        this.playDuration = 0;
        this.playNumber = CSVDataGather.parseInt(record.get("game_play_number"));
        this.quarter = CSVDataGather.parseInt(record.get("qtr"));
        this.remainingTime = CSVDataGather.parseInt(record.get("time"));
        this.homeScore = CSVDataGather.parseInt(record.get("home_score"));
        this.awayScore = CSVDataGather.parseInt(record.get("away_score"));
    }

    public Matchup getMatchup() {
        return matchup;
    }

    public int getGameID() {
        return gameID;
    }

    public String getSeason() {
        return season;
    }

    public String getDate() {
        return date;
    }

    public int getPlayNumber() {
        return playNumber;
    }

    public int getQuarter() {
        return quarter;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }


    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }


    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public CSVRecord getRecord() {
        return record;
    }

    /**
     * Takes in the string of the path for that player's id and finds their matching object.
     * @param path Pathway for player id.
     * @return player of id; Null otherwise
     */
    protected Player findPlayer(String path){
     return matchup.findPlayerObject(CSVDataGather.parseInt(record.get(path)));
    }

    public String toString(){
        return "This is a play";
    }
}
