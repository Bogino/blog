package main.api.response;

import java.util.Comparator;

public class PostComparator implements Comparator<ApiPostResponse.PostResponse> {
    @Override
    public int compare(ApiPostResponse.PostResponse o1, ApiPostResponse.PostResponse o2) {
        if (o1.getLikeCount() > o2.getLikeCount()){
        return -1;}
        if (o1.getLikeCount() < o2.getLikeCount()){
            return 1;
        }
        return 0;
    }
}
