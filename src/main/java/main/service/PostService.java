package main.service;

import lombok.AllArgsConstructor;
import main.api.request.AddCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.response.*;
import main.exception.CommentNotFoundException;
import main.exception.NullPointerCommentTextException;
import main.exception.PostNotFoundException;
import main.model.*;
import main.model.repository.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentRepository postCommentRepository;
    private final TagRepository tagRepository;
    private final SettingsService settingsService;


    @Transactional
    public PostsListResponse getPosts(String mode, int offset, int limit) {

        int page = offset / limit;
        Page<Post> posts = null;

        switch (mode) {
            case ("early"):
                posts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit, Sort.by("time_post").ascending()));
                break;
            case ("best"):
                posts = postRepository.getBestPosts(PageRequest.of(page, limit));
                break;
            case ("popular"):
                posts = postRepository.getPopularPosts(PageRequest.of(page, limit));
                break;
            default:
                posts = postRepository.getActivePosts("ACCEPTED", PageRequest.of(page, limit, Sort.by("time_post").descending()));
                break;
        }

        return getPostsList(posts);
    }

    @Transactional
    public PostsListResponse getPostsByQuery(String query, int offset, int limit) {

        int page = offset / limit;
        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByTitleContaining(query, pageWithTenElements);

        return getPostsList(posts);
    }

    @Transactional
    public ApiPostResponseById getPostsById(int id) {

        Post post = postRepository.findByIdAcceptedPost(id).orElseThrow(() -> new PostNotFoundException("Походу нет такого поста :("));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isUserAuthenticated()) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
            if (user.getIsModerator() != 1 && !post.getUserId().equals(user)) {
                int count = post.getViewCount();
                post.setViewCount(++count);
                postRepository.save(post);
            }
        }

        ApiPostResponseById apiPostResponseById = new ApiPostResponseById(post);

        for (PostComment pc : post.getComments()) {
            apiPostResponseById.addComment(pc.getId(),
                    pc.getTime().toLocalTime().toEpochSecond(LocalDate.now(), ZoneOffset.UTC) / 1000,
                    pc.getText(), pc.getUserId().getId(), pc.getUserId().getName(), pc.getUserId().getPhoto());
        }

        return apiPostResponseById;
    }

    @Transactional
    public PostsListResponse getPostsByDate(String date, int offset, int limit) {

        int page = offset / limit;
        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByDate(date, pageWithTenElements);

        return getPostsList(posts);

    }

    @Transactional
    public PostsListResponse getPostsByTag(String name, int offset, int limit) {

        int page = offset / limit;
        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.findByTagName(name, pageWithTenElements);

        return getPostsList(posts);

    }

    @Transactional
    public PostsListResponse getPostsForModeration(String status, int offset, int limit) {

        int page = offset / limit;
        Pageable pageWithTenElements = PageRequest.of(page, limit);
        Page<Post> posts = postRepository.getActivePosts(status, pageWithTenElements);

        return getPostsList(posts);

    }

    @Transactional
    public PostsListResponse getMyPosts(String status, int offset, int limit) {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        int page = offset / limit;
        Page<Post> posts = null;

        switch (status) {
            case ("inactive"):
                posts = postRepository.getMyInActivePosts(PageRequest.of(page, limit), user.getId());
                break;
            case ("pending"):
                posts = postRepository.getMyActivePosts("NEW", PageRequest.of(page, limit), user.getId());
                break;
            case ("declined"):
                posts = postRepository.getMyActivePosts("DECLINED", PageRequest.of(page, limit), user.getId());
                break;
            case ("published"):
                posts = postRepository.getMyActivePosts("ACCEPTED", PageRequest.of(page, limit), user.getId());
                break;
        }

        return getPostsList(Optional.of(posts).orElseThrow(() -> new PostNotFoundException("Увы, посты не найдены")));

    }

    @Transactional
    public Result addPost(long timestamp, int active, String title, List<String> tags, String text) {

        text = cleanTextFromTags(text);
        Map<String, String> errors = checkErrors(title, text);

        if (!errors.isEmpty()) {
            return new ErrorResponse(false, errors);
        }

        Post post = new Post();
        Result result = new Result(true);
        LocalDateTime date = LocalDateTime.now();

        if (date.toEpochSecond(ZoneOffset.UTC) > timestamp) {
            date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        }

        post.setTime(date);
        post.setIsActive(active);
        if (settingsService.getSettingsResponse().isPostPremoderation()) {
            post.setStatus(ModerationStatus.NEW);
        } else if (post.getIsActive() == 1) {
            post.setStatus(ModerationStatus.ACCEPTED);
        }
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

        text = cleanTextFromTags(text);

        Map<String, String> errors = checkErrors(title, text);

        if (!errors.isEmpty()) {
            return new ErrorResponse(false, errors);
        }

        Post post = postRepository.findById(id).orElseThrow();
        Result result = new Result(true);
        LocalDateTime date = LocalDateTime.now();

        if (date.toEpochSecond(ZoneOffset.UTC) > timestamp) {
            date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        }

        post.setTime(date);
        post.setIsActive(active);
        post.setText(text);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        if (user.getIsModerator() == 0) {
            post.setStatus(ModerationStatus.valueOf("NEW"));
        }

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

        postRepository.getDatesByYear(year)
                .forEach(date -> apiCalendarResponse
                        .getPosts()
                        .put(dateFormat.format(date), postRepository.getCountPostsByDate(date)));

        return apiCalendarResponse;

    }

    public ApiCalendarResponse getCountPostsForCurrentYear() {

        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int currentYear = LocalDateTime.now().getYear();

        apiCalendarResponse.getYears().add(currentYear);

        for (Date date : postRepository.getDatesByYear(String.valueOf(currentYear))) {
            apiCalendarResponse.getPosts().put(dateFormat.format(date), postRepository.getCountPostsByDate(date));
        }
        return apiCalendarResponse;

    }

    public ApiStatisticResponse getStatistic() {

        int postsCount = postRepository.getAllAcceptedPosts().size();

        ArrayList<Post> posts = postRepository.getAllAcceptedPosts();

        long firstPublication = postRepository.getEarlyPosts().orElse(null)
                .get(0)
                .getTime()
                .toEpochSecond(ZoneOffset.UTC);

        ApiStatisticResponse apiStatisticResponse = new ApiStatisticResponse();
        apiStatisticResponse.setPostsCount(postsCount);
        apiStatisticResponse.setFirstPublication(firstPublication);

        if (posts.size() > 0) {
            countVoteAndViews(posts, apiStatisticResponse);
        }

        if (settingsService.getSettingsResponse().isStatisticsIsPublic()) {

            return apiStatisticResponse;

        } else {
            if (isUserAuthenticated() && isModerator())
                return apiStatisticResponse;
        }
        return null;
    }


    @Transactional
    public ApiCommentResponse addComment(AddCommentRequest request) throws NullPointerCommentTextException {

        if (request.getText() == null) {
            throw new NullPointerCommentTextException();
        }

        PostComment comment = new PostComment();

        if (request.getParentId() != 0) {
            postCommentRepository.findById(request.getParentId()).orElseThrow(() -> new CommentNotFoundException("Комментария больше нет"));
            comment.setParentId(request.getParentId());

        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        comment.setTime(LocalDateTime.now());
        comment.setUserId(user);
        comment.setText(cleanTextFromTags(request.getText()));
        comment.setPostId(postRepository.findByIdAcceptedPost(request.getPostId()).orElseThrow());
        postCommentRepository.save(comment);

        return new ApiCommentResponse(comment.getId());

    }

    @Transactional
    public Result moderate(PostModerationRequest request) {

        Result result = new Result(true);
        Post post = postRepository.findByPostId(request.getPostId()).orElseThrow();
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        post.setModeratorId(user);

        if (request.getDecision().equals("accept")) {
            post.setStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setStatus(ModerationStatus.DECLINED);
        }

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

        if (postVote.getValue() > 0) {
            return new Result(false);
        }
        if (postVote.getValue() < 0) {
            postVote.setValue(1);
        }

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

        if (postVote.getValue() < 0) {
            return new Result(false);
        }
        if (postVote.getValue() > 0) {
            postVote.setValue(-1);
        }

        postVoteRepository.save(postVote);

        return new Result(true);

    }

    @Override
    public ApiStatisticResponse getMyStatistic() {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        List<Post> posts = postRepository.findPostsByUserId(user.getId());

        long firstPublication = 0L;
        int postsCount = posts.size();

        ApiStatisticResponse apiStatisticResponse = new ApiStatisticResponse();
        apiStatisticResponse.setPostsCount(postsCount);

        if (posts.size() > 0) {
            firstPublication = posts
                    .get(0)
                    .getTime()
                    .toEpochSecond(ZoneOffset.UTC);
            countVoteAndViews(posts, apiStatisticResponse);
            apiStatisticResponse.setFirstPublication(firstPublication);
        }

        return apiStatisticResponse;

    }

    private Map<String, String> checkErrors(String title, String text) {

        Map<String, String> errors = new HashMap<>();

        if (title == null) {
            errors.put("title", "Заголовок отсутствует");
        } else if (title.length() < 3) {
            errors.put("title", "Заголовок слишком короткий");
        }
        if (text == null) {
            errors.put("text", "Текст поста не задан");
        } else if (text.length() < 50) {
            errors.put("text", "Текст публикации слишком короткий");
        }

        return errors;
    }

    private PostsListResponse getPostsList(Page<Post> posts) {

        List<PostsListResponse.PostResponse> postsSingleResponse = posts
                .stream()
                .map(PostsListResponse.PostResponse::new)
                .collect(Collectors.toList());

        return PostsListResponse
                .builder()
                .count(posts.getTotalElements())
                .posts(postsSingleResponse)
                .build();
    }

    private String cleanTextFromTags(String text) {
        String html = text;
        text = Jsoup.clean(html, Whitelist.none());
        return text;
    }

    private boolean isUserAuthenticated() {
        return !SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
    }

    private boolean isModerator() {

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        return user.getIsModerator() == 1;

    }

    private void countVoteAndViews(List<Post> posts, ApiStatisticResponse response) {

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

        response.setDislikesCount(dislikesCount);
        response.setLikesCount(likesCount);
        response.setViewsCount(viewsCount);
    }


}
