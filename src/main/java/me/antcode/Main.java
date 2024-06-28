package me.antcode;

import me.antcode.TypesOfAction.Actions;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String matchupPath = "src/main/java/me/antcode/Matchup1Month.csv";
        String playByPlayPath = "src/main/java/me/antcode/PlayByPlay1Month.csv";
        List<Matchup> allMatchups;
        CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
        allMatchups = csvDataGather.extractAllMatchups();
        for (Matchup matchup : allMatchups){
         matchup.setLabeledPlayList(csvDataGather.establishLabeledPlays(matchup));
        }
        for (Matchup matchup : allMatchups){
            matchup.setPlayByPlays(csvDataGather.setPlayByPlayList(matchup));
        }




        for (Matchup matchup : allMatchups){
      for (Play play : matchup.getPlayByPlays()) {
        if (play.getPlayType() != PlayTypes.UNIDENTIFIEDPLAYTYPE) {
          for (LabeledPlay labelPlays : play.getMakeUpOfPlay()){
            System.out.println(labelPlays.action() + " | " + labelPlays.gamePlayNumber() + " | " + labelPlays.gameID());
          }
          System.out.println("--------------");
        }
                }
        }

    }
}