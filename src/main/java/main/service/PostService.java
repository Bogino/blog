package main.service;

import main.api.response.ApiPostResponse;
import main.api.response.ApiPostResponseById;
import main.model.Post;
import main.model.PostComment;
import main.model.PostVote;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final PostVoteRepository postVoteRepository;

    @Autowired
    private final PostCommentRepository postCommentRepository;

    @Autowired
    private final TagRepository tagRepository;

    private long count = 0;

    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
    }


    @Transactional
    public ApiPostResponse getPosts(String mode, int offset, int limit) {

        int page = offset / limit;
        Page<Post> posts = null;

        switch (mode) {
            case ("recent"):
                posts = Optional.of(postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit, Sort.by("time").descending()))).orElseThrow();
                break;
            case ("early"):
                posts = Optional.of(postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit, Sort.by("time").ascending()))).orElseThrow();
                break;
            case ("best"):
            case ("popular"):
                posts = Optional.of(postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit))).orElseThrow();
                break;
        }


        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();

        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }

            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        if (mode.equals("popular"))

            apiPostResponse.sortPostsByCommentCount();

        if (mode.equals("best"))

            apiPostResponse.sortPostsByLikes();


        return apiPostResponse;

    }

    @Transactional
    public ApiPostResponse getPostsByQuery(String query, int offset, int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = Optional.of(postRepository.findByTitleContaining(query, pageWithTenElements)).orElseThrow();
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();
        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }
            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }


        return apiPostResponse;
    }

    @Transactional
    public ApiPostResponseById getPostsById(int id) {

        Optional<Post> optionalPost = postRepository.findById(id);

        Post post = optionalPost.orElseThrow();


        int likes = 0;
        int dislikes = 0;
        List<PostVote> postVotes = postVoteRepository.findByPost(post);
        for (PostVote pv : postVotes) {
            if (pv.getValue() < 0) {
                dislikes++;
            } else likes++;
        }

        List<String> tags = tagRepository.findByPostId(post.getId());

        ApiPostResponseById apiPostResponseById = new ApiPostResponseById(post.getTime().getTime() / 1000, true, post.getUserId().getId(), post.getUserId().getName(),
                post.getTitle(), post.getText(), likes, dislikes, 1, tags);
        for (PostComment pc : postCommentRepository.getCommentsByPostId(post.getId())) {
            apiPostResponseById.addComment(pc.getId(), pc.getTime().getTime() / 1000, pc.getText(), pc.getUserId().getId(), pc.getUserId().getName(), pc.getUserId().getPhoto());
        }

        return apiPostResponseById;
    }

    @Transactional
    public ApiPostResponse getPostsByDate(String date, int offset, int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = Optional.of(postRepository.findByDate(date, pageWithTenElements)).orElseThrow();
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();
        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }
            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }


        return apiPostResponse;

    }

    @Transactional
    public ApiPostResponse getPostsByTag(String name, int offset, int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = Optional.of(postRepository.findByTagName(name, pageWithTenElements)).orElseThrow();
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();
        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }
            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }


        return apiPostResponse;

    }

    public ApiPostResponse getPostsForModeration(String status, int offset, int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = Optional.of(postRepository.getActivePosts(status, pageWithTenElements)).orElseThrow();
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();

        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }
            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        return apiPostResponse;

    }

    public ApiPostResponse getMyPosts(String status, int offset, int limit) {

        int page = offset / limit;
        Page<Post> posts = null;

        switch (status) {
            case ("inactive"):
                posts = Optional.of(postRepository.getInActivePosts(PageRequest.of(page, limit))).orElseThrow();
                break;
            case ("pending"):
                posts = Optional.of(postRepository.getActivePosts("NEW", PageRequest.of(page, limit))).orElseThrow();
                break;
            case ("declined"):
                posts = Optional.of(postRepository.getActivePosts("DECLINED", PageRequest.of(page, limit))).orElseThrow();
                break;
            case ("published"):
                posts = Optional.of(postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit))).orElseThrow();
                break;
        }

        ApiPostResponse apiPostResponse = new ApiPostResponse();
        count = posts.getTotalElements();
        for (Post p : posts) {
            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(p);
            int commentsCount = postCommentRepository.getCountCommentsByPostId(p.getId());
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }

            apiPostResponse.addApiPostResponse(count, p.getId(), p.getTime().getTime() / 1000, p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }


        return apiPostResponse;
    }


}
