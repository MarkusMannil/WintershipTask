package DataObjects;

import java.util.UUID;

public class Match {

    private final String uuid;
    private final float returnRateA;
    private final float returnRateB;
    private final String result;



    public Match(String uuid, float returnRateA, float returnRateB, String result) {
        this.uuid = uuid;
        this.returnRateA = returnRateA;
        this.returnRateB = returnRateB;
        this.result = result;
    }

    public String getUuid() {
        return uuid;
    }


    /**
     * Calculates how much a bet pays out
     * @param side - side bet on
     * @param amount of coins bet
     * @return amount of coins added/removed from player account
     */
    public int processBet(String side, int amount){
        // no winnings
        if(result.equals("DRAW")) return 0;
        // if bet won calculate correct amount of pay out
        // there can be done an optimisation where you only hold winning side rate,
        // but I decided against it for now because then if you want to build upon this
        // application, for example if the rates are calculated correctly then you would need to write another parser
        // and another class
        if(side.equals(result)) return (int) Math.floor((amount * (result.equals("A") ? returnRateA : returnRateB)));
        // return negative amount of bet as it is removed from player
        return -amount;
    }



    @Override
    public String toString() {

        return "\n" + uuid +","+ returnRateA +","+ returnRateB + "," + result;
    }
}
