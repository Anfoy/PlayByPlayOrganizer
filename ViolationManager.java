package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class ViolationManager  extends Manager {


    /**
     * Creates a violation play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createViolationPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.VIOLATION, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = null;
        if (lPlayOne.getAthlete_id_1() != 0){
            playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
        }
        if (playerOne != null && playerOne.getId() != 0){
            play.setWhoViolated(playerOne);
        }
        if (lPlayOne.getTypeText().contains("goaltending")){
            play.setDefensiveGoalTending(true);
        } else if (lPlayOne.getTypeText().contains("kicked ball")){
            play.setKickedBall(true);
        } else if (lPlayOne.getTypeText().contains(" double lane")){
            play.setWasDoubleLane(true);
        }else if (lPlayOne.getTypeText().contains("delay of game")){
            play.setWasDelayOfGame(true);
        }else if (lPlayOne.getTypeText().contains("lane")){
            play.setWasLane(true);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }
}
