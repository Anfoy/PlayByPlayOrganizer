package me.antcode;

import me.antcode.plays.Play;
import java.util.List;

public class Matchup {

    private final String date;

    private final String homeTeam;

    private final String awayTeam;

    private final String gameType;

    private final int gameID;

    private final List<Player> homeStarters;

    private final List<Player> homeBench;

    private final List<Player> awayStarters;

    private final List<Player> awayBench;

    private final List<Play> playByPlays;


    public Matchup(String date, String homeTeam, String awayTeam, String gameType, int gameID, List<Player> homeStarters, List<Player> homeBench, List<Player> awayStarters, List<Player> awayBench, List<Play> playByPlays) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameType = gameType;
        this.gameID = gameID;
        this.homeStarters = homeStarters;
        this.homeBench = homeBench;
        this.awayStarters = awayStarters;
        this.awayBench = awayBench;
        this.playByPlays = playByPlays;
    }

    public String getDate() {
        return date;
    }

    public String getGameType() {
        return gameType;
    }

    public int getGameID() {
        return gameID;
    }

    public List<Player> getHomeStarters() {
        return homeStarters;
    }

    public List<Player> getHomeBench() {
        return homeBench;
    }

    public List<Player> getAwayStarters() {
        return awayStarters;
    }

    public List<Player> getAwayBench() {
        return awayBench;
    }

    public List<Play> getPlayByPlays() {
        return playByPlays;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }
}
