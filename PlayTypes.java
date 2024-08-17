package me.antcode.plays;

/**
 * All different types of plays combined
 */
public enum PlayTypes {

    BLOCK_AND_POSSESSION_CHANGE,
    BLOCK_AND_NO_POSSESSION_CHANGE,
    BLOCK,
    MADE_SHOT,
    ASSIST_MADE_SHOT,
    MISSED_SHOT,
    MISSED_SHOT_OFFENSIVE_REBOUND,
    MISSED_SHOT_DEFENSIVE_REBOUND,
    FOUL,
    FLAGRANT_FOUL_TYPE_ONE,
    FLAGRANT_FOUL_TYPE_TWO,
    OFFENSIVE_REBOUND,
    DEFENSIVE_REBOUND,
    DOUBLE_TECHNICAL_FOUL,
    FIRST_TECHNICAL_FOUL,
    DELAY_TECHNICAL_FOUL,
    DELAY_TECHNICAL,
    SECOND_TECHNICAL_FOUL,
    EJECTION,
    PERSONAL_TAKE_FOUL,
    TRANSITION_TAKE_FOUL,
    TOO_MANY_PLAYERS_TECHNICAL,
    DEFENSIVE_THREE_SECONDS_FOUL,
    PERSONAL_FOUL,
    SHOOTING_FOUL,
    LOOSE_BALL_FOUL,
    //    OFFENSIVE_FOUL,
//    OFFENSIVE_CHARGE,
    AWAY_FROM_PLAY_FOUL, //Results in one free throw
    CLEAR_PATH_FOUL, //Results in two free throws
    REBOUND,
    FREE_THROW,
    FREE_THROW_TECHNICAL,
    FREE_THROW_DEFENSIVE_REBOUND,
    FREE_THROW_OFFENSIVE_REBOUND,
    SUBSTITUTION,
    TURNOVER,


    STEAL_TURNOVER,

    //    SHOT_CLOCK_TURNOVER,
//    DOUBLE_DRIBBLE_TURNOVER,
//    BAD_PASS_TURNOVER,
//    TRAVELING_TURNOVER,
//    LOST_BALL_TURNOVER,
//    OUT_OF_BOUNCE_TURNOVER,
//    OFFENSIVE_FOUL_TURNOVER,
//    BACK_COURT_TURNOVER,
//    THREE_SECOND_TURNOVER,
//    KICKED_BALL_TURNOVER,
//    GOAL_TENDING_TURNOVER,
//    EIGHT_SECOND_TURNOVER,
//    DISC_DRIBBLE_TURNOVER,
//    INBOUND_TURNOVER,
//    LANE_VIOLATION_TURNOVER,
//    ILLEGAL_ASSIST_TURNOVER,
//    PUNCHED_BALL_TURNOVER,
//    FIVE_SECOND_TURNOVER,
//    BASKET_FROM_BELOW_TURNOVER,
//    FIVE_SECOND_BACK_TO_BASKET_TURNOVER,
    JUMPBALL,
    VIOLATION,
    FREE_THROW_FLAGRANT,
    UNIDENTIFIEDPLAYTYPE,
    TIMEOUT

}
