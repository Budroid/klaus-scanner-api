package nl.budroid.klaus.scanner.tasks;

import lombok.NoArgsConstructor;
import nl.budroid.klaus.scanner.util.CommandLineRunner;
import nl.budroid.klaus.scanner.util.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@NoArgsConstructor
public class ScanTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ScanTask.class);

    @Override
    public void run() {
        try {
            CommandLineRunner.run(Commands.CMD_START_SCAN, false, true);
            logger.info("Scan finished?");
        } catch (IOException e) {
            logger.info("IOException in run (Scantask)");
            e.printStackTrace();
        }
    }

}
