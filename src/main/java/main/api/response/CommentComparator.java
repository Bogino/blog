package main.api.response;

import java.util.Comparator;

public class CommentComparator implements Comparator<PostsListResponse.PostResponse> {
    @Override
    public int compare(PostsListResponse.PostResponse o1, PostsListResponse.PostResponse o2) {

        if (o1.getCommentCount() > o2.getCommentCount()){
            return -1;
        }
        if (o1.getCommentCount() < o2.getCommentCount()){
            return 1;
        }

        return 0;
    }
}
