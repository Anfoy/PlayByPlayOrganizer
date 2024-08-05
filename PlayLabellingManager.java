package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayLabellingManager extends Manager {

    /**
     * Goes through each record for a specific matchup and labels it appropriately for future play design.
     */


    public List<LabeledPlay> labelAllPlays(String playByPlayCSVPath){
        List<LabeledPlay> allPlaysLabeled = new ArrayList<>();
        try (Reader reader = new FileReader(playByPlayCSVPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
            int count = 0;
            for (CSVRecord csvRecord : csvParser) {
                count++;
                Actions action = determineAction(csvRecord);
                System.out.println("Creating Labeled Play " + count);
                allPlaysLabeled.add(createLabeledPlay(csvRecord, action));
            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
        return allPlaysLabeled;
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
            case String t when t.contains("timeout") || t.contains("time out") -> Actions.TIMEOUT;
            case String t when t.contains("jumpball") || t.contains("jump ball") -> Actions.JUMPBALL;
            case String t when t.contains("ejection") || csvRecord.get("text").contains("ejection") -> Actions.EJECTION;
            default -> {
                if (csvRecord.get("ignore").equals("true") || type.contains("no foul")){
                    yield Actions.IGNORE;
                }
                else if (type.contains("free throw") && csvRecord.get("text").contains("shooting foul"))
                {
                    yield Actions.FOUL;
                }
                else if (type.contains("shooting foul") && (csvRecord.get("text").contains("makes") || csvRecord.get("text").contains("misses")))
                { //For niche case of shot being registered as foul for some reason
                    yield determineShootingAction(csvRecord);
                }
//                else if ((csvRecord.get("text").contains("makes") || csvRecord.get("text").contains("misses"))
//                        && csvRecord.get("shooting_play").equals("TRUE") && !type.contains("free throw"))
//                    {
//                    yield determineShootingAction(csvRecord);
//                }
                else if (type.contains("shot") && csvRecord.get("text").contains("free throw"))
                {
                    yield Actions.FREE_THROW;
                }
                else if (type.contains("free throw"))
                {
                    yield Actions.FREE_THROW;
                }
                else if (csvRecord.get("wasTeam").equals("true"))
                {
                    yield Actions.TEAM;
                }
                else if (csvRecord.get("rebound").equals("true"))
                {
                    yield Actions.REBOUND;
                }
                else if (type.contains("rebound") && (csvRecord.get("text").contains("makes") || csvRecord.get("text").contains("misses")))
                {
                    yield Actions.SHOT;
                }
                else if (type.contains("rebound") && (csvRecord.get("text").contains("blocks")))
                {
                    yield Actions.BLOCK;
                }
                else if (type.contains("offensive rebound") || type.contains("defensive rebound"))
                {
                    yield Actions.REBOUND;
                }else if (type.contains("rebound"))
                {
                    yield Actions.TEAM;
                }
                else if (csvRecord.get("shooting_play").equalsIgnoreCase("true"))
                {
                    yield determineShootingAction(csvRecord);
                }
                else if (csvRecord.get("foul").equals("true") || type.contains("charge") || csvRecord.get("text").contains("charge"))
                {
                    yield csvRecord.get("flagrant").equals("true") ? Actions.FLAGRANT_FOUL : Actions.FOUL;
                }
                else if (type.contains("shooting foul") || type.contains("technical foul"))
                {
                    yield Actions.FOUL;
                }
                else if (csvRecord.get("turnover").equals("true"))
                {
                    yield Actions.TURNOVER;
                }
                else if (csvRecord.get("violation").equals("true"))
                {
                    yield Actions.VIOLATION;
                }
                else if (csvRecord.get("technical").equals("true"))
                {
                    yield Actions.FOUL;
                }
                else {
                    yield Actions.UNKNOWN;
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
        if (csvRecord.get("assist").equals("true") || csvRecord.get("text").toLowerCase().contains("assists")) {
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
                getInt(csvRecord, "game_id"),
                getInt(csvRecord, "qtr"),
                convertToSeconds(csvRecord.get("time")),
                getInt(csvRecord, "away_score"),
                getInt(csvRecord, "home_score"),
                getAthleteID(csvRecord, 1),
                getAthleteID(csvRecord, 2),
                getAthleteID(csvRecord, 3),
                csvRecord.get("type_text").toLowerCase(), csvRecord.get("text").toLowerCase(),
                getInt(csvRecord, "shot_made") > 0,
                getInt(csvRecord, "Steal_athlete2") > 0,
                getInt(csvRecord, "distance"),
                csvRecord.get("offensive").equals("true"),
                !csvRecord.get("offensive").equals("true"),
                getInt(csvRecord, "game_play_number"),
                csvRecord.get("flagrant").equals("true"),
                getInt(csvRecord, "distance") > 22 || description.contains("three point"),
                action
        );
    }

    public void printLabeledPlaysForMatchup(int gameID, List<Matchup> matchups){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
                System.out.println("------------------------");
                System.out.println(labeledPlay);
                System.out.println("ID ONE: " + labeledPlay.getAthlete_id_1());
                System.out.println("ID TWO: " + labeledPlay.getAthlete_id_2());
                System.out.println("ID THREE: " + labeledPlay.getAthlete_id_3());
                System.out.println("------------------------");
            }
        }
    }

    public void printSpecificLabeledPlaysForMatchup(int gameID, Actions actions, List<Matchup> matchups){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
                if (labeledPlay.getAction() != actions) continue;
                System.out.println("------------------------");
                System.out.println(labeledPlay);
                System.out.println("------------------------");
            }
        }
    }

    public void printPlayerInvolvedLabeledPlaysForMatchup(int gameID, int playerID, List<Matchup> matchups){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
                if (labeledPlay.getAthlete_id_1() != playerID && labeledPlay.getAthlete_id_2() != playerID && labeledPlay.getAthlete_id_3() != playerID) continue;
                System.out.println("------------------------");
                System.out.println(labeledPlay);
                System.out.println("------------------------");
            }
        }
    }

    public void printPlayerInvolvedPlaysForMatchup(int gameID, int playerID, List<Matchup> matchups){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (Play play : matchup.getPlayByPlays()){
                for (LabeledPlay labeledPlay : play.getMakeUpOfPlay()){
                    if (labeledPlay.getAthlete_id_1() != playerID && labeledPlay.getAthlete_id_2() != playerID && labeledPlay.getAthlete_id_3() != playerID) continue;
                    System.out.println(play.getPlayType());
                    System.out.println("------------------------");
                    for (LabeledPlay labeledPlay1 : play.getMakeUpOfPlay()){
                        System.out.println(labeledPlay1);
                        System.out.println("PLAYER ONE ID: " + labeledPlay1.getAthlete_id_1());
                        System.out.println("PLAYER TWO ID: " + labeledPlay1.getAthlete_id_2());
                        System.out.println("PLAYER THREE: " + labeledPlay1.getAthlete_id_3());
                    }
                    System.out.println("------------------------");
                    break;
                }
            }
        }
    }

    public void printPlaysForMatchup(List<Matchup> matchups, int gameID){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (Play play : matchup.getPlayByPlays()){
                System.out.println(play.getPlayType());
                System.out.println("------------------------");
                for (LabeledPlay labeledPlay1 : play.getMakeUpOfPlay()){
                    System.out.println(labeledPlay1);
                    System.out.println("PLAYER ONE ID: " + labeledPlay1.getAthlete_id_1());
                    System.out.println("PLAYER TWO ID: " + labeledPlay1.getAthlete_id_2());
                    System.out.println("PLAYER THREE: " + labeledPlay1.getAthlete_id_3());
                }
                System.out.println("------------------------");
            }
        }
    }

    public void printPlayerInvolvedSpecificLabeledPlaysForMatchup(int gameID, int playerID, List<Matchup> matchups, Actions action){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getLabeledPlayList()){
                if (labeledPlay.getAthlete_id_1() != playerID && labeledPlay.getAthlete_id_2() != playerID && labeledPlay.getAthlete_id_3() != playerID) continue;
                if (action != labeledPlay.getAction()) continue;
                System.out.println("------------------------");
                System.out.println(labeledPlay);
                System.out.println("------------------------");
            }
        }
    }

    public Matchup generateSpecificMatchupPlays(String playByPlayCSVPath, int gameID, List<Matchup> matchups){
        List<LabeledPlay> allPlaysLabeled = new ArrayList<>();
        try (Reader reader = new FileReader(playByPlayCSVPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
            for (CSVRecord csvRecord : csvParser) {
                if (getInt(csvRecord, "game_id") != gameID) continue;
                Actions action = determineAction(csvRecord);
                allPlaysLabeled.add(createLabeledPlay(csvRecord, action));
            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
        allPlaysLabeled.sort(Comparator
                .comparingInt(LabeledPlay::getGameID)
                .thenComparingInt(LabeledPlay::getGamePlayNumber));
        int count = 0;
        Matchup desiredMatchup = null;
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            desiredMatchup = matchup;
            break;
        }
        for (LabeledPlay labeledPlay : allPlaysLabeled){
            if (labeledPlay.getAction() != Actions.UNKNOWN && labeledPlay.getAction() != Actions.IGNORE){
                desiredMatchup.getLabeledPlayList().add(labeledPlay);
            }
        }
        return desiredMatchup;
    }
}
