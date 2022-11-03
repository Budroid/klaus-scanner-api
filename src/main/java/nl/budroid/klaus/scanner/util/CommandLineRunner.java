package nl.budroid.klaus.scanner.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);
    /**
     * @param command the command to run
     * @return the output of the command
     * @throws IOException if an I/O error occurs
     */
    public static List<String> run(String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return in.lines().collect(Collectors.toList());
        }
    }

    public static List<String> runWithoutErrorStream(String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(false);
        Process process = pb.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return in.lines().collect(Collectors.toList());
        }
    }

    /**
     * Prevent construction.
     */
    private CommandLineRunner() {}

    public static List<String> runPipedCommand(String command) throws IOException {

        List<ProcessBuilder> processBuilderList = new ArrayList<>();
        for (String subCommand : command.split(" \\| ")){
            processBuilderList.add(new ProcessBuilder(Commands.toArray(subCommand)).redirectErrorStream(true));
        }
        List<Process> processes = ProcessBuilder.startPipeline(processBuilderList);
        Process last = processes.get(processes.size() - 1);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(last.getInputStream()))) {
            return in.lines().collect(Collectors.toList());
        }
    }

    public static void runOutputToFile(String command, String path) throws IOException {
        File output = new File(path);
        ProcessBuilder pb = new ProcessBuilder(Commands.toArray(command)).redirectOutput(output);
        Process scan = pb.start();
        try {
            logger.info("Wait for command to finish");
            scan.waitFor();
        } catch (InterruptedException e) {
            logger.info("Interrupted in run, destroying process...");
            scan.destroy();
        }
    }
}
