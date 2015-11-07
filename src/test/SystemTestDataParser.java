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
    private static final String DATE_HEADER = "%1s      %2s";
    private static final String EVENT_ROW_KEYWORD = "EVENT";
    private static final String DATE_DIRECT_OUTPUT_KEYWORD = "DATE_DIRECT_OUTPUT";
    private static final String DATE_OUTPUT_KEYWORD = "DATE_OUTPUT";
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
            line = reader.readLine();
            while (line != null) {
                String input = null;
                if (line.equals(INPUT_KEYWORD)) {
                    input = processInput(reader);
                    inputs.add(input);
                    // System.out.println(input);
                } else if (line.equals(EVENT_INPUT_KEYWORD)) {
                    input = processEventInput(reader);
                    inputs.add(input);
                    // System.out.println(input);
                } else if (line.equals(OUTPUT_KEYWORD)) {
                    ViewDataPackage list = processOutput(reader);
                    outputs.add(list);
                    // System.out.println();
                } else if (line.equals(DIRECT_INPUT_KEYWORD)) {
                    input = reader.readLine();
                    inputs.add(input);
                    reader.readLine();
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String processEventInput(BufferedReader reader) {
        String line = null;
        try {
            line = "";
            String input = "";
            for (int i = 0; i < 3; i++) {
                input += (reader.readLine() + SPACE_STRING);
            }
            line = reader.readLine();
            if (line.equals(DATE_DIRECT_INPUT_KEYWORD)) {
                input += reader.readLine();
            } else if (line.equals(DATE_INPUT_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
            } else if (line.equals(DATE_INPUT_WITH_TIME_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
                input += SPACE_STRING + reader.readLine();
            }
            input += SPACE_STRING + reader.readLine() + SPACE_STRING;
            line = reader.readLine();
            if (line.equals(DATE_DIRECT_INPUT_KEYWORD)) {
                input += reader.readLine();
            } else if (line.equals(DATE_INPUT_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
            } else if (line.equals(DATE_INPUT_WITH_TIME_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
                input += SPACE_STRING + reader.readLine();
            }
            reader.readLine();
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public String processInput(BufferedReader reader) {
        String line = null;
        try {
            line = "";
            String input = "";
            for (int i = 0; i < 3; i++) {
                input += (reader.readLine() + SPACE_STRING);
            }
            line = reader.readLine();
            if (line.equals(DATE_DIRECT_INPUT_KEYWORD)) {
                input += reader.readLine();
            } else if (line.equals(DATE_INPUT_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
            } else if (line.equals(DATE_INPUT_WITH_TIME_KEYWORD)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
                input += SPACE_STRING + reader.readLine();
            }
            reader.readLine();
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public ViewDataPackage processOutput(BufferedReader reader) {
        String line = null;
        String response = null;
        TaskGroupPackage dataPack = null;
        ArrayList<TaskGroupPackage> array = new ArrayList<TaskGroupPackage>();
        try {
            // response is placed after output header
            line = reader.readLine();
            response = line;
            line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                if (line.equals(DATE_OUTPUT_KEYWORD)) {
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                    int days = Integer.parseInt(reader.readLine());
                    LocalDateTime date = LocalDateTime.now().plusDays(days);
                    String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
                    String dateString = date.format(dateFormat);
                    dateString = String.format(DATE_HEADER, day, dateString);
                    dataPack = processGroupPackage(reader, dateString);
                    array.add(dataPack);
                } else if (line.equals(DATE_DIRECT_OUTPUT_KEYWORD)) {
                    String header = reader.readLine();
                    dataPack = processGroupPackage(reader, header);
                    array.add(dataPack);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ViewDataPackage(array, response);
    }

    public TaskGroupPackage processGroupPackage(BufferedReader reader, String header) throws IOException {
        String line = reader.readLine();
        int count = Integer.parseInt(line);
        ArrayList<String> description = new ArrayList<String>();
        ArrayList<String> indexString = new ArrayList<String>();
        ArrayList<String> dateString = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            line = reader.readLine();
            if(line.equals(EVENT_ROW_KEYWORD)){
                indexString.add(reader.readLine());
                description.add(reader.readLine());
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                int days = Integer.parseInt(reader.readLine());
                LocalDateTime dateStart = LocalDateTime.now().plusDays(days);
                String dateStartString = dateStart.format(dateFormat);
                String timeStartString = reader.readLine();
                days = Integer.parseInt(reader.readLine());
                LocalDateTime dateEnd = LocalDateTime.now().plusDays(days);
                String dateEndString = dateEnd.format(dateFormat);
                String timeEndString = reader.readLine();
                String eventDateString = String.format(EVENT_DATE_TIME_FORMAT, dateStartString, timeStartString,
                        dateEndString, timeEndString);
                dateString.add(eventDateString);   
                
            } else {
                indexString.add(line);
                description.add(reader.readLine());
                dateString.add(reader.readLine());                
            }
        }
        String[] desString = new String[description.size()];
        description.toArray(desString);
        String[] idString = new String[indexString.size()];
        indexString.toArray(idString);
        String[] dates = new String[dateString.size()];
        dateString.toArray(dates);

        return new TaskGroupPackage(header, desString, dates, idString);   
    }
}
