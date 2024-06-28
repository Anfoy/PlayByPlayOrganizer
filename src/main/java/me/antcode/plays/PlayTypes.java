package me.antcode.plays;

/**
 * All different types of plays combined
 */
public enum PlayTypes {

    BLOCK_AND_POSSESSION_CHANGE(2),
    BLOCK_AND_NO_POSSESSION_CHANGE(2),
    MADE_SHOT(1),
    MADE_SHOT_FOUL(2),
    ASSIST_MADE_SHOT(1),
    ASSIST_MADE_SHOT_FOUL(2),
    MISSED_SHOT(1),
    MISSED_SHOT_FOUL(2),
    MISSED_SHOT_OFFENSIVE_REBOUND(2),
    MISSED_SHOT_OFFENSIVE_REBOUND_FOUL(3),
    MISSED_SHOT_DEFENSIVE_REBOUND(2),
    MISSED_SHOT_DEFENSIVE_REBOUND_FOUL(3),
    FOUL_FREE_THROW_MAKE(2),
    FOUL_FREE_THROW_MISS(2),
    FOUL_FREE_THROW_MAKE_FREE_THROW_MISS(3),
    FOUL_FREE_THROW_MAKE_FREE_THROW_MAKE(3),
    FOUL_FREE_THROW_MISS_FREE_THROW_MISS(3),
    FREE_THROW(1),
    SUBSTITUTION(1),
    TURNOVER(1),
    TURNOVER_FOUL(2),
    JUMPBALL(1),
    UNIDENTIFIEDPLAYTYPE(0);

    private final int partsRequired;

    PlayTypes(int partsRequired) {
        this.partsRequired = partsRequired;
    }

    public int getPartsRequired() {
        return partsRequired;
    }
}
