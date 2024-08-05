package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class FoulManager extends Manager {


    public int handleFouls(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        LabeledPlay lPlayTwo;
        try{
            lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (lPlayTwo.getAction() == Actions.EJECTION) {
                if (lPlayOne.getTypeText().contains("double technical")){
                    LabeledPlay lPlayThree = matchup.getLabeledPlayList().get(index + 2);
                    if (lPlayThree.getAction() == Actions.EJECTION){
                        createEjectionPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo, lPlayThree);
                        index+= 2;
                    }
                }else{
                    createEjectionPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo, null);
                    index++;
                }
            }else{
                createFoulPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            }
        }catch (IndexOutOfBoundsException e){
            createFoulPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        }
        return index;
    }

    public void createSingleEjectionPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.EJECTION, List.of(lPlayOne), homeOnCourt ,awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (playerOne != null && playerOne.getId() != 0){
            play.getPlayersEjected().add(playerOne);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    private void createEjectionPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo, LabeledPlay lPlayThree){
        Play play = new Play(matchup, PlayTypes.SECOND_TECHNICAL_SINGLE_EJECTION, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayTwo.getHomeScore(), lPlayTwo.getAwayScore());
        Player playerOne = null;
        Player playerTwo = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (lPlayTwo.getAthlete_id_1() != 0){
            playerTwo = matchup.findPlayerObject(lPlayTwo.getAthlete_id_1());
        }
        if (playerOne != null && playerOne.getId() != 0){
            playerOne.addFouls(1);
            if (lPlayOne.getTypeText().contains("double technical")){
                play.setTechOnePlayer(playerOne);
                play.setTechTwoPlayer(playerTwo);
            }else{
                play.setFoulCommitter(playerOne);
            }
        }
        if (playerTwo != null && playerTwo.getId() != 0){
            play.getPlayersEjected().add(playerTwo);
        }
        if (lPlayThree != null){
            play.getPlayersEjected().add(matchup.findPlayerObject(lPlayThree.getAthlete_id_1()));
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a foul play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createFoulPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.FOUL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        String text = lPlayOne.getTypeText();
        if (playerOne != null && playerOne.getId() != 0){
            play.setFoulCommitter(playerOne);
            if (!text.contains("3-seconds") && !text.contains("double technical")) { //ADDS FOUL IF PLAY DOES NOT CONTAIN THESE WORDS
                playerOne.addFouls(1);
            }else if (text.contains("double technical")){
                play.setTechOnePlayer(playerOne);
                play.setTechTwoPlayer(matchup.findPlayerObject(lPlayOne.getAthlete_id_2()));
            }
        }

        if (lPlayOne.getAction() == Actions.FLAGRANT_FOUL){
            play.setPlayType(text.contains("type 1") ? PlayTypes.FLAGRANT_FOUL_TYPE_ONE : text.contains("type 2") ? PlayTypes.FLAGRANT_FOUL_TYPE_TWO : play.getPlayType());
        } else {
            if (lPlayOne.getText().contains("delay technical")){
                play.setPlayType(PlayTypes.DELAY_TECHNICAL);
            }else{
                play.setPlayType(determineFoulPlayType(text));
                for (Play play1 : matchup.getPlayByPlays()) {
                    if (!isDuplicatedOrUpgraded(play, play1)) continue;
                    if (play1.getFoulCommitter() != null){
                        play1.getFoulCommitter().setFouls(play1.getFoulCommitter().getFouls() - 1);
                        matchup.getPlayByPlays().remove(play1);
                    }
                    matchup.getPlayByPlays().add(play);
                    return;
                }
            }
            if (play.getPlayType() == PlayTypes.PERSONAL_FOUL){
                if (lPlayOne.getAthlete_id_2() != 0){
                    play.setPlayerFouled(matchup.findPlayerObject(lPlayOne.getAthlete_id_2()));
                }
            }
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    private boolean isDuplicatedOrUpgraded(Play play, Play playInLoop){
        if (!playInLoop.getPlayType().toString().contains("FOUL")) return false;
        if (playInLoop.getTimeLeftInQuarter() != play.getTimeLeftInQuarter()) return false;
        if (play.getFoulCommitter() != null && playInLoop.getFoulCommitter() != null){
            if (play.getFoulCommitter() != playInLoop.getFoulCommitter()) return false;
        }
        return playInLoop.getQuarter() == play.getQuarter();
    }



    /**
     * Determines the foul play type based on the text description.
     * @param text The text description of the play.
     * @return The determined PlayType.
     */
    private PlayTypes determineFoulPlayType(String text) {
        return switch (text) {
            case String t when t.contains("personal foul") -> PlayTypes.PERSONAL_FOUL;
//            case String t when t.contains("offensive foul") -> PlayTypes.OFFENSIVE_FOUL;
            case String t when t.contains("shooting foul") -> PlayTypes.SHOOTING_FOUL;
            case String t when t.contains("loose ball foul") -> PlayTypes.LOOSE_BALL_FOUL;
            case String t when t.contains("3-seconds") -> PlayTypes.DEFENSIVE_THREE_SECONDS_FOUL;
            case String t when t.contains("transition take") -> PlayTypes.TRANSITION_TAKE_FOUL;
            case String t when t.contains("personal take") -> PlayTypes.PERSONAL_TAKE_FOUL;
            case String t when t.contains("double technical") -> PlayTypes.DOUBLE_TECHNICAL_FOUL;
            case String t when t.contains("technical") -> PlayTypes.FIRST_TECHNICAL_FOUL;
//            case String t when t.contains("charge") -> PlayTypes.OFFENSIVE_CHARGE;
            case String t when t.contains("delay technical") -> PlayTypes.DELAY_TECHNICAL_FOUL;
            case String t when t.contains("too many players") -> PlayTypes.TOO_MANY_PLAYERS_TECHNICAL;
            case String t when t.contains("away from play") -> PlayTypes.AWAY_FROM_PLAY_FOUL;
            case String t when t.contains("clear path") -> PlayTypes.CLEAR_PATH_FOUL;
            default -> PlayTypes.FOUL;
        };
    }


//    /**
//     * Handles the processing of fouls.
//     * @param index The current index in the labeled plays list.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The first labeled play.
//     * @return The updated index after processing the fouls.
//     */
//    public int handleFouls(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
//        try {
//            LabeledPlay lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
//            if (lPlayTwo.getAction() == Actions.EJECTION) {
//                index = handleEjectionPlays(index, matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
//            } else {
//                createFoulPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
//            }
//        } catch (IndexOutOfBoundsException e) {
//            createFoulPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
//        }
//        return index;
//    }
//
//    /**
//     * Handles the processing of ejection plays.
//     * @param index The current index in the labeled plays list.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The first labeled play.
//     * @param lPlayTwo The second labeled play.
//     * @return The updated index after processing the ejection plays.
//     */
//    private int handleEjectionPlays(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo) {
//        if (lPlayOne.getTypeText().contains("double technical")) {
//            LabeledPlay lPlayThree = getNextLabeledPlay(matchup, index + 2);
//            if (lPlayThree != null && lPlayThree.getAction() == Actions.EJECTION) {
//                createEjectionPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo, lPlayThree);
//                index += 2;
//                return index;
//                }
//            }
//        createEjectionPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo, null);
//        index++;
//        return index;
//    }
//
//    /**
//     * Retrieves the next labeled play from the matchup list.
//     * @param matchup The matchup object containing labeled plays.
//     * @param index The index of the labeled play to retrieve.
//     * @return The labeled play at the specified index, or null if out of bounds.
//     */
//    private LabeledPlay getNextLabeledPlay(Matchup matchup, int index) {
//        try {
//            return matchup.getLabeledPlayList().get(index);
//        } catch (IndexOutOfBoundsException e) {
//            return null;
//        }
//    }
//
//    /**
//     * Creates a single ejection play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The labeled play.
//     */
//    public void createSingleEjectionPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
//        Play play = createPlay(matchup, PlayTypes.EJECTION, homeOnCourt, awayOnCourt, List.of(lPlayOne));
//        addEjectedPlayer(matchup, play, lPlayOne.getAthlete_id_1());
//        finalizePlay(matchup, play, homeOnCourt, awayOnCourt);
//    }
//
//    /**
//     * Creates an ejection play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The first labeled play.
//     * @param lPlayTwo The second labeled play.
//     * @param lPlayThree The third labeled play, can be null.
//     */
//    private void createEjectionPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo, LabeledPlay lPlayThree) {
//        Play play = createPlay(matchup, PlayTypes.SECOND_TECHNICAL_SINGLE_EJECTION, homeOnCourt, awayOnCourt, List.of(lPlayOne, lPlayTwo));
//        updateEjectionPlayDetails(matchup, play, lPlayOne, lPlayTwo, lPlayThree);
//        finalizePlay(matchup, play, homeOnCourt, awayOnCourt);
//    }
//
//    /**
//     * Updates the details of an ejection play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param play The play to update.
//     * @param lPlayOne The first labeled play.
//     * @param lPlayTwo The second labeled play.
//     * @param lPlayThree The third labeled play, can be null.
//     */
//    private void updateEjectionPlayDetails(Matchup matchup, Play play, LabeledPlay lPlayOne, LabeledPlay lPlayTwo, LabeledPlay lPlayThree) {
//        Player playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
//        Player playerTwo = matchup.findPlayerObject(lPlayTwo.getAthlete_id_1());
//        if (playerOne != null) {
//            playerOne.addFouls(1);
//            if (lPlayOne.getTypeText().contains("double technical")) {
//                play.setTechOnePlayer(playerOne);
//                play.setTechTwoPlayer(playerTwo);
//            } else {
//                play.setFoulCommitter(playerOne);
//            }
//        }
//        if (playerTwo != null) {
//            play.getPlayersEjected().add(playerTwo);
//        }
//        if (lPlayThree != null) {
//            play.getPlayersEjected().add(matchup.findPlayerObject(lPlayThree.getAthlete_id_1()));
//        }
//    }
//
//    /**
//     * Creates a foul play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The labeled play.
//     */
//    private void createFoulPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
//        Play play = createPlay(matchup, PlayTypes.FOUL, homeOnCourt, awayOnCourt, List.of(lPlayOne));
//        updateFoulPlayDetails(matchup, play, lPlayOne);
//        finalizePlay(matchup, play, homeOnCourt, awayOnCourt);
//    }
//
//    /**
//     * Updates the details of a foul play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param play The play to update.
//     * @param lPlayOne The labeled play.
//     */
//    private void updateFoulPlayDetails(Matchup matchup, Play play, LabeledPlay lPlayOne) {
//        Player playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
//        String text = lPlayOne.getTypeText();
//        if (playerOne != null) {
//            play.setFoulCommitter(playerOne);
//            if (!text.contains("3-seconds") && !text.contains("double technical")) {
//                playerOne.addFouls(1);
//            } else if (text.contains("double technical")) {
//                play.setTechOnePlayer(playerOne);
//                play.setTechTwoPlayer(matchup.findPlayerObject(lPlayOne.getAthlete_id_2()));
//            }
//        }
//
//        if (lPlayOne.getAction() == Actions.FLAGRANT_FOUL) {
//            play.setPlayType(text.contains("type 1") ? PlayTypes.FLAGRANT_FOUL_TYPE_ONE : text.contains("type 2") ? PlayTypes.FLAGRANT_FOUL_TYPE_TWO : play.getPlayType());
//        } else {
//            if (lPlayOne.getText().contains("delay technical")) {
//                play.setPlayType(PlayTypes.DELAY_TECHNICAL);
//            } else {
//                play.setPlayType(determineFoulPlayType(text));
//            }
//            if (play.getPlayType() == PlayTypes.PERSONAL_FOUL) {
//                if (lPlayOne.getAthlete_id_2() != 0) {
//                    play.setPlayerFouled(matchup.findPlayerObject(lPlayOne.getAthlete_id_2()));
//                }
//            }
//        }
//    }
//
//    /**
//     * Creates a new Play object.
//     * @param matchup The matchup object containing labeled plays.
//     * @param playType The type of play to create.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param labeledPlays The labeled plays involved in the play.
//     * @return The created Play object.
//     */
//    private Play createPlay(Matchup matchup, PlayTypes playType, List<Player> homeOnCourt, List<Player> awayOnCourt, List<LabeledPlay> labeledPlays) {
//        return new Play(matchup, playType, labeledPlays, homeOnCourt, awayOnCourt, labeledPlays.getFirst().getHomeScore(), labeledPlays.getFirst().getAwayScore());
//    }
//
//    /**
//     * Adds an ejected player to the play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param play The play to update.
//     * @param athleteId The ID of the ejected player.
//     */
//    private void addEjectedPlayer(Matchup matchup, Play play, int athleteId) {
//        Player player = matchup.findPlayerObject(athleteId);
//        if (player != null) {
//            play.getPlayersEjected().add(player);
//        }
//    }
//
//    /**
//     * Finalizes the play by adding minutes to players and adding the play to the matchup.
//     * @param matchup The matchup object containing labeled plays.
//     * @param play The play to finalize.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     */
//    private void finalizePlay(Matchup matchup, Play play, List<Player> homeOnCourt, List<Player> awayOnCourt) {
//        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
//        matchup.getPlayByPlays().add(play);
//    }
//
//    /**
//     * Determines the foul play type based on the text description.
//     * @param text The text description of the play.
//     * @return The determined PlayType.
//     */
//    private PlayTypes determineFoulPlayType(String text) {
//        return switch (text) {
//            case String t when t.contains("personal foul") -> PlayTypes.PERSONAL_FOUL;
//            case String t when t.contains("shooting foul") -> PlayTypes.SHOOTING_FOUL;
//            case String t when t.contains("loose ball foul") -> PlayTypes.LOOSE_BALL_FOUL;
//            case String t when t.contains("3-seconds") -> PlayTypes.DEFENSIVE_THREE_SECONDS_FOUL;
//            case String t when t.contains("transition take") -> PlayTypes.TRANSITION_TAKE_FOUL;
//            case String t when t.contains("personal take") -> PlayTypes.PERSONAL_TAKE_FOUL;
//            case String t when t.contains("double technical") -> PlayTypes.DOUBLE_TECHNICAL_FOUL;
//            case String t when t.contains("technical") -> PlayTypes.FIRST_TECHNICAL_FOUL;
//            case String t when t.contains("delay technical") -> PlayTypes.DELAY_TECHNICAL_FOUL;
//            case String t when t.contains("too many players") -> PlayTypes.TOO_MANY_PLAYERS_TECHNICAL;
//            case String t when t.contains("away from play") -> PlayTypes.AWAY_FROM_PLAY_FOUL;
//            case String t when t.contains("clear path") -> PlayTypes.CLEAR_PATH_FOUL;
//            default -> PlayTypes.FOUL;
//        };
//    }

}
