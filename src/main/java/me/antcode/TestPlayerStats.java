package me.antcode;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TestPlayerStats {

    private final Map<String, CSVRecord> recordMap = new HashMap<>();

    public void loadCSV() {
        try (Reader reader = new FileReader("src/main/java/me/antcode/TempBoxScore.csv");
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
        String key = matchup.getGameID() + "-" + player.getId();
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
            System.out.println(player.getName() + " failed rebound check");
            System.out.println("Actual Rebounds: " + getInt(csvRecord, "rebounds"));
            System.out.println("Recorded Rebounds: " + player.getRebounds());
          }
          if (assists != player.getAssists()) {
            System.out.println(player.getName() + " failed assist check");
            System.out.println("Actual Assists: " + getInt(csvRecord, "assists"));
            System.out.println("Recorded Assists: " + player.getAssists());
          }
          if (blocks != player.getBlocks()) {
            System.out.println(player.getName() + " failed block check");
            System.out.println("Actual Blocks: " + getInt(csvRecord, "blocks"));
            System.out.println("Recorded Blocks: " + player.getBlocks());
          }
          if (steals != player.getSteals()) {
            System.out.println(player.getName() + " failed steal check");
            System.out.println("Actual Steals: " + getInt(csvRecord, "steals"));
            System.out.println("Recorded Steals: " + player.getSteals());
          }
          if (turnovers != player.getTurnovers()) {
            System.out.println(player.getName() + " failed turnover check");
            System.out.println("Actual Turnovers: " + getInt(csvRecord, "turnovers"));
            System.out.println("Recorded Turnovers: " + player.getTurnovers());
          }
          if (points != player.getPoints()) {
            System.out.println(player.getName() + " failed point check");
            System.out.println("Actual Points: " + getInt(csvRecord, "points"));
            System.out.println("Recorded Points: " + player.getPoints());
          }
          if (fouls != player.getFouls()) {
            System.out.println(player.getName() + " failed fouls check");
            System.out.println("Actual Fouls: " + getInt(csvRecord, "fouls"));
            System.out.println("Recorded Fouls: " + player.getFouls());
          }
//          if (minutes != convertSecondsToMinutes(player.getMinutes())) {
//            System.out.println(player.getName() + " failed minutes check");
//            System.out.println("Actual Minutes: " + getInt(csvRecord, "minutes"));
//            System.out.println("Recorded Minutes: " + convertSecondsToMinutes(player.getMinutes()));
//          }
          if (convertMinutesToSeconds(minutes) - player.getMinutes() > greatestTimeDiff){
            greatestTimeDiff = convertMinutesToSeconds(minutes) - player.getMinutes();
          }
          if (field_goals_made != player.getFieldGoalsMade()) {
            System.out.println(player.getName() + " failed field_goals_made check");
            System.out.println("Actual Field Goals Made: " + getInt(csvRecord, "field_goals_made"));
            System.out.println("Recorded Field Goals Made: " + player.getFieldGoalsMade());
          }
          if (field_goals_attempted != player.getFieldGoalsAttempted()) {
            System.out.println(player.getName() + " failed field_goals_attempted check");
            System.out.println(
                "Actual Field Goals Attempted: " + getInt(csvRecord, "field_goals_attempted"));
            System.out.println(
                "Recorded Field Goals Attempted: " + player.getFieldGoalsAttempted());
          }
          if (three_point_field_goals_made != player.getThreePointFieldGoalsMade()) {
            System.out.println(player.getName() + " failed three_point_field_goals_made check");
            System.out.println(
                "Actual Three Point Field Goals Made: "
                    + getInt(csvRecord, "three_point_field_goals_made"));
            System.out.println(
                "Recorded Three Point Field Goals Made: " + player.getThreePointFieldGoalsMade());
          }
          if (three_point_field_goals_attempted != player.getThreePointFieldGoalsAttempted()) {
            System.out.println(
                player.getName() + " failed three_point_field_goals_attempted check");
            System.out.println(
                "Actual Three Point Field Goals Attempted: "
                    + getInt(csvRecord, "three_point_field_goals_attempted"));
            System.out.println(
                "Recorded Three Point Field Goals Attempted: "
                    + player.getThreePointFieldGoalsAttempted());
          }
        } else {
          System.out.println(
              "No data found for player ID "
                  + player.getId()
                  + " in game ID "
                  + matchup.getGameID());
        }
      }
      System.out.println("Greatest time Diff: " + greatestTimeDiff);
        }

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
}
