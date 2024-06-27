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
    List<LabeledPlay> labeledPlaysList;

    public CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath){
        this.matchupsCSVPath = matchupsCSVPath;
        this.playByPlayCSVPath = playByPlayCSVPath;
        labeledPlaysList = new ArrayList<>();
    }
    public List<Matchup> extractAllMatchups() {
        List<Matchup> totalMatchups = new ArrayList<>();
        try (Reader reader = new FileReader(matchupsCSVPath);
             //Opens the parser and starts at second row since first row is the headers/column names
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            //Creates all the matchups
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
     * Labels every play for the parameterized matchup for future data managing.
     * @param matchup Matchup to look at
     * @return A list of all labeled plays for that matchup.
     */
    public List<LabeledPlay> establishLabeledPlays(Matchup matchup) {
        List<LabeledPlay> totalPlays = new ArrayList<>();

        try (Reader reader = new FileReader(playByPlayCSVPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord csvRecord : csvParser) {
                LabeledPlay play = createLabeledPlay(csvRecord, matchup);
        if (play.action() != Actions.UNIDENTIFIED) {
          totalPlays.add(play);
                }

            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
        return totalPlays;
    }

    /**
     * Establishes the full Play meaning, SHOT+FOUL or ASSIST+SHOT+FOUL
     * @param matchup Matchup to look at
     * @return List of Play By Play lists
     */
    public List<Play> setPlayByPlayList(Matchup matchup){
        List<LabeledPlay> playsToEvaluate = matchup.getLabeledPlayList();
        List<Play> totalPlays = new ArrayList<>();
        int index = 0;
        if (!(index >= playsToEvaluate.size())){
            //TODO: CONSTRUCT LOGIC TO GET THREE PLAY or MORE PLAYS. MAYBE JUST MAKE SEPARATE METHODS. OR DO
            //A LIST AND ACCESS THEM INDIVIDUALLY IN THE LIST.
        }
        return totalPlays;
    }



    /**
     * Creates a matchup From a specific Row of the CSV
     * @param csvRecord which row to look at
     * @return A matchup compiled of all data gathered from the row.
     */
    private Matchup createMatchupFromRecord(CSVRecord csvRecord) {
        //Grab generic information for a matchup
        int gameId = parseInt(csvRecord.get("game_id"));
        String gameType = csvRecord.get("type_abbreviation");
        String gameDate = csvRecord.get("game_date");
        String homeTeam = csvRecord.get("home_display_name");
        String awayTeam = csvRecord.get("away_display_name");

        //Get the list of the home and away starters & bench
        List<Player> homeStarters = extractPlayersFromMatchupData(csvRecord, "home_starter_", 5);
        List<Player> awayStarters = extractPlayersFromMatchupData(csvRecord, "away_starter_", 5);
        List<Player> homeBench = extractPlayersFromMatchupData(csvRecord, "home_bench_", 10);
        List<Player> awayBench = extractPlayersFromMatchupData(csvRecord, "away_bench_", 10);

        return new Matchup(gameDate, homeTeam, awayTeam, gameType, gameId, homeStarters, homeBench, awayStarters, awayBench, new ArrayList<>());
    }

    /**
     * Creates the list of players based on the row provided
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
   * Creates the proper labeled play based on the gameID of the matchup
   * @param record Row to look at
   * @param matchup Matchup to use for gathering information
   */
  private LabeledPlay createLabeledPlay(CSVRecord record, Matchup matchup) {
    int gameId = parseInt(record.get("game_id"));
    if (gameId == matchup.getGameID()) {
      String type = record.get("type_text");
      // If the play was a shooting play
        for (Actions action : Actions.values()){
            if (record.get("text").contains(action.getDescription())){
                return newLabelPlayObject(action, record);
            }
        if (action.getDescription().equals(type)) {
          return newLabelPlayObject(action, record);
            }
        }

    }
      return newLabelPlayObject(Actions.UNIDENTIFIED, record);
    }

    private LabeledPlay newLabelPlayObject(Actions actions, CSVRecord record){
    return new LabeledPlay(
        actions,
            getInt(record, "season"),
            getInt(record, "game_id"),
            record.get("game_date"),
            getInt(record, "game_play_number"),
            record.get("type_text"),
            record.get("text"),
            getInt(record, "qtr"),
            convertToSeconds(record.get("time")),
            record.get("athlete_name_1"),
            record.get("athlete_name_2"),
            record.get("athlete_name_3"),
            getInt(record, "athlete_id_1"),
            getInt(record, "athlete_id_2"),
            getInt(record, "athlete_id_3"),
            getInt(record, "away_score"),
            getInt(record, "home_score"),
            record.get("shooting_play"),
            getInt(record, "shot_made") > 0,
            getInt(record, "distance"),
            getInt(record, "shooting_foul_commited") > 0,
            getInt(record, "offensive_rebound") > 0,
            getInt(record, "defensive_rebound") > 0,
            getInt(record, "personal_foul") > 0,
            getInt(record, "free_throw") > 0,
            getInt(record, "shooting_foul_drawn_athlete2") > 0,
            getInt(record, "Steal_athlete2") > 0,
            getInt(record, "block_athlete2") > 0,
            record.get("type_abbreviation"),
            record.get("home_display_name"),
            record.get("away_display_name"));
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

    private int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Creates a designed Play based on multiple labeled plays, and fills the required fields in the play object.
     * @param playOne Play one of the design
     * @param playTwo Play two of the design
     * @param playThree Play three of the design
     * @param matchup Matchup to look at for getting initial starters
     * @return A Play Object designed.
     */
    private Play designPlay(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup){
        Play play;
        List<Player> fiveOnCourtHome;
        List<Player> fiveOnCourtAway;
        if (matchup.getPlayByPlays().isEmpty()){
            fiveOnCourtHome = matchup.getHomeStarters();
            fiveOnCourtAway = matchup.getAwayStarters();
        }else{
            List<Play> recordedPlays = matchup.getPlayByPlays();
            fiveOnCourtHome = recordedPlays.getLast().getFiveOnCourtHome();
            fiveOnCourtAway = recordedPlays.getLast().getFiveOnCourtAway();
        }

    if (playOne.action().getDescription().contains("Shot")) {
      if (playOne.shotMade()) {
        if ((playTwo.shootingFoulCommitted() || playTwo.personalFoul())
            && (playOne.time() - playTwo.time()) < 4) {
          if (playOne.text().contains("assists")) {
            play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT_FOUL, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
            play.setPlayerAssisted(matchup.findPlayerObject(playOne.athleteOneID()));
            play.setPlayerAssisting(matchup.findPlayerObject(playOne.athleteTwoID()));
            play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
            play.setDistance(playOne.distance());
            play.setShotType(playOne.action());
            play.setFoulCommitter(matchup.findPlayerObject(playTwo.athleteTwoID()));
            return play;
          }
            play = new Play(matchup, PlayTypes.MADE_SHOT_FOUL, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
            play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
            play.setDistance(playOne.distance());
            play.setShotType(playOne.action());
            play.setFoulCommitter(matchup.findPlayerObject(playTwo.athleteTwoID()));
            return play;
        }
          play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
          play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
          play.setDistance(playOne.distance());
          play.setShotType(playOne.action());
          return play;
      }
            }
        if (playOne.action() == Actions.JUMP_BALL){
             play = new Play(matchup, PlayTypes.JUMPBALL, List.of(playOne), fiveOnCourtHome, fiveOnCourtAway);
            play.setJumperOne(matchup.findPlayerObject(playOne.athleteOneID()));
            play.setJumperTwo(matchup.findPlayerObject(playOne.athleteTwoID()));
            play.setJumperReceiver(matchup.findPlayerObject(playOne.athleteThreeID()));
            return play;
        }
        return new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, List.of(playOne, playTwo, playThree), fiveOnCourtHome, fiveOnCourtAway);
    }


    /**
     * Checks to see if the spot in the CSV has a piece of information that can be grabbed
     * @param record Row/ CSVRecord to look at
     * @param key column to look at in that specified row/CSVRecord.
     * @return true if there is a value; false otherwise.
     */
    private boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

    private int convertToSeconds(String time){
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }
}
