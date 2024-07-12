package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class to gather data from CSV file
 */
public class CSVDataGather {

    private final String matchupsCSVPath;
    private final String playByPlayCSVPath;

    /**
     * Constructor to initialize CSVDataGather with paths to the CSV files.
     * @param matchupsCSVPath Path to the matchups CSV file.
     * @param playByPlayCSVPath Path to the play-by-play CSV file.
     */
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
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord csvRecord : csvParser) {
                totalMatchups.add(createMatchupFromRecord(csvRecord));
            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
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
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord csvRecord : csvParser) {
                if (getInt(csvRecord, "game_id") != matchup.getGameID()) continue;

                Actions action = determineAction(csvRecord);
                if (action != Actions.IGNORE) {
                    matchup.getLabeledPlayList().add(createLabeledPlay(csvRecord, action));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
    }

    /**
     * Determines the action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined action type.
     */
    private Actions determineAction(CSVRecord csvRecord) {
        String type = csvRecord.get("type_text").toLowerCase();

        return switch (type) {
            case "substitution" -> Actions.SUBSTITUTION;
            case String t when t.contains("timeout") -> Actions.TIMEOUT;
            case String t when t.contains("jumpball") -> Actions.JUMPBALL;
            case String t when t.contains("free throw") -> Actions.FREE_THROW;
            default -> {
                if (csvRecord.get("wasTeam").equals("true")) {
                    yield Actions.TEAM;
                } else if (csvRecord.get("rebound").equals("true")) {
                    yield Actions.REBOUND;
                } else if (csvRecord.get("shooting_play").equalsIgnoreCase("true")) {
                    yield determineShootingAction(csvRecord);
                } else if (csvRecord.get("foul").equals("true") || type.contains("charge")) {
                    yield csvRecord.get("flagrant").equals("true") ? Actions.FLAGRANT_FOUL : Actions.FOUL;
                } else if (csvRecord.get("turnover").equals("true")) {
                    yield Actions.TURNOVER;
                } else if (csvRecord.get("violation").equals("true")) {
                    yield Actions.VIOLATION;
                } else {
                    yield Actions.IGNORE;
                }
            }
        };
    }

    /**
     * Determines the shooting action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined shooting action type.
     */
    private Actions determineShootingAction(CSVRecord csvRecord) {
        if (csvRecord.get("assist").equals("true")) {
            return Actions.ASSIST;
        } else if (getInt(csvRecord, "block_athlete2") > 0) {
            return Actions.BLOCK;
        } else {
            return Actions.SHOT;
        }
    }

    /**
     * Creates a labeled play object from the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @param action The action type of the play.
     * @return A LabeledPlay object.
     */
    private LabeledPlay createLabeledPlay(CSVRecord csvRecord, Actions action) {
        String description = csvRecord.get("text").toLowerCase();
        return new LabeledPlay(
                action,
                getInt(csvRecord, "game_id"),
                getInt(csvRecord, "qtr"),
                convertToSeconds(csvRecord.get("time")),
                getInt(csvRecord, "away_score"),
                getInt(csvRecord, "home_score"),
                getAthleteID(csvRecord, 1),
                getAthleteID(csvRecord, 2),
                getAthleteID(csvRecord, 3),
                csvRecord.get("type_text").toLowerCase(),
                getInt(csvRecord, "shot_made") > 0,
                getInt(csvRecord, "Steal_athlete2") > 0,
                getInt(csvRecord, "distance"),
                csvRecord.get("offensive").equals("true"),
                !csvRecord.get("offensive").equals("true"),
                getInt(csvRecord, "game_play_number"),
                csvRecord.get("flagrant").equals("true"),
                getInt(csvRecord, "distance") > 22 || description.contains("three point")
        );
    }

    /**
     * Creates a matchup from a specific row of the CSV.
     * @param csvRecord which row to look at
     * @return A matchup compiled of all data gathered from the row.
     */
    private Matchup createMatchupFromRecord(CSVRecord csvRecord) {
        return new Matchup(
                csvRecord.get("game_date"),
                csvRecord.get("home_display_name"),
                csvRecord.get("away_display_name"),
                csvRecord.get("type_abbreviation"),
                parseInt(csvRecord.get("game_id")),
                extractPlayersFromMatchupData(csvRecord, "home_starter_", 5),
                extractPlayersFromMatchupData(csvRecord, "home_bench_", 10),
                extractPlayersFromMatchupData(csvRecord, "away_starter_", 5),
                extractPlayersFromMatchupData(csvRecord, "away_bench_", 10),
                new ArrayList<>()
        );
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
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> createPlayerFromRecord(record, prefix, i))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Creates a player object from the given CSV record and prefix.
     * @param record The CSV record containing player data.
     * @param prefix The prefix for the player columns.
     * @param index The index of the player in the columns.
     * @return A Player object or null if the player data is not valid.
     */
    private Player createPlayerFromRecord(CSVRecord record, String prefix, int index) {
        String idKey = prefix + index + "_id";
        String nameKey = prefix + index;
        if (rowHasValue(record, idKey) && rowHasValue(record, nameKey)) {
            return new Player(parseInt(record.get(idKey)), record.get(nameKey));
        }
        return null;
    }

    /**
     * Processes plays for the given matchup starting from a specific index.
     * @param index the starting index in the labeled plays list
     * @param matchup the matchup object containing labeled plays
     */
    public void getPlays(int index, Matchup matchup){
        while (index < matchup.getLabeledPlayList().size()) {
            List<Player> homeOnCourt = getPlayersOnCourt(matchup, true);
            List<Player> awayOnCourt = getPlayersOnCourt(matchup, false);
            LabeledPlay lPlayOne = matchup.getLabeledPlayList().get(index);

            switch (lPlayOne.action()) {
                case JUMPBALL -> createJumpBallPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case TIMEOUT -> createTimeoutPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case SUBSTITUTION -> createSubstitutionPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case FREE_THROW -> index = handleFreeThrow(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case VIOLATION -> createViolationPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case TURNOVER -> createTurnoverPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case SHOT -> index = handleShot(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case ASSIST -> createAssistPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case FOUL, FLAGRANT_FOUL -> createFoulPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case BLOCK -> index = handleBlock(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case REBOUND, TEAM -> createReboundPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                default -> createUnidentifiedPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            }
            index++;
        }
    }

    /**
     * Gets the players currently on the court for the specified team.
     * @param matchup The matchup object containing play-by-play data.
     * @param isHome True if retrieving home team players, false for away team.
     * @return A list of players on the court.
     */
    private List<Player> getPlayersOnCourt(Matchup matchup, boolean isHome) {
        if (matchup.getPlayByPlays().isEmpty()) {
            return isHome ? matchup.getHomeStarters() : matchup.getAwayStarters();
        } else {
            return isHome ? matchup.getPlayByPlays().getLast().getFiveOnCourtHome() : matchup.getPlayByPlays().getLast().getFiveOnCourtAway();
        }
    }

    /**
     * Handles the processing of a free throw play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the free throw play.
     */
    private int handleFreeThrow(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        LabeledPlay lPlayTwo;
        try {
            lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (lPlayTwo.action() != Actions.TEAM && lPlayTwo.action() != Actions.REBOUND) {
                createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            } else {
                createFreeThrowPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleFTPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        }
        return index;
    }

    /**
     * Handles the processing of a shot play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the shot play.
     */
    private int handleShot(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        if (lPlayOne.shotMade()) {
            createMadeShotPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        } else {
            LabeledPlay lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (!lPlayTwo.typeText().contains("rebound")) {
                createSingleMissedShotPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            } else {
                createMissedShotPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                index++;
            }
        }
        return index;
    }

    /**
     * Handles the processing of a block play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the block play.
     */
    private int handleBlock(int index, Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        LabeledPlay lPlayTwo;
        try {
            lPlayTwo = matchup.getLabeledPlayList().get(index + 1);
            if (lPlayTwo.action() != Actions.TEAM && lPlayTwo.action() != Actions.REBOUND) {
                createSingleBlockPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
            } else {
                createBlockPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleBlockPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
        }
        return index;
    }

    /**
     * Creates an unidentified play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createUnidentifiedPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a single free throw play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleFTPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setMadeFreeThrow(lPlayOne.shotMade());
        if (lPlayOne.shotMade()){
            playerOne.addPoints(1);
        }
        play.setPlayerShooting(playerOne);
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
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates an assist play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createAssistPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        Player playerTwo = matchup.findPlayerObject(lPlayOne.athlete_id_2());
        play.setDistance(lPlayOne.distance());

        if (lPlayOne.isThreePointer()){
            playerOne.addThreePointFieldGoalsMade(1);
            playerOne.addThreePointFieldGoalsAttempted(1);
            playerOne.addFieldGoalsAttempted(1);
            playerOne.addFieldGoalsMade(1);
            playerOne.addPoints(3);
        } else {
            playerOne.addFieldGoalsAttempted(1);
            playerOne.addFieldGoalsMade(1);
            playerOne.addPoints(2);
        }

        playerTwo.addAssists(1);
        play.setPlayerShooting(playerOne);
        play.setPlayerAssisted(playerOne);
        play.setPlayerAssisting(playerTwo);

        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a jump ball play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createJumpBallPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.JUMPBALL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        play.setJumperOne(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        play.setJumperTwo(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
        play.setJumperReceiver(matchup.findPlayerObject(lPlayOne.athlete_id_3()));
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a free throw play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createFreeThrowPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.FREE_THROW, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setFreeThrowShooter(playerOne);
        play.setMadeFreeThrow(lPlayOne.shotMade());
        if (lPlayOne.shotMade()){
            playerOne.addPoints(1);
        }
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
                play.setMakeUpOfPlay(List.of(lPlayOne));
                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
                play.setWasDefensive(true);
                play.setWasTeam(true);
            } else {
                play.setMakeUpOfPlay(List.of(lPlayOne));
                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
                play.setWasOffensive(true);
                play.setWasTeam(true);
            }
        } else if (lPlayTwo.action() == Actions.REBOUND){
            Player playerTwo = matchup.findPlayerObject(lPlayTwo.athlete_id_1());
            playerTwo.addRebounds(1);
            if (lPlayTwo.defensive()) {
                play.setPlayType(PlayTypes.FREE_THROW_DEFENSIVE_REBOUND);
                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
                play.setWasDefensive(true);
            } else if (lPlayTwo.offensive()){
                play.setPlayType(PlayTypes.FREE_THROW_OFFENSIVE_REBOUND);
                play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
                play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
                play.setWasOffensive(true);
            }
            play.setMakeUpOfPlay(List.of(lPlayOne, lPlayTwo));
            play.setRebounder(playerTwo);
        } else {
            play.setFreeThrowNumber(parseInt(lPlayOne.typeText().split(" ")[3]));
            play.setFreeThrowTotal(parseInt(lPlayOne.typeText().split(" ")[5]));
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a substitution play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
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
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a timeout play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createTimeoutPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.TIMEOUT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a violation play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createViolationPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.VIOLATION, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setWhoViolated(playerOne);
        if (lPlayOne.typeText().contains("goaltending")){
            play.setDefensiveGoalTending(true);
        } else if (lPlayOne.typeText().contains("kicked ball")){
            play.setKickedBall(true);
        } else if (lPlayOne.typeText().contains("lane")){
            play.setWasLane(true);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a turnover play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createTurnoverPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.TURNOVER, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        Player playerTwo = matchup.findPlayerObject(lPlayOne.athlete_id_2());
        if (lPlayOne.wasSteal()){
            play.setPlayType(PlayTypes.STEAL_TURNOVER);
            play.setStealer(playerTwo);
            playerOne.addTurnovers(1);
            playerTwo.addSteals(1);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        } else if (lPlayOne.typeText().contains("traveling")){
            play.setPlayType(PlayTypes.TRAVELING_TURNOVER);
            playerOne.addTurnovers(1);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
        } else if (lPlayOne.typeText().contains("shot clock")){
            play.setPlayType(PlayTypes.SHOT_CLOCK_TURNOVER);
        } else if (lPlayOne.typeText().contains("out of bounds")){
            play.setPlayType(PlayTypes.OUT_OF_BOUNCE_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
            playerOne.addTurnovers(1);
        } else if (lPlayOne.typeText().contains("bad pass")){
            play.setPlayType(PlayTypes.BAD_PASS_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
            playerOne.addTurnovers(1);
        } else if (lPlayOne.typeText().contains("double dribble")){
            play.setPlayType(PlayTypes.DOUBLE_DRIBBLE_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
            playerOne.addTurnovers(1);
        } else if (lPlayOne.typeText().contains("foul turnover")){
            play.setPlayType(PlayTypes.OFFENSIVE_FOUL_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
            playerOne.addTurnovers(1);
        } else if (lPlayOne.typeText().contains("lost ball")){
            play.setPlayType(PlayTypes.LOST_BALL_TURNOVER);
            play.setTurnoverSpecificPlayer(true);
            play.setTurnoverCommitter(playerOne);
            playerOne.addTurnovers(1);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a made shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createMadeShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setDistance(lPlayOne.distance());
        if (lPlayOne.isThreePointer()){
            playerOne.addPoints(3);
            playerOne.addThreePointFieldGoalsMade(1);
            playerOne.addThreePointFieldGoalsAttempted(1);
        } else {
            playerOne.addPoints(2);
        }
        playerOne.addFieldGoalsAttempted(1);
        playerOne.addFieldGoalsMade(1);
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        play.setPlayerShooting(matchup.findPlayerObject(lPlayOne.athlete_id_1()));
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a single missed shot play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleMissedShotPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setDistance(lPlayOne.distance());
        if ( lPlayOne.isThreePointer()){
            playerOne.addThreePointFieldGoalsAttempted(1);
            playerOne.addFieldGoalsAttempted(1);
        } else {
            playerOne.addFieldGoalsAttempted(1);
        }
        play.setPlayerShooting(playerOne);
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a missed shot play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createMissedShotPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        if (lPlayOne.isThreePointer()){
            playerOne.addThreePointFieldGoalsAttempted(1);
            playerOne.addFieldGoalsAttempted(1);
        } else {
            playerOne.addFieldGoalsAttempted(1);
        }
        play.setDistance(lPlayOne.distance());
        play.setPlayerShooting(playerOne);

        if (lPlayTwo.action() == Actions.TEAM){
            play.setPlayType(lPlayTwo.offensive() ? PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND : PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            play.setWasTeam(true);
            play.setWasOffensive(lPlayTwo.offensive());
            play.setWasDefensive(!lPlayTwo.offensive());
        } else if (lPlayTwo.action() == Actions.REBOUND){
            Player playerTwo = matchup.findPlayerObject(lPlayTwo.athlete_id_1());
            play.setWasTeam(false);
            playerTwo.addRebounds(1);
            play.setPlayType(lPlayTwo.offensive() ? PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND : PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            play.setRebounder(playerTwo);
            play.setWasOffensive(lPlayTwo.offensive());
            play.setWasDefensive(!lPlayTwo.offensive());
        }

        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a foul play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createFoulPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.FOUL, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        String text = lPlayOne.typeText();
        if (playerOne != null) {
            play.setFoulCommitter(playerOne);
            if (!text.contains("3-seconds") && !text.contains("double technical")) {
                playerOne.addFouls(1);
            }else if (text.contains("double technical")){
                play.setTechOnePlayer(playerOne);
                play.setTechTwoPlayer(matchup.findPlayerObject(lPlayOne.athlete_id_2()));
            }
        }

        if (lPlayOne.action() == Actions.FLAGRANT_FOUL){
            play.setPlayType(text.contains("type 1") ? PlayTypes.FLAGRANT_FOUL_TYPE_ONE : text.contains("type 2") ? PlayTypes.FLAGRANT_FOUL_TYPE_TWO : play.getPlayType());
        } else {
            play.setPlayType(determineFoulPlayType(text));
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Determines the foul play type based on the text description.
     * @param text The text description of the play.
     * @return The determined PlayType.
     */
    private PlayTypes determineFoulPlayType(String text) {
        return switch (text) {
            case String t when t.contains("personal foul") -> PlayTypes.PERSONAL_FOUL;
            case String t when t.contains("offensive foul") -> PlayTypes.OFFENSIVE_FOUL;
            case String t when t.contains("shooting foul") -> PlayTypes.SHOOTING_FOUL;
            case String t when t.contains("loose ball foul") -> PlayTypes.LOOSE_BALL_FOUL;
            case String t when t.contains("3-seconds") -> PlayTypes.DEFENSIVE_THREE_SECONDS_FOUL;
            case String t when t.contains("transition take") -> PlayTypes.TRANSITION_TAKE_FOUL;
            case String t when t.contains("personal take") -> PlayTypes.PERSONAL_TAKE_FOUL;
            case String t when t.contains("double technical") -> PlayTypes.DOUBLE_TECHNICAL_FOUL;
            case String t when t.contains("technical") -> PlayTypes.TECHNICAL_FOUL;
            case String t when t.contains("charge") -> PlayTypes.OFFENSIVE_CHARGE;
            default -> PlayTypes.FOUL;
        };
    }

    /**
     * Creates a rebound play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createReboundPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.REBOUND, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        play.setPlayType(lPlayOne.offensive() ? PlayTypes.OFFENSIVE_REBOUND : PlayTypes.DEFENSIVE_REBOUND);
        play.setWasOffensive(lPlayOne.offensive());
        play.setWasDefensive(!lPlayOne.offensive());
        if (lPlayOne.action() == Actions.TEAM){
            play.setWasTeam(true);
        } else if (lPlayOne.action() == Actions.REBOUND){
            playerOne.addRebounds(1);
            play.setWasTeam(false);
            play.setRebounder(playerOne);
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a block play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createBlockPlays(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.BLOCK_AND_NO_POSSESSION_CHANGE, List.of(lPlayOne, lPlayTwo), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        Player playerTwo = matchup.findPlayerObject(lPlayOne.athlete_id_2());
        playerTwo.addBlocks(1);
        playerOne.addFieldGoalsAttempted(1);
        if (lPlayOne.isThreePointer()){
            playerOne.addThreePointFieldGoalsAttempted(1);
        }
        play.setBlockedPlayer(playerOne);
        play.setPlayerBlocking(playerTwo);
        if (lPlayTwo.action() == Actions.TEAM || lPlayTwo.action() == Actions.REBOUND){
            if (lPlayTwo.action() == Actions.REBOUND) {
                Player rebounder = matchup.findPlayerObject(lPlayTwo.athlete_id_1());
                play.setWasTeam(false);
                rebounder.addRebounds(1);
            }
            if (lPlayTwo.action() == Actions.TEAM){
                play.setWasTeam(true);
            }
            if (lPlayTwo.defensive()) {
                play.setPlayType(PlayTypes.BLOCK_AND_POSSESSION_CHANGE);
            }
        }
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a single block play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createSingleBlockPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.BLOCK, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.homeScore(), lPlayOne.awayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.athlete_id_1());
        Player playerTwo = matchup.findPlayerObject(lPlayOne.athlete_id_2());
        playerTwo.addBlocks(1);
        playerOne.addFieldGoalsAttempted(1);
        if (lPlayOne.isThreePointer()){
            playerOne.addThreePointFieldGoalsAttempted(1);
        }
        play.setBlockedPlayer(playerOne);
        play.setPlayerBlocking(playerTwo);
        addMinutesToPlayers(homeOnCourt, awayOnCourt, play);
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Parses a string value to an integer value.
     * @param value The string to parse.
     * @return The integer value, or 0 if parsing fails.
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
     * @param record The CSV record containing play-by-play data.
     * @param id The athlete number.
     * @return The athlete ID.
     */
    private int getAthleteID(CSVRecord record, int id){
        return rowHasValue(record, "athlete_id_" + id) ? getInt(record, "athlete_id_" + id) : 0;
    }

    /**
     * Retrieves an integer value from the record.
     * @param record The CSV record containing play-by-play data.
     * @param value The column name.
     * @return The integer value.
     */
    private int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Checks if a CSV record has a valid value for the given key.
     * @param record The CSV record to check.
     * @param key The column name to check.
     * @return True if the value is valid, false otherwise.
     */
    private boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

    /**
     * Converts a time string to seconds.
     * @param time The time string in the format "MM:SS" or "SS".
     * @return The time in seconds.
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
     * Adds the play duration to the minutes played by each player on the court.
     * @param homeCourt The home team players on the court.
     * @param awayCourt The away team players on the court.
     * @param play The play object containing the play duration.
     */
    private void addMinutesToPlayers(List<Player> homeCourt, List<Player> awayCourt, Play play){
        homeCourt.forEach(player -> player.addMinutes(play.getPlayDuration()));
        awayCourt.forEach(player -> player.addMinutes(play.getPlayDuration()));
    }

    public void developPlayTypesCSV(List<Matchup> allMatchups){
                try (FileWriter writer = new FileWriter("playTypes.csv");
                     CSVPrinter printer =
                     new CSVPrinter(
                             writer,
                             CSVFormat.DEFAULT
                                     .builder()
                                     .setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr")
                                     .build())) {

            for (Matchup matchup : allMatchups) {
                for (Play play : matchup.getPlayByPlays()) {
                    printer.printRecord(
                            play.getGameID(),
                            play.getPlayType(),
                            play.getPlayDuration(),
                            convertSecondsToMinuteFormat(play.getTimeLeftInQuarter()),
                            play.getQuarter());
                }
            }

            printer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deployMatchupAndPlayByPlayCSV(List<Matchup> matchups){
        try (FileWriter writer = new FileWriter("playspermatchup.csv");
             CSVPrinter printer =
                     new CSVPrinter(
                             writer,
                             CSVFormat.DEFAULT
                                     .builder()
                                     .setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr",
                                             "shooter_name", "shooter_id", "rebounder_name", "rebounder_id", "assister_name",
                                             "assister_id", "blocker_name", "blocker_id", "fouler_name", "fouler_id", "turnover_player_name",
                                             "turnover_player_id", "violater_name", "violater_id", "stealer_name", "stealer_id", "distance", "jumper_one_name",
                                             "jumper_one_id", "jumper_two_name", "jumper_two_id", "jumper_three_name", "jumper_three_id", "tech_one_name", "tech_one_id",
                                     "tech_two_name", "tech_two_id", "free_throw_number", "free_throw_total", "made_free_throw", "was_team")
                                     .build())) {

            for (Matchup matchup : matchups) {
                for (Play play : matchup.getPlayByPlays()) {
                    printer.printRecord(
                            checkNull(play.getGameID()),
                            checkNull(play.getPlayType()),
                            checkNull(play.getPlayDuration()),
                            checkNull(convertSecondsToMinuteFormat(play.getTimeLeftInQuarter())),
                            checkNull(play.getQuarter()),
                            checkNull(play.getPlayerShooting() != null ? play.getPlayerShooting().getName() : null),
                            checkNull(play.getPlayerShooting() != null ? play.getPlayerShooting().getId() : null),
                            checkNull(play.getRebounder() != null ? play.getRebounder().getName() : null),
                            checkNull(play.getRebounder() != null ? play.getRebounder().getId() : null),
                            checkNull(play.getPlayerAssisting() != null ? play.getPlayerAssisting().getName() : null),
                            checkNull(play.getPlayerAssisting() != null ? play.getPlayerAssisting() .getId() : null),
                            checkNull(play.getPlayerBlocking() != null ? play.getPlayerBlocking().getName() : null),
                            checkNull(play.getPlayerBlocking() != null ? play.getPlayerBlocking().getId() : null),
                            checkNull(play.getFoulCommitter() != null ? play.getFoulCommitter().getName() : null),
                            checkNull(play.getFoulCommitter() != null ? play.getFoulCommitter().getId() : null),
                            checkNull(play.getTurnoverCommitter() != null ? play.getTurnoverCommitter() .getName() : null),
                            checkNull(play.getTurnoverCommitter()  != null ? play.getTurnoverCommitter() .getId() : null),
                            checkNull(play.getWhoViolated() != null ?play.getWhoViolated().getName() : null),
                            checkNull(play.getWhoViolated() != null ? play.getWhoViolated().getId() : null),
                            checkNull(play.getStealer() != null ? play.getStealer().getName() : null),
                            checkNull(play.getStealer() != null ? play.getStealer().getId() : null),
                            play.getDistance(),
                            checkNull(play.getJumperOne() != null ? play.getJumperOne().getName() : null),
                            checkNull(play.getJumperOne() != null ? play.getJumperOne().getId() : null),
                            checkNull(play.getJumperTwo() != null ? play.getJumperTwo().getName() : null),
                            checkNull(play.getJumperTwo() != null ? play.getJumperTwo().getId() : null),
                            checkNull(play.getJumperReceiver() != null ? play.getJumperReceiver().getName() : null),
                            checkNull(play.getJumperReceiver() != null ? play.getJumperReceiver().getId() : null),
                            checkNull(play.getTechOnePlayer() != null ? play.getTechOnePlayer().getName() : null),
                            checkNull(play.getTechOnePlayer() != null ? play.getTechOnePlayer().getId() : null),
                            checkNull(play.getTechTwoPlayer() != null ? play.getTechTwoPlayer().getName() : null),
                            checkNull(play.getTechTwoPlayer() != null ? play.getTechTwoPlayer().getId() : null),
                            play.getFreeThrowNumber(),
                            play.getFreeThrowTotal(),
                            play.isMadeFreeThrow(),
                            play.isWasTeam()
                    );
                }
            }

            printer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String checkNull(Object value) {
        return value == null ? "NA" : value.toString();
    }


    private   String convertSecondsToMinuteFormat(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) ((totalSeconds - (int) totalSeconds) * 1000);

        // Convert milliseconds to string and remove trailing zeros

        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds);
    }

}
