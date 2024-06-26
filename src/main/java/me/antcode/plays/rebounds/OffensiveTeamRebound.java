package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class used if team makes an offensive rebound. Checks for team by using the "text" column and substrings it.
 */
public class OffensiveTeamRebound extends Rebound{

    private final String team;
    public OffensiveTeamRebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
       int index = record.get("text").indexOf(" offensive team rebound");
       if (index != -1){
           team = record.get("text").substring(0, index);
    } else {
      team = record.get("text");
       }
    }

    public String getTeam() {
        return team;
    }

    @Override
    public String toString(){
        return team + " had a rebound";
    }
}
