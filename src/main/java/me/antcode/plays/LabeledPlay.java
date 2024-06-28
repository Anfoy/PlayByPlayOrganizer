package me.antcode.plays;


import me.antcode.TypesOfAction.Actions;

/**
 * LABELED PLAY OBJECT
 * @param action Action type: SHOT, REBOUND, TURNOVER, FOUL, FREE THROW
 * @param season Season of individual play
 * @param gameID GameID of the individual play
 * @param gameDate Date of Play
 * @param gamePlayNumber Play Number in game
 * @param typeText What type of play was it
 * @param text Description of the play
 * @param quarter Quarter individual play took place in
 * @param time Time left in quarter after individual play finished
 * @param athleteOneName First athlete in data. Using main one doing action
 * @param athleteTwoName Second athlete in data. Using the assister or stealer
 * @param athleteThreeName Third athlete in data. Usually jump ball receiver
 * @param athleteOneID Athlete one ID.
 * @param athleteTwoID Athlete two ID.
 * @param athleteThreeID Athlete three ID.
 * @param awayScore Away score after that individual play
 * @param homeScore Home score after that individual play.
 * @param shootingPlay Is the play a shooting play; IS EITHER TRUE OR FALSE
 * @param shotMade Whether shot was made
 * @param distance Distance of shot
 * @param shootingFoulCommitted Was a shooting foul committed.
 * @param offensiveRebound Was an offensive rebound committed.
 * @param defensiveRebound Was a defensive rebound committed.
 * @param personalFoul Was a personal foul committed.
 * @param freeThrow Was a free throw committed.
 * @param athleteTwoDrawsShootingFoul Did Athlete two draw a shooting foul.
 * @param athleteTwoStoleBall Did athlete two steal the ball
 * @param athleteTwoBlocksBall Did Athlete two block the ball
 * @param typeOfGame Type of Game(EX: FINAL, SEMI)
 * @param homeName Home name
 * @param awayName Away name
 */
public record LabeledPlay(Actions action, int season, int gameID, String gameDate, int gamePlayNumber, String typeText, String text, int quarter,
                          double time, String athleteOneName, String athleteTwoName,
                          String athleteThreeName, int athleteOneID, int athleteTwoID, int athleteThreeID,
                          int awayScore, int homeScore, String shootingPlay, boolean shotMade, int distance, boolean shootingFoulCommitted,
                          boolean offensiveRebound, boolean defensiveRebound, boolean personalFoul, boolean freeThrow, boolean athleteTwoDrawsShootingFoul,
                          boolean athleteTwoStoleBall, boolean athleteTwoBlocksBall, String typeOfGame, String homeName, String awayName) {
    }
