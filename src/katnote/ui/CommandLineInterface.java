package katnote.ui;

import java.util.Scanner;

import katnote.Logic;
import katnote.UIFeedback;

public class CommandLineInterface {
    private Logic logic;
    private boolean toExit;
    private Scanner scanner;

    public CommandLineInterface() {
        initialize();
        runCoreProcess();
    }

    private void runCoreProcess() {
        while (!toExit) {
            printMessage("Enter your input: ");
            String input = readInput();
            UIFeedback feedback;
            try {
                feedback = logic.execute(input);
                printMessageLine(feedback.getMessage());
            } catch (Exception e) {
                printMessageLine(e.getMessage());
            }
        }
    }

    private void initialize() {
        try {
            logic = new Logic();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        toExit = false;
        scanner = new Scanner(System.in);
    }

    private String readInput() {
        return scanner.nextLine();
    }

    /**
     * Prints message onto command interface without a new line
     * 
     * @param message
     */
    private static void printMessage(String message) {
        printMessage(message, false);
    }

    /**
     * Prints message onto command interface with a new line
     * 
     * @param message
     */
    private static void printMessageLine(String message) {
        printMessage(message, true);
    }

    /**
     * Prints message onto command interface
     * 
     * @param message
     *            - String to be printed
     * @param hasNewLine
     *            - boolean to determine if the message ends with a new line
     */
    private static void printMessage(String message, boolean hasNewLine) {
        if (hasNewLine) {
            System.out.println(message);
        } else {
            System.out.print(message);
        }
    }
}
