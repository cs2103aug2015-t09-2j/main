package katnote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class KatNoteLogger {

    private static final String DEFAULT_LOGGING_PROPERTIES_FILENAME = "/katnote/resources/logging.properties";
    private static final String LOGGING_PROPERTIES_FILENAME = "./logging.properties";
    private static final String KATNOTE_LOG_FILENAME = "katnote.log";
    private static final LogManager logManager = LogManager.getLogManager();
    private static final Logger log = Logger.getLogger("katnote");

    private static FileHandler fh = null;

    public static KatNoteLogger instance = null;

    /**
     * Singleton class set up to load logging properties from file or within
     * application
     */
    private KatNoteLogger() {
        try {
            File logFile = new File(LOGGING_PROPERTIES_FILENAME);
            if (!logFile.exists()) {
                InputStream defaultFile = getClass().getResourceAsStream(DEFAULT_LOGGING_PROPERTIES_FILENAME);
                FileOutputStream outStream = new FileOutputStream(logFile);
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = defaultFile.read(buffer)) > 0) {
                    outStream.write(buffer, 0, readBytes);
                }
                defaultFile.close();
                outStream.close();
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

    /**
     * Gets the singleton object of the KatNoteLogger class
     * 
     * @return KatNoteLogger singleton object
     */
    public static KatNoteLogger getInstance() {
        if (instance == null) {
            instance = new KatNoteLogger();
        }
        return instance;
    }

    /**
     * Sets the KatNote undefined level
     * 
     * @param level default log level
     */
    public void setLevel(Level level) {
        log.setLevel(level);
    }

    /**
     * Ensures the logging property has been loaded by loading the instance of
     * the class
     * 
     * @param className class name of class to be logged
     * @return Logger object
     */
    public static Logger getLogger(String className) {
        getInstance();
        Logger log = Logger.getLogger(className);
        return log;
    }
}
