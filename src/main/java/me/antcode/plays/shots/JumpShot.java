package me.antcode.plays.shots;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class should be used if Jump Shot was attempted
 */
public class JumpShot extends Shot {


    public JumpShot(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }

    @Override
    public String toString(){
        return "This is a jump shot";
    }
}
