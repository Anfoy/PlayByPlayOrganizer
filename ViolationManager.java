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
     * @param lPlayOne The labeled play.
     */
    public void createViolationPlays(Matchup matchup,  LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.VIOLATION, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        String typeText = lPlayOne.getTypeText();
        if (playerOne != null && playerOne.getId() != 0){
            play.setWhoViolated(playerOne);
        }
        if (typeText.contains("goaltending")){
            play.setDefensiveGoalTending(true);
        } else if (typeText.contains("kicked ball")){
            play.setKickedBall(true);
        } else if (typeText.contains(" double lane")){
            play.setWasDoubleLane(true);
        }else if (typeText.contains("delay of game")){
            play.setWasDelayOfGame(true);
        }else if (typeText.contains("lane")){
            play.setWasLane(true);
        }
        play.setWasOffensive(lPlayOne.isOffensive());
        play.setWasDefensive(lPlayOne.isOffensive());
        matchup.getPlayByPlays().add(play);
    }
}
