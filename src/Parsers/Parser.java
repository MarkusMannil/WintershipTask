package Parsers;

import DataObjects.Match;
import DataObjects.Player;
import Exceptions.MatchNotFoundException;

import java.io.*;
import java.util.*;

public class Parser {

    private final File matchData;
    private final File playerData;
    private final File result = new File("src/result.txt");
    private final ArrayList<Match> matches = new ArrayList<>();
    private final ArrayList<Player> players = new ArrayList<>();
    private final HashMap<Player, String> illegalPlayers = new HashMap<>();

    // constructor for pre determined file paths
    public Parser() {
        matchData = new File("resource/match_data.txt");
        playerData = new File("resource/player_data.txt");
    }

    // constructor to set data files
    public Parser(String matchData, String playerData) {
        this.matchData = new File(matchData);
        this.playerData = new File(playerData);
    }

    /**
     * Reads file containing match data
     * $format -> ID,A side rate, B side rate, result
     * Writes all matches into matches list
     *
     * @throws IOException
     */
    public void parseMatches() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(matchData));

        String match_info;

        while ((match_info = br.readLine()) != null) {
            /*
             * [0] match UID
             * [1] A rate
             * [2] B rate
             * [3] result
             */
            String[] stats = match_info.split(",");

            matches.add(
                    new Match(stats[0],
                            Float.parseFloat(stats[1]),
                            Float.parseFloat(stats[2]),
                            stats[3])
            );
        }
    }

    /**
     * Reads file containing match data
     * $format -> Id,action,match_id?,coin amount for action,side bet on?
     * Process actions and change different player data
     * and sorts players into legal and ilegal players
     *
     * @throws IOException
     * @throws MatchNotFoundException
     */
    public void parsePlayers() throws IOException, MatchNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(playerData));

        String playerAction;

        Player currentPlayer;

        while ((playerAction = br.readLine()) != null) {
            /*
             * [0] player UID
             * [1] Action
             * [2] Match -if exists
             * [3] Amount
             * [4] Side
             */
            String[] data = playerAction.split(",");

            // Skip the action checking if player has already done an illegal action
            if (findIllegalPlayerById(data[0]) != null) {
                continue;
            }
            // check if we need to instantiate a new player
            if ((currentPlayer = findPlayerById(data[0])) == null) {
                currentPlayer = new Player(data[0]);
                players.add(currentPlayer);
            }
            System.out.println(currentPlayer.getUuid() + " "+ currentPlayer.getAccountBalance());

            // process player action
            boolean legal = switch (data[1]) {
                case "DEPOSIT" -> actionDeposit(currentPlayer, Integer.parseInt(data[3]));
                case "WITHDRAW" -> actionWithdraw(currentPlayer, Integer.parseInt(data[3]));
                case "BET" -> actionBet(
                        currentPlayer,
                        findMatchById(data[2]),
                        Integer.parseInt(data[3]),
                        data[4]
                );
                default -> false;
            };

            // if move was illegal move player to illegal, and add correctly formatted player line
            if (!legal) {

                String illegalAction = data[0] + " "
                        + data[1] + " "
                        + (data[2].equals("") ? "null" : data[2]) + " "
                        + data[3] + " "
                        + (data.length == 4 ? "null" : data[4]);

                moveFromLegalToIllegal(currentPlayer, illegalAction);
            }
        }
    }

    /**
     * writes collected data to result.txt
     * @throws IOException
     */
    public void writeToFile() throws IOException {

        BufferedWriter br = new BufferedWriter(new FileWriter(result));

        int casinoHostBalance = 0;

        if (players.size() != 0) {
            // sort the player list by id and write them to the file, adding their total win to casino balance
            for (Player player : players.stream().sorted(Comparator.comparing(Player::getUuid)).toList()) {
                br.write(player.toString() + "\n");
                casinoHostBalance += player.getTotalWin();
            }
        } else br.write("\n");

        br.write("\n");

        if (illegalPlayers.size() != 0) {
            // sort the player list by id and write their illegal move to file
            for (Player player : illegalPlayers.keySet().stream().sorted(Comparator.comparing(Player::getUuid)).toList()) {
                br.write(illegalPlayers.get(player) + "\n");
            }
        } else br.write("\n");

        br.write("\n");

        br.write(String.valueOf(casinoHostBalance));

        br.close();
    }


    /**
     * @param uuid id of a player that is searched from List<players>
     * @return Player object with matching uuid if exists else return null
     */
    private Player findPlayerById(String uuid) {
        return players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * @param uuid id of a match that is searched from List<matches>
     * @return match with matching uuid if exists
     * @throws MatchNotFoundException if match does not exist
     */
    private Match findMatchById(String uuid) throws MatchNotFoundException {
        Match m = matches.stream().filter(match -> match.getUuid().equals(uuid)).findFirst().
                orElse(null);
        // my reasoning is that if user has a mistake in their file it would be better to have a tailored error
        if (m == null) throw new MatchNotFoundException(uuid);
        return m;
    }

    /**
     * @param uuid id of an illegal player that is searched from illegalPlayers.keySet()
     * @return Player with matching id if exists else null
     */
    private Player findIllegalPlayerById(String uuid) {
        return illegalPlayers.keySet().stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * @param player that is moved from players to illegalPlayers
     * @param illegal first illegal action of the player
     */
    private void moveFromLegalToIllegal(Player player, String illegal) {
        players.remove(player);
        illegalPlayers.put(player, illegal);
    }

    /**
     * adds coins to player balance
     * @param player who deposits coins to their account
     * @param amount of coins deposited
     * @return true as this action cannot be illegal
     */
    private boolean actionDeposit(Player player, int amount) {
        player.setAccountBalance(player.getAccountBalance() + amount);
        return true;
    }

    /**
     * Takes out coins from player balance
     * @param player who withdraws coins from his balance
     * @param amount of coins withdrew
     * @return if action was legal (balance is larger than the amount withdrew)
     */
    private boolean actionWithdraw(Player player, int amount) {

        if (player.getAccountBalance() < amount) return false;

        player.setAccountBalance(player.getAccountBalance() - amount);
        return true;
    }

    /**
     * Places a player bet, changes player balance, total win and wins
     * @param player who bet
     * @param match which the player bet on
     * @param amount of coins player bet
     * @param side on which player bet on
     * @return if action legal (balance is larger than the amount bet)
     */
    private boolean actionBet(Player player, Match match, int amount, String side) {
        // check for illegal actions
        if (player.getAccountBalance() < amount) return false;
        // change plays amount for win
        player.setPlays(player.getPlays() + 1);
        // get player coins return on bet
        int playerCoinChange = match.processBet(side, amount);
        // keep track of player coin changes to casino balance
        player.setTotalWin(player.getTotalWin() + playerCoinChange);
        // change casino balance
        player.setAccountBalance(player.getAccountBalance() + (long) playerCoinChange);
        // update player wins
        if (playerCoinChange > 0) {
            player.setWins(player.getWins() + 1);
        }
        return true;
    }
}
