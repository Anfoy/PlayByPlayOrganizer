package me.antcode.TypesOfAction;

/**
 * Individual plays
 */
public enum Actions {

    LOST_BALL("Lost Ball Turnover"),
    BAD_PASS("bad pass"),
    OUT_OF_BOUNDS_BAD_PASS("Out of Bounds - Bad Pass Turnover"),
    OUT_OF_BOUNDS_LOST_BALL("Out of Bounds - Lost Ball Turnover"),
    OFFENSIVE_FOUL_TURNOVER("Offensive Foul Turnover"),
    DEFENSIVE_FOUL_TURNOVER("Defensive Foul Turnover"),
    DRIVING_DUNK_SHOT("Driving Dunk Shot"),
    JUMP_SHOT("Jump Shot"),
    PULL_UP_JUMP_SHOT("Pullup Jump Shot"),
    FADE_AWAY_JUMP_SHOT("Fade Away Jump Shot"),
    TIP_SHOT("Tip Shot"),
    RUNNING_DUNK_SHOT("Running Dunk Shot"),
    LAYUP_SHOT("Layup Shot"),
    DRIVING_FLOATING_JUMP_SHOT("Driving Floating Jump Shot"),
    REVERSE_LAYUP_SHOT("Reverse Layup Shot"),
    STEP_BACK_JUMP_SHOT("Step Back Jump Shot"),
    RUNNING_LAYUP_SHOT("Running Layup Shot"),
    DRIVING_LAYUP_SHOT("Driving Layup Shot"),
    TURNAROUND_FADE_AWAY_BANK_JUMP_SHOT("Turnaround Fadeaway Bank Jump Shot"),
    RUNNING_JUMP_SHOT("Running Jump Shot"),
    HOOK_SHOT("Hook Shot"),
    DRIVING_HOOK_SHOT("Driving Hook Shot"),
    CUTTING_LAYUP_SHOT("Cutting Layup Shot"),
    TURNAROUND_JUMP_SHOT("Turnaround Jump Shot"),
    DUNK_SHOT("Dunk Shot"),
    DRIVING_FINGER_ROLL_LAYUP("Driving Finger Roll Layup"),
    PUTBACK_DUNK_SHOT("Putback Dunk Shot"),
    DRIVING_FLOATING_BANK_JUMP_SHOT("Driving Floating Bank Jump Shot"),
    RUNNING_PULLUP_JUMP_SHOT("Running Pullup Jump Shot"),
    CUTTING_DUNK_SHOT("Cutting Dunk Shot"),
    ALLEY_OOP_DUNK_SHOT("Alley Oop Dunk Shot"),
    LAYUP_DRIVING_REVERSE("Layup Driving Reverse"),
    FLOATING_JUMP_SHOT("Floating Jump Shot"),
    LAYUP_SHOT_PUTBACK("Layup Shot Putback"),
    JUMP_BALL("Jumpball"),
    OFFENSIVE_REBOUND("Offensive Rebound"),
    DEFENSIVE_REBOUND("Defensive Rebound"),
    OFFENSIVE_TEAM_REBOUND("Offensive Team Rebound"),
    DEFENSIVE_TEAM_REBOUND("Defensive Team Rebound"),
    TRAVELING("Traveling"),
    SHOOTING_FOUL("Shooting Foul"),
    LOOSE_BALL_FOUL("Loose Ball Foul"),
    PERSONAL_FOUL("Personal Foul"),
    TECHNICAL_FOUL("Technical Foul"),
    DOUBLE_TECHNICAL_FOUL("Double Technical Foul"),
    OFFENSIVE_FOUL("Offensive Foul"),
    DEFENSIVE_FOUL("Defensive Foul"),
    DEFENSIVE_THREE_SECONDS_TECH("Defensive 3-Seconds Technical"),
    FLAGRANT_FOUL_TYPE_ONE("Flagrant Foul Type 1"),
    INTENTIONAL_FOUL("Intentional Foul"),
    DEFENSIVE_GOALTENDING("Defensive Goaltending"),
    OFFENSIVE_GOALTENDING("Offensive Goaltending"),
    TRANSITION_TAKE_FOUL("Transition Take Foul"),
    BLOCK("blocks"),
    BLOCK_OFFENSIVE_REBOUND("Block To Offensive Rebound"),
    BLOCK_DEFENSIVE_REBOUND("Block to Defensive Rebound"),
    SUBSTITUTION("Substitution"),
    FREE_THROW_ONE_OF_TWO("Free Throw - 1 of 2"),
    FREE_THROW_TWO_OF_TWO("Free Throw - 2 of 2"),
    FREE_THROW_ONE_OF_THREE("Free Throw - 1 of 3"),
    FREE_THROW_TWO_OF_THREE("Free Throw - 2 of 3"),
    FREE_THROW_THREE_OF_THREE("Free Throw - 3 of 3"),

    FREE_THROW_ONE_OF_ONE("Free Throw - 1 of 1"),

    UNIDENTIFIED("This action was not picked up");

    private final String description;

    Actions(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
