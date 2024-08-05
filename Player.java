package me.antcode;

/**
 * Class representing a player with various statistics.
 */
public class Player {

    // Player's unique identifier
    private final int id;


    //ID for players that have multiple ids
    private int extraID;

    // Player's name
    private final String name;

    // Player's statistics
    private int rebounds;
    private int points;
    private int assists;
    private int blocks;
    private int steals;
    private int turnovers;
    private int fouls;
    private int fieldGoalsMade;
    private int fieldGoalsAttempted;
    private int threePointFieldGoalsMade;
    private int threePointFieldGoalsAttempted;
    private double minutes;

    /**
     * Constructor to initialize a player with id and name, and reset all statistics to zero.
     *
     * @param id   the player's unique identifier
     * @param name the player's name
     */
    public Player(int id, String name) {
        this.id = id;
        this.name = name;

        // Initialize all statistics to zero
        rebounds = 0;
        points = 0;
        assists = 0;
        blocks = 0;
        turnovers = 0;
        fouls = 0;
        steals = 0;
        minutes = 0.0;
        fieldGoalsMade = 0;
        fieldGoalsAttempted = 0;
        threePointFieldGoalsMade = 0;
        threePointFieldGoalsAttempted = 0;
    }

    public int getExtraID() {
        return extraID;
    }

    public void setExtraID(int extraID) {
        this.extraID = extraID;
    }

    // Getter for player's id
    public int getId() {
        return id;
    }

    // Getter for player's name
    public String getName() {
        return name;
    }

    // Getter for rebounds
    public int getRebounds() {
        return rebounds;
    }

    // Setter for rebounds
    public void setRebounds(int rebounds) {
        this.rebounds = rebounds;
    }

    // Method to add a specified amount to rebounds
    public void addRebounds(int amount) {
        this.rebounds += amount;
    }

    // Getter for points
    public int getPoints() {
        return points;
    }

    // Setter for points
    public void setPoints(int points) {
        this.points = points;
    }

    // Method to add a specified amount to points
    public void addPoints(int amount) {
        this.points += amount;
    }

    // Getter for assists
    public int getAssists() {
        return assists;
    }

    // Setter for assists
    public void setAssists(int assists) {
        this.assists = assists;
    }

    // Method to add a specified amount to assists
    public void addAssists(int amount) {
        this.assists += amount;
    }

    // Getter for blocks
    public int getBlocks() {
        return blocks;
    }

    // Setter for blocks
    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    // Method to add a specified amount to blocks
    public void addBlocks(int amount) {
        this.blocks += amount;
    }

    // Getter for turnovers
    public int getTurnovers() {
        return turnovers;
    }

    // Setter for turnovers
    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    // Method to add a specified amount to turnovers
    public void addTurnovers(int amount) {
        this.turnovers += amount;
    }

    // Getter for fouls
    public int getFouls() {
        return fouls;
    }

    // Setter for fouls
    public void setFouls(int fouls) {
        this.fouls = fouls;
    }

    // Method to add a specified amount to fouls
    public void addFouls(int amount) {
        this.fouls += amount;
    }

    // Getter for minutes
    public double getMinutes() {
        return minutes;
    }

    // Setter for minutes
    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }

    // Method to add a specified amount to minutes
    public void addMinutes(double amount) {
        this.minutes += amount;
    }

    // Getter for field goals made
    public int getFieldGoalsMade() {
        return fieldGoalsMade;
    }

    // Setter for field goals made
    public void setFieldGoalsMade(int fieldGoalsMade) {
        this.fieldGoalsMade = fieldGoalsMade;
    }

    // Method to add a specified amount to field goals made
    public void addFieldGoalsMade(int amount) {
        this.fieldGoalsMade += amount;
    }

    // Getter for field goals attempted
    public int getFieldGoalsAttempted() {
        return fieldGoalsAttempted;
    }

    // Setter for field goals attempted
    public void setFieldGoalsAttempted(int fieldGoalsAttempted) {
        this.fieldGoalsAttempted = fieldGoalsAttempted;
    }

    // Method to add a specified amount to field goals attempted
    public void addFieldGoalsAttempted(int amount) {
        this.fieldGoalsAttempted += amount;
    }

    // Getter for three-point field goals made
    public int getThreePointFieldGoalsMade() {
        return threePointFieldGoalsMade;
    }

    // Setter for three-point field goals made
    public void setThreePointFieldGoalsMade(int threePointFieldGoalsMade) {
        this.threePointFieldGoalsMade = threePointFieldGoalsMade;
    }

    // Method to add a specified amount to three-point field goals made
    public void addThreePointFieldGoalsMade(int amount) {
        this.threePointFieldGoalsMade += amount;
    }

    // Getter for three-point field goals attempted
    public int getThreePointFieldGoalsAttempted() {
        return threePointFieldGoalsAttempted;
    }

    // Setter for three-point field goals attempted
    public void setThreePointFieldGoalsAttempted(int threePointFieldGoalsAttempted) {
        this.threePointFieldGoalsAttempted = threePointFieldGoalsAttempted;
    }

    // Method to add a specified amount to three-point field goals attempted
    public void addThreePointFieldGoalsAttempted(int amount) {
        this.threePointFieldGoalsAttempted += amount;
    }

    // Getter for steals
    public int getSteals() {
        return steals;
    }

    // Setter for steals
    public void setSteals(int steals) {
        this.steals = steals;
    }

    // Method to add a specified amount to steals
    public void addSteals(int amount) {
        this.steals += amount;
    }

    @Override
    public String toString(){
        return   "--------------------" + "\n" +

                "Player: " + name + "\n" +
                "ID: " + id+ "\n" +
                "REBOUNDS: " + rebounds + "\n" +
                "ASSISTS: " + assists + "\n" +
                "BLOCKS: " + blocks + "\n" +
                "STEALS: " + steals + "\n" +
                "TURNOVERS: " + turnovers + " \n" +
                "POINTS: " + points + "\n"
                + "FOULS: " + fouls + "\n"
                + "MINUTES: " +  ((int) minutes/60) + "\n"
                + "FIELD GOALS: " + fieldGoalsMade + "\n"
                + "FIELD GOALS ATTEMPTED: " + fieldGoalsAttempted + "\n"
                + "THREE POINTS: " + threePointFieldGoalsMade + "\n"
                + "THREE POINTS ATTEMPTED: " + threePointFieldGoalsAttempted + "\n"
                +  "--------------------";


    }
}
