package me.antcode.plays;

import me.antcode.Player;
import me.antcode.plays.shots.Shot;

/**
 * Class should be used if there was an assist on the play. Assist players are found in athlete_id_2 column
 */
public class Assist extends Play {

    private final Shot shot;

    private final Player playerAssisted;

    private final Player playerAssisting;


    public Assist(Shot shot) {
        super(shot.getMatchup(), shot.getRecord());
        this.shot = shot;
        playerAssisted = shot.getShooter();
        playerAssisting = findPlayer("athlete_id_2");
    }

    public Shot getShot() {
        return shot;
    }

    public Player getPlayerAssisted() {
        return playerAssisted;
    }

    public Player getPlayerAssisting() {
        return playerAssisting;
    }
}
