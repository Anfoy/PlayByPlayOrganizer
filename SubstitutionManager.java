package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class SubstitutionManager extends Manager {

    /**
     * Creates a substitution play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    public void createSubstitutionPlays(
            Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        try {
            for (int i = 0; i < homeOnCourt.size(); i++) {
                if (homeOnCourt.get(i).getId() == lPlayOne.getAthlete_id_2()) {
                    if (matchup.findPlayerObject(lPlayOne.getAthlete_id_1()) != null) {
                        homeOnCourt.set(i, matchup.findPlayerObject(lPlayOne.getAthlete_id_1()));
                        break;
                    }
                }
            }
            for (int i = 0; i < awayOnCourt.size(); i++) {
                if (awayOnCourt.get(i).getId() == lPlayOne.getAthlete_id_2()) {
                    if (matchup.findPlayerObject(lPlayOne.getAthlete_id_1()) != null) {
                        awayOnCourt.set(i, matchup.findPlayerObject(lPlayOne.getAthlete_id_1()));
                        break;
                    }
                }
            }
            Play play =
                    new Play(
                            matchup,
                            PlayTypes.SUBSTITUTION,
                            List.of(lPlayOne),
                            homeOnCourt,
                            awayOnCourt,
                            lPlayOne.getHomeScore(),
                            lPlayOne.getAwayScore());
            addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
            matchup.getPlayByPlays().add(play);
        } catch (NullPointerException e) {
            System.out.println(
                    lPlayOne.getGameID()
                            + " | "
                            + lPlayOne.getGamePlayNumber()
                            + " | "
                            + lPlayOne.getTypeText()
                            + " | "
                            + lPlayOne.getText()
                            + " | "
                            + lPlayOne.getAction());
        }
    }
}
