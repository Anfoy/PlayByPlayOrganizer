package me.antcode.plays.freethrows;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;

/**
 * Free Throw class to be used whenever a free throw is attempted
 */
public class FreeThrow extends Play {

    private final Player freeThrowShooter;
    private final int freeThrowNumber;
    private final boolean makeOrMiss;

    public FreeThrow(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        freeThrowShooter = findPlayer("athlete_id_1");
        String type = record.get("type_text");
        freeThrowNumber = Integer.parseInt(String.valueOf(type.replaceAll("[^0-9]", "").charAt(0)));

        makeOrMiss = (CSVDataGather.parseInt(record.get("shot_made")) > 0);
    }


    public Player getFreeThrowShooter() {
        return freeThrowShooter;
    }

    public int getFreeThrowNumber() {
        return freeThrowNumber;
    }


    public boolean isMadeBasket() {
        return makeOrMiss;
    }

    @Override
    public String toString(){
        return "Free Throw " + getFreeThrowNumber() + " | " + makeOrMiss;
    }
}
