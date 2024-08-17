package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.managers.*;
import me.antcode.plays.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to gather data from CSV file
 */
public class CSVDataGather {


    private final List<Matchup> matchups;
  public static  HashMap<String, String> nbaTeams = new HashMap<>();
    private final AssistManager assistManager;
    private final FoulManager foulManager;
    private final FreeThrowManager freeThrowManager;
    private final ReboundManager reboundManager;
    private final ShotManager shotManager;
    private final SubstitutionManager substitutionManager;
    private final TimeoutManager timeoutManager;
    private final TurnoverManager turnoverManager;
    private final ViolationManager violationManager;
    private final BlockManager blockManager;
    MatchupManager matchupManager;
    PlayLabellingManager playLabellingManager;

    private final LinkedHashMap<CSVRecord, Actions> pbps;

    private final List<CSVRecord> orderedCSVList;

    private final String matchupCSVPath;

    private final String playByPlayCSVPath;

    /**
     * Constructor to initialize CSVDataGather with paths to the CSV files.
     * @param matchupsCSVPath Path to the matchups CSV file.
     * @param playByPlayCSVPath Path to the play-by-play CSV file.
     */
    public CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath){
        this.matchupCSVPath = matchupsCSVPath;
        this.playByPlayCSVPath = playByPlayCSVPath;
    pbps = new LinkedHashMap<>();
    orderedCSVList = new ArrayList<>();
        assistManager = new AssistManager();
        foulManager = new FoulManager();
        freeThrowManager = new FreeThrowManager();
        reboundManager = new ReboundManager();
        shotManager = new ShotManager();
        substitutionManager = new SubstitutionManager();
        timeoutManager = new TimeoutManager();
        turnoverManager = new TurnoverManager();
        violationManager = new ViolationManager();
        blockManager = new BlockManager();
        matchupManager = new MatchupManager();
        playLabellingManager = new PlayLabellingManager();
        matchups = matchupManager.extractAllMatchups(matchupsCSVPath);
        nbaTeams.put("ATL", "Atlanta Hawks");
        nbaTeams.put("BOS", "Boston Celtics");
        nbaTeams.put("BKN", "Brooklyn Nets");
        nbaTeams.put("CHA", "Charlotte Hornets");
        nbaTeams.put("CHI", "Chicago Bulls");
        nbaTeams.put("CLE", "Cleveland Cavaliers");
        nbaTeams.put("DAL", "Dallas Mavericks");
        nbaTeams.put("DEN", "Denver Nuggets");
        nbaTeams.put("DET", "Detroit Pistons");
        nbaTeams.put("GSW", "Golden State Warriors");
        nbaTeams.put("HOU", "Houston Rockets");
        nbaTeams.put("IND", "Indiana Pacers");
        nbaTeams.put("LAC", "LA Clippers");
        nbaTeams.put("LAL", "Los Angeles Lakers");
        nbaTeams.put("MEM", "Memphis Grizzlies");
        nbaTeams.put("MIA", "Miami Heat");
        nbaTeams.put("MIL", "Milwaukee Bucks");
        nbaTeams.put("MIN", "Minnesota Timberwolves");
        nbaTeams.put("NOP", "New Orleans Pelicans");
        nbaTeams.put("NYK", "New York Knicks");
        nbaTeams.put("OKC", "Oklahoma City Thunder");
        nbaTeams.put("ORL", "Orlando Magic");
        nbaTeams.put("PHI", "Philadelphia 76ers");
        nbaTeams.put("PHX", "Phoenix Suns");
        nbaTeams.put("POR", "Portland Trail Blazers");
        nbaTeams.put("SAC", "Sacramento Kings");
        nbaTeams.put("SAS", "San Antonio Spurs");
        nbaTeams.put("TOR", "Toronto Raptors");
        nbaTeams.put("UTA", "Utah Jazz");
        nbaTeams.put("WAS", "Washington Wizards");
    }



    public Matchup curateSpecificPlay(int gameID){
        Matchup matchup = playLabellingManager.generateSpecificMatchupPlays(playByPlayCSVPath, gameID, matchups);
        getPlays( matchup);
        System.out.println("Finished Generated Report");
        return matchup;
    }


//    public void printSpecLabelPlaysForPlayerInMatchup(int playerID, int gameID, Actions actions){
//        playLabellingManager.printPlayerInvolvedSpecificLabeledPlaysForMatchup(gameID, playerID, matchups, actions);
//    }

    // Update this method to process all CSV files in the folder
    public void labelAllPlays(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                System.out.println("Processing file: " + file.getName());
                try (Reader reader = new FileReader(file);
                     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                    processRecords(csvParser, matchups);
                } catch (IOException e) {
                    System.out.println("Failed to read file: " + file.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No CSV files found in the folder.");
        }
    }

    private void processRecords(CSVParser csvParser, List<Matchup> matchups) {
        Matchup currentMatchup = null;

        for (CSVRecord csvRecord : csvParser) {
            String date = CSVUtils.getDate(csvRecord);
            String awayPlayer = CSVUtils.getAwayPlayerOne(csvRecord);
            int gameID = CSVUtils.getGameID(csvRecord);

            // Find the corresponding matchup
            Matchup matchup = findCorrelatingMatchupWithID(date, awayPlayer, matchups, gameID);

            if (matchup != null) {
                if (currentMatchup == null) {
                    // Start processing for the first matchup
                    currentMatchup = matchup;
                } else if (!currentMatchup.equals(matchup)) {
                    // Process previous matchup data
                    processPlays(currentMatchup);

                    // Clear data structures

                    // Move to new matchup
                    currentMatchup = matchup;
                }

                // Process the record for the current matchup
                Actions action = determineAction(csvRecord);
                if (action != Actions.IGNORE && action != Actions.UNKNOWN && action != Actions.END_OF_PERIOD) {
                    System.out.println("Added a play for matchup: " + currentMatchup.getDate());
                    currentMatchup.getAllLabeledPlays().add(createLabeledPlay(csvRecord, action));
                }
            }
        }

        // Process any remaining plays after the loop
        if (currentMatchup != null) {
            processPlays(currentMatchup);
        }
    }

    private void processPlays(Matchup matchup) {
        // Sort the list by play number
        matchup.getAllLabeledPlays().sort((r1, r2) -> {
            int playNumber1 = r1.getGamePlayNumber();
            int playNumber2 = r2.getGamePlayNumber();
            return Integer.compare(playNumber1, playNumber2);
        });

        // Process collected plays
        getPlays(matchup);
    }

    private void getPlays(Matchup matchup) {
        int count = 0;
        int index = 0;
        while (index < matchup.getAllLabeledPlays().size()) {
            LabeledPlay targetPlay = matchup.getAllLabeledPlays().get(index);
            Actions action = targetPlay.getAction();
            switch (action) {
                case JUMPBALL -> shotManager.createJumpBallPlay(matchup, targetPlay);
                case TIMEOUT -> timeoutManager.createTimeoutPlays(matchup, targetPlay);
                case SUBSTITUTION -> substitutionManager.createSubstitutionPlays(matchup, targetPlay);
                case FREE_THROW -> index = freeThrowManager.handleFreeThrow(index, matchup, targetPlay);
                case VIOLATION -> violationManager.createViolationPlays(matchup, targetPlay);
                case TURNOVER -> turnoverManager.createTurnoverPlays(matchup, targetPlay);
                case SHOT -> index = shotManager.handleShot(index, matchup, targetPlay);
                case ASSIST -> assistManager.createAssistPlays(matchup, targetPlay);
                case FOUL, FLAGRANT_FOUL -> foulManager.createFoulPlays(matchup, targetPlay);
                case BLOCK -> index = blockManager.handleBlock(index, matchup, targetPlay);
                case REBOUND, TEAM -> reboundManager.createReboundPlay(matchup, targetPlay);
                case EJECTION -> foulManager.createSingleEjectionPlay(matchup, targetPlay);
                default -> createUnidentifiedPlay(matchup, targetPlay);
            }
            index++;
            count++;
            System.out.println("Created Play Design: " + count);
        }
    }



//    /**
//     * Gets the players currently on the court for the specified team.
//     * @param matchup The matchup object containing play-by-play data.
//     * @param isHome True if retrieving home team players, false for away team.
//     * @return A list of players on the court.
//     */
//    private List<Player> getPlayersOnCourt(Matchup matchup, boolean isHome) {
//        if (matchup.getPlayByPlays().isEmpty()) {
//            return isHome ? matchup.getHomeStarters() : matchup.getAwayStarters();
//        } else {
//            return isHome ? matchup.getPlayByPlays().getLast().getFiveOnCourtHome() : matchup.getPlayByPlays().getLast().getFiveOnCourtAway();
//        }
//    }



    /**
     * Creates an unidentified play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    private void createUnidentifiedPlay(Matchup matchup,LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        matchup.getPlayByPlays().add(play);
    }



    /**
     * Parses a string value to an integer value.
     * @param value The string to parse.
     * @return The integer value, or 0 if parsing fails.
     */
    public int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
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
     * Determines the action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined action type.
     */
    private Actions determineAction(CSVRecord csvRecord) {
        String eventType = csvRecord.get("event_type").toLowerCase();
        if (eventType.equals("jump ball")){
            return Actions.JUMPBALL;
        }else if (eventType.equals("substitution")){
            return Actions.SUBSTITUTION;
        }else  if (csvRecord.get("timeout").equals("true")){
            return Actions.TIMEOUT;
        }else if (csvRecord.get("ignore").equals("true")){
            return Actions.IGNORE;
        }
        else if (csvRecord.get("rebound").equals("true") && csvRecord.get("wasTeam").equals("false")){
            return Actions.REBOUND;
        }else if (csvRecord.get("wasTeam").equals("true")){
            return Actions.TEAM;
        }else if (csvRecord.get("violation").equals("true")){
            return Actions.VIOLATION;
        }else if (eventType.equals("ejection")){
            return Actions.EJECTION;
        }else if (csvRecord.get("free_throw").equals("true")){
            return Actions.FREE_THROW;
        }else if (csvRecord.get("flagrant").equals("true") && csvRecord.get("foul").equals("true")){
            return Actions.FLAGRANT_FOUL;
        }else if (csvRecord.get("foul").equals("true")){
            return Actions.FOUL;
        }else if (!csvRecord.get("block").isEmpty()){
            return Actions.BLOCK;
        }else if (csvRecord.get("turnover").equals("true")){
            return Actions.TURNOVER;
        }else if (csvRecord.get("assists").equals("true")){
            return Actions.ASSIST;
        } else if (eventType.equals("shot")){
            return determineShootingAction(csvRecord);
        }else if (csvRecord.get("end_period").equals("true")){
            return Actions.END_OF_PERIOD;
        }
        else {
            return Actions.UNKNOWN;
        }
    }


    /**
     * Determines the shooting action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined shooting action type.
     */
    private Actions determineShootingAction(CSVRecord csvRecord) {
        if (csvRecord.get("assists").equals("true")) {
            return Actions.ASSIST;
        }
        return Actions.SHOT;
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
                     new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr",
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

    public void deployTestCSV(List<Matchup> matchups){
        try (FileWriter writer = new FileWriter("stats.csv");
             CSVPrinter printer =
                     new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("game_id", "athlete_id", "rebounds", "assists", "blocks",
                                     "steals", "turnovers", "points", "fouls", "minutes", "f_g_made", "f_g_att", "t_f_made", "t_f_att")
                             .build())) {
            for (Matchup matchup : matchups) {
        if (matchup.getGameID() != 0) {
          for (Player player : matchup.getTotalPlayers()) {
            printer.printRecord(
                matchup.getGameID(),
                player.getId(),
                player.getRebounds(),
                player.getAssists(),
                player.getBlocks(),
                player.getSteals(),
                player.getTurnovers(),
                player.getPoints(),
                player.getFouls(),
                (int) player.getMinutes() / 60,
                player.getFieldGoalsMade(),
                player.getFieldGoalsAttempted(),
                player.getThreePointFieldGoalsMade(),
                player.getThreePointFieldGoalsAttempted());
          }
                }
            }
            printer.flush();
        }catch (IOException e) {
            throw new RuntimeException(e);
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


    public List<Matchup> getMatchups() {
        return matchups;
    }

    public String getPlayByPlayCSVPath() {
        return playByPlayCSVPath;
    }

    public String getMatchupCSVPath() {
        return matchupCSVPath;
    }


    private boolean isMatchup(Matchup matchup, String date, String name, int id) {
        if (matchup.getGameID() == 0){
            if (matchup.getDate().equals(date)) {
                for (Player player : matchup.getTotalPlayers()){
                    if (player.getName().equals(name)){
                        matchup.setGameID(id);
                        return true;
                    }
                }
            }
        }else{
            return matchup.getGameID() == id;
        }
        return false;
    }


    private Matchup findCorrelatingMatchupWithID(String date,String name, List<Matchup> matchups, int id){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() == 0){
                if (matchup.getDate().equals(date)) {
                    for (Player player : matchup.getTotalPlayers()){
                        if (player.getName().equals(name)){
                            matchup.setGameID(id);
                            return matchup;
                        }
                    }
                }
            }else{
                if (matchup.getGameID() == id){
                    return matchup;
                }
            }
        }
        return null;
    }

        /**
     * Creates a labeled play object from the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @param action The action type of the play.
     * @return A LabeledPlay object.
     */
    private LabeledPlay createLabeledPlay(CSVRecord csvRecord, Actions action) {
        LabeledPlay labeledPlay = new LabeledPlay(
                action,
                parseInt(csvRecord.get("game_id")),
                parseInt(csvRecord.get("period")),
                convertToSeconds(csvRecord.get("remaining_time")),
                parseInt(csvRecord.get("away_score")),
                parseInt(csvRecord.get("home_score")),
                csvRecord.get("event_type"),
                csvRecord.get("type"),
                csvRecord.get("description"),
                csvRecord.get("result").equals("made"),
                csvRecord.get("steal"),
                csvRecord.get("assist"),
                csvRecord.get("block"),
                csvRecord.get("player"),
                csvRecord.get("opponent"),
                parseInt(csvRecord.get("shot_distance")),
                csvRecord.get("offensive").equals("true"),
                csvRecord.get("defensive").equals("true"),
                parseInt(csvRecord.get("play_id")),
                csvRecord.get("flagrant").equals("true"),
                csvRecord.get("3pt").equals("true"),
                csvRecord.get("date"),
                convertToSeconds(csvRecord.get("play_length")),
                getInt(csvRecord, "num"),
                getInt(csvRecord, "outof"),
                csvRecord.get("away"),
                csvRecord.get("home"),
                csvRecord.get("possession")
        );
        labeledPlay.setCourtPlayers(csvRecord);
        return labeledPlay;
    }

    /**
     * Converts a time string to seconds.
     * @param time The time string in the format "HH:MM:SS", "MM:SS" or "SS".
     * @return The time in seconds.
     */
    public double convertToSeconds(String time) {
        String[] parts = time.split(":");
        double hours = 0;
        double minutes = 0;
        double seconds;

        if (parts.length == 3) {
            hours = Double.parseDouble(parts[0]);
            minutes = Double.parseDouble(parts[1]);
            seconds = Double.parseDouble(parts[2]);
        } else if (parts.length == 2) {
            minutes = Double.parseDouble(parts[0]);
            seconds = Double.parseDouble(parts[1]);
        } else if (parts.length == 1) {
            seconds = Double.parseDouble(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        return hours * 3600 + minutes * 60 + seconds;
    }
}
