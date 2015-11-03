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
    public static final String SYSTEM_TEST_FILE = "TestFiles/SystemTestFiles/systemTestInputOutput.txt";
    private static final String DATE_TIME_PATTERN = "dd MMM yy hh:mm a";
    private static final String TIME_PATTERN = "hh:mm a";
    private static final String DATE_PATTERN = "dd MMM yy";
    BufferedReader reader = null;
    ArrayList<String> inputs = new ArrayList<String>();
    ArrayList<ViewDataPackage> outputs = new ArrayList<ViewDataPackage>();
    
    public ArrayList<ViewDataPackage> getOutputs() {
        return outputs;
    }

    public ArrayList<String> getInputs(){
        return inputs;
    }
    
    
    public SystemTestDataParser() {
        try {
            reader = new BufferedReader(new FileReader(SYSTEM_TEST_FILE));
            String line = null;
            line = reader.readLine();
            while(line != null){
                String input = null;
                if(line.equals("INPUT")){
                    input = processInput(reader);
                    inputs.add(input);
                    //System.out.println(input);
                } else if(line.equals("OUTPUT")){
                    ViewDataPackage list = processOutput(reader);
                    outputs.add(list);
                    //System.out.println();
                } else if(line.equals("DIRECT_INPUT")){
                    input = reader.readLine();
                    inputs.add(input);
                    reader.readLine();
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public String processInput(BufferedReader reader){
        String line = null;
        try {
            line = "";
            String input = "";
            for(int i = 0; i < 3; i++){
                input += (reader.readLine() + " ");
            }
            line = reader.readLine();
            if(line.equals("DATE_DIRECT_INPUT")){
                input += reader.readLine();
            } else if(line.equals("DATE_INPUT")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
            } else if(line.equals("DATE_INPUT_2")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(reader.readLine());
                int days = Integer.parseInt(reader.readLine());
                input += LocalDateTime.now().plusDays(days).format(formatter);
                input += " " + reader.readLine();
            }
            reader.readLine();
            return input;
        } catch (IOException e){
            e.printStackTrace();
        }
        return line;
    }
    
    public ViewDataPackage processOutput(BufferedReader reader){
        String line = null;        
        String response = null;
        TaskGroupPackage dataPack = null;
        ArrayList<TaskGroupPackage> array = new ArrayList<TaskGroupPackage>();
        try {
            //response is placed after output header
            line = reader.readLine();
            response = line;
            line = reader.readLine();
            while(line != null && !line.isEmpty()){
                if(line.equals("DATE_OUTPUT")){
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);
                    int days = Integer.parseInt(reader.readLine());
                    LocalDateTime date = LocalDateTime.now().plusDays(days);
                    String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
                    String dateString = date.format(dateFormat);
                    dateString = day + "      " + dateString;
                    dataPack = processGroupPackage(reader, dateString);
                    array.add(dataPack);
                } else if(line.equals("DATE_DIRECT_OUTPUT")){
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
    
    public TaskGroupPackage processGroupPackage(BufferedReader reader, String header) throws IOException{
        int count = Integer.parseInt(reader.readLine());
        ArrayList<String> description = new ArrayList<String>();
        ArrayList<String> indexString = new ArrayList<String>();
        ArrayList<String> dateString = new ArrayList<String>();
        for(int i = 0; i<count; i++){
            indexString.add(reader.readLine());
            description.add(reader.readLine());
            dateString.add(reader.readLine());
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
