package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class used if the team makes a defensive rebound. Checks for team by using the "text" column and substrings it.
 */
public class DefensiveTeamRebound extends Rebound{

    private final String team;
    public DefensiveTeamRebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        int index = record.get("text").indexOf(" defensive team rebound");
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
