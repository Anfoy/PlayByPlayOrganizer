package me.antcode.plays;

import me.antcode.Matchup;
import me.antcode.Player;

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

    private final double playDuration; //How long did the play last since the last play that took place

    PlayTypes playType;

    private final double timeLeftInQuarter;

    private  final int awayScore;

    private final int homeScore;

    //JUMP BALL
    private Player jumperOne;

    private Player jumperTwo;

    private Player jumperReceiver;

    //SHOT INFORMATION

    private Player playerShooting;
    private int distance;



    //Assist INFORMATION:

    private Player playerAssisted;

    private Player playerAssisting;


    //REBOUND INFORMATION

    private Player rebounder;

    private boolean wasOffensive = false, wasDefensive = false;

    private boolean wasTeam = false;

    //TURNOVERS

    private boolean turnoverSpecificPlayer = false;
    private Player turnoverCommitter;
    private Player stealer;

    //FOUL
    private Player foulCommitter;

    private Player techOnePlayer;

    private Player techTwoPlayer;

    private Player playerFouled;

    //BLOCK
    private Player blockedPlayer;

    private Player playerBlocking;
    //FREETHROW
    private boolean wasTechnical = false;

    private int freeThrowNumber;

    private int freeThrowTotal;

    private boolean madeFreeThrow = false;

    private Player freeThrowShooter;

    //VIOLATIONS

    private Player whoViolated;

    private boolean wasLane = false, defensiveGoalTending = false, kickedBall = false;


    private  List<LabeledPlay> makeUpOfPlay;
    private final List<Player> fiveOnCourtHome;

    private final List<Player> fiveOnCourtAway;



    public Play(Matchup matchup, PlayTypes playType, List<LabeledPlay> makeUpOfPlay, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway, int homeScore, int awayScore){
        this.matchup = matchup;
        this.gameID = matchup.getGameID();
        this.season = matchup.getSeason();
        this.date = matchup.getDate();
        this.fiveOnCourtHome = new ArrayList<>();
        this.fiveOnCourtAway = new ArrayList<>();
        this.fiveOnCourtHome.addAll(fiveOnCourtHome);
        this.fiveOnCourtAway.addAll(fiveOnCourtAway);
        if (matchup.getPlayByPlays().isEmpty() || matchup.getPlayByPlays().getLast().getMakeUpOfPlay().getLast().quarter()!= makeUpOfPlay.getLast().quarter()){
            this.playDuration = 720 - makeUpOfPlay.getLast().time();
        }else{
            this.playDuration = matchup.getPlayByPlays().getLast().getMakeUpOfPlay().getLast().time() - makeUpOfPlay.getLast().time();
        }
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.quarter = makeUpOfPlay.getLast().quarter();
        this.playType = playType;
        this.makeUpOfPlay = makeUpOfPlay;
        this.timeLeftInQuarter = makeUpOfPlay.getLast().time();
    }

    public Player getTechOnePlayer() {
        return techOnePlayer;
    }

    public void setTechOnePlayer(Player techOnePlayer) {
        this.techOnePlayer = techOnePlayer;
    }

    public Player getTechTwoPlayer() {
        return techTwoPlayer;
    }

    public void setTechTwoPlayer(Player techTwoPlayer) {
        this.techTwoPlayer = techTwoPlayer;
    }

    public boolean isWasLane() {
        return wasLane;
    }

    public void setWasLane(boolean wasLane) {
        this.wasLane = wasLane;
    }

    public boolean isDefensiveGoalTending() {
        return defensiveGoalTending;
    }

    public void setDefensiveGoalTending(boolean defensiveGoalTending) {
        this.defensiveGoalTending = defensiveGoalTending;
    }

    public boolean isKickedBall() {
        return kickedBall;
    }

    public void setKickedBall(boolean kickedBall) {
        this.kickedBall = kickedBall;
    }

    public void setMakeUpOfPlay(List<LabeledPlay> makeUpOfPlay) {
        this.makeUpOfPlay = makeUpOfPlay;
    }


    public boolean isTurnoverSpecificPlayer() {
        return turnoverSpecificPlayer;
    }

    public void setTurnoverSpecificPlayer(boolean turnoverSpecificPlayer) {
        this.turnoverSpecificPlayer = turnoverSpecificPlayer;
    }


    public Player getStealer() {
        return stealer;
    }

    public void setStealer(Player stealer) {
        this.stealer = stealer;
    }

    public Player getWhoViolated() {
        return whoViolated;
    }

    public void setWhoViolated(Player whoViolated) {
        this.whoViolated = whoViolated;
    }

    public Player getFreeThrowShooter() {
        return freeThrowShooter;
    }

    public void setFreeThrowShooter(Player freeThrowShooter) {
        this.freeThrowShooter = freeThrowShooter;
    }

    public boolean isWasTechnical() {
        return wasTechnical;
    }

    public void setWasTechnical(boolean wasTechnical) {
        this.wasTechnical = wasTechnical;
    }

    public int getFreeThrowNumber() {
        return freeThrowNumber;
    }

    public void setFreeThrowNumber(int freeThrowNumber) {
        this.freeThrowNumber = freeThrowNumber;
    }

    public int getFreeThrowTotal() {
        return freeThrowTotal;
    }

    public void setFreeThrowTotal(int freeThrowTotal) {
        this.freeThrowTotal = freeThrowTotal;
    }

    public boolean isMadeFreeThrow() {
        return madeFreeThrow;
    }

    public void setMadeFreeThrow(boolean madeFreeThrow) {
        this.madeFreeThrow = madeFreeThrow;
    }



    public double getTimeLeftInQuarter() {
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

    public String toString(){
        return playType.toString();
    }

    public List<Player> getFiveOnCourtHome() {
        return fiveOnCourtHome;
    }

    public List<Player> getFiveOnCourtAway() {
        return fiveOnCourtAway;
    }

    public double getPlayDuration() {
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
        return jumperReceiver;
    }

    public void setJumperReceiver(Player ballReceiver) {
        this.jumperReceiver = ballReceiver;
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

}
