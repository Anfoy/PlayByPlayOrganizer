package me.antcode.plays;


import me.antcode.TypesOfAction.Actions;


public record LabeledPlay(Actions action, int gameID, int quarter, double time, int awayScore, int homeScore,
                          int athlete_id_1, int athlete_id_2, int athlete_id_3, String typeText, boolean shotMade,
                          boolean wasSteal, int distance, boolean offensive, boolean defensive, int gamePlayNumber, boolean flagrant, boolean isThreePointer) {
    }
