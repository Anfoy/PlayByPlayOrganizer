package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.managers.*;
import me.antcode.plays.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class to gather data from CSV file
 */
public class CSVDataGather {

    private  List<LabeledPlay> allPlaysLabeled;

    private final List<Matchup> matchups;

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
        allPlaysLabeled = new ArrayList<>();
    }


    public void filterAndDesignatePlays(){
        allPlaysLabeled = playLabellingManager.labelAllPlays(playByPlayCSVPath);
        allPlaysLabeled.sort(Comparator
                .comparingInt(LabeledPlay::getGameID)
                .thenComparingInt(LabeledPlay::getGamePlayNumber));
        int count = 0;
        for (LabeledPlay labeledPlay : allPlaysLabeled){
            if (findCorrelatingMatchupWithID(labeledPlay.getGameID()) == null) continue;
            if (findCorrelatingMatchupWithID(labeledPlay.getGameID()) != null){
                if (labeledPlay.getAction() != Actions.UNKNOWN && labeledPlay.getAction() != Actions.IGNORE){
                    Matchup matchup = findCorrelatingMatchupWithID(labeledPlay.getGameID());
                    matchup.getLabeledPlayList().add(labeledPlay);
                    count++;
                    System.out.println("Added play " + count);
                }
            }
        }
    }

    public Matchup curateSpecificPlay(int gameID){
        Matchup matchup = playLabellingManager.generateSpecificMatchupPlays(playByPlayCSVPath, gameID, matchups);
        getPlays(0, matchup);
        System.out.println("Finished Generated Report");
        return matchup;
    }

    private Matchup findCorrelatingMatchupWithID(int id){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() == id){
                return matchup;
            }
        }
        return null;
    }

    public void printSpecLabelPlaysForPlayerInMatchup(int playerID, int gameID, Actions actions){
        playLabellingManager.printPlayerInvolvedSpecificLabeledPlaysForMatchup(gameID, playerID, matchups, actions);
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
            switch (lPlayOne.getAction()) {
                case JUMPBALL -> shotManager.createJumpBallPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case TIMEOUT -> timeoutManager.createTimeoutPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case SUBSTITUTION -> substitutionManager.createSubstitutionPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case FREE_THROW -> index = freeThrowManager.handleFreeThrow(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case VIOLATION -> violationManager.createViolationPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case TURNOVER -> turnoverManager.createTurnoverPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case SHOT -> index = shotManager.handleShot(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case ASSIST -> assistManager.createAssistPlays(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case FOUL, FLAGRANT_FOUL -> index = foulManager.handleFouls(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case BLOCK -> index = blockManager.handleBlock(index, matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case REBOUND, TEAM -> reboundManager.createReboundPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
                case EJECTION -> foulManager.createSingleEjectionPlay(matchup, homeOnCourt, awayOnCourt, lPlayOne);
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
     * Creates an unidentified play.
     * @param matchup The matchup object containing labeled plays.
     * @param homeOnCourt The home team players on the court.
     * @param awayOnCourt The away team players on the court.
     * @param lPlayOne The labeled play.
     */
    private void createUnidentifiedPlay(Matchup matchup, List<Player> homeOnCourt, List<Player> awayOnCourt, LabeledPlay lPlayOne) {
        Play play = new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, List.of(lPlayOne), homeOnCourt, awayOnCourt, lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
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
                for (Player player : matchup.getTotalPlayers()){
                    printer.printRecord(matchup.getGameID(), player.getId(), player.getRebounds(), player.getAssists(), player.getBlocks(),
                            player.getSteals(), player.getTurnovers(), player.getPoints(), player.getFouls(), (int) player.getMinutes()/60, player.getFieldGoalsMade(),
                            player.getFieldGoalsAttempted(), player.getThreePointFieldGoalsMade(), player.getThreePointFieldGoalsAttempted()
                    );
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
}
