**<h1> Play By Play Example </h1>**

- Identifies the type of Play; EX ASSIST_MADE_SHOT as Enum (Check PlayTypes, still more to come)
- Creates a Play object with the enum in it, and then fills a bunch of fields in the object based on what is true
- about the plays.
- **EX**:
- **PLAY**: Player makes an assisted shot.
- **LABEL (Enum)**: ASSIST_MADE_SHOT
- **INFORMATION FILLED**:
  - **All relevant play information** (Date, game_ID, season, play#, quarter, duration of play(in seconds), score, remaining time in quarter, court players)
  - **Unique Play Information**: Ball Shooter, Who assisted/ was assisted, distance of shot. If a field is not filled, it will be a null value.











**<h2>Extraction from Matchups </h2>**


- **<u>Game ID:</u>** 
  -    401656363 
- **<u>Game Date:</u>** 
  -    2024-06-17
- **<u>Game Type:</u>** 
  -    FINAL
- **<u>Home Team:</u>** 
  -    Boston Celtics
- **<u>Away Team:</u>** 
  -    Dallas Mavericks 
- **<u>Home Starters:</u>** 
  -     Jayson Tatum, Al Horford, Jrue Holiday, Derrick White, Jaylen Brown
- **<u>Away Starters:</u>**  
  -     P.J. Washington, Derrick Jones Jr., Daniel Gafford, Kyrie Irving, Luka Doncic 
- **<u>Home Bench:</u>** 
  -     Sam Hauser, Oshae Brissett, Luke Kornet, Kristaps Porzingis, Payton Pritchard, Svi Mykhailiuk, Xavier Tillman, Neemias Queta, Jaden Springer, Jordan Walsh 
- **<u>Away Bench:</u>** 
  -     Maxi Kleber, Olivier-Maxence Prosper, Tim Hardaway Jr., Dwight Powell, Dereck Lively II, Josh Green, Dante Exum, A.J. Lawson, Jaden Hardy, Markieff Morris