# Wintership java task 2023
### author: Markus Männil

---
## Overview

This application takes in two data files
- Match data 
- Player data 

Data can be given two ways
- Editing the files in resource folder
  - match_data.txt for match data
  - player_data.txt for player data
- Giving main arguments
  - first argument match data path
  - second argument player data path

The program writes results into result.txt located in src folder


## Structure 

- src 
  - DataObjects
    - Match
    - Player
  - Parsers 
    - Parser
  - Exceptions
    - MatchNotFoundException
