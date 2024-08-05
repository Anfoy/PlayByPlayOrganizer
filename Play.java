package me.antcode.plays;

import me.antcode.Matchup;
import me.antcode.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Top of the hierarchy for all plays. Contains crucial information about the plays.
 * IMPORTANT:
 * IF getter returns an integer, that means that if the value was NA for that column it would be 0 in the getter. Important
 * to note for distance.
 */
public class Play {

    // Basic information about the play
    private final int gameID;
    private final String season;
    private final String date;
    private final Matchup matchup;
    private final int quarter;
    private final double playDuration; // Duration since the last play
    private PlayTypes playType;
    private final double timeLeftInQuarter;
    private final int awayScore;
    private final int homeScore;

    // Jump ball information
    private Player jumperOne;
    private Player jumperTwo;
    private Player jumperReceiver;

    // Shot information
    private Player playerShooting;
    private int distance;

    // Assist information
    private Player playerAssisted;
    private Player playerAssisting;

    // Rebound information
    private Player rebounder;
    private boolean wasOffensive = false;
    private boolean wasDefensive = false;
    private boolean wasTeam = false;

    // Turnover information
    private boolean turnoverSpecificPlayer = false;
    private Player turnoverCommitter;
    private Player stealer;

    // Foul information
    private Player foulCommitter;
    private Player techOnePlayer;
    private Player techTwoPlayer;
    private Player playerFouled;

    // Block information
    private Player blockedPlayer;
    private Player playerBlocking;

    // Free throw information
    private boolean wasTechnical = false;
    private int freeThrowNumber;
    private int freeThrowTotal;
    private boolean madeFreeThrow = false;
    private Player freeThrowShooter;

    // Violation information
    private Player whoViolated;
    private boolean wasLane = false;
    private boolean defensiveGoalTending = false;
    private boolean kickedBall = false;
    private boolean wasDelayOfGame = false;
    private boolean wasDoubleLane = false;

    // Other information
    private List<LabeledPlay> makeUpOfPlay;
    private final List<Player> fiveOnCourtHome;
    private final List<Player> fiveOnCourtAway;

    //Ejection information
    private List<Player> playersEjected;

    /**
     * Constructor to initialize a play with necessary details.
     *
     * @param matchup          The matchup associated with this play.
     * @param playType         The type of the play.
     * @param makeUpOfPlay     The sequence of labeled plays making up this play.
     * @param fiveOnCourtHome  The home players on court during the play.
     * @param fiveOnCourtAway  The away players on court during the play.
     * @param homeScore        The home team score.
     * @param awayScore        The away team score.
     */
    public Play(Matchup matchup, PlayTypes playType, List<LabeledPlay> makeUpOfPlay, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway, int homeScore, int awayScore) {
        this.matchup = matchup;
        this.gameID = matchup.getGameID();
        this.season = matchup.getSeason();
        this.date = matchup.getDate();
        this.fiveOnCourtHome = new ArrayList<>(fiveOnCourtHome);
        this.fiveOnCourtAway = new ArrayList<>(fiveOnCourtAway);
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.quarter = makeUpOfPlay.getLast().getQuarter();
        this.playType = playType;
        this.makeUpOfPlay = makeUpOfPlay;
        this.timeLeftInQuarter = makeUpOfPlay.getLast().getTime();
        this.playersEjected = new ArrayList<>();

        if (matchup.getPlayByPlays().isEmpty() || matchup.getPlayByPlays().getLast().getMakeUpOfPlay().getLast().getQuarter() != this.quarter) {
            this.playDuration = 720 - this.timeLeftInQuarter;
        } else {
            this.playDuration = matchup.getPlayByPlays().getLast().getMakeUpOfPlay().getLast().getTime() - this.timeLeftInQuarter;
        }
    }

    // Getter and setter methods


    public boolean isWasDoubleLane() {
        return wasDoubleLane;
    }

    public void setWasDoubleLane(boolean wasDoubleLane) {
        this.wasDoubleLane = wasDoubleLane;
    }

    public boolean isWasDelayOfGame() {
        return wasDelayOfGame;
    }

    public void setWasDelayOfGame(boolean wasDelayOfGame) {
        this.wasDelayOfGame = wasDelayOfGame;
    }

    public List<Player> getPlayersEjected() {
        return playersEjected;
    }

    public void setPlayersEjected(List<Player> playersEjected) {
        this.playersEjected = playersEjected;
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

    /**
     * Returns the distance of the shot.
     * @return returns distance of shot, otherwise returns 0 if shot had no distance, or it was not a shot.
     */
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

    public void setJumperReceiver(Player jumperReceiver) {
        this.jumperReceiver = jumperReceiver;
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

    @Override
    public String toString() {
        return playType.toString();
    }
}
