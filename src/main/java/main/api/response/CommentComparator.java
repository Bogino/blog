package main.api.response;

import java.util.Comparator;

public class CommentComparator implements Comparator<ApiPostResponse.PostResponse> {
    @Override
    public int compare(ApiPostResponse.PostResponse o1, ApiPostResponse.PostResponse o2) {

        if (o1.getCommentCount() > o2.getCommentCount()){
            return -1;
        }
        if (o1.getCommentCount() < o2.getCommentCount()){
            return 1;
        }

        return 0;
    }
}
