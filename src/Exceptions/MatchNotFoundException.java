package Exceptions;

import DataObjects.Match;

public class MatchNotFoundException extends Exception {

    public MatchNotFoundException(String matchID) {
        super("Match with Id " + matchID + " not found");
    }
}
