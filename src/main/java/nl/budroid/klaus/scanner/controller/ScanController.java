package nl.budroid.klaus.scanner.controller;

import nl.budroid.klaus.scanner.domain.ComplexWirelessNetwork;
import nl.budroid.klaus.scanner.domain.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class ScanController {

    private static final Logger logger = LoggerFactory.getLogger(ScanController.class);

    private SimpMessagingTemplate template;

    @Autowired
    public void setTemplate(SimpMessagingTemplate injectedTemplate) {
        this.template = injectedTemplate;
    }

    @PostMapping("scan")
    public ResponseEntity<Boolean> startScan(@RequestBody boolean isRunning) {
        Scanner scanner = Scanner.getInstance();
        logger.info("Running: " + scanner.isRunning());

        if (isRunning) {
            scanner.stop();
            return ResponseEntity.ok(false);
        } else {
            scanner.start(this);
            return ResponseEntity.ok(true);
        }
    }

    public void send(List<ComplexWirelessNetwork> message){
        this.template.convertAndSend("/topic/networks", message);
    }

}
