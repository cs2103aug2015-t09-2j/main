package katnote.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import katnote.Logic;
import katnote.task.Task;
import katnote.task.TaskType;
import katnote.UIFeedback;

public class CommandLineInterface {
    private static final String NORMAL_TASK_DATE_FORMAT = "Due: %1s %2s ";
    private static final String TIME_PATTERN = "hh:mm a";
    private static final String DATE_PATTERN = "dd MMM yy";
    
    private Logic logic;
    private boolean toExit;
    private Scanner scanner;

    public CommandLineInterface() {
        initialize();
        runCoreProcess();
    }

    private void runCoreProcess() {
        while(!toExit){
            printMessageLine("===============================");
            printMessage("Enter your input: ");
            String input = readInput();
            printMessageLine("===============================");
            UIFeedback feedback;
            try {
                feedback = logic.execute(input);
                if(feedback.getMessage() != null){
                    printMessageLine(feedback.getMessage());
                }
                if(feedback.getViewState() != null){
                    renderTasks(feedback.getViewState().getNormalTasks());
                }
            } catch (Exception e) {
                printMessageLine(e.getMessage());
            }
        }
        scanner.close();
    }
    private void renderTasks(ArrayList<Task> tasks){
        printMessageLine("Tasks");
        printMessageLine("-----------------------------------");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN);
        for(int i = 0; i < tasks.size(); i++){
            Task t = tasks.get(i);
            printMessageLine((i+1) + ". " + t.getTitle());
            if(t.getTaskType() == TaskType.NORMAL || t.getTaskType() == null){
                LocalDateTime date = t.getEndDate();
                String dateString = date.format(dateFormat);
                String timeString = date.format(timeFormat);
                String dateTime = String.format(NORMAL_TASK_DATE_FORMAT, dateString, timeString);  
                printMessageLine("                         " + dateTime);
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
