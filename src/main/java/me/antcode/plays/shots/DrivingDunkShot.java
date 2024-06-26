package me.antcode.plays.shots;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class should be used if a Driving Dunk Shot was attempted.
 */
public class DrivingDunkShot extends Shot{


    public DrivingDunkShot(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }

    @Override
    public String toString(){
        return "This is a Driving Dunk Shot";
    }

}
