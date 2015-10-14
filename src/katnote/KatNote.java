package katnote;

import katnote.ui.CommandLineInterface;
import katnote.ui.GraphicalUserInterface;

public class KatNote {

    public static void main(String[] args) {
        if (args.length != 0 && args[0].equals("cli")) {
            CommandLineInterface cli = new CommandLineInterface();
        } else {
            GraphicalUserInterface.launch(GraphicalUserInterface.class, args);
        }
    }
}
