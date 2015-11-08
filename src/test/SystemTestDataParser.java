//@@author A0125447E
package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import test.ViewDataPackage.TaskGroupPackage;

public class SystemTestDataParser {
    private static final String DATE_OUTPUT = "DATE_OUTPUT";
    private static final String DATE_HEADER = "%1s      %2s";
    private static final String EVENT_ROW_KEYWORD = "EVENT";
    private static final String DIRECT_OUTPUT_KEYWORD = "DIRECT_OUTPUT_HEADER";
    private static final String DATE_OUTPUT_KEYWORD = "DATE_OUTPUT_HEADER";
    private static final String SPACE_STRING = " ";
    private static final String DATE_INPUT_WITH_TIME_KEYWORD = "DATE_INPUT_2";
    private static final String DATE_INPUT_KEYWORD = "DATE_INPUT";
    private static final String DATE_DIRECT_INPUT_KEYWORD = "DATE_DIRECT_INPUT";
    private static final String DIRECT_INPUT_KEYWORD = "DIRECT_INPUT";
    private static final String OUTPUT_KEYWORD = "OUTPUT";
    private static final String EVENT_INPUT_KEYWORD = "EVENT_INPUT";
    private static final String INPUT_KEYWORD = "INPUT";
    public static final String SYSTEM_TEST_FILE = "TestFiles/SystemTestFiles/systemTestInputOutput.txt";
    private static final String DATE_PATTERN = "dd MMM yy";
    private static final String EVENT_DATE_TIME_FORMAT = "Start: %1s %2s           End: %3s %4s";
    BufferedReader reader = null;
    ArrayList<String> inputs = new ArrayList<String>();
    ArrayList<ViewDataPackage> outputs = new ArrayList<ViewDataPackage>();

    public ArrayList<ViewDataPackage> getOutputs() {
        return outputs;
    }

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public SystemTestDataParser() {
        try {
            reader = new BufferedReader(new FileReader(SYSTEM_TEST_FILE));
            String line = null;
            line = readLine();
            while (line != null) {
                String input = null;
                if (line.equals(INPUT_KEYWORD)) {
                    input = processInput();
                    inputs.add(input);
                    // System.out.println(input);
                } else if (line.equals(EVENT_INPUT_KEYWORD)) {
                    input = processEventInput();
                    inputs.add(input);
                    // System.out.println(input);
                } else if (line.equals(OUTPUT_KEYWORD)) {
                    ViewDataPackage list = processOutput(reader);
                    outputs.add(list);
                    // System.out.println();
                } else if (line.equals(DIRECT_INPUT_KEYWORD)) {
                    input = readLine();
                    inputs.add(input);
                    readLine();
                }
                line = readLine();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String readLine() throws IOException {
        String line = null;
        line = reader.readLine();
        if (line == null) {
            return line;
        }
        // filter comment lines
        while (!line.isEmpty() && line.charAt(0) == '#') {
            line = reader.readLine();
        }
        return line;
    }

    public String processEventInput() {
        String line = null;
        try {
            line = "";
            String input = "";
            input += readLine() + SPACE_STRING; // first part of input
            line = readLine(); // date header
            input += processDateInput(line);
            input += SPACE_STRING + readLine() + SPACE_STRING; // event
                                                               // conntector
                                                               // string
            line = readLine(); // date header
            input += processDateInput(line);
            readLine(); // newline
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private String processDateInput(String line) throws IOException {
        String input = "";
        if (line.equals(DATE_DIRECT_INPUT_KEYWORD)) {
            input += readLine();
        } else if (line.equals(DATE_INPUT_KEYWORD)) {
            input += prcoessSingleDate();
        } else if (line.equals(DATE_INPUT_WITH_TIME_KEYWORD)) {
            input += prcoessSingleDate();
            input += SPACE_STRING + readLine();
        }
        return input;
    }

    public String processInput() {
        String line = null;
        try {
            line = "";
            String input = "";
            input += readLine() + SPACE_STRING; // first part of input
            line = readLine(); // date header
            input += processDateInput(line);
            readLine(); // newline
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private String prcoessSingleDate() throws IOException {
        String dateFormat = readLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        int days = Integer.parseInt(readLine());
        String date = LocalDateTime.now().plusDays(days).format(formatter);
        return date;
    }

    public ViewDataPackage processOutput(BufferedReader reader) {
        String line = null;
        String response = null;
        TaskGroupPackage dataPack = null;
        ArrayList<TaskGroupPackage> array = new ArrayList<TaskGroupPackage>();
        try {
            // response is placed after output header
            line = readLine();
            response = line;
            line = readLine();
            while (line != null && !line.isEmpty()) {
                if (line.equals(DATE_OUTPUT_KEYWORD)) {
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                    int days = Integer.parseInt(readLine());
                    LocalDateTime date = LocalDateTime.now().plusDays(days);
                    String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
                    String dateString = date.format(dateFormat);
                    dateString = String.format(DATE_HEADER, day, dateString);
                    dataPack = processGroupPackage(reader, dateString);
                    array.add(dataPack);
                } else if (line.equals(DIRECT_OUTPUT_KEYWORD)) {
                    String header = readLine();
                    dataPack = processGroupPackage(reader, header);
                    array.add(dataPack);
                }
                line = readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ViewDataPackage(array, response);
    }

    public TaskGroupPackage processGroupPackage(BufferedReader reader, String header) throws IOException {
        String line = readLine();
        int count = Integer.parseInt(line);
        ArrayList<String> description = new ArrayList<String>();
        ArrayList<String> indexString = new ArrayList<String>();
        ArrayList<String> dateStrings = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            line = readLine();
            if (line.equals(EVENT_ROW_KEYWORD)) {
                indexString.add(readLine());
                description.add(readLine());
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                int days = Integer.parseInt(readLine());
                LocalDateTime dateStart = LocalDateTime.now().plusDays(days);
                String dateStartString = dateStart.format(dateFormat);
                String timeStartString = readLine();
                days = Integer.parseInt(readLine());
                LocalDateTime dateEnd = LocalDateTime.now().plusDays(days);
                String dateEndString = dateEnd.format(dateFormat);
                String timeEndString = readLine();
                String eventDateString = String.format(EVENT_DATE_TIME_FORMAT, dateStartString, timeStartString,
                        dateEndString, timeEndString);
                dateStrings.add(eventDateString);

            } else if (line.equals(DATE_OUTPUT)) {
                indexString.add(readLine());
                description.add(readLine());
                
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                String dueDateString = readLine() + SPACE_STRING; //"Due:"
                int days = Integer.parseInt(readLine());
                LocalDateTime date = LocalDateTime.now().plusDays(days);
                dueDateString += date.format(dateFormat) + SPACE_STRING;
                dueDateString += readLine();
                dateStrings.add(dueDateString);
                
            } else {
                indexString.add(line);
                description.add(readLine());
                dateStrings.add(readLine());
            }
        }
        String[] desString = new String[description.size()];
        description.toArray(desString);
        String[] idString = new String[indexString.size()];
        indexString.toArray(idString);
        String[] dates = new String[dateStrings.size()];
        dateStrings.toArray(dates);

        return new TaskGroupPackage(header, desString, dates, idString);
    }
}
