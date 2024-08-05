package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class ReboundManager extends Manager {

    /**
     * Creates a rebound play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createReboundPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        if (matchup.getPlayByPlays().getLast().getPlayType() != PlayTypes.OFFENSIVE_REBOUND && matchup.getPlayByPlays().getLast().getPlayType() != PlayTypes.DEFENSIVE_REBOUND){
            Play play = new Play(matchup, PlayTypes.REBOUND, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
            Player playerOne = null;
            if (lPlayOne.getAthlete_id_1() != 0){
                playerOne = matchup.findPlayerObject(lPlayOne.getAthlete_id_1());
            }
            play.setPlayType(lPlayOne.isOffensive() ? PlayTypes.OFFENSIVE_REBOUND : PlayTypes.DEFENSIVE_REBOUND);
            play.setWasOffensive(lPlayOne.isOffensive());
            play.setWasDefensive(!lPlayOne.isOffensive());
            if (lPlayOne.getAction() == Actions.TEAM){
                play.setWasTeam(true);
            } else if (lPlayOne.getAction() == Actions.REBOUND){
                if (playerOne != null && playerOne.getId() != 0){
                    playerOne.addRebounds(1);
                    play.setRebounder(playerOne);
                    play.setWasTeam(false);
                }
            }
            for (Play play1 : matchup.getPlayByPlays()) {
                if (!isDuplicatedOrUpgraded(play, play1)) continue;
                if (play1.getRebounder() != null) {
                    play1.getRebounder().setRebounds(play1.getRebounder().getRebounds() - 1);
                }
                matchup.getPlayByPlays().remove(play1);
                matchup.getPlayByPlays().add(play);
                return;
            }

            addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
            matchup.getPlayByPlays().add(play);
        }
    }

    private boolean isDuplicatedOrUpgraded(Play play, Play playInLoop){
        if (!playInLoop.getPlayType().toString().contains("REBOUND")) return false;
        if (playInLoop.getMakeUpOfPlay().getLast().getAction() != play.getMakeUpOfPlay().getFirst().getAction()) return false;
        if (playInLoop.getTimeLeftInQuarter() != play.getTimeLeftInQuarter()) return false;
        return playInLoop.getQuarter() == play.getQuarter();
    }
}
