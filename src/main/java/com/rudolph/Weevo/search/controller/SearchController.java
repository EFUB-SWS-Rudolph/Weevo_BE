package com.rudolph.Weevo.search.controller;

import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import com.rudolph.Weevo.search.dto.response.SearchResponse;
import com.rudolph.Weevo.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @RequestParam String keyword
    ) {
        SearchResponse resp = searchService.search(keyword);
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }
}
