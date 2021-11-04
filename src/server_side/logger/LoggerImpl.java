package server_side.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoggerImpl implements Logger {
    private final File file;

    public LoggerImpl(String logFileName) {
        file = new File(logFileName);
    }

    public void logToFile(String username, String actionName) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(LocalDateTime.now() + " : " + username + " : " + actionName + '\n');
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
