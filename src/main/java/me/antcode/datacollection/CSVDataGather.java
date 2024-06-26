package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.*;
import me.antcode.plays.freethrows.FreeThrow;
import me.antcode.plays.freethrows.TechnicalFreeThrow;
import me.antcode.plays.rebounds.DefensivePlayerRebound;
import me.antcode.plays.rebounds.DefensiveTeamRebound;
import me.antcode.plays.rebounds.OffensivePlayerRebound;
import me.antcode.plays.rebounds.OffensiveTeamRebound;
import me.antcode.plays.shots.DrivingDunkShot;
import me.antcode.plays.shots.JumpShot;
import me.antcode.plays.shots.PullupJumpShot;
import me.antcode.plays.shots.Shot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Record to gather data from CSV file
 * @param matchupsCSVPath Pathway of matchupsCSV in project
 * @param playByPlayCSVPath Pathway of playByPlayCSV in project
 */
public record CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath) {

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

    public List<Play> extractPlayByPlay(Matchup matchup) {
        List<Play> totalPlays = new ArrayList<>();
        int rowCount = 0; // Variable to keep track of the row number

        try (Reader reader = new FileReader(playByPlayCSVPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord csvRecord : csvParser) {
                Play play = createPlayFromRecord(csvRecord, matchup);
                if (play != null) {
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
     * Creates the proper play based on the gameID of the matchup
     * @param record Row to look at
     * @param matchup Matchup to use for gathering information
     * @return Properly identified play; Null if it is not a play or unidentified still.
     */
    private Play createPlayFromRecord(CSVRecord record, Matchup matchup){
        int gameId = parseInt(record.get("game_id"));
        if (gameId == matchup.getGameID()){
            String type = record.get("type_text");
            //If the play was a shooting play
            if (record.get("shooting_play").equals("TRUE")){
                Shot shot;
                //Switches between all different types of shooting plays
                switch (type){
          case "Driving Dunk Shot" -> shot = new DrivingDunkShot(matchup, record);
          case "Jump Shot" -> shot = new JumpShot(matchup, record);
          case "Pullup Jump Shot" -> shot = new PullupJumpShot(matchup, record);
                    default -> shot = new Shot(matchup, record);
        }

        //Checks to see if shot was blocked
                if (parseInt(record.get("block_athlete2")) > 0){
                    return new Block(shot);
                }
        //Checks to see if shot had an assist
                if (record.get("text").contains("assists")){
                    return new Assist(shot);
                }
        //Checks to see if shot was a free Throw. If so, was it technical
                if (type.contains("Free Throw")){
                    if (type.contains("Technical")){
                        return new TechnicalFreeThrow(matchup, record);
          } else {
            return new FreeThrow(matchup, record);
                    }
                }
                return shot;
            }
            //If shot was not a shooting play, switch through these
            switch (type){
                case "Jumpball" -> {
                    return new JumpBall(matchup, record);
                }
                case "Offensive Rebound" -> {
                    if (rowHasValue(record, "athlete_id_1")){
                        return new OffensivePlayerRebound(matchup, record);
                    }else{
                        return new OffensiveTeamRebound(matchup, record);
                    }
                }
                case "Defensive Rebound" -> {
                    if (rowHasValue(record, "athlete_id_1")){
                        return new DefensivePlayerRebound(matchup, record);
                    }else{
                        return new DefensiveTeamRebound(matchup, record);
                    }
                }

            }



        }
        return null;

    }







    /**
     * Changes a String value to an integer value.
     * @param value String to get integer from.
     * @return value of String as integer; Otherwise returns 0;
     */
    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
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
}
