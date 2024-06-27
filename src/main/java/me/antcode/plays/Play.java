package me.antcode.plays;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Top of Hierachy for all plays. Contains crucial information about the plays
 */
public class Play {

    private final int gameID;

    private final String season;

    private final String date;

    private final Matchup matchup;

    private final int quarter;

    //Which play it was. Was it the first play of the game or the 30th?
    private ArrayList<Integer> playNumbers;

    private int playDuration; //How long did the play last since the last play that took place

    PlayTypes playType;

    private final int timeLeftInQuarter;

    private  final int awayScore;

    private final int homeScore;

    //JUMP BALL
    private Player jumperOne;

    private Player jumperTwo;

    private Player ballReceiver;

    //SHOT INFORMATION

    private Player playerShooting;
    private Actions shotType;
    private int distance;



    //Assist INFORMATION:

    private Player playerAssisted;

    private Player playerAssisting;

    private List<Actions> occurredActions;

    //REBOUND INFORMATION

    private Player rebounder;

    private boolean wasOffensive, wasDefensive;

    private boolean wasTeam;

    //TURNOVERS

    private Player turnoverCommitter;

    //FOULS


    private Player foulCommitter;

    private Player playerFouled;

    //BLOCK

    private Player blockedPlayer;

    private Player playerBlocking;

    private final List<LabeledPlay> makeUpOfPlay;
    private final List<Player> fiveOnCourtHome;

    private final List<Player> fiveOnCourtAway;

    public Play(Matchup matchup, PlayTypes playType, List<LabeledPlay> makeUpOfPlay, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway){
        this.matchup = matchup;
        this.gameID = matchup.getGameID();
        this.season = matchup.getSeason();
        this.date = matchup.getDate();
        this.fiveOnCourtHome = fiveOnCourtHome;
        this.fiveOnCourtAway = fiveOnCourtAway;
        if (matchup.getPlayByPlays().isEmpty()){
            this.playDuration = 720 - makeUpOfPlay.getLast().time();
        }else{
            this.playDuration = matchup.getPlayByPlays().getLast().getMakeUpOfPlay().getLast().time() - makeUpOfPlay.getLast().time();
        }
        this.awayScore = makeUpOfPlay.getLast().awayScore();
        this.homeScore = makeUpOfPlay.getLast().homeScore();
        this.quarter = makeUpOfPlay.getLast().quarter();
        this.playType = playType;
        this.makeUpOfPlay = makeUpOfPlay;
        this.timeLeftInQuarter = makeUpOfPlay.getLast().time();
    }

    public int getTimeLeftInQuarter() {
        return timeLeftInQuarter;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getQuarter() {
        return quarter;
    }

    public List<LabeledPlay> getMakeUpOfPlay() {
        return makeUpOfPlay;
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


    public int getAwayScore() {
        return awayScore;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public String toString(){
        return playType.toString();
    }

    public List<Player> getFiveOnCourtHome() {
        return fiveOnCourtHome;
    }

    public List<Player> getFiveOnCourtAway() {
        return fiveOnCourtAway;
    }

    public ArrayList<Integer> getPlayNumbers() {
        return playNumbers;
    }

    public void setPlayNumbers(ArrayList<Integer> playNumbers) {
        this.playNumbers = playNumbers;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public PlayTypes getPlayType() {
        return playType;
    }

    public void setPlayType(PlayTypes playType) {
        this.playType = playType;
    }

    public Player getJumperOne() {
        return jumperOne;
    }

    public void setJumperOne(Player jumperOne) {
        this.jumperOne = jumperOne;
    }

    public Player getJumperTwo() {
        return jumperTwo;
    }

    public void setJumperTwo(Player jumperTwo) {
        this.jumperTwo = jumperTwo;
    }

    public Player getJumperReceiver() {
        return ballReceiver;
    }

    public void setJumperReceiver(Player ballReceiver) {
        this.ballReceiver = ballReceiver;
    }

    public Player getPlayerShooting() {
        return playerShooting;
    }

    public void setPlayerShooting(Player playerShooting) {
        this.playerShooting = playerShooting;
    }

    public Player getPlayerAssisted() {
        return playerAssisted;
    }

    public void setPlayerAssisted(Player playerAssisted) {
        this.playerAssisted = playerAssisted;
    }

    public Player getPlayerAssisting() {
        return playerAssisting;
    }

    public void setPlayerAssisting(Player playerAssisting) {
        this.playerAssisting = playerAssisting;
    }

    public List<Actions> getOccurredActions() {
        return occurredActions;
    }

    public void setOccurredActions(List<Actions> occurredActions) {
        this.occurredActions = occurredActions;
    }

    public Player getRebounder() {
        return rebounder;
    }

    public void setRebounder(Player rebounder) {
        this.rebounder = rebounder;
    }

    public boolean isWasOffensive() {
        return wasOffensive;
    }

    public void setWasOffensive(boolean wasOffensive) {
        this.wasOffensive = wasOffensive;
    }

    public boolean isWasDefensive() {
        return wasDefensive;
    }

    public void setWasDefensive(boolean wasDefensive) {
        this.wasDefensive = wasDefensive;
    }

    public boolean isWasTeam() {
        return wasTeam;
    }

    public void setWasTeam(boolean wasTeam) {
        this.wasTeam = wasTeam;
    }

    public Player getTurnoverCommitter() {
        return turnoverCommitter;
    }

    public void setTurnoverCommitter(Player turnoverCommitter) {
        this.turnoverCommitter = turnoverCommitter;
    }

    public Player getFoulCommitter() {
        return foulCommitter;
    }

    public void setFoulCommitter(Player foulCommitter) {
        this.foulCommitter = foulCommitter;
    }

    public Player getPlayerFouled() {
        return playerFouled;
    }

    public void setPlayerFouled(Player playerFouled) {
        this.playerFouled = playerFouled;
    }

    public Player getBlockedPlayer() {
        return blockedPlayer;
    }

    public void setBlockedPlayer(Player blockedPlayer) {
        this.blockedPlayer = blockedPlayer;
    }

    public Player getPlayerBlocking() {
        return playerBlocking;
    }

    public void setPlayerBlocking(Player playerBlocking) {
        this.playerBlocking = playerBlocking;
    }

    public Actions getShotType() {
        return shotType;
    }

    public void setShotType(Actions shotType) {
        this.shotType = shotType;
    }
}
