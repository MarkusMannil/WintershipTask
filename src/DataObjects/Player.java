package DataObjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private String uuid;
    private long accountBalance;
    private int wins;
    private int plays;
    // player coin win and loss total for calculating casino total balance change
    private int totalWin;

    public Player(String uuid) {
        this.uuid = uuid;
        accountBalance = 0;
        wins = 0;
        plays = 0;
        totalWin = 0;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public int getTotalWin() {

        return totalWin;
    }

    public void setTotalWin(int totalWin) {
        this.totalWin = totalWin;
    }

    /**
     * Calculates win rate
     * @return win rate
     */
    private BigDecimal getWinRate(){

        BigDecimal win = BigDecimal.valueOf(wins);
        BigDecimal total = BigDecimal.valueOf(plays);

        return win.divide(total,2,RoundingMode.DOWN);
    }

    @Override
    public String toString(){
        return uuid + " " + accountBalance + " " + getWinRate();
    }
}
