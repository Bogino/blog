package main.service;

import main.api.response.ApiPostResponse;
import main.api.response.ApiPostResponseById;
import main.api.response.Result;
import main.model.*;
import main.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private final Tag2PostRepository tag2PostRepository;

    private long count = 0;

    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository, Tag2PostRepository tag2PostRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
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

    @Transactional
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

    @Transactional
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

    @Transactional
    public Result addPost(long timestamp, int active, String title, List<String> tags, String text) {
        Post post = new Post();
        Date date = null;
        Result result = new Result(true);

        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
            date = new Date(timestamp);
        }
        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
            date = new Date();
        }
        post.setTime(date);
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        post.setStatus(ModerationStatus.valueOf("NEW"));
        postRepository.save(post);

        for (String tagName : tags) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tagRepository.save(tag);
            Tag2Post tag2Post = new Tag2Post();
            tag2Post.setPost(post);
            tag2Post.setTag(tag);
            tag2PostRepository.save(tag2Post);
        }



        return result;
    }

    @Transactional
    public Result editPost(int id, long timestamp, int active, String title, List<String> tags, String text) {


        Post post = postRepository.findById(id).orElseThrow();
        Date date = null;
        Result result = new Result(true);

        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
            date = new Date(timestamp);
        }
        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
            date = new Date();
        }
        Set<Tag2Post> tag2PostSet = new HashSet<>();
        post.setIsActive(active);
        post.setText(text);
        post.setTime(date);
        post.setTitle(title);
        post.setStatus(ModerationStatus.valueOf("NEW"));
        for (String tagName : tags) {
            Tag tag = new Tag();
            tag.setName(tagName);
            Tag2Post tag2Post = new Tag2Post();
            tag2Post.setPost(post);
            tag2Post.setTag(tag);
            tag2PostSet.add(tag2Post);
        }
        post.setTag2Posts(tag2PostSet);

        postRepository.save(post);

        return result;

    }


}
