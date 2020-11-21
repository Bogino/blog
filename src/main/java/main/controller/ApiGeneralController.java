package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final SettingsService settingsService;
    private final InitResponse initResponse;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private ResponseEntity<SettingsResponse> settings(){
        return new ResponseEntity<>(settingsService.getGlobalSettings(), HttpStatus.OK);
    }


}
