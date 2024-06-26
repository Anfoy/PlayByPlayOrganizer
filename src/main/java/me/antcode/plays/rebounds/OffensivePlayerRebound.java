package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * class used of player makes an offensive rebound
 */
public class OffensivePlayerRebound extends PlayerRebound{



    public OffensivePlayerRebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }
}
