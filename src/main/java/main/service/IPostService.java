package main.service;

import main.api.request.AddCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.response.*;
import main.exception.NullPointerCommentTextException;

import java.util.List;

public interface IPostService {

    PostsListResponse getPosts(String mode, int offset, int limit);

    PostsListResponse getPostsByQuery(String query, int offset, int limit);

    ApiPostResponseById getPostsById(int id);

    PostsListResponse getPostsByDate(String date, int offset, int limit);

    PostsListResponse getPostsByTag(String name, int offset, int limit);

    PostsListResponse getPostsForModeration(String status, int offset, int limit);

    PostsListResponse getMyPosts(String status, int offset, int limit);

    Result addPost(long timestamp, int active, String title, List<String> tags, String text);

    Result editPost(int id, long timestamp, int active, String title, List<String> tags, String text);

    ApiCalendarResponse getCountPostsByYear(String year);

    ApiCalendarResponse getCountPostsForCurrentYear();

    ApiStatisticResponse getStatistic();

    ApiCommentResponse addComment(AddCommentRequest request) throws NullPointerCommentTextException;

    Result moderate(PostModerationRequest request);

    Result likePost(int postId);

    Result dislikePost(int postId);


    ApiStatisticResponse getMyStatistic();
}
