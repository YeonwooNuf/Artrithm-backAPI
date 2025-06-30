package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordRepository keywordRepository;

    @GetMapping("/random")
    public List<String> getRandomKeywords(@RequestParam(name = "count", defaultValue = "5") int count) {
        List<String> allKeywords = keywordRepository.findAll()
                .stream()
                .map(keyword -> "#" + keyword.getName())
                .collect(Collectors.toList());

        Collections.shuffle(allKeywords);
        return allKeywords.stream().limit(count).collect(Collectors.toList());
    }
}