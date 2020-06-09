package com.backend.template.service;

import com.backend.template.dto.input.CreateStoryComment;
import com.backend.template.dto.input.UpdateStoryComment;
import com.backend.template.dto.response.PageResponseBuilder;
import com.backend.template.exception.RecordNotFoundException;
import com.backend.template.exception.RequestInvalidException;
import com.backend.template.locale.Translator;
import com.backend.template.model.StoryComment;
import com.backend.template.paging.PagingInfo;
import com.backend.template.repositories.StoryCommentRepository;
import com.backend.template.repositories.UserStoryRepository;
import com.backend.template.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class StoryCommentService {

    @Autowired
    StoryCommentRepository storyCommentRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    ModelMapper mapper = new ModelMapper();

    public PagingInfo<StoryComment> getAllStoryComments(Pageable pageable) {
        Page<StoryComment> storyComments = storyCommentRepository.findAll(pageable);
        return PageResponseBuilder.buildPagingData(storyComments, storyComments.getPageable());
    }

    public StoryComment getStoryComment(String id) {
        return storyCommentRepository.findById(id).orElseThrow(()
                -> new RecordNotFoundException(Translator.toLocale("error.msg.record.not_found")));
    }

    public StoryComment createStoryComment(CreateStoryComment createStoryComment, Long userId) {
        if (!userStoryRepository.findById(createStoryComment.getStoryId()).isPresent())
            throw new RecordNotFoundException(Translator.toLocale("error.msg.story.not_found"));
        StoryComment storyComment = mapper.map(createStoryComment, StoryComment.class);
        storyComment.setCommentedTime(Utils.getUnixTimeInSecond());
        storyComment.setUserId(userId);
        return storyCommentRepository.save(storyComment);
    }

    public StoryComment updateStoryComment(UpdateStoryComment updateStoryComment, Long userId) {
        Optional<StoryComment> storyComment = storyCommentRepository.findById(updateStoryComment.getId());
        updateStoryCommentValidation(storyComment, userId);
        storyComment.get().setContent(updateStoryComment.getContent());
        storyComment.get().setStatus(updateStoryComment.getStatus());

        return storyCommentRepository.save(storyComment.get());
    }

    private void updateStoryCommentValidation(Optional<StoryComment> storyComment, Long userId) {
        if (!storyComment.isPresent())
            throw new RecordNotFoundException(Translator.toLocale("error.msg.record.not_found"));
        if (storyComment.get().getUserId() != userId) {
            throw new RequestInvalidException(Translator.toLocale("error.msg.request.invalid"));
        }
    }
}