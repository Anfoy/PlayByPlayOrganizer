package me.antcode;
import me.antcode.TypesOfAction.Actions;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.datacollection.DebugPlayerStat;
import me.antcode.datacollection.StatBreakdown;
import me.antcode.managers.PlayLabellingManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    // TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE
    // TIME.
    PlayLabellingManager playLabellingManager = new PlayLabellingManager();
    String matchupPath = "src/main/java/me/antcode/MATCHUPS_converted.csv";
    String playByPlayPath = "src/main/java/me/antcode/processedCSVs";
    List<Matchup> allMatchups;
    CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
    TestPlayerStats testPlayerStats = new TestPlayerStats();
    testPlayerStats.loadCSV();
    allMatchups = csvDataGather.getMatchups();
    allMatchups.sort((m1, m2) -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      try {
        Date date1 = dateFormat.parse(m1.getDate());
        Date date2 = dateFormat.parse(m2.getDate());
        return date1.compareTo(date2);
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    });
      csvDataGather.labelAllPlays(playByPlayPath);
    for (Matchup matchup : allMatchups){
      if (matchup.getPlayByPlays().isEmpty()) continue;
      testPlayerStats.checkPlayerStatsForMatchup(matchup);
    }

//         testPlayerStats.checkPlayerStatsForMatchup(csvDataGather.curateSpecificPlay(41500303));
//         playLabellingManager.printPlayerInvolvedSpecificLabeledPlaysForMatchup(41500303,
//     allMatchups, Actions.FOUL);
//         playLabellingManager.printPlaysForMatchup(allMatchups, 41500303);

    for (DebugPlayerStat debugPlayerStat : testPlayerStats.getDebugStats()) {
      if (!debugPlayerStat.getType().equals("MINUTES")) {
        if (debugPlayerStat.getRecordedAmount() == 0) {
          System.out.println(debugPlayerStat);
        }
        }
      }

    for (StatBreakdown statBreakdown : testPlayerStats.getAllStats()){
        System.out.println(statBreakdown);
    }
    csvDataGather.developPlayTypesCSV(allMatchups);
    csvDataGather.deployTestCSV(allMatchups);
    csvDataGather.deployMatchupAndPlayByPlayCSV(allMatchups);
  }
}


