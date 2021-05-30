package main.service;

import main.api.response.ApiTagResponse;

public interface ITagService {

    ApiTagResponse getTags();

    ApiTagResponse getTagsByQuery(String query);
}
