package main.service;

import main.api.request.AddCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.response.*;
import main.exception.NullPointerCommentTextException;
import main.exception.PostNotFoundException;
import main.model.*;
import main.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PostVoteRepository postVoteRepository;

    @Autowired
    private final PostCommentRepository postCommentRepository;


    @Autowired
    private final TagRepository tagRepository;

    private final SettingsService settingsService;

    private long count = 0;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository, SettingsService settingsService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;

        this.tagRepository = tagRepository;
        this.settingsService = settingsService;
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
    public ApiPostResponseById getPostsById(int id) throws PostNotFoundException {

        Post post = postRepository.findByIdAcceptedPost(id).orElseThrow(() -> new PostNotFoundException());


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

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
            if (p.getUserId().getEmail().equals(principal.getUsername())) {
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
        }


        return apiPostResponse;
    }

    @Transactional
    public Result addPost(long timestamp, int active, String title, List<String> tags, String text) {

        Map<String, String> errors = checkErrors(title, text);

        if (!errors.isEmpty()) {
            return new ErrorResponse(false, errors);
        }

        Post post = new Post();

        Date date = null;
        Result result = new Result(true);

        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
            date = new Date(timestamp);
        }
        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
            date = new Date();
        }

        if (settingsService.getGlobalSettings().isPostPremoderation()) {
            post.setStatus(ModerationStatus.valueOf("NEW"));
        } else if (post.getIsActive() == 1) {
            post.setStatus(ModerationStatus.ACCEPTED);
        }

        post.setTime(date);
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        post.setViewCount(0);


        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        post.setUserId(user);


        if (tags.size() != 0) {
            for (String tagName : tags) {

                Tag tag = tagRepository.findByName(tagName);
                if (tag != null) {
                    post.getTags().add(tag);
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tagRepository.save(newTag);
                    post.getTags().add(newTag);

                }
            }
        }
        postRepository.save(post);


        return result;
    }


    @Transactional
    public Result editPost(int id, long timestamp, int active, String title, List<String> tags, String text) {

        ErrorResponse response = new ErrorResponse(false, checkErrors(title, text));

        if (!response.getErrors().isEmpty()) {
            return response;
        }

        Post post = postRepository.findByIdAcceptedPost(id).orElseThrow();
        Date date = null;
        Result result = new Result(true);

        if (Calendar.getInstance().getTime().getTime() / 1000 < timestamp) {
            date = new Date(timestamp);
        }

        if (Calendar.getInstance().getTime().getTime() / 1000 > timestamp) {
            date = new Date();
        }

        post.setIsActive(active);

        if (text != null)
            post.setText(text);
        post.setTime(date);

        if (title != null)
            post.setTitle(title);
        post.setStatus(ModerationStatus.valueOf("NEW"));

        if (tags.size() != 0) {
            for (String tagName : tags) {

                Tag tag = tagRepository.findByName(tagName);
                if (tag != null) {
                    post.getTags().add(tag);
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tagRepository.save(newTag);
                    post.getTags().add(newTag);

                }
            }
        }


        postRepository.save(post);

        return result;


    }

    public ApiCalendarResponse getCountPostsByYear(String year) {

        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer y : postRepository.getYearsOfPosts()) {
            apiCalendarResponse.getYears().add(y);
        }

        for (Date date : postRepository.getDatesByYear(year)) {
            apiCalendarResponse.getPosts().put(dateFormat.format(date), postRepository.getCountPostsByDate(date));
        }


        return apiCalendarResponse;

    }

    public ApiCalendarResponse getCountPostsForCurrentYear() {

        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.setTime(new Date());
        int currentYear = calendar.get(java.util.Calendar.YEAR);
        apiCalendarResponse.getYears().add(currentYear);

        for (Date date : postRepository.getDatesByYear(String.valueOf(currentYear))) {
            apiCalendarResponse.getPosts().put(dateFormat.format(date), postRepository.getCountPostsByDate(date));
        }

        return apiCalendarResponse;

    }

    public ApiStatisticResponse getStatistic() {

        int postsCount = postRepository.getAllAcceptedPosts().size();

        ArrayList<Post> posts = postRepository.getAllAcceptedPosts();

        int likesCount = 0;
        int dislikesCount = 0;
        int viewsCount = 0;

        for (Post p : posts) {

            viewsCount += p.getViewCount();

            List<PostVote> postVotes = postVoteRepository.findByPost(p);

            for (PostVote pv : postVotes) {
                if (pv.getValue() < 0) {
                    dislikesCount++;
                } else likesCount++;
            }
        }

        Page<Post> earlyPosts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(0, 1, Sort.by("time").ascending()));

        long firstPublication = earlyPosts.iterator().next().getTime().getTime() / 1000;

        ApiStatisticResponse apiStatisticResponse = new ApiStatisticResponse(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);

        if (settingsService.getGlobalSettings().isStatisticsIsPublic() == true) {

            return apiStatisticResponse;

        } else {

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

            if (user.getIsModerator() == 1) {

                return apiStatisticResponse;

            }

        }

        return apiStatisticResponse;


    }

    @Transactional
    public int addComment(AddCommentRequest request) throws NullPointerCommentTextException {

        if (request.getText() == null) {

            throw new NullPointerCommentTextException();

        }

        PostComment comment = new PostComment();

        if (request.getParentId() != 0) {

            comment = postCommentRepository.findById(request.getParentId()).orElseThrow();

        }


        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();



        comment.setTime(new Date());
        comment.setUserId(user);
        comment.setText(request.getText());
        comment.setPostId(postRepository.findByIdAcceptedPost(request.getPostId()).orElseThrow());
        postCommentRepository.save(comment);

        return comment.getId();

    }

    @Transactional
    public Result moderate(PostModerationRequest request) {

        Result result = new Result(true);
        Post post = postRepository.findByPostId(request.getPostId()).orElseThrow();
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        post.setModeratorId(user);


        if (request.getDecision().equals("accept"))
            post.setStatus(ModerationStatus.ACCEPTED);
        else post.setStatus(ModerationStatus.DECLINED);


        postRepository.save(post);
        return result;

    }

    @Transactional
    public Result likePost(int postId) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        Post post = postRepository.findByPostId(postId).orElseThrow();

        PostVote postVote = postVoteRepository.findByUserIdAndPostId(post, user);

        if (postVote == null) {
            postVote = new PostVote();
            postVote.setUserId(user);
            postVote.setPostId(post);
            postVote.setValue(1);
            postVoteRepository.save(postVote);
            return new Result(true);
        }

        if (postVote.getValue() > 0)
            return new Result(false);
        if (postVote.getValue() < 0)
            postVote.setValue(1);

        postVoteRepository.save(postVote);

        return new Result(true);

    }

    @Transactional
    public Result dislikePost(int postId) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        Post post = postRepository.findById(postId).orElseThrow();

        PostVote postVote = postVoteRepository.findByUserIdAndPostId(post, user);

        if (postVote == null) {
            postVote = new PostVote();
            postVote.setUserId(user);
            postVote.setPostId(post);
            postVote.setValue(-1);
            postVoteRepository.save(postVote);
            return new Result(true);
        }

        if (postVote.getValue() < 0)
            return new Result(false);
        if (postVote.getValue() > 0)
            postVote.setValue(-1);

        postVoteRepository.save(postVote);

        return new Result(true);

    }

    private Map<String, String> checkErrors(String title, String text) {

        Map<String, String> errors = new HashMap<>();

        if (title == null) {
            errors.put("title", "Заголовок отсутствует");
        }
        if (title.length() < 3) {
            errors.put("title", "Заголовок слишком короткий");
        }
        if (text.length() < 50) {
            errors.put("text", "Текст публикации слишком короткий");
        }

        return errors;
    }


}
