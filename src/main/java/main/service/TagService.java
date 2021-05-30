package main.service;


import main.api.response.ApiTagResponse;
import main.model.Post;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService implements ITagService {


    @Autowired
    private final TagRepository tagRepository;

    @Autowired
    private final PostRepository postRepository;

    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public ApiTagResponse getTags() {

        Set<Tag> tags = new HashSet<>(tagRepository.findAll());

        if (tags.isEmpty()) {
            return new ApiTagResponse();
        }

        double totalPosts = postRepository.getAllAcceptedPosts().size();
        Tag tagWithMaxPosts = tagRepository.getTagWithMaxPostsCount().orElseThrow();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagWithMaxPosts.getName());
        double weight = countMaxPostsByTag / totalPosts;
        double factor = 1.0 / weight;

        ApiTagResponse apiTagResponse = new ApiTagResponse();

        for (Tag tag : tags) {
            for (Post post : tag.getPosts()){
                if (post.getStatus().name().equals("ACCEPTED")){
                    apiTagResponse.addTag(tag.getName(), postRepository.getCountPostsByTagName(tag.getName()) / totalPosts * factor);
                }
            }
        }

        return apiTagResponse;

    }

    public ApiTagResponse getTagsByQuery(String query) {
        List<Tag> tags = tagRepository.findByNameContaining(query);

        if (tags.isEmpty())

            return new ApiTagResponse();

        Tag tagWithMaxPosts = tagRepository.getTagWithMaxPostsCount().orElseThrow();
        double totalPosts = postRepository.getAllAcceptedPosts().size();
        double countMaxPostsByTag = postRepository.getCountPostsByTagName(tagWithMaxPosts.getName());
        double weight = countMaxPostsByTag / totalPosts;
        double factor = 1.0 / weight;

        ApiTagResponse apiTagResponse = new ApiTagResponse();

        for (Tag tag : tags) {

            apiTagResponse.addTag(tag.getName(), postRepository.getCountPostsByTagName(tag.getName()) / totalPosts * factor);

        }

        return apiTagResponse;
    }
}
