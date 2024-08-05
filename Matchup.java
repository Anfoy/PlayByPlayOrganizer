package me.antcode;

import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Matchup information for all games.
 */
public class Matchup {

    private final String date;

    private final String season;//Game date

    private final String homeTeam;

    private final String awayTeam;

    private final String gameType; //EX: regular, final, semi, in season

    private  int gameID;

    private  List<Player> totalPlayers;

    private final List<Player> homePlayers;

    private final List<Player> awayPlayers;

    private final List<Player> homeStarters;

    private final List<Player> homeBench;

    private final List<Player> awayStarters;

    private final List<Player> awayBench;

    private  List<Play> playByPlays;//Play by play for this matchup

    private final List<LabeledPlay> labeledPlayList;


    public Matchup(String date, String homeTeam, String awayTeam, String gameType, List<Player> homeStarters, List<Player> homeBench, List<Player> awayStarters, List<Player> awayBench, List<Play> playByPlays) {
        this.date = date;
        this.season = this.date.substring(0, 4);
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameType = gameType;
        this.gameID = 0;
        this.homeStarters = homeStarters;
        this.homeBench = homeBench;
        this.awayStarters = awayStarters;
        this.awayBench = awayBench;
        this.playByPlays = playByPlays;
        labeledPlayList = new ArrayList<>();
        this.homePlayers = Stream.of(homeStarters, homeBench).flatMap(List::stream).collect(Collectors.toList());
        this.awayPlayers = Stream.of(awayStarters, awayBench).flatMap(List::stream).collect(Collectors.toList());
        this.totalPlayers = Stream.of(homePlayers, awayPlayers).flatMap(List::stream).collect(Collectors.toList());
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setPlayByPlays(List<Play> playByPlays) {
        this.playByPlays = playByPlays;
    }

    public void setTotalPlayers(List<Player> totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getSeason() {
        return season;
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

    public List<Player> getHomePlayers() {
        return homePlayers;
    }

    public List<Player> getAwayPlayers() {
        return awayPlayers;
    }

    public List<Player> getTotalPlayers() {
        return totalPlayers;
    }

    /**
     * Loops through all the player objects in this matchup to find the matching object with the parameterized id.
     * @param playerID ID to look for
     * @return Found player object.
     */
    public Player findPlayerObject(int playerID) {
        for (Player player : totalPlayers) {
            if (player.getId() != playerID && player.getExtraID() != playerID) continue;
            return player;
        }
        return null;
    }


    public List<LabeledPlay> getLabeledPlayList() {
        return labeledPlayList;
    }
}
