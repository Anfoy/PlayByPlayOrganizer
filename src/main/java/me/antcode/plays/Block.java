package me.antcode.plays;

import me.antcode.Player;
import me.antcode.plays.shots.Shot;

/**
 * Class should be used if there was a block on the play. Players executing block are found in "athlete_id_2" column
 */
public class Block extends Play {

    private final Shot shot;

    private Player whoWasBlocked;

    private Player whoPreformedBlock;

    public Block(Shot shot) {
    super(shot.getMatchup(), shot.getRecord());
        this.shot = shot;
        whoWasBlocked = shot.getShooter();
        whoPreformedBlock = findPlayer("athlete_id_2");
    }


    public Player getWhoWasBlocked() {
        return whoWasBlocked;
    }

    public Player getWhoPreformedBlock() {
        return whoPreformedBlock;
    }

    public Shot getShot() {
        return shot;
    }
}
