package nl.budroid.klaus.scanner.controller;

import nl.budroid.klaus.scanner.domain.HackInfo;
import nl.budroid.klaus.scanner.domain.Hacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class HackController {

    private static final Logger logger = LoggerFactory.getLogger(HackController.class);

    private SimpMessagingTemplate template;

    @Autowired
    public void setTemplate(SimpMessagingTemplate injectedTemplate) {
        this.template = injectedTemplate;
    }


    @PostMapping("hack")
    public ResponseEntity<Boolean> startHack(@RequestBody boolean isRunning)  {
        Hacker hacker = Hacker.getInstance();
        logger.info("Running: " + hacker.isRunning());

        if (isRunning) {
            hacker.stop();
            return ResponseEntity.ok(false);
        } else {
            hacker.start(this);
            return ResponseEntity.ok(true);
        }
    }

    public void send(List<HackInfo> message){
        this.template.convertAndSend("/topic/hacks", message);
    }


}
