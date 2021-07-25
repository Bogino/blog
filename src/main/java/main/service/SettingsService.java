package main.service;

import lombok.AllArgsConstructor;
import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.SettingsRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService implements ISettingsService {

    private final SettingsRepository repository;

    public void setGlobalSettings(boolean isMultiuserMode, boolean isPostPremoderation, boolean isStatisticsPublic) {

        GlobalSettings multiuserMode = repository.getSetting("MULTIUSER_MODE");
        GlobalSettings postPremoderation = repository.getSetting("POST_PREMODERATION");
        GlobalSettings statisticsIsPublic  = repository.getSetting("STATISTICS_IS_PUBLIC");

        if (isMultiuserMode){
            multiuserMode.setValue("YES");
        }else {
            multiuserMode.setValue("NO");
        }

        if (isPostPremoderation){
            postPremoderation.setValue("YES");
        }else {
            postPremoderation.setValue("NO");
        }

        if (isStatisticsPublic){
            statisticsIsPublic.setValue("YES");
        }else {
            statisticsIsPublic.setValue("NO");
        }

        repository.save(multiuserMode);
        repository.save(postPremoderation);
        repository.save(statisticsIsPublic);

    }

    public SettingsResponse getSettingsResponse(){

        GlobalSettings multiuserMode = repository.getSetting("MULTIUSER_MODE");
        GlobalSettings postPremoderation = repository.getSetting("POST_PREMODERATION");
        GlobalSettings statisticsIsPublic  = repository.getSetting("STATISTICS_IS_PUBLIC");
        SettingsResponse response = new SettingsResponse();

        response.setMultiuserMode(multiuserMode.getValue().equals("YES"));

        response.setPostPremoderation(postPremoderation.getValue().equals("YES"));

        response.setStatisticsIsPublic(statisticsIsPublic.getValue().equals("YES"));

        return response;
    }
}
