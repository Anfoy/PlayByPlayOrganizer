package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVUtils;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class AssistManager extends Manager {



    /**
     * Creates an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    public void createAssistPlays(Matchup matchup, LabeledPlay lPlayOne) {
        Play play = createPlay(matchup, lPlayOne);
        updatePlayerStatsForAssist(lPlayOne, play, matchup);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a new Play object for an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     * @return The created Play object.
     */
    private Play createPlay(Matchup matchup, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        play.setDistance(lPlayOne.getDistance());
        return play;
    }

    /**
     * Updates player statistics for an assist play.
     * @param lPlayOne The labeled play.
     * @param play The Play object to update.
     */
    private void updatePlayerStatsForAssist( LabeledPlay lPlayOne, Play play, Matchup matchup) {
        Player shooter = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        Player assister = matchup.findPlayerObject(lPlayOne.getAssistPlayer());
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
