package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class FreeThrowManager extends Manager {


//    /**
//     * Handles the processing of a free throw play.
//     * @param index The current index in the labeled plays list.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The first labeled play.
//     * @return The updated index after processing the free throw play.
//     */
//    public int handleFreeThrow(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
//        LabeledPlay lPlayTwo;
//        try {
//            lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
//            if (lPlayTwo.action() != Actions.TEAM && lPlayTwo.action() != Actions.REBOUND) {
//                createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
//            } else {
//                createFreeThrowPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
//                index++;
//            }
//        } catch ( IndexOutOfBoundsException e) {
//            createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
//        }
//        return index;
//    }
//
//
//    /**
//     * Creates a free throw play with two labeled plays.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The first labeled play.
//     * @param lPlayTwo The second labeled play.
//     */
//    private void createFreeThrowPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
//        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
//        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
//        if (playerOne != null){
//            play.setFreeThrowShooter(playerOne);
//            play.setMadeFreeThrow(lPlayOne.shotMade());
//            if (lPlayOne.shotMade()){
//                playerOne.addPoints(1);
//            }
//            if (matchup.getPlayByPlays().getLast().getFoulCommitter() != null){
//                matchup.getPlayByPlays().getLast().setPlayerFouled(playerOne);
//            }
//            play.setPlayerShooting(playerOne);
//        }
//        if (lPlayOne.typeText().contains("technical")){
//            play.setPlayType(PlayTypes.FREE_THROW_TECHNICAL);
//            play.setFreeThrowTotal(1);
//            play.setFreeThrowNumber(1);
//            play.setWasTechnical(true);
//        } else if (lPlayOne.flagrant()){
//            play.setPlayType(PlayTypes.FREE_THROW_FLAGRANT);
//            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[4]));
//            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[6]));
//        } else if (lPlayTwo.action() == Actions.TEAM){
//            if (lPlayTwo.defensive()){
//                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//                play.setWasDefensive(true);
//                play.setWasTeam(true);
//            } else {
//                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//                play.setWasOffensive(true);
//                play.setWasTeam(true);
//            }
//            play.setMakeUpOfPlay(List.of(lPlayOne));
//        } else if (lPlayTwo.action() == Actions.REBOUND){
//
//            Player playerTwo = matchup.findPlayerObject(lPlayTwo.athlete_id_1());
//            if (playerTwo != null){
//                playerTwo.addRebounds(1);
//                play.setRebounder(playerTwo);
//                play.setWasTeam(false);
//            }else{
//                play.setWasTeam(true);
//            }
//            if (lPlayTwo.defensive()) {
//                play.setPlayType(PlayTypes.FREE_THROW_DEFENSIVE_REBOUND);
//                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//                play.setWasDefensive(true);
//            } else if (lPlayTwo.offensive()){
//                play.setPlayType(PlayTypes.FREE_THROW_OFFENSIVE_REBOUND);
//                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//                play.setWasOffensive(true);
//            }
//            play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
//        } else {
//            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//        }
//        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
//        matchup.getPlayByPlays().add(play);
//    }
//    /**
//     * Creates a single free throw play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The labeled play.
//     */
//    private void createSingleFTPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
//        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
//        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
//        try{
//            if (playerOne != null){
//                play.setMadeFreeThrow(lPlayOne.shotMade());
//                if (lPlayOne.shotMade()){
//                    playerOne.addPoints(1);
//                }
//                play.setPlayerShooting(playerOne);
//            }
//            if (matchup.getPlayByPlays().getLast().getFoulCommitter() != null){
//                matchup.getPlayByPlays().getLast().setPlayerFouled(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//            if (lPlayOne.typeText().contains("technical")){
//                play.setPlayType(PlayTypes.FREE_THROW_TECHNICAL);
//                play.setFreeThrowTotal(1);
//                play.setFreeThrowNumber(1);
//                play.setWasTechnical(true);
//            } else if (lPlayOne.flagrant()){
//                play.setPlayType(PlayTypes.FREE_THROW_FLAGRANT);
//                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[4]));
//                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[6]));
//            } else {
//                int[] freeThrowNumInfo =  extractNumbers(lPlayOne.text());
//                if (freeThrowNumInfo == null){
//                    play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
//                    play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
//                }else{
//                    play.setFreeThrowNumber(freeThrowNumInfo[0]);
//                    play.setFreeThrowTotal(freeThrowNumInfo[1]);
//                }
//            }
//            addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
//            matchup.getPlayByPlays().add(play);
//        }catch (ArrayIndexOutOfBoundsException e){
//            System.out.println(lPlayOne.gameID() + " | " + lPlayOne.gamePlayNumber() + " | " + lPlayOne.typeText() + " | " + lPlayOne.text() + " | " + lPlayOne.action());
//        }
//    }

    /**
     * Handles the processing of a free throw play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the free throw play.
     */
    public int handleFreeThrow(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        try {
            LabeledPlay lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (lPlayTwo.getAction() != Actions.TEAM && lPlayTwo.getAction() != Actions.REBOUND) {
                createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            } else {
                createFreeThrowPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        }
        return index;
    }

    /**
     * Creates a free throw play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createFreeThrowPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo) {
        Play play = createFreeThrowPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        Player playerOne = updateFreeThrowShooterStats(matchup, play, lPlayOne);

        if (lPlayOne.getTypeText().contains("technical")) {
            updateTechnicalFreeThrow(play);
        } else if (lPlayOne.isFlagrant()) {
            updateFlagrantFreeThrow(play, lPlayOne);
        } else if (lPlayTwo.getAction() == Actions.TEAM) {
            updateTeamFreeThrow(play, lPlayOne, lPlayTwo);
        } else if (lPlayTwo.getAction() == Actions.REBOUND) {
            updateReboundFreeThrow(matchup, play, lPlayOne, lPlayTwo);
        } else {
            updateStandardFreeThrow(play, lPlayOne);
        }
        finalizeFreeThrowPlay(matchup, play, homeOnCourt, awayOnCourt);
    }

    /**
     * Creates a single free throw play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleFTPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = createFreeThrowPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        Player playerOne = updateFreeThrowShooterStats(matchup, play, lPlayOne);

        if (lPlayOne.getTypeText().contains("technical")) {
            updateTechnicalFreeThrow(play);
        } else if (lPlayOne.isFlagrant()) {
            updateFlagrantFreeThrow(play, lPlayOne);
        } else {
            updateStandardFreeThrow(play, lPlayOne);
        }
        finalizeFreeThrowPlay(matchup, play, homeOnCourt, awayOnCourt);
    }

    /**
     * Creates a new Play object for a free throw.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The created Play object.
     */
    private Play createFreeThrowPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        return new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
    }

    /**
     * Updates the statistics for the free throw shooter.
     * @param matchup The matchup object containing labeled plays.
     * @param play The play object to update.
     * @param lPlayOne The labeled play.
     * @return The player who took the free throw.
     */
    private Player updateFreeThrowShooterStats(Matchup matchup, Play play, LabeledPlay lPlayOne) {
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
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
        play.setFreeThrowNumber(parseInt(lPlayOne.getTypeText().split(" ")[4]));
        play.setFreeThrowTotal(parseInt(lPlayOne.getTypeText().split(" ")[6]));
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
        play.setFreeThrowNumber(parseInt(lPlayOne.getTypeText().split(" ")[3]));
        play.setFreeThrowTotal(parseInt(lPlayOne.getTypeText().split(" ")[5]));
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
        Player playerTwo = null;
        if (lPlayTwo.getAthlete_id_1() != 0){
            playerTwo = matchup.findPlayerObject(lPlayTwo.getAthlete_id_1());
        }
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
        play.setFreeThrowNumber(parseInt(lPlayOne.getTypeText().split(" ")[3]));
        play.setFreeThrowTotal(parseInt(lPlayOne.getTypeText().split(" ")[5]));
        play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
    }

    /**
     * Updates the play for a standard free throw.
     * @param play The play object to update.
     * @param lPlayOne The labeled play.
     */
    private void updateStandardFreeThrow(Play play, LabeledPlay lPlayOne) {
        int[] freeThrowNumInfo = extractNumbers(lPlayOne.getText());
        if (freeThrowNumInfo == null) {
            play.setFreeThrowNumber(parseInt(lPlayOne.getTypeText().split(" ")[3]));
            play.setFreeThrowTotal(parseInt(lPlayOne.getTypeText().split(" ")[5]));
        } else {
            play.setFreeThrowNumber(freeThrowNumInfo[0]);
            play.setFreeThrowTotal(freeThrowNumInfo[1]);
        }
    }

    /**
     * Finalizes the free throw play by adding minutes to players and adding the play to the matchup.
     * @param matchup The matchup object containing labeled plays.
     * @param play The play object to finalize.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     */
    private void finalizeFreeThrowPlay(Matchup matchup, Play play, List<Player> homeOnCourt, List<Player> awayOnCourt) {
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }


}
