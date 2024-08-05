package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class AssistManager extends Manager {

//    /**
//     * Creates an assist play.
//     * @param matchup The matchup object containing labeled plays.
//     * @param homeOnCourt The home team players on the court.
//     * @param awayOnCourt The away team players on the court.
//     * @param lPlayOne The labeled play.
//     */
//    public void createAssistPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
//        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
//        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
//        Player playerTwo = matchup.findPlayerObject(lPlayOne.athlete_id_2());
//        play.setDistance(lPlayOne.distance());
//
//        if (playerOne != null){
//            if (lPlayOne.isThreePointer()){
//                playerOne.addThreePointFieldGoalsMade(1);
//                playerOne.addThreePointFieldGoalsAttempted(1);
//                playerOne.addFieldGoalsAttempted(1);
//                playerOne.addFieldGoalsMade(1);
//                playerOne.addPoints(3);
//            } else {
//                playerOne.addFieldGoalsAttempted(1);
//                playerOne.addFieldGoalsMade(1);
//                playerOne.addPoints(2);
//            }
//        }
//        if (playerTwo != null){
//            playerTwo.addAssists(1);
//        }
//        play.setPlayerShooting(playerOne);
//        play.setPlayerAssisted(playerOne);
//        play.setPlayerAssisting(playerTwo);
//
//        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
//        matchup.getPlayByPlays().add(play);
//    }


    /**
     * Creates an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createAssistPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = createPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        updatePlayerStatsForAssist(matchup, lPlayOne, play);
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a new Play object for an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     * @return The created Play object.
     */
    private Play createPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        play.setDistance(lPlayOne.getDistance());
        return play;
    }

    /**
     * Updates player statistics for an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     * @param play The Play object to update.
     */
    private void updatePlayerStatsForAssist(Matchup matchup, LabeledPlay lPlayOne, Play play) {
        Player shooter = null;
        Player assister = null;
        if (lPlayOne.getAthlete_id_1() != 0) {
            shooter = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (lPlayOne.getAthlete_id_2() != 0) {
            assister = matchup.findPlayerObject(lPlayOne.getAthlete_id_2());
        }
        if (shooter != null && shooter.getId() != 0){
            updateShootingStats(shooter, lPlayOne);
            play.setPlayerShooting(shooter);
            play.setPlayerAssisted(shooter);
        }

        if (assister != null && assister.getId() != 0){
            assister.addAssists(1);
            play.setPlayerAssisting(assister);
        }

    }

    /**
     * Updates shooting statistics for a player.
     * @param shooter The player who made the shot.
     * @param lPlayOne The labeled play.
     */
    private void updateShootingStats(Player shooter, LabeledPlay lPlayOne) {
        if (lPlayOne.isThreePointer()) {
            shooter.addThreePointFieldGoalsMade(1);
            shooter.addThreePointFieldGoalsAttempted(1);
            shooter.addPoints(3);
        } else {
            shooter.addPoints(2);
        }
        shooter.addFieldGoalsMade(1);
        shooter.addFieldGoalsAttempted(1);
    }

}
