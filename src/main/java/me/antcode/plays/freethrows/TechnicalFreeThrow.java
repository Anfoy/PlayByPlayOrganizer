package me.antcode.plays.freethrows;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;

/**
 * This class should be used whenever a technical Free throw is attempted.
 */
public class TechnicalFreeThrow  extends Play {
    private final Player freeThrowShooter;
    private final boolean makeOrMiss;

    public TechnicalFreeThrow(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        freeThrowShooter = findPlayer("athlete_id_1");
        String type = record.get("type_text");
        makeOrMiss = (CSVDataGather.parseInt(record.get("shot_made")) > 0);
    }

    public Player getFreeThrowShooter() {
        return freeThrowShooter;
    }

    public boolean isMadeBasket() {
        return makeOrMiss;
    }

    @Override
    public String toString(){
        return "Technical freeThrow attempted by: " + freeThrowShooter.getName() + " | " + makeOrMiss;
    }
}
