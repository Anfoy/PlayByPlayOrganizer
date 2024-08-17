package me.antcode;

import me.antcode.datacollection.StatBreakdown;
import me.antcode.datacollection.DebugPlayerStat;
import me.antcode.datacollection.StatBreakdown;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPlayerStats {

  private final Map<String, CSVRecord> recordMap = new HashMap<>();

  private final List<DebugPlayerStat> debugStats = new ArrayList<>();

  StatBreakdown assistsAnalyze = new StatBreakdown("assists");
  StatBreakdown reboundAnalyze = new StatBreakdown("rebounds");
  StatBreakdown blocksAnalyze = new StatBreakdown("blocks");
  StatBreakdown stealsAnalyze = new StatBreakdown("steals");
  StatBreakdown turnoversAnalyze = new StatBreakdown("turnovers");
  StatBreakdown pointsAnalyze = new StatBreakdown("points");
  StatBreakdown foulsAnalyze = new StatBreakdown("fouls");
  StatBreakdown field_goals_madeAnalyze = new StatBreakdown("field_goals_made");
  StatBreakdown field_goals_attemptedAnalyze = new StatBreakdown("field_goals_attempted");
  StatBreakdown three_point_field_goals_madeAnalyze = new StatBreakdown("three_point_field_goals_made");
  StatBreakdown three_point_field_goals_attemptedAnalyze = new StatBreakdown("three_point_field_goals_attempted");
  StatBreakdown minutesAnalyze = new StatBreakdown("minutes");


  private final ArrayList<StatBreakdown> allStats = new ArrayList<>();
  public TestPlayerStats(){
    allStats.add(assistsAnalyze);
    allStats.add(reboundAnalyze);
    allStats.add(blocksAnalyze);
    allStats.add(stealsAnalyze);
    allStats.add(turnoversAnalyze);
    allStats.add(pointsAnalyze);
    allStats.add(foulsAnalyze);
    allStats.add(field_goals_madeAnalyze);
    allStats.add(field_goals_attemptedAnalyze);
    allStats.add(three_point_field_goals_madeAnalyze);
    allStats.add(minutesAnalyze);
    allStats.add(three_point_field_goals_attemptedAnalyze);
  }
  public void loadCSV() {
    try (Reader reader = new FileReader("src/main/java/me/antcode/teambox.csv");
         CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

      for (CSVRecord csvRecord : csvParser) {
        String key = csvRecord.get("game_id") + "-" + csvRecord.get("athlete_id");
        recordMap.put(key, csvRecord);
      }
    } catch (IOException e) {
      System.out.println("Failed to read file.");
      e.printStackTrace();
    }
  }
  public void checkPlayerStatsForMatchup(Matchup matchup) {
    double greatestTimeDiff = 0;
    for (Player player : matchup.getTotalPlayers()) {
      String key = matchup.getStatSheetID() + "-" + player.getId();
      CSVRecord csvRecord = recordMap.get(key);
      if (csvRecord != null) {
        int rebounds = getInt(csvRecord, "rebounds");
        int assists = getInt(csvRecord, "assists");
        int blocks = getInt(csvRecord, "blocks");
        int steals = getInt(csvRecord, "steals");
        int turnovers = getInt(csvRecord, "turnovers");
        int points = getInt(csvRecord, "points");
        int fouls = getInt(csvRecord, "fouls");
        int minutes = getInt(csvRecord, "minutes");
        int field_goals_made = getInt(csvRecord, "field_goals_made");
        int field_goals_attempted = getInt(csvRecord, "field_goals_attempted");
        int three_point_field_goals_made = getInt(csvRecord, "three_point_field_goals_made");
        int three_point_field_goals_attempted =
                getInt(csvRecord, "three_point_field_goals_attempted");

        if (rebounds != player.getRebounds()) {
          reboundAnalyze.identifySplit(player.getRebounds(), rebounds, player);
          debugStats.add(
                  new DebugPlayerStat(player, "REBOUNDS", player.getRebounds(), getInt(csvRecord, "rebounds"), matchup.getGameID(), matchup));
        }
        if (assists != player.getAssists()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "ASSISTS",
                          player.getAssists(),
                          getInt(csvRecord, "assists"),
                          matchup.getGameID(), matchup));
          assistsAnalyze.identifySplit(player.getAssists(), assists, player);
        }
        if (blocks != player.getBlocks()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "BLOCKS",
                          player.getBlocks(),
                          getInt(csvRecord, "blocks"),
                          matchup.getGameID(), matchup));
          blocksAnalyze.identifySplit(player.getBlocks(), blocks, player);
        }
        if (steals != player.getSteals()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "STEALS",
                          player.getSteals(),
                          getInt(csvRecord, "steals"),
                          matchup.getGameID(), matchup));
          stealsAnalyze.identifySplit(player.getSteals(), steals, player);
        }
        if (turnovers != player.getTurnovers()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "TURNOVERS",
                          player.getTurnovers(),
                          getInt(csvRecord, "turnovers"),
                          matchup.getGameID(), matchup));
          turnoversAnalyze.identifySplit(player.getTurnovers(), turnovers, player);
        }
        if (points != player.getPoints()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "POINTS",
                          player.getPoints(),
                          getInt(csvRecord, "points"),
                          matchup.getGameID(), matchup));
          pointsAnalyze.identifySplit(player.getPoints(), points, player);
        }
        if (fouls != player.getFouls()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "FOULS",
                          player.getFouls(),
                          getInt(csvRecord, "fouls"),
                          matchup.getGameID(), matchup));
          foulsAnalyze.identifySplit(player.getFouls(), fouls, player);
        }
        if (minutes != convertSecondsToMinutes(player.getMinutes())) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "MINUTES",
                          convertSecondsToMinutes(player.getMinutes()),
                          getInt(csvRecord, "minutes"),
                          matchup.getGameID(), matchup));
          minutesAnalyze.identifySplit(convertSecondsToMinutes(player.getMinutes()), minutes, player);
        }
        if (convertMinutesToSeconds(minutes) - player.getMinutes() > greatestTimeDiff) {
          greatestTimeDiff = convertMinutesToSeconds(minutes) - player.getMinutes();
        }
        if (field_goals_made != player.getFieldGoalsMade()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "FGM",
                          player.getFieldGoalsMade(),
                          getInt(csvRecord, "field_goals_made"),
                          matchup.getGameID(), matchup));
          field_goals_madeAnalyze.identifySplit(
                  player.getFieldGoalsMade(), field_goals_made, player);
        }
        if (field_goals_attempted != player.getFieldGoalsAttempted()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "FGA",
                          player.getFieldGoalsAttempted(),
                          getInt(csvRecord, "field_goals_attempted"),
                          matchup.getGameID(), matchup));
          field_goals_attemptedAnalyze.identifySplit(
                  player.getFieldGoalsAttempted(), field_goals_attempted, player);
        }
        if (three_point_field_goals_made != player.getThreePointFieldGoalsMade()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "3PM",
                          player.getThreePointFieldGoalsMade(),
                          getInt(csvRecord, "three_point_field_goals_made"),
                          matchup.getGameID(), matchup));
          three_point_field_goals_madeAnalyze.identifySplit(
                  player.getThreePointFieldGoalsMade(), three_point_field_goals_made, player);
        }
        if (three_point_field_goals_attempted != player.getThreePointFieldGoalsAttempted()) {
          debugStats.add(
                  new DebugPlayerStat(
                          player,
                          "3PA",
                          player.getThreePointFieldGoalsAttempted(),
                          getInt(csvRecord, "three_point_field_goals_attempted"),
                          matchup.getGameID(), matchup));
          three_point_field_goals_attemptedAnalyze.identifySplit(player.getThreePointFieldGoalsAttempted(), three_point_field_goals_attempted, player);
        }
      } else {
        System.out.println(
                "No data found for player ID "
                        + player.getId()
                        + " in game ID "
                        + matchup.getStatSheetID());
      }
    }
  }
  //      System.out.println("Greatest time Diff: " + greatestTimeDiff);


  private int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0; // or some default value or throw an exception
    }
  }

  private int getInt(CSVRecord record, String value) {
    return parseInt(record.get(value));
  }

  private int convertSecondsToMinutes(double seconds) {
    return (int) (seconds / 60);
  }

  private double convertMinutesToSeconds(int minutes) {
    return (minutes * 60);
  }

  public StatBreakdown getAssistsAnalyze() {
    return assistsAnalyze;
  }

  public StatBreakdown getReboundAnalyze() {
    return reboundAnalyze;
  }

  public StatBreakdown getBlocksAnalyze() {
    return blocksAnalyze;
  }

  public StatBreakdown getStealsAnalyze() {
    return stealsAnalyze;
  }

  public StatBreakdown getTurnoversAnalyze() {
    return turnoversAnalyze;
  }

  public StatBreakdown getPointsAnalyze() {
    return pointsAnalyze;
  }

  public StatBreakdown getFoulsAnalyze() {
    return foulsAnalyze;
  }

  public StatBreakdown getField_goals_madeAnalyze() {
    return field_goals_madeAnalyze;
  }

  public StatBreakdown getField_goals_attemptedAnalyze() {
    return field_goals_attemptedAnalyze;
  }

  public StatBreakdown getThree_point_field_goals_madeAnalyze() {
    return three_point_field_goals_madeAnalyze;
  }

  public ArrayList<StatBreakdown> getAllStats() {
    return allStats;
  }

  public List<DebugPlayerStat> getDebugStats() {
    return debugStats;
  }
}
