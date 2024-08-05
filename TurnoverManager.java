package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class TurnoverManager  extends Manager {


    /**
     * Creates a turnover play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createTurnoverPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.TURNOVER, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        Player playerTwo = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (lPlayOne.getAthlete_id_2() != 0) {
            playerTwo = matchup.findPlayerObject(lPlayOne.getAthlete_id_2());
        }
        if (playerOne != null && playerOne.getId() != 0) {
            if (!lPlayOne.getTypeText().contains("shot clock")) {
                playerOne.addTurnovers(1);
            }
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.getAthlete_id_1()));
        }
        if (lPlayOne.isWasSteal()){
            play.setPlayType(PlayTypes.STEAL_TURNOVER);
            if (playerTwo != null){
                play.setStealer(playerTwo);
                playerTwo.addSteals(1);
            }


        }
        for (Play play1 : matchup.getPlayByPlays()) {
            if (!isDuplicatedOrUpgraded(play, play1)) continue;
            if (play1.getTurnoverCommitter() != null) {
                play1.getTurnoverCommitter().setTurnovers(play1.getTurnoverCommitter().getTurnovers() - 1);
            }
            matchup.getPlayByPlays().remove(play1);
            matchup.getPlayByPlays().add(play);
            return;
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    private boolean isDuplicatedOrUpgraded(Play play, Play playInLoop){
        if (!playInLoop.getPlayType().toString().contains("TURNOVER")) return false;
        if (playInLoop.getTimeLeftInQuarter() != play.getTimeLeftInQuarter()) return false;
        return playInLoop.getQuarter() == play.getQuarter();
    }

    //        } else if (lPlayOne.typeText().contains("traveling") || lPlayOne.typeText().contains("palming")){
//            play.setPlayType(PlayTypes.TRAVELING_TURNOVER);
//        } else if (lPlayOne.typeText().contains("shot clock")){
//            play.setPlayType(PlayTypes.SHOT_CLOCK_TURNOVER);
//            play.setTurnoverSpecificPlayer(false);
//        } else if (lPlayOne.typeText().contains("out of bounds")){
//            play.setPlayType(PlayTypes.OUT_OF_BOUNCE_TURNOVER);
//        } else if (lPlayOne.typeText().contains("bad pass")){
//            play.setPlayType(PlayTypes.BAD_PASS_TURNOVER);
//        } else if (lPlayOne.typeText().contains("double dribble")){
//            play.setPlayType(PlayTypes.DOUBLE_DRIBBLE_TURNOVER);
//        } else if (lPlayOne.typeText().contains("foul turnover")){
//            play.setPlayType(PlayTypes.OFFENSIVE_FOUL_TURNOVER);
//        } else if (lPlayOne.typeText().contains("lost ball")){
//            play.setPlayType(PlayTypes.LOST_BALL_TURNOVER);
//        }
//        else if (lPlayOne.typeText().contains("back court")){
//            play.setPlayType(PlayTypes.BACK_COURT_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("3-second")){
//            play.setPlayType(PlayTypes.BACK_COURT_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("no turnover")){
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("kicked ball")){
//            play.setPlayType(PlayTypes.KICKED_BALL_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("8-second")){
//            play.setPlayType(PlayTypes.EIGHT_SECOND_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("goaltending")){
//            play.setPlayType(PlayTypes.GOAL_TENDING_TURNOVER);
//            if (lPlayOne.typeText().contains("offensive")){
//                play.setWasOffensive(true);
//            }else{
//                play.setWasDefensive(false);
//            }
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("disc dribble")){
//            play.setPlayType(PlayTypes.DISC_DRIBBLE_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("inbound turnover")){
//            play.setPlayType(PlayTypes.DOUBLE_DRIBBLE_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("lane violation turnover")){
//            play.setPlayType(PlayTypes.LANE_VIOLATION_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setWasLane(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("illegal assist turnover")){
//            play.setPlayType(PlayTypes.LANE_VIOLATION_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("5-second turnover")){
//            play.setPlayType(PlayTypes.FIVE_SECOND_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("punched ball turnover")){
//            play.setPlayType(PlayTypes.FIVE_SECOND_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
//        else if (lPlayOne.typeText().contains("inbound turnover")){
//            play.setPlayType(PlayTypes.DOUBLE_DRIBBLE_TURNOVER);
//            if (playerOne != null) {
//                playerOne.addTurnovers(1);
//                play.setTurnoverSpecificPlayer(true);
//                play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
//            }
//        }
}
