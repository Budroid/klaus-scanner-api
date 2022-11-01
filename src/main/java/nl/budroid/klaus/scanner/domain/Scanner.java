package nl.budroid.klaus.scanner.domain;

import lombok.Getter;

import nl.budroid.klaus.scanner.controller.ScanController;
import nl.budroid.klaus.scanner.tasks.NotifyTask;
import nl.budroid.klaus.scanner.tasks.ScanTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public enum Scanner {

    INSTANCE;
    @Getter
    boolean running;
    ExecutorService executorService;
    Future<?> scanTask;
    Future<?> notifyTask;

    private static final Logger logger = LoggerFactory.getLogger(Scanner.class);
    public static Scanner getInstance() {
        return INSTANCE;
    }

    public void start(ScanController context){
        this.running = true;

        // Remove old pcap file
        try {
            Files.delete(Paths.get("/home/robert/hcxtools/hcxdumptool/klaus/dump.pcapng"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(2);
        scanTask = executorService.submit(new ScanTask());
        notifyTask = executorService.submit(new NotifyTask(context));
        logger.info("Scanner started");
    }

    public void stop() {
        this.running = false;
        notifyTask.cancel(true);
        scanTask.cancel(true);
        executorService.shutdown();
        logger.info("Scanner stopped");
    }

}
