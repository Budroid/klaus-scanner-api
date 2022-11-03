package nl.budroid.klaus.scanner.domain;

import lombok.Getter;
import nl.budroid.klaus.scanner.controller.HackController;
import nl.budroid.klaus.scanner.tasks.HackTask;
import nl.budroid.klaus.scanner.tasks.NotifyHackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public enum Hacker {

    INSTANCE;
    @Getter
    boolean running;
    ExecutorService executorService;
    Future<?> hackTask;
    Future<?> notifyHackTask;

    private static final Logger logger = LoggerFactory.getLogger(Hacker.class);
    public static Hacker getInstance() {
        return INSTANCE;
    }

    public void start(HackController context){
        this.running = true;

        // Remove old pcap file
        try {
            Files.delete(Paths.get("/home/robert/hcxtools/hcxdumptool/klaus/test/klaus_dump.pcapng"));
        } catch (IOException e ) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(2);
        hackTask = executorService.submit(new HackTask());
        notifyHackTask = executorService.submit(new NotifyHackTask(context));
        logger.info("Hacker started");
    }

    public void stop() {
        this.running = false;
        notifyHackTask.cancel(true);
        hackTask.cancel(true);
        executorService.shutdown();
        logger.info("Scanner stopped");
    }

}
