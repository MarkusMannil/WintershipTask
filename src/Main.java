import Exceptions.MatchNotFoundException;
import Parsers.Parser;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, MatchNotFoundException {
        Parser parser;

        // use pre set paths
        if (args.length < 2) {
            parser = new Parser();

        } else { // use args for paths
            parser = new Parser(args[0],args[1]);
        }
        parser.parseMatches();
        parser.parsePlayers();
        parser.writeToFile();
    }
}