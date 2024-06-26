package me.antcode.plays.shots;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;

/**
 * Generic class for a shot. Should be used as parent class for all types of shots except Blocked, or Assisted
 * Shooting player is found in column "athlete_id_1". Class also gets the distance and whether the shot was successful.
 */
public class Shot extends Play {

    private final Player shooter;

    private final int distance;

    private final boolean madeBasket;

    public Shot(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        this.distance = CSVDataGather.parseInt(record.get("distance"));
        shooter = findPlayer("athlete_id_1");
        madeBasket = (CSVDataGather.parseInt(record.get("shot_made")) > 0);
    }


    public boolean isMadeBasket() {
        return madeBasket;
    }

    public Player getShooter() {
        return shooter;
    }

    public int getDistance() {
        return distance;
    }

    public String toString(){
        return "This is a regular SHOT";
    }

}
