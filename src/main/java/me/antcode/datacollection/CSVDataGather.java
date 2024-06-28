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
                if (parseInt(csvRecord.get("game_id")) != matchup.getGameID()) continue;
                LabeledPlay play = createLabeledPlay(csvRecord, matchup);
        if (play.action() != Actions.TIMEOUT && play.action() != Actions.COACHCHALLENGE
        && play.action() != Actions.END_GAME && play.action() != Actions.END_PERIOD && play.action() != Actions.REF_REVIEW
        && play.action() != Actions.UNAVAILABLE) {
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
    public List<Play> setPlayByPlayList(Matchup matchup) {
        List<LabeledPlay> playsToEvaluate = matchup.getLabeledPlayList();
        List<Play> totalPlays = new ArrayList<>();
        int index = 0;

        while (index < playsToEvaluate.size()) {
            List<LabeledPlay> playsForDesign = new ArrayList<>(); // Reinitialize inside the loop

            playsForDesign.add(playsToEvaluate.get(index));
            if (index + 1 < playsToEvaluate.size()) {
                playsForDesign.add(playsToEvaluate.get(index + 1));
            }
            if (index + 2 < playsToEvaluate.size()) {
                playsForDesign.add(playsToEvaluate.get(index + 2));
            }

            Play play = designPlay(playsForDesign, matchup);
            if (play.getPlayType().getPartsRequired() == 1){
                index += 1;
            }else if (play.getPlayType().getPartsRequired() == 2){
                index += 2;
            }else {
                index += 3;
            }
            totalPlays.add(play);
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
     * Checks to see if the spot in the CSV has a piece of information that can be grabbed
     * @param record Row/ CSVRecord to look at
     * @param key column to look at in that specified row/CSVRecord.
     * @return true if there is a value; false otherwise.
     */
    private boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

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


    private Play designPlay(List<LabeledPlay> threePlays, Matchup matchup) {
        List<Player> fiveOnCourtHome = getCurrentPlayersOnCourt(matchup, true);
        List<Player> fiveOnCourtAway = getCurrentPlayersOnCourt(matchup, false);
        LabeledPlay playOne = threePlays.getFirst();
        LabeledPlay playTwo = null; //establish as Null. If the play requires two plays, this should never be null as it goes through the checks
        LabeledPlay playThree = null; //establish as Null. If the play requires three plays, this should never be null as it goes through the checks
        if (threePlays.size() > 1){
            playTwo = threePlays.get(1);
            if (threePlays.size() > 2){
                playThree = threePlays.get(2);
            }
        }
        if (isShot(playOne)) {
            return createShotPlay(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }

        if (playOne.action() == Actions.JUMP_BALL) {
            return createJumpBallPlay(playOne, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }

        return createUnidentifiedPlay(threePlays, matchup, fiveOnCourtHome, fiveOnCourtAway);
    }

    private Play createShotPlay(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        if (playOne.shotMade()) {
            return handleMadeShot(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway);
        } else if (!playOne.shotMade()){
            return handleMissedShot(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }
        return null;
    }

    private Play handleMadeShot(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        if (playOne.text().contains("assists")) {
            if (isFoulWithinTime(playOne, playTwo, playThree)) {
                return createAssistMadeShotFoulPlay(playOne, playTwo, matchup, fiveOnCourtHome, fiveOnCourtAway);
            }
            return createAssistMadeShotPlay(playOne, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }
        if (isFoulWithinTime(playOne, playTwo, playThree)) {
            return createMadeShotFoulPlay(playOne, playTwo, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }
        return createMadeShotPlay(playOne, matchup, fiveOnCourtHome, fiveOnCourtAway);
    }

        private Play handleMissedShot(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        if (playTwo != null && (playTwo.offensiveRebound() || playTwo.defensiveRebound())) {
            return handleMissedShotWithRebound(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }
        if (isFoulWithinTime(playOne, playTwo, playThree)) {
            return createMissedShotFoulPlay(playOne, playTwo, matchup, fiveOnCourtHome, fiveOnCourtAway);
        }
        return createMissedShotPlay(playOne, matchup, fiveOnCourtHome, fiveOnCourtAway);
    }

    private Play handleMissedShotWithRebound(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        if (isFoulWithinTime(playOne, playTwo, playThree)) {
            if (playTwo.offensiveRebound()) {
                return createMissedShotReboundFoulPlay(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway, PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND_FOUL);
            } else {
                return createMissedShotReboundFoulPlay(playOne, playTwo, playThree, matchup, fiveOnCourtHome, fiveOnCourtAway, PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND_FOUL);
            }
        } else {
            if (playTwo.offensiveRebound()) {
                return createMissedShotReboundPlay(playOne, playTwo, matchup, fiveOnCourtHome, fiveOnCourtAway, PlayTypes.MISSED_SHOT_OFFENSIVE_REBOUND);
            } else {
                return createMissedShotReboundPlay(playOne, playTwo, matchup, fiveOnCourtHome, fiveOnCourtAway, PlayTypes.MISSED_SHOT_DEFENSIVE_REBOUND);
            }
        }
    }

    private boolean isFoulWithinTime(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree) {
        if (playTwo != null && (playTwo.shootingFoulCommitted() || playTwo.personalFoul())) {
            return (playOne.time() - playTwo.time()) < 4;
        }
        if (playThree != null && (playThree.shootingFoulCommitted() || playThree.personalFoul())) {
            return (playOne.time() - playThree.time()) < 4;
        }
        return false;
    }


    private List<Player> getCurrentPlayersOnCourt(Matchup matchup, boolean isHome) {
        if (matchup.getPlayByPlays().isEmpty()) {
            return isHome ? matchup.getHomeStarters() : matchup.getAwayStarters();
        }
        List<Play> recordedPlays = matchup.getPlayByPlays();
        return isHome ? recordedPlays.getLast().getFiveOnCourtHome() : recordedPlays.getLast().getFiveOnCourtAway();
    }

    private boolean isShot(LabeledPlay play) {
        return play.action().getDescription().contains("Shot");
    }




    private Play createAssistMadeShotFoulPlay(LabeledPlay playOne, LabeledPlay playTwo, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT_FOUL, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerAssisted(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setPlayerAssisting(matchup.findPlayerObject(playOne.athleteTwoID()));
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        play.setFoulCommitter(matchup.findPlayerObject(playTwo.athleteTwoID()));
        return play;
    }

    private Play createAssistMadeShotPlay(LabeledPlay playOne, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.ASSIST_MADE_SHOT, List.of(playOne), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        play.setPlayerAssisted(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setPlayerAssisting(matchup.findPlayerObject(playOne.athleteTwoID()));
        return play;
    }

    private Play createMadeShotFoulPlay(LabeledPlay playOne, LabeledPlay playTwo, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.MADE_SHOT_FOUL, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        play.setFoulCommitter(matchup.findPlayerObject(playTwo.athleteTwoID()));
        play.setFoulType(playTwo.action());
        return play;
    }

    private Play createMissedShotFoulPlay(LabeledPlay playOne, LabeledPlay playTwo, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.MISSED_SHOT_FOUL, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        play.setFoulCommitter(matchup.findPlayerObject(playTwo.athleteTwoID()));
        play.setFoulType(playTwo.action());
        return play;
    }

    private Play createMissedShotReboundFoulPlay(LabeledPlay playOne, LabeledPlay playTwo, LabeledPlay playThree, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway, PlayTypes playTypes) {
        Play play = new Play(matchup, playTypes, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setRebounder(matchup.findPlayerObject(playTwo.athleteTwoID()));
        play.setWasOffensive(playTwo.offensiveRebound());
        play.setWasDefensive(playTwo.defensiveRebound());
        play.setReboundType(playTwo.action());
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        play.setFoulCommitter(matchup.findPlayerObject(playThree.athleteThreeID()));
        play.setFoulType(playThree.action());
        return play;
    }

    private Play createMissedShotReboundPlay(LabeledPlay playOne, LabeledPlay playTwo, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway, PlayTypes playTypes) {
        Play play = new Play(matchup, playTypes, List.of(playOne, playTwo), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setRebounder(matchup.findPlayerObject(playTwo.athleteTwoID()));
        play.setWasOffensive(playTwo.offensiveRebound());
        play.setWasDefensive(playTwo.defensiveRebound());
        play.setReboundType(playTwo.action());
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        return play;
    }

    private Play createMadeShotPlay(LabeledPlay playOne, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.MADE_SHOT, List.of(playOne), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setDistance(playOne.distance());
        play.setShotType(playOne.action());
        return play;
    }

    private Play createJumpBallPlay(LabeledPlay playOne, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        Play play = new Play(matchup, PlayTypes.JUMPBALL, List.of(playOne), fiveOnCourtHome, fiveOnCourtAway);
        play.setJumperOne(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setJumperTwo(matchup.findPlayerObject(playOne.athleteTwoID()));
        play.setJumperReceiver(matchup.findPlayerObject(playOne.athleteThreeID()));
        return play;
    }

    private Play createMissedShotPlay(LabeledPlay playOne, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway){
    Play play = new Play(matchup, PlayTypes.MISSED_SHOT, List.of(playOne), fiveOnCourtHome, fiveOnCourtAway);
        play.setPlayerShooting(matchup.findPlayerObject(playOne.athleteOneID()));
        play.setShotType(playOne.action());
        play.setDistance(playOne.distance());
        return play;
}
    private Play createUnidentifiedPlay(List<LabeledPlay> unknownPlays, Matchup matchup, List<Player> fiveOnCourtHome, List<Player> fiveOnCourtAway) {
        return new Play(matchup, PlayTypes.UNIDENTIFIEDPLAYTYPE, unknownPlays, fiveOnCourtHome, fiveOnCourtAway);
    }



}
