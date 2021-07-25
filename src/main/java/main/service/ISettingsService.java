package main.service;

import main.api.response.SettingsResponse;

public interface ISettingsService {

   void setGlobalSettings(boolean isMultiuserMode, boolean isPostPremoderation, boolean isStatisticsPublic);

   SettingsResponse getSettingsResponse();

}
