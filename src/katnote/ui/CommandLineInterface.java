package katnote.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import katnote.Logic;
import katnote.task.Task;
import katnote.task.TaskType;
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
                if(!feedback.getTaskList().isEmpty()){
                    renderTasks(feedback.getTaskList());
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        for(int i = 0; i < tasks.size(); i++){
            Task t = tasks.get(i);
            printMessageLine((i+1) + ". " + t.getTitle());
            if(t.getTaskType() == TaskType.NORMAL || t.getTaskType() == null){
                Date taskDate = t.getEndDate();
                String dateString = dateFormat.format(taskDate);
                String timeString = timeFormat.format(taskDate);
                String dateTime = "Due: " + dateString + " " + timeString; 
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
    private String readInput(){
        return scanner.nextLine();
    }
    
    /**
     * Prints message onto command interface without a new line
     * @param message
     */
    private static void printMessage(String message){
        printMessage(message, false);
    }

    /**
     * Prints message onto command interface with a new line
     * @param message
     */
    private static void printMessageLine(String message){
        printMessage(message, true);
    }
    /**
     * Prints message onto command interface
     * @param message - String to be printed
     * @param hasNewLine - boolean to determine if the message ends
     *                      with a new line
     */
    private static void printMessage(String message, boolean hasNewLine){
        if(hasNewLine){
            System.out.println(message);
        } else {
            System.out.print(message);
        }
    }
}
