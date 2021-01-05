package main.controller;

import main.api.response.ApiPostResponse;
import main.api.response.ApiPostResponseById;
import main.api.response.Result;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private ApiPostResponse apiPostResponse;

    private ApiPostResponseById apiPostResponseById;

    private long count = 0;

    public ApiPostController(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("")
    private ResponseEntity<ApiPostResponse> getPosts(
            @RequestParam(required = false, defaultValue = "recent") String mode,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        if (mode.equals("recent")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getRecentPosts(pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (mode.equals("early")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getEarlyPosts(pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (mode.equals("best")) {

            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.findAll(pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            apiPostResponse.sortPostsByLikes();

            return new ResponseEntity(apiPostResponse, HttpStatus.OK);

        }

        if (mode.equals("popular")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.findAll(pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            apiPostResponse.sortPostsByCommentCount();

            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    private ResponseEntity<ApiPostResponse> getPostsBySearch(
            @RequestParam("query") String query,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByTitleContaining(query, pageWithTenElements);

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
            apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(apiPostResponse, HttpStatus.OK);

    }

    @GetMapping("/moderation")
    private ResponseEntity getPostsForModeration(
            @RequestParam("status") String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        Pageable pageWithTenElements = PageRequest.of(page, limit);


        Page<Post> posts = postRepository.getActivePosts(status, pageWithTenElements);

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
            apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(apiPostResponse, HttpStatus.OK);

    }

    @GetMapping("/my")
    private ResponseEntity getMyPosts(
            @RequestParam("status") String status,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        int page = offset / limit;

        if (status.equals("inactive")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getInActivePosts(pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (status.equals("pending")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getActivePosts("NEW", pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());
            }


            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        if (status.equals("declined")) {

            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getActivePosts("DECLINED", pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }

            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity(apiPostResponse, HttpStatus.OK);

        }

        if (status.equals("published")) {
            Pageable pageWithTenElements = PageRequest.of(page, limit);
            Page<Post> posts = postRepository.getActivePosts("ACCEPTED", pageWithTenElements);

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

                apiPostResponse = new ApiPostResponse(count, p.getId(), p.getTime(), p.getUserId().getId(), p.getUserId().getName(), p.getTitle(), p.getText(), likes, dislikes, commentsCount, p.getViewCount());

            }

            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity(apiPostResponse, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {

        Optional<Post> optionalPost = postRepository.findById(id);

        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Post post = optionalPost.get();

        if (post.getIsActive() == 1 && post.getStatus().name() == "ACCEPTED" && post.getTime().getTime() <= Calendar.getInstance().getTime().getTime()) {

            int likes = 0;
            int dislikes = 0;
            List<PostVote> postVotes = postVoteRepository.findByPost(post);
            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikes++;
                } else likes++;
            }

            List<String> tags = tagRepository.findByPostId(post.getId());

            apiPostResponseById = new ApiPostResponseById(post.getTime().getTime(), true, post.getUserId().getId(), post.getUserId().getName(),
                    post.getTitle(), post.getText(), likes, dislikes, 1, tags);
            for (PostComment pc : postCommentRepository.getCommentsByPostId(post.getId())) {
                apiPostResponseById.addComment(pc.getId(), pc.getTime().getTime(), pc.getText(), pc.getUserId().getId(), pc.getUserId().getName(), pc.getUserId().getPhoto());
            }
            return new ResponseEntity(apiPostResponseById, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    private Result addPost(
            @RequestParam("timestamp") long timestamp,
            @RequestParam("active") int active,
            @RequestParam("title") String title,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text) {

        Post post = new Post();
        Date date = null;
        Result result = new Result(true);
        if (Calendar.getInstance().getTime().getTime() < timestamp) {
            date = new Date(timestamp);
        }
        if (Calendar.getInstance().getTime().getTime() > timestamp) {
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
