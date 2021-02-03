package main.controller;

import main.api.response.ApiPostResponse;
import main.api.response.ApiPostResponseById;
import main.api.response.Result;
import main.model.ModerationStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private TagRepository tagRepository;


    private ApiPostResponseById apiPostResponseById;

    private long count = 0;

    public ApiPostController(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("")
    private ResponseEntity getPosts(
            @RequestParam(required = false, defaultValue = "recent") String mode,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;
        Page<Post> posts = null;

        switch (mode){
            case ("recent") : posts = postRepository.getRecentPosts(PageRequest.of(page, limit, Sort.by("time").descending()));break;
            case ("early") : posts = postRepository.getEarlyPosts(PageRequest.of(page, limit, Sort.by("time").ascending()));break;
            case ("best") :
            case ("popular") : posts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit));break;
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


        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(apiPostResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    private ResponseEntity getPostsBySearch(
            @RequestParam() String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByTitleContaining(query, pageWithTenElements);
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

        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(apiPostResponse, HttpStatus.OK);
    }

    @GetMapping("/byDate")
    private ResponseEntity getPostsByDate(
            @RequestParam("time") String date,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByDate(date, pageWithTenElements);
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

        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(apiPostResponse, HttpStatus.OK);


    }

    @GetMapping("/byTag")
    private ResponseEntity getPostsByTag(
            @RequestParam("tag") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByTagName(name, pageWithTenElements);
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

        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(apiPostResponse, HttpStatus.OK);

    }

    @GetMapping("/moderation")
    private ResponseEntity getPostsForModeration(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.getActivePosts(status, pageWithTenElements);
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

        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(apiPostResponse, HttpStatus.OK);

    }

    @GetMapping("/my")
    private ResponseEntity getMyPosts(
            @RequestParam() String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;
        Page<Post> posts = null;

        switch (status){
            case ("inactive") : posts = postRepository.getInActivePosts(PageRequest.of(page, limit)); break;
            case ("pending") : posts = postRepository.getActivePosts("NEW", PageRequest.of(page, limit)); break;
            case ("declined") : posts = postRepository.getActivePosts("DECLINED", PageRequest.of(page, limit)); break;
            case ("published") :  posts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit)); break;
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

        if (posts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(apiPostResponse, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {

        Optional<Post> optionalPost = postRepository.findById(id);

        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Post post = optionalPost.get();

        if (post.getIsActive() == 1 && post.getStatus().name() == "ACCEPTED" && post.getTime().getTime() / 1000 <= Calendar.getInstance().getTime().getTime() / 1000) {

            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(post);
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }

            List<String> tags = tagRepository.findByPostId(post.getId());

            apiPostResponseById = new ApiPostResponseById(post.getTime().getTime() / 1000, true, post.getUserId().getId(), post.getUserId().getName(),
                    post.getTitle(), post.getText(), likes, dislikes, 1, tags);
            for (PostComment pc : postCommentRepository.getCommentsByPostId(post.getId())) {
                apiPostResponseById.addComment(pc.getId(), pc.getTime().getTime() / 1000, pc.getText(), pc.getUserId().getId(), pc.getUserId().getName(), pc.getUserId().getPhoto());
            }
            return new ResponseEntity(apiPostResponseById, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    private Result addPost(
            @RequestParam() long timestamp,
            @RequestParam() int active,
            @RequestParam() String title,
            @RequestParam() String tags,
            @RequestParam() String text) {

        Post post = new Post();
        Date date = null;
        Result result = new Result(true);
        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
            date = new Date(timestamp);
        }
        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
            date = Calendar.getInstance().getTime();
        }
        if ((title.length() == 0 || title.length() < 3) && (text.length() == 0 || text.length() < 50)) {
            throw new RuntimeException("Некорректные данные");
        }
        post.setTime(date);
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        post.setStatus(ModerationStatus.valueOf("NEW"));
        postRepository.save(post);

        return result;
    }

}
