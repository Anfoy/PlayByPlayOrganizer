package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;

/**
 * Parent class of all rebounds
 */
public class Rebound extends Play {


    public Rebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }

    @Override
    public String toString(){
        return "this is a rebound";
    }
}
