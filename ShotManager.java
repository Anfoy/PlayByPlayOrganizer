package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class ShotManager extends Manager {



    /**
     * Handles the processing of a shot play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the shot play.
     */
    public int handleShot(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        if (lPlayOne.isShotMade()) {
            createMadeShotPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        } else {
            try{

                LabeledPlay lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
                if (!lPlayTwo.getTypeText().contains("rebound")) {
                    createSingleMissedShotPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                } else {
                    createMissedShotPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                    index++;
                }
            }catch (IndexOutOfBoundsException e){
                createSingleMissedShotPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            }
        }
        return index;
    }

    /**
     * Creates a single missed shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleMissedShotPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        play.setDistance(lPlayOne.getDistance());
        if (playerOne != null && playerOne.getId() != 0){

            if ( lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            playerOne.addFieldGoalsAttempted(1);
            play.setPlayerShooting(playerOne);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a missed shot play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createMissedShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (playerOne != null){
            if (lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            playerOne.addFieldGoalsAttempted(1);
            play.setDistance(lPlayOne.getDistance());
            play.setPlayerShooting(playerOne);
        }
        if (lPlayTwo.getAction() == Actions.TEAM){
            play.setPlayType(lPlayTwo.isOffensive() ? PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND : PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            play.setWasTeam(true);
            play.setWasOffensive(lPlayTwo.isOffensive());
            play.setWasDefensive(!lPlayTwo.isOffensive());
        } else if (lPlayTwo.getAction() == Actions.REBOUND){
            Player rebounder = matchup.findPlayerObject(lPlayTwo.getAthlete_id_1());
            play.setWasTeam(false);
            if (rebounder != null){
                play.setRebounder(rebounder);

                if (!lPlayTwo.getText().equals("na")) {
                    rebounder.addRebounds(1);
                }
                play.setRebounder(rebounder);
            }
            play.setPlayType(lPlayTwo.isOffensive() ? PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND : PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            play.setWasOffensive(lPlayTwo.isOffensive());
            play.setWasDefensive(!lPlayTwo.isOffensive());
        }

        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a made shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createMadeShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        play.setDistance(lPlayOne.getDistance());
        if (playerOne != null && playerOne.getId() != 0){
            if (lPlayOne.isThreePointer()){
                playerOne.addPoints(3);
                playerOne.addThreePointFieldGoalsMade(1);
                playerOne.addThreePointFieldGoalsAttempted(1);
            } else {
                playerOne.addPoints(2);
            }
            playerOne.addFieldGoalsAttempted(1);
            playerOne.addFieldGoalsMade(1);
            play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.getAthlete_id_1()));
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a jump ball play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createJumpBallPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.JUMPBALL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        play.setJumperOne(matchup.findPlayerObject(lPlayOne.getAthlete_id_1()));
        play.setJumperTwo(matchup.findPlayerObject(lPlayOne.getAthlete_id_2()));
        play.setJumperReceiver(matchup.findPlayerObject(lPlayOne.getAthlete_id_3()));
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

}
