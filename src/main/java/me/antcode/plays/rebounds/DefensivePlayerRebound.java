package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class used if a player makes a defensive rebound.
 */
public class DefensivePlayerRebound extends PlayerRebound {


    public DefensivePlayerRebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }
}
