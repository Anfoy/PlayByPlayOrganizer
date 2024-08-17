package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.datacollection.CSVUtils;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class TimeoutManager extends Manager {

    /**
     * Creates a timeout play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    public void createTimeoutPlays(Matchup matchup, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.TIMEOUT, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        matchup.getPlayByPlays().add(play);
    }
}
