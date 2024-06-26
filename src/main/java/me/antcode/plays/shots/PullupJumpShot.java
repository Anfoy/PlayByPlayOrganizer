package me.antcode.plays.shots;

import me.antcode.Matchup;
import org.apache.commons.csv.CSVRecord;

/**
 * Class should be used if Pull Up Jump Shot was attempted.
 */
public class PullupJumpShot extends JumpShot {


    public PullupJumpShot(Matchup matchup, CSVRecord record) {
        super(matchup, record);
    }

    @Override
    public String toString(){
        return "This is a Pull Up Jump Shot";
    }
}
