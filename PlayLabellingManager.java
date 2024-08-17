package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.TypesOfAction.Actions;
import me.antcode.datacollection.CSVUtils;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class PlayLabellingManager extends Manager {

    /**
     * Goes through each record for a specific matchup and labels it appropriately for future play design.
     */
private LinkedHashMap<CSVRecord, Actions> pbp = new LinkedHashMap<>();

  //    public void labelAllPlays(String playByPlayCSVPath, List<Matchup> matchups){
  //        int count = 0;
  //        try (Reader reader = new FileReader(playByPlayCSVPath);
  //             CSVParser csvParser = new CSVParser(reader,
  // CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
  //            for (CSVRecord csvRecord : csvParser) {
  //                count++;
  //                Matchup matchup = findCorrelatingMatchupWithID(CSVUtils.getDate(csvRecord),
  // CSVUtils.getAwayPlayerOne(csvRecord), matchups, CSVUtils.getGameID(csvRecord));
  //                if (matchup == null) continue;
  //                Actions action = determineAction(csvRecord);
  //        if (action != Actions.IGNORE
  //            && action != Actions.UNKNOWN) {
  //          System.out.println("added a play: " + count);
  //          matchup.getCsvRecordActionHashMap().put(csvRecord, action);
  //          matchup.getOrderedCSVRecords().add(csvRecord);
  //        }
  //            }
  //        } catch (IOException e) {
  //            System.out.println("Failed to read file.");
  //            e.printStackTrace();
  //        }
  //    }

  public LinkedHashMap<CSVRecord, Actions> labelAllPlays(String playByPlayCSVPath, Matchup matchup) {
    int count = 0;
    try (Reader reader = new FileReader(playByPlayCSVPath);
        CSVParser csvParser =
            new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        if (isMatchup(matchup, CSVUtils.getDate(csvRecord), CSVUtils.getAwayPlayerOne(csvRecord), CSVUtils.getGameID(csvRecord))) {
          Actions action = determineAction(csvRecord);
          if (action != Actions.IGNORE && action != Actions.UNKNOWN) {
            System.out.println("added a play: " + count);
            // Potentially write this to a file or database instead of keeping it all in memory
            pbp.put(csvRecord, action);
            count++;

          } else {
            if (count > 0) {
                break;
            }
          }

          // Optional: Periodically clear memory
        }
      }
        }catch (IOException e) {
        System.out.println("Failed to read file.");
        e.printStackTrace();
      }
      return pbp;
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
        }else if (eventType.equals("shot")){
            return determineShootingAction(csvRecord);
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
//
//    public void printLabeledPlaysForMatchup(int gameID, List<Matchup> matchups){
//        for (Matchup matchup : matchups){
//            if (matchup.getGameID() != gameID) continue;
//            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
//                System.out.println("------------------------");
//                System.out.println(labeledPlay);
//                System.out.println("------------------------");
//            }
//        }
//    }
//
//    public void printSpecificLabeledPlaysForMatchup(int gameID, Actions actions, List<Matchup> matchups){
//        for (Matchup matchup : matchups){
//            if (matchup.getGameID() != gameID) continue;
//            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
//                if (labeledPlay.getAction() != actions) continue;
//                System.out.println("------------------------");
//                System.out.println(labeledPlay);
//                System.out.println("------------------------");
//            }
//        }
//    }

//    public void printPlayerInvolvedLabeledPlaysForMatchup(int gameID, int playerID, List<Matchup> matchups){
//        for (Matchup matchup : matchups){
//            if (matchup.getGameID() != gameID) continue;
//            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
//                if (labeledPlay.getAthlete_id_1() != playerID && labeledPlay.getAthlete_id_2() != playerID && labeledPlay.getAthlete_id_3() != playerID) continue;
//                System.out.println("------------------------");
//                System.out.println(labeledPlay);
//                System.out.println("------------------------");
//            }
//        }
//    }
//
//    public void printPlayerInvolvedPlaysForMatchup(int gameID, int playerID, List<Matchup> matchups){
//        for (Matchup matchup : matchups){
//            if (matchup.getGameID() != gameID) continue;
//            for (Play play : matchup.getPlayByPlays()){
//                for (LabeledPlay labeledPlay : play.getMakeUpOfPlay()){
//                    if (labeledPlay.getAthlete_id_1() != playerID && labeledPlay.getAthlete_id_2() != playerID && labeledPlay.getAthlete_id_3() != playerID) continue;
//                    System.out.println(play.getPlayType());
//                    System.out.println("------------------------");
//                    for (LabeledPlay labeledPlay1 : play.getMakeUpOfPlay()){
//                        System.out.println(labeledPlay1);
//                        System.out.println("PLAYER ONE ID: " + labeledPlay1.getAthlete_id_1());
//                        System.out.println("PLAYER TWO ID: " + labeledPlay1.getAthlete_id_2());
//                        System.out.println("PLAYER THREE: " + labeledPlay1.getAthlete_id_3());
//                    }
//                    System.out.println("------------------------");
//                    break;
//                }
//            }
//        }
//    }
//
    public void printPlaysForMatchup(List<Matchup> matchups, int gameID){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (Play play : matchup.getPlayByPlays()){
                System.out.println(play.getPlayType());
                System.out.println("------------------------");
                for (LabeledPlay labeledPlay1 : play.getMakeUpOfPlay()){
                    System.out.println(labeledPlay1);
                }
                System.out.println("------------------------");
            }
        }
    }

    public void printPlayerInvolvedSpecificLabeledPlaysForMatchup(int gameID, List<Matchup> matchups, Actions action){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getAllLabeledPlays()){
                if (action != labeledPlay.getAction()) continue;
                System.out.println("------------------------");
                System.out.println(labeledPlay);
                System.out.println("------------------------");
            }
        }
    }

    public Matchup generateSpecificMatchupPlays(String playByPlayCSVPath, int gameID, List<Matchup> matchups){
        List<LabeledPlay> allPlaysLabeled = new ArrayList<>();
        File folder = new File(playByPlayCSVPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

    if (files != null) {
      for (File file : files) {
        System.out.println("Processing file: " + file.getName());
        try (Reader reader = new FileReader(file);
            CSVParser csvParser =
                new CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
          for (CSVRecord csvRecord : csvParser) {
            if (getInt(csvRecord, "game_id") != gameID) continue;
            Actions action = determineAction(csvRecord);
            LabeledPlay labeledPlay = createLabeledPlay(csvRecord, action);
            if (labeledPlay.getAction() != Actions.IGNORE
                && labeledPlay.getAction() != Actions.UNKNOWN) {
              allPlaysLabeled.add(labeledPlay);
            }
          }
        } catch (IOException e) {
          System.out.println("Failed to read file.");
          e.printStackTrace();
        }
      }
            }
        allPlaysLabeled.sort(Comparator
                .comparingInt(LabeledPlay::getGameID)
                .thenComparingInt(LabeledPlay::getGamePlayNumber));
        int count = 0;
        Matchup desiredMatchup = findCorrelatingMatchupWithID(allPlaysLabeled.getFirst().getDate(), allPlaysLabeled.getFirst().getAwayOnCourt().getFirst(), matchups, allPlaysLabeled.getFirst().getGameID());

        for (LabeledPlay labeledPlay : allPlaysLabeled){
            labeledPlay.setMatchup(desiredMatchup);
                desiredMatchup.getAllLabeledPlays().add(labeledPlay);
        }
        return desiredMatchup;
    }

}
