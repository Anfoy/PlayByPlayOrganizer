package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class FreeThrowManager extends Manager {




    /**
     * Handles the processing of a free throw play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the free throw play.
     */
    public int handleFreeThrow(int index, Matchup matchup, LabeledPlay lPlayOne) {
        try {
            LabeledPlay lPlayTwo = matchup.getAllLabeledPlays().get(index + 1);
            Actions action = lPlayTwo.getAction();
            if (action != Actions.TEAM && action != Actions.REBOUND) {
                createSingleFTPlay(matchup, lPlayOne);
            } else {
                createFreeThrowPlays(matchup,  lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleFTPlay(matchup,  lPlayOne);
        }
        return index;
    }

    /**
     * Creates a free throw play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createFreeThrowPlays(Matchup matchup,LabeledPlay lPlayOne, LabeledPlay lPlayTwo) {
        Play play = createFreeThrowPlay(matchup, lPlayOne);
        Player playerOne = updateFreeThrowShooterStats(matchup, play, lPlayOne);
        Actions actions = lPlayTwo.getAction();
        if (lPlayOne.getTypeText().contains("technical")) {
            updateTechnicalFreeThrow(play);
        } else if (lPlayOne.isFlagrant()) {
            updateFlagrantFreeThrow(play, lPlayOne);
        } else if (actions == Actions.TEAM) {
            updateTeamFreeThrow(play, lPlayOne, lPlayTwo);
        } else if (actions == Actions.REBOUND) {
            updateReboundFreeThrow(matchup, play, lPlayOne, lPlayTwo);
        } else {
            updateStandardFreeThrow(play, lPlayOne);
        }
        finalizeFreeThrowPlay(matchup, play);
    }

    /**
     * Creates a single free throw play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    private void createSingleFTPlay(Matchup matchup, LabeledPlay lPlayOne) {
        Play play = createFreeThrowPlay(matchup, lPlayOne);
        Player playerOne = updateFreeThrowShooterStats(matchup, play, lPlayOne);

        if (lPlayOne.getTypeText().contains("free throw technical")) {
            updateTechnicalFreeThrow(play);
        } else if (lPlayOne.isFlagrant()) {
            updateFlagrantFreeThrow(play, lPlayOne);
        } else {
            updateStandardFreeThrow(play, lPlayOne);
        }
        finalizeFreeThrowPlay(matchup, play);
    }

    /**
     * Creates a new Play object for a free throw.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @return The created Play object.
     */
    private Play createFreeThrowPlay(Matchup matchup,  LabeledPlay lPlayOne) {
        return new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne),lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
    }

    /**
     * Updates the statistics for the free throw shooter.
     * @param matchup The matchup object containing labeled plays.
     * @param play The play object to update.
     * @param lPlayOne The labeled play.
     * @return The player who took the free throw.
     */
    private Player updateFreeThrowShooterStats(Matchup matchup, Play play, LabeledPlay lPlayOne) {
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        if (playerOne != null && playerOne.getId() != 0) {
            play.setFreeThrowShooter(playerOne);
            play.setMadeFreeThrow(lPlayOne.isShotMade());
            if (lPlayOne.isShotMade()) {
                playerOne.addPoints(1);
            }
            play.setPlayerShooting(playerOne);
        }
        if (matchup.getPlayByPlays().getLast().getFoulCommitter() != null) {
            matchup.getPlayByPlays().getLast().setPlayerFouled(playerOne);
        }
        return playerOne;
    }

    /**
     * Updates the play for a technical free throw.
     * @param play The play object to update.
     */
    private void updateTechnicalFreeThrow(Play play) {
        play.setPlayType(PlayTypes.FREE_THROW_TECHNICAL);
        play.setFreeThrowTotal(1);
        play.setFreeThrowNumber(1);
        play.setWasTechnical(true);
    }

    /**
     * Updates the play for a flagrant free throw.
     * @param play The play object to update.
     * @param lPlayOne The labeled play.
     */
    private void updateFlagrantFreeThrow(Play play, LabeledPlay lPlayOne) {
        play.setPlayType(PlayTypes.FREE_THROW_FLAGRANT);
        updateStandardFreeThrow(play, lPlayOne);
    }

    /**
     * Updates the play for a team free throw.
     * @param play The play object to update.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void updateTeamFreeThrow(Play play, LabeledPlay lPlayOne, LabeledPlay lPlayTwo) {
        if (lPlayTwo.isDefensive()) {
            play.setWasDefensive(true);
        } else {
            play.setWasOffensive(true);
        }
        play.setWasTeam(true);
        updateStandardFreeThrow(play, lPlayOne);
        play.setMakeUpOfPlay(List.of(lPlayOne));
    }

    /**
     * Updates the play for a rebound free throw.
     * @param matchup The matchup object containing labeled plays.
     * @param play The play object to update.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void updateReboundFreeThrow(Matchup matchup, Play play, LabeledPlay lPlayOne, LabeledPlay lPlayTwo) {
        Player playerTwo = matchup.findPlayerObject(lPlayTwo.getMultUsePlayer());
        if (playerTwo != null && playerTwo.getId() != 0) {
            playerTwo.addRebounds(1);
            play.setRebounder(playerTwo);
            play.setWasTeam(false);
        } else {
            play.setWasTeam(true);
        }
        if (lPlayTwo.isDefensive()) {
            play.setPlayType(PlayTypes.FREE_THROW_DEFENSIVE_REBOUND);
            play.setWasDefensive(true);
    } else if (lPlayTwo.isOffensive()) {
            play.setPlayType(PlayTypes.FREE_THROW_OFFENSIVE_REBOUND);
            play.setWasOffensive(true);
        }
        updateStandardFreeThrow(play, lPlayOne);
        play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
    }

    /**
     * Updates the play for a standard free throw.
     * @param play The play object to update.
     * @param lPlayOne The labeled play.
     */
    private void updateStandardFreeThrow(Play play, LabeledPlay lPlayOne) {
        play.setFreeThrowNumber(lPlayOne.getFreeThrowNum());
        play.setFreeThrowTotal(lPlayOne.getFreeThrowOutOf());
    }

    /**
     * Finalizes the free throw play by adding minutes to players and adding the play to the matchup.
     * @param matchup The matchup object containing labeled plays.
     * @param play The play object to finalize.
     */
    private void finalizeFreeThrowPlay(Matchup matchup, Play play) {
        matchup.getPlayByPlays().add(play);
    }


}
