package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class SubstitutionManager extends Manager {

  /**
   * Creates a substitution play.
   * @param matchup The matchup object containing labeled plays.
   * @param lPlayOne The labeled play.
   */
  public void createSubstitutionPlays(Matchup matchup, LabeledPlay lPlayOne) {
    //        try {
    //            if (homeOnCourt.contains(matchup.findPlayerObject(lPlayOne.getMultUsePlayer()))){
    //
    // homeOnCourt.set(homeOnCourt.indexOf(matchup.findPlayerObject(lPlayOne.getMultUsePlayer())),
    // matchup.findPlayerObject(lPlayOne.getEnteredPlayer()));
    //            }else{
    //                if
    // (awayOnCourt.contains(matchup.findPlayerObject(lPlayOne.getMultUsePlayer()))){
    //
    // awayOnCourt.set(awayOnCourt.indexOf(matchup.findPlayerObject(lPlayOne.getMultUsePlayer())),
    // matchup.findPlayerObject(lPlayOne.getEnteredPlayer()));
    //                }else{
    //                    for (Player player : awayOnCourt) {
    //                        System.out.print(player.getName() + ", ");
    //                    }
    //          System.out.println("--");
    //          System.out.println(lPlayOne);
    //                }
    //            }

    Play play = new Play(matchup, PlayTypes.SUBSTITUTION, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
    matchup.getPlayByPlays().add(play);
    //        } catch (NullPointerException e) {
    //            System.out.println(
    //                    lPlayOne.getGameID()
    //                            + " | "
    //                            + lPlayOne.getGamePlayNumber()
    //                            + " | "
    //                            + lPlayOne.getEventTypeText()
    //                            + " | "
    //                            + lPlayOne.getText()
    //                            + " | "
    //                            + lPlayOne.getAction());
    //        }
    //    }
  }
}
