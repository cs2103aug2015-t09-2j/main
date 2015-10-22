package katnote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.google.common.io.Files;

public class KatNoteLogger {

    private static final String DEFAULT_LOGGING_PROPERTIES_FILENAME = "/katnote/resources/logging.properties";
    private static final String LOGGING_PROPERTIES_FILENAME = "./logging.properties";
    private static final String KATNOTE_LOG_FILENAME = "katnote.log";
    private static final LogManager logManager = LogManager.getLogManager();
    public static KatNoteLogger instance = null;
    private static FileHandler fh = null;
    private static final Logger log = Logger.getLogger("katnote");

    private KatNoteLogger() {        
        try {
            File logFile = new File(LOGGING_PROPERTIES_FILENAME);
            String url = getClass().getResource(DEFAULT_LOGGING_PROPERTIES_FILENAME).getPath();
            File defaultLogFile = new File(url);
            if(!logFile.exists()){
                Files.copy(defaultLogFile, logFile);
            }
            logManager.readConfiguration(new FileInputStream(logFile));
            fh = new FileHandler(KATNOTE_LOG_FILENAME, false);
        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        log.addHandler(fh);
        log.setLevel(Level.ALL);
    }

    public static KatNoteLogger getInstance() {
        if (instance == null) {
            instance = new KatNoteLogger();
        }
        return instance;
    }

    public void setLevel(Level level) {
        log.setLevel(level);
    }

    public static Logger getLogger(String className) {
        getInstance();
        Logger log = Logger.getLogger(className);
        return log;
    }
}
