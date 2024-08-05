package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class BlockManager extends Manager {

    /**
     * Handles the processing of a block play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the block play.
     */
    public int handleBlock(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        LabeledPlay lPlayTwo;
        try {
            lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (lPlayTwo.getAction() != Actions.TEAM && lPlayTwo.getAction() != Actions.REBOUND) {
                createSingleBlockPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            } else {
                createBlockPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleBlockPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        }
        return index;
    }

    /**
     * Creates a block play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createBlockPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.BLOCK_AND_NO_POSSESSION_CHANGE, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        Player playerTwo = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (lPlayOne.getAthlete_id_1() != 0){
            playerTwo = matchup.findPlayerObject(lPlayOne.getAthlete_id_2());
        }
        if (playerTwo != null && playerTwo.getId() != 0){
            playerTwo.addBlocks(1);
            play.setPlayerBlocking(playerTwo);
        }
        if (playerOne != null && playerOne.getId() != 0){
            playerOne.addFieldGoalsAttempted(1);
            if (lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            play.setPlayerShooting(playerOne);
            play.setBlockedPlayer(playerOne);
        }
        if (lPlayTwo.getAction() == Actions.TEAM || lPlayTwo.getAction() == Actions.REBOUND){
            if (lPlayTwo.getAction() == Actions.REBOUND) {
                Player rebounder = matchup.findPlayerObject(lPlayTwo.getAthlete_id_1());
                if (rebounder != null && rebounder.getId() != 0){

                    rebounder.addRebounds(1);
                    play.setRebounder(rebounder);
                    play.setWasTeam(false);
                }
            }
            if (lPlayTwo.getAction() == Actions.TEAM){
                play.setWasTeam(true);
            }
            if (lPlayTwo.isDefensive()) {
                play.setPlayType(PlayTypes.BLOCK_AND_POSSESSION_CHANGE);
            }

            for (Play play1 : matchup.getPlayByPlays()) {
                if (!isDuplicatedOrUpgraded(play, play1)) continue;
                if (play1.getPlayerBlocking() != null) {
                    play1.getPlayerBlocking().setBlocks(play1.getPlayerBlocking().getBlocks() - 1);
                }
                matchup.getPlayByPlays().remove(play1);
                matchup.getPlayByPlays().add(play);
                return;
            }
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    private boolean isDuplicatedOrUpgraded(Play play, Play playInLoop){
        if (!playInLoop.getPlayType().toString().contains("BLOCK")) return false;
        if (playInLoop.getTimeLeftInQuarter() != play.getTimeLeftInQuarter()) return false;
        if (playInLoop.getQuarter() != play.getQuarter()) return false;
        return playInLoop.getPlayerBlocking() == play.getPlayerBlocking();
    }

    /**
     * Creates a single block play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleBlockPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.BLOCK, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        Player playerTwo = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (lPlayOne.getAthlete_id_1() != 0){
            playerTwo = matchup.findPlayerObject(lPlayOne.getAthlete_id_2());
        }
        if (playerOne != null && playerOne.getId() != 0){
            playerOne.addFieldGoalsAttempted(1);
            if (lPlayOne.isThreePointer()){
                playerOne.addThreePointFieldGoalsAttempted(1);
            }
            play.setPlayerShooting(playerOne);
            play.setBlockedPlayer(playerOne);
        }
        if (playerTwo != null && playerTwo.getId() != 0){
            play.setPlayerBlocking(playerTwo);
            playerTwo.addBlocks(1);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }


}
