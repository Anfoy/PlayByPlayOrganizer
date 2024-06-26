package me.antcode.plays.rebounds;

import me.antcode.Matchup;
import me.antcode.Player;
import org.apache.commons.csv.CSVRecord;

/**
 * Generic hierachy class for a player rebound
 */
public class PlayerRebound extends Rebound{

    private final Player rebounder;

    public PlayerRebound(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        rebounder = findPlayer("athlete_id_1");
    }

    public Player getRebounder() {
        return rebounder;
    }

    @Override
    public String toString(){
        return rebounder.getName() + " had a rebound";
    }
}
