package nl.budroid.klaus.scanner.tasks;

import lombok.NoArgsConstructor;
import nl.budroid.klaus.scanner.util.CommandLineRunner;
import nl.budroid.klaus.scanner.util.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@NoArgsConstructor
public class HackTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(HackTask.class);

    @Override
    public void run() {
        try {
            CommandLineRunner.runOutputToFile(Commands.CMD_HCXDUMPTOOL_START, "/home/robert/hcxtools/hcxdumptool/klaus/test/output");
            logger.info("Scan finished?");
        } catch (IOException e) {
            logger.info("IOException in run()");
            e.printStackTrace();
        }
    }

}
