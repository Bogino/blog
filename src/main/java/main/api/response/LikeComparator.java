package main.api.response;

import java.util.Comparator;

public class LikeComparator implements Comparator<PostsListResponse.PostResponse> {
    @Override
    public int compare(PostsListResponse.PostResponse o1, PostsListResponse.PostResponse o2) {
        if (o1.getLikeCount() > o2.getLikeCount()){
        return -1;}
        if (o1.getLikeCount() < o2.getLikeCount()){
            return 1;
        }
        return 0;
    }
}
