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


    public int processBet(String side, int amount){
        // no winnings
        if(result.equals("DRAW")) return 0;

        if(side.equals(result)) return (int) Math.floor((amount * (result.equals("A") ? returnRateA : returnRateB)));

        return -amount;
    }



    @Override
    public String toString() {

        return "\n" + uuid +","+ returnRateA +","+ returnRateB + "," + result;
    }
}
