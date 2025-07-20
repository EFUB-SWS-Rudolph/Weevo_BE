package com.rudolph.Weevo.tag.service;

import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    public final TagRepository tagRepository;

    public List<Tag> findByNames(List<String> names) {
        List<Tag> tags = tagRepository.findAllByNameIn(names);
        if (tags.size() != names.size()) {
            throw new IllegalArgumentException("선택한 키워드 중 존재하지 않는 항목이 있습니다.");
        }
        return tags;
    }
}

