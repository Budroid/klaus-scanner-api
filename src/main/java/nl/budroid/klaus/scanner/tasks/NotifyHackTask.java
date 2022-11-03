package nl.budroid.klaus.scanner.tasks;

import nl.budroid.klaus.scanner.controller.HackController;
import nl.budroid.klaus.scanner.domain.HackInfo;
import nl.budroid.klaus.scanner.util.CommandLineRunner;
import nl.budroid.klaus.scanner.util.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;

public class NotifyHackTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(NotifyHackTask.class);
    private final HackController context;

    public NotifyHackTask(HackController context) {
        this.context = context;
    }

    @Override
    public void run() {

//        Path input = Paths.get("/home/robert/hcxtools/hcxdumptool/klaus/test/hackinfo");
        List<HackInfo> resultSet = new ArrayList<>();
        try {
            // Now periodically check the output file
            while(true){
                // Give the other process a headstart of 1 sec
                Thread.sleep(1000);
                // Read the hack output
                List<String> lines = CommandLineRunner.run(Commands.CMD_GET_HACKSTATE);
                if(lines.size() < 3) continue;
                List<HackInfo> updatedResultSet = getResultSet(lines);
                // Check for new hacked AP's
                List<HackInfo> newAps = getNewInfo(resultSet, updatedResultSet);
                if(!newAps.isEmpty()){
//                    newAps.forEach(hackInfo -> logger.info(hackInfo.getSsid() + ":" + hackInfo.getAp() + ":" + hackInfo.getHashLine()));
                    newAps.forEach(hackInfo -> {
                        hackInfo.setRssi(getRssi(hackInfo.getAp()));
                        logger.info(hackInfo.getSsid() + ":" + hackInfo.getAp() + ":" + hackInfo.getRssi() + hackInfo.getHashLine());
                    });
                    logger.info("Sending new hackinfo");
                    context.send(newAps);
                    resultSet = updatedResultSet;
                }
            }
        } catch (InterruptedException e) {
            logger.info("Sleep interrupted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRssi(String ap) {
        try {
            List<String> output = CommandLineRunner.runWithoutErrorStream(Commands.rssiCommand(ap));
            if(output == null || output.isEmpty()){
                return -100;
            }
            IntSummaryStatistics iss = output.stream().filter(line-> !line.isBlank()).mapToInt(Integer::parseInt).summaryStatistics();
            return (int) iss.getAverage();
        } catch (IOException e) {
            logger.info("No RSSI found");
            return -100;
        }
    }

    private List<HackInfo> getNewInfo(List<HackInfo> resultSet, List<HackInfo> updatedResultSet) {
        List<HackInfo> newInfo = new ArrayList<>(updatedResultSet);
        newInfo.removeAll(resultSet);
        return newInfo;
    }

    private List<HackInfo> getResultSet(List<String> lines) {
        List<HackInfo> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i+=3) {
            String ssid = getSsid(lines.get(i));
            String ap = getAp(lines.get(i + 1));
            String hashLine = getHashLine(lines.get(i+2));
            result.add(new HackInfo(ssid, ap, hashLine));
        }

        return result;
    }

    private String getSsid(String line) {
        return line.substring(line.indexOf(": ")+2);
    }

    private String getAp(String line) {
        return line.substring(line.indexOf(": ")+2, line.indexOf("(")-1);
    }

    private String getHashLine(String line) {
        return line.substring(line.indexOf(": ")+2);
    }

}
