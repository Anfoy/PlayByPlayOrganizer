package me.antcode.plays;

import me.antcode.Matchup;
import me.antcode.Player;
import org.apache.commons.csv.CSVRecord;

/**
 * Class should be used if the play was a jump Ball. jump ball players are found in columns "athlete_id_1",
 * "athlete_id_2",
 * "athlete_id_3".
 */
public class JumpBall extends Play{


    private final Player jumperOne;

    private final Player jumperTwo;

    private final Player ballReceiver;


    public JumpBall(Matchup matchup, CSVRecord record) {
        super(matchup, record);
        jumperOne = findPlayer("athlete_id_1");
        jumperTwo = findPlayer("athlete_id_2");
        ballReceiver = findPlayer("athlete_id_3");
    }

    public Player getJumperOne() {
        return jumperOne;
    }

    public Player getJumperTwo() {
        return jumperTwo;
    }

    public Player getBallReceiver() {
        return ballReceiver;
    }

    @Override
    public String toString(){
        return "this is a jump ball";
    }
}
