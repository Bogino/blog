package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.model.TagRepository;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;

    @Autowired
    private final TagRepository tagRepository;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagRepository tagRepository) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private ResponseEntity<SettingsResponse> settings(){
        return new ResponseEntity<>(settingsService.getGlobalSettings(), HttpStatus.OK);
    }

    @GetMapping("/tag")
    private ResponseEntity<List> tags(){
        return new ResponseEntity(tagRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/tag",
            params = "query",
            method = GET)
    private ResponseEntity<List> list(@RequestParam("query") final String query){
        return new ResponseEntity<>(tagRepository.findByNameContaining(query), HttpStatus.OK);
    }

    @PostMapping("/image")
    private ResponseEntity postImage(File file) {

        try {
            ImageIO.read(file);
            Path destination = Paths.get("upload/");
            Files.copy(file.toPath(), destination.resolve(file.toPath().getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(file.getPath(), HttpStatus.OK);
    }


}
