package nl.budroid.klaus.scanner.controller;

import nl.budroid.klaus.scanner.util.CommandLineRunner;
import nl.budroid.klaus.scanner.util.Commands;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/gps")
public class GpsController {

    @GetMapping("/location")
    public ResponseEntity<?> getLocation() {
        List<String> result;
        try {
            result = CommandLineRunner.runPipedCommand(Commands.CMD_GET_LOCATION);
            if (result.get(0) == null || result.get(0).split(",").length < 2) {
                throw new IOException("Result has an unexpected format");
            }
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }

        List<Double> collect = Stream.of(result.get(0).split(",")[1], result.get(0).split(",")[0])
                .map(Double::valueOf)
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }
}