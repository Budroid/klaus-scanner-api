package nl.budroid.klaus.scanner.tasks;

import nl.budroid.klaus.scanner.controller.ScanController;
import nl.budroid.klaus.scanner.domain.AccessPoint;
import nl.budroid.klaus.scanner.domain.ComplexWirelessNetwork;
import nl.budroid.klaus.scanner.domain.SimpleWirelessNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NotifyTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(NotifyTask.class);
    private final ScanController context;

    public NotifyTask(ScanController context) {
        this.context = context;
    }

    @Override
    public void run() {
        Path input = Paths.get("/home/robert/hcxtools/hcxdumptool/klaus/klaus_output");
        try {
            // Now periodically check the output file
            while(true){
                // Give the other process a headstart of 1 sec
                Thread.sleep(1000);
                List<String> lines = Files.readAllLines(input, Charset.defaultCharset());
                List<SimpleWirelessNetwork> latestSection = getLatestSection(lines);
                List<ComplexWirelessNetwork> latestDataset = convertToComplexWirelessNetworks(latestSection);
                context.send(latestDataset);
            }
        } catch (InterruptedException e) {
            logger.info("Sleep interrupted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ComplexWirelessNetwork> convertToComplexWirelessNetworks(List<SimpleWirelessNetwork> latestSection) {
        List<ComplexWirelessNetwork> result = new ArrayList<>();
        for (SimpleWirelessNetwork simpleWirelessNetwork : latestSection){
            Optional<ComplexWirelessNetwork> complexNetwork = getComplexNetwork(result, simpleWirelessNetwork);
            if(complexNetwork.isPresent() ){
                // Add accesspoint to existing network
                AccessPoint accessPoint = new AccessPoint();
                accessPoint.setBssid(simpleWirelessNetwork.getBssid());
                accessPoint.setRssi(simpleWirelessNetwork.getRssi());
                accessPoint.setChannel(simpleWirelessNetwork.getChannel());
                complexNetwork.get().getProperties().getAccessPoints().add(accessPoint);
            }else{
                // Add to the list
                result.add(simpleWirelessNetwork.toComplex());
            }
        }
        return result;
    }

    private Optional<ComplexWirelessNetwork> getComplexNetwork(List<ComplexWirelessNetwork> networks, SimpleWirelessNetwork simpleWirelessNetwork){
       return networks.stream().filter(complexWirelessNetwork -> simpleWirelessNetwork.getSsid().equals(complexWirelessNetwork.getProperties().getSsid())).findFirst();
    }

    private List<SimpleWirelessNetwork> getLatestSection(List<String> lines) {
        return IntStream.iterate(lines.size() - 1, i -> i > 0, i -> i - 1)
                .mapToObj(lines::get)
                .takeWhile(line -> !line.contains("-----------------"))
                .map(this::convertToWirelessNetwork)
                .filter(wirelessNetwork -> !"<hidden>".equals(wirelessNetwork.getSsid()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private SimpleWirelessNetwork convertToWirelessNetwork(String line){
        String[] split = line.split("\\s+");
        String ssid = getSsid(split);
        SimpleWirelessNetwork network = new SimpleWirelessNetwork();
        network.setBssid(split[1]);
        network.setSsid(ssid);
        network.setChannel(Integer.parseInt(split[3]));
        network.setRssi(Integer.parseInt(split[4]));
        return network;
    }

    private String getSsid(String[] split) {
        StringBuilder ssid = new StringBuilder();
        if (split.length == 7) {
            ssid = new StringBuilder("<hidden>");
        } else if (split.length > 7) {
            for (int i = 7; i < split.length; i++) {
                ssid.append(" ").append(split[i]);
            }
        } else {
            logger.warn("Weird line...");
        }
        return ssid.toString().strip();
    }

}
