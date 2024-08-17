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
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the shot play.
     */
    public int handleShot(int index, Matchup matchup, LabeledPlay lPlayOne ) {
        if (lPlayOne.isShotMade()) {
            createMadeShotPlays(matchup, lPlayOne);
        } else {
            try{
                LabeledPlay lPlayTwo = matchup.getAllLabeledPlays().get(index + 1);
                Actions action = lPlayTwo.getAction();
                if (action != Actions.REBOUND && action != Actions.TEAM) {
                    createSingleMissedShotPlay(matchup, lPlayOne);
                } else {
                    createMissedShotPlays(matchup, lPlayOne, lPlayTwo);
                    index++;
                }
            }catch (IndexOutOfBoundsException e){
                createSingleMissedShotPlay(matchup, lPlayOne);
            }
        }
        return index;
    }

    /**
     * Creates a single missed shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    private void createSingleMissedShotPlay(Matchup matchup, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        play.setDistance(lPlayOne.getDistance());
        if (playerOne != null && playerOne.getId() != 0){

            if (lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            playerOne.addFieldGoalsAttempted(1);
            play.setPlayerShooting(playerOne);
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a missed shot play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createMissedShotPlays(Matchup matchup, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne, lPlayTwo),lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        if (playerOne != null){
            if (lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            playerOne.addFieldGoalsAttempted(1);
            play.setDistance(lPlayOne.getDistance());
            play.setPlayerShooting(playerOne);
        }
        Actions action = lPlayTwo.getAction();
        if (action == Actions.TEAM){
            play.setWasTeam(true);
            play.setWasOffensive(lPlayTwo.isOffensive());
            play.setWasDefensive(!lPlayTwo.isOffensive());
        } else if (action == Actions.REBOUND){
            Player rebounder = matchup.findPlayerObject(lPlayTwo.getMultUsePlayer());
            play.setWasTeam(false);
            if (rebounder != null){
                play.setRebounder(rebounder);
                rebounder.addRebounds(1);
            }
            play.setPlayType(lPlayTwo.isOffensive() ? PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND : PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            play.setWasOffensive(lPlayTwo.isOffensive());
            play.setWasDefensive(!lPlayTwo.isOffensive());
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a made shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    private void createMadeShotPlays(Matchup matchup, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(lPlayOne),lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
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
            play.setPlayerShooting(playerOne);
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a jump ball play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    public void createJumpBallPlay(Matchup matchup, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.JUMPBALL, List.of(lPlayOne),lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        play.setJumperOne(matchup.findPlayerObject(lPlayOne.getHomeJumper()));
        play.setJumperTwo(matchup.findPlayerObject(lPlayOne.getAwayJumper()));
        play.setJumperReceiver(matchup.findPlayerObject(lPlayOne.getOpponentColumn()));
        matchup.getPlayByPlays().add(play);
    }

}
