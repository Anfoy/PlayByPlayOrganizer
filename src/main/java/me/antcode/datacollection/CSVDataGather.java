package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to gather data from CSV file
 */
public class CSVDataGather {

    private final String matchupsCSVPath;
    private final String playByPlayCSVPath;

    public CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath){
        this.matchupsCSVPath = matchupsCSVPath;
        this.playByPlayCSVPath = playByPlayCSVPath;
    }

    /**
     * Extracts all data for matchups and creates a corresponding object.
     * @return a List of all matchups from the file.
     */
    public List<Matchup> extractAllMatchups() {
        List<Matchup> totalMatchups = new ArrayList<>();
        try (Reader reader = new FileReader(matchupsCSVPath);
             // Opens the parser and starts at the second row since the first row is the headers/column names
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            // Creates all the matchups
            for (CSVRecord csvRecord : csvParser) {
                Matchup matchup = createMatchupFromRecord(csvRecord);
                totalMatchups.add(matchup);
            }
        } catch (IOException e) {
            System.out.println("failed to read file.");
            e.printStackTrace();
        }
        return totalMatchups;
    }

    /**
     * Goes through each record for a specific matchup and labels it appropriately for future play design.
     * @param matchup Matchup to look for.
     */
    public void labelAllPlays(Matchup matchup){
        try (Reader reader = new FileReader(playByPlayCSVPath);
             // Opens the parser and starts at the second row since the first row is the headers/column names
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            // Labels all the plays
            for (CSVRecord csvRecord : csvParser) {
                if (getInt(csvRecord, "game_id") != matchup.getGameID()) continue;
                Actions actions;
                String type = csvRecord.get("type_text").toLowerCase();

                // Determines the type of action
                if (type.equals("substitution")){
                    actions = Actions.SUBSTITUTION;
                } else if (type.contains("timeout")){
                    actions = Actions.TIMEOUT;
                } else if (type.contains("jumpball")){
                    actions = Actions.JUMPBALL;
                } else if (type.contains("free throw")){
                    actions = Actions.FREE_THROW;
                } else if (csvRecord.get("wasTeam").equals("true")){
                    actions = Actions.TEAM;
                } else if (csvRecord.get("rebound").equals("true")){
                    actions = Actions.REBOUND;
                } else if (csvRecord.get("shooting_play").equals("TRUE")){
                    if (csvRecord.get("assist").equals("true")){
                        actions = Actions.ASSIST;
                    } else if (getInt(csvRecord, "block_athlete2") > 0){
                        actions = Actions.BLOCK;
                    } else {
                        actions = Actions.SHOT;
                    }
                } else if (csvRecord.get("foul").equals("true")){
                    if (csvRecord.get("flagrant").equals("true")){
                        actions = Actions.FLAGRANT_FOUL;
                    } else {
                        actions = Actions.FOUL;
                    }
                } else if (csvRecord.get("turnover").equals("true")){
                    actions = Actions.TURNOVER;
                } else if (csvRecord.get("violation").equals("true")){
                    actions = Actions.VIOLATION;
                } else {
                    actions = Actions.IGNORE;
                }

                // Extracts additional play details
                int gameID = getInt(csvRecord, "game_id");
                int quarter = getInt(csvRecord, "qtr");
                double timeInSeconds = convertToSeconds(csvRecord.get("time"));
                int awayScore = getInt(csvRecord, "away_score");
                int homeScore = getInt(csvRecord, "home_score");
                int athleteOneID = getAthleteID(csvRecord, 1);
                int athleteTwoID = getAthleteID(csvRecord, 2);
                int athleteThreeID = getAthleteID(csvRecord, 3);
                boolean madeShot = getInt(csvRecord, "shot_made") > 0;
                boolean wasSteal = getInt(csvRecord, "Steal_athlete2") > 0;
                int distance = getInt(csvRecord, "distance");
                int playNum = getInt(csvRecord, "game_play_number");
                boolean wasOffensive = csvRecord.get("offensive").equals("true");
                boolean wasDefensive = !wasOffensive;
                boolean isFlagrant = csvRecord.get("flagrant").equals("true");

                // Adds labeled play to matchup if it is not ignored
                if (actions != Actions.IGNORE) {
                    matchup.getLabeledPlayList().add(new LabeledPlay(actions, gameID, quarter, timeInSeconds, awayScore, homeScore,
                            athleteOneID, athleteTwoID, athleteThreeID, type, madeShot, wasSteal, distance, wasOffensive, wasDefensive, playNum, isFlagrant));
                }
            }
        } catch (IOException e) {
            System.out.println("failed to read file.");
            e.printStackTrace();
        }
    }

    /**
     * Creates a matchup from a specific row of the CSV.
     * @param csvRecord which row to look at
     * @return A matchup compiled of all data gathered from the row.
     */
    private Matchup createMatchupFromRecord(CSVRecord csvRecord) {
        // Grab generic information for a matchup
        int gameId = parseInt(csvRecord.get("game_id"));
        String gameType = csvRecord.get("type_abbreviation");
        String gameDate = csvRecord.get("game_date");
        String homeTeam = csvRecord.get("home_display_name");
        String awayTeam = csvRecord.get("away_display_name");

        // Get the list of the home and away starters & bench
        List<Player> homeStarters = extractPlayersFromMatchupData(csvRecord, "home_starter_", 5);
        List<Player> awayStarters = extractPlayersFromMatchupData(csvRecord, "away_starter_", 5);
        List<Player> homeBench = extractPlayersFromMatchupData(csvRecord, "home_bench_", 10);
        List<Player> awayBench = extractPlayersFromMatchupData(csvRecord, "away_bench_", 10);

        return new Matchup(gameDate, homeTeam, awayTeam, gameType, gameId, homeStarters, homeBench, awayStarters, awayBench, new ArrayList<>());
    }

    /**
     * Creates the list of players based on the row provided.
     * BENCH HAS UP TO 10 PLAYERS
     * STARTERS HAVE UP TO 5 PLAYERS
     * @param record Which row, or CSVRecord, to look at
     * @param prefix What column should it be looking for in that row. EX: "home_starter_"
     * @param count how many player columns should it look at
     * @return List of players grabbed from row.
     */
    private List<Player> extractPlayersFromMatchupData(CSVRecord record, String prefix, int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String idKey = prefix + i + "_id";
            String nameKey = prefix + i;
            if (rowHasValue(record, idKey) && rowHasValue(record, nameKey)) {
                int playerId = parseInt(record.get(idKey));
                String playerName = record.get(nameKey);
                players.add(new Player(playerId, playerName));
            }
        }
        return players;
    }

    /**
     * Processes plays for the given matchup starting from a specific index.
     * @param index the starting index in the labeled plays list
     * @param matchup the matchup object containing labeled plays
     */
    public void getPlays(int index, Matchup matchup){
        while (index < matchup.getLabeledPlayList().size()){
            List<Player> homeOnCourt;
            List<Player> awayOnCourt;
            if (matchup.getPlayByPlays().isEmpty()){
                homeOnCourt = matchup.getHomeStarters();
                awayOnCourt = matchup.getAwayStarters();
            } else {
                homeOnCourt = matchup.getPlayByPlays().getLast().getFiveOnCourtHome();
                awayOnCourt = matchup.getPlayByPlays().getLast().getFiveOnCourtAway();
            }
            LabeledPlay lPlayOne = matchup.getLabeledPlayList().get(index);

            // Processes the play based on its type
            if (lPlayOne.action() == Actions.JUMPBALL){
                createJumpBallPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.TIMEOUT){
                createTimeoutPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.SUBSTITUTION){
                createSubstitutionPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.FREE_THROW){
                // Handles free throw actions
                LabeledPlay lPlayTwo;
                try {
                    lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
                    if (lPlayTwo.action() != Actions.TEAM && lPlayTwo.action() != Actions.REBOUND) {
                        createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                        index++;
                    } else {
                        index += 2;
                        createFreeThrowPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                    }
                } catch (IndexOutOfBoundsException e) {
                    createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                    index++;
                }
            } else if (lPlayOne.action() == Actions.VIOLATION) {
                createViolationPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.TURNOVER){
                createTurnoverPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.SHOT){
                if (lPlayOne.shotMade()){
                    createMadeShotPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                    index++;
        } else {
          LabeledPlay play = matchup.getLabeledPlayList().get(index + 1);
          if (!play.typeText().contains("rebound")) {
            createSingleMissedShotPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            index++;
          } else {
            createMissedShotPlays(
                matchup,
                homeOnCourt,
                awayOnCourt,
                lPlayOne,
                matchup.getLabeledPlayList().get(index + 1));
            index += 2;
          }
                    }
            } else if (lPlayOne.action() == Actions.ASSIST){
                createAssistPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.FOUL || lPlayOne.action() == Actions.FLAGRANT_FOUL){
                createFoulPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            } else if (lPlayOne.action() == Actions.BLOCK){
                createBlockPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayOne);
                index += 2;
            }else if (lPlayOne.action() == Actions.REBOUND || lPlayOne.action() == Actions.TEAM){
                createReboundPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                index++;
            }
            else {
                Play play = new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
                matchup.getPlayByPlays().add(play);
                index++;
            }
        }
    }

    /**
     * Creates a free throw play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createSingleFTPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt,awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setMadeFreeThrow(lPlayOne.shotMade());
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        if (matchup.getPlayByPlays().getLast().getFoulCommitter() != null){
            matchup.getPlayByPlays().getLast().setPlayerFouled(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }
        if (lPlayOne.typeText().contains("technical")){
            play.setPlayType(PlayTypes.FREE_THROW_TECHNICAL);
            play.setFreeThrowTotal(1);
            play.setFreeThrowNumber(1);
            play.setWasTechnical(true);
        } else if (lPlayOne.flagrant()){
            play.setPlayType(PlayTypes.FREE_THROW_FLAGRANT);
            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[4]));
            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[6]));
        } else {
            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates an assist play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createAssistPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setDistance(lPlayOne.distance());
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setPlayerAssisted(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setPlayerAssisting(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a jump ball play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createJumpBallPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.JUMPBALL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setJumperOne(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setJumperTwo(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
        play.setJumperReceiver(matchup.findPlayerObject(lPlayOne.athlete_id_3()));
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates free throw plays with multiple actions.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the first labeled play
     * @param lPlayTwo the second labeled play
     */
    private void createFreeThrowPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setFreeThrowShooter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setMadeFreeThrow(lPlayOne.shotMade());
        if (matchup.getPlayByPlays().getLast().getFoulCommitter() != null){
            matchup.getPlayByPlays().getLast().setPlayerFouled(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        if (lPlayOne.typeText().contains("technical")){
            play.setPlayType(PlayTypes.FREE_THROW_TECHNICAL);
            play.setFreeThrowTotal(1);
            play.setFreeThrowNumber(1);
            play.setWasTechnical(true);
        } else if (lPlayOne.flagrant()){
            play.setPlayType(PlayTypes.FREE_THROW_FLAGRANT);
            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[4]));
            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[6]));
        } else if (lPlayTwo.action() == Actions.TEAM){
            if (lPlayTwo.defensive()){
                play.setPlayType(PlayTypes.FREE_THROW_DEFENSIVE_REBOUND_TEAM);
                play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
                play.setWasDefensive(true);
                play.setWasTeam(true);
            } else {
                play.setPlayType(PlayTypes.FREE_THROW_OFFENSIVE_REBOUND_TEAM);
                play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
                play.setWasOffensive(true);
                play.setWasTeam(true);
            }
        } else if (lPlayTwo.action() == Actions.REBOUND){
            if (lPlayTwo.defensive()) {
                play.setPlayType(PlayTypes.FREE_THROW_DEFENSIVE_REBOUND);
                play.setWasDefensive(true);
            } else if (lPlayTwo.offensive()){
                play.setPlayType(PlayTypes.FREE_THROW_OFFENSIVE_REBOUND);
                play.setWasOffensive(true);
            }
            play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
            play.setRebounder(matchup.findPlayerObject(lPlayTwo.athlete_id_1()));
        } else {
            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a substitution play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createSubstitutionPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        for (int i = 0; i < homeOnCourt.size(); i++) {
            if (homeOnCourt.get(i).getId() == lPlayOne.athlete_id_2()) {
                homeOnCourt.set(i, matchup.findPlayerObject(lPlayOne.athlete_id_1()));
                break;
            }
        }
        for (int i = 0; i < awayOnCourt.size(); i++) {
            if (awayOnCourt.get(i).getId() == lPlayOne.athlete_id_2()) {
                awayOnCourt.set(i, matchup.findPlayerObject(lPlayOne.athlete_id_1()));
                break;
            }
        }
        Play play = new Play(matchup, PlayTypes.SUBSTITUTION, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a timeout play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createTimeoutPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.TIMEOUT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a violation play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createViolationPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.VIOLATION, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setWhoViolated(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        if (lPlayOne.typeText().contains("goaltending")){
            play.setDefensiveGoalTending(true);
        }else if (lPlayOne.typeText().contains("kicked ball")){
            play.setKickedBall(true);
        }else if (lPlayOne.typeText().contains("lane")){
            play.setWasLane(true);
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a turnover play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createTurnoverPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.TURNOVER, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        if (lPlayOne.wasSteal()){
            play.setPlayType(PlayTypes.STEAL_TURNOVER);
            play.setStealer(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        } else if (lPlayOne.typeText().contains("traveling")){
            play.setPlayType(PlayTypes.TRAVELING_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        } else if (lPlayOne.typeText().contains("shot clock")){
            play.setPlayType(PlayTypes.SHOT_CLOCK_TURNOVER);
        } else if (lPlayOne.typeText().contains("out of bounds")){
            play.setPlayType(PlayTypes.OUT_OF_BOUNCE_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));

        }else if (lPlayOne.typeText().contains("bad pass")){
            play.setPlayType(PlayTypes.BAD_PASS_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }else if (lPlayOne.typeText().contains("double dribble")){
            play.setPlayType(PlayTypes.DOUBLE_DRIBBLE_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }else if (lPlayOne.typeText().contains("foul turnover")){
            play.setPlayType(PlayTypes.OFFENSIVE_FOUL_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }else if (lPlayOne.typeText().contains("lost ball")){
            play.setPlayType(PlayTypes.LOST_BALL_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a made shot play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createMadeShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setDistance(lPlayOne.distance());
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        matchup.getPlayByPlays().add(play);
    }

    private void createSingleMissedShotPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setDistance(lPlayOne.distance());
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        matchup.getPlayByPlays().add(play);
    }
    /**
     * Creates a missed shot play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the first labeled play
     * @param lPlayTwo the second labeled play
     */
    private void createMissedShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setDistance(lPlayOne.distance());
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        if (lPlayTwo.action() == Actions.TEAM){
            if (lPlayTwo.offensive()){
                play.setPlayType(PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND_TEAM);
                play.setWasTeam(true);
                play.setWasOffensive(true);
            } else {
                play.setPlayType(PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND_TEAM);
                play.setWasTeam(true);
                play.setWasDefensive(true);
            }
        }
        if (lPlayTwo.action() == Actions.REBOUND){
            if (lPlayTwo.offensive()){
                play.setPlayType(PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND);
            } else {
                play.setPlayType(PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            }
            play.setRebounder(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }

        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a foul play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the labeled play
     */
    private void createFoulPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.FOUL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setFoulCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        if (lPlayOne.action() == Actions.FLAGRANT_FOUL){
            if (lPlayOne.typeText().contains("type 1")){
        play.setPlayType(PlayTypes.FLAGRANT_FOUL_TYPE_ONE);
      }else if (lPlayOne.typeText().contains("type 2")){
                play.setPlayType(PlayTypes.FLAGRANT_FOUL_TYPE_TWO);
            }
    }else if (lPlayOne.typeText().contains("personal foul")){
            play.setPlayType(PlayTypes.PERSONAL_FOUL);
        }else if (lPlayOne.typeText().contains("offensive foul")){
            play.setPlayType(PlayTypes.OFFENSIVE_FOUL);
        }else if (lPlayOne.typeText().contains("shooting foul")){
            play.setPlayType(PlayTypes.SHOOTING_FOUL);
        }else if (lPlayOne.typeText().contains("loose ball foul")){
            play.setPlayType(PlayTypes.LOOSE_BALL_FOUL);
        }else if (lPlayOne.typeText().contains("3-seconds")){
            play.setPlayType(PlayTypes.DEFENSIVE_THREE_SECONDS_FOUL);
        }else if (lPlayOne.typeText().contains("transition take")){
            play.setPlayType(PlayTypes.TRANSITION_TAKE_FOUL);
        }else if (lPlayOne.typeText().contains("personal take")){
            play.setPlayType(PlayTypes.PERSONAL_TAKE_FOUL);
        }else if (lPlayOne.typeText().contains("double technical")){
            play.setTechOnePlayer(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
            play.setTechTwoPlayer(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
            play.setPlayType(PlayTypes.DOUBLE_TECHNICAL_FOUL);
        }else if (lPlayOne.typeText().contains("technical")){
            play.setPlayType(PlayTypes.TECHNICAL_FOUL);
        }
        matchup.getPlayByPlays().add(play);
    }

    private void createReboundPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.REBOUND, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        if (lPlayOne.action() == Actions.TEAM){
            if (lPlayOne.offensive()){
                play.setPlayType(PlayTypes.OFFENSIVE_REBOUND_TEAM);
                play.setWasTeam(true);
                play.setWasOffensive(true);
            } else {
                play.setPlayType(PlayTypes.DEFENSIVE_REBOUND_TEAM);
                play.setWasTeam(true);
                play.setWasDefensive(true);
            }
        }
        if (lPlayOne.action() == Actions.REBOUND){
            if (lPlayOne.offensive()){
                play.setPlayType(PlayTypes.OFFENSIVE_REBOUND);
            } else {
                play.setPlayType(PlayTypes.DEFENSIVE_REBOUND);
            }
            play.setRebounder(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a block play.
     * @param matchup the matchup object
     * @param homeOnCourt the list of home players on court
     * @param awayOnCourt the list of away players on court
     * @param lPlayOne the first labeled play
     * @param lPlayTwo the second labeled play
     */
    private void createBlockPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.BLOCK_AND_NO_POSSESSION_CHANGE, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setBlockedPlayer(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setPlayerBlocking(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
        if (lPlayTwo.defensive() && (lPlayTwo.action() == Actions.TEAM || lPlayTwo.action() == Actions.REBOUND)){
            play.setPlayType(PlayTypes.BLOCK_AND_POSSESSION_CHANGE);
        }
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Changes a String value to an integer value.
     * @param value String to get integer from.
     * @return value of String as integer; Otherwise returns 0;
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
    }

    /**
     * Retrieves the athlete ID from the record.
     * @param record the CSV record
     * @param id the athlete number
     * @return the athlete ID
     */
    private int getAthleteID(CSVRecord record, int id){
        if (record.get("athlete_id_" + id).equals("NA")) return 0;
        return getInt(record, "athlete_id_" + id);
    }

    /**
     * Retrieves an integer value from the record.
     * @param record the CSV record
     * @param value the column name
     * @return the integer value
     */
    private int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Checks to see if the spot in the CSV has a piece of information that can be grabbed.
     * @param record Row/ CSVRecord to look at
     * @param key column to look at in that specified row/CSVRecord.
     * @return true if there is a value; false otherwise.
     */
    private boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

    /**
     * Converts a time string to seconds.
     * @param time the time string
     * @return the time in seconds
     */
    private double convertToSeconds(String time) {
        String[] parts = time.split(":");
        double minutes = 0;
        double seconds;

        if (parts.length == 2) {
            minutes = Double.parseDouble(parts[0]);
            seconds = Double.parseDouble(parts[1]);
        } else if (parts.length == 1) {
            seconds = Double.parseDouble(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        return minutes * 60 + seconds;
    }

    /**
     * Gets the current players on court for a matchup.
     * @param matchup the matchup object
     * @param isHome boolean indicating if it's for the home team
     * @return the list of players on court
     */
    private List<Player> getCurrentPlayersOnCourt(Matchup matchup, boolean isHome) {
        if (matchup.getPlayByPlays().isEmpty()) {
            return isHome ? matchup.getHomeStarters() : matchup.getAwayStarters();
        }
        List<Play> recordedPlays = matchup.getPlayByPlays();
        return isHome ? recordedPlays.getLast().getFiveOnCourtHome() : recordedPlays.getLast().getFiveOnCourtAway();
    }
}
