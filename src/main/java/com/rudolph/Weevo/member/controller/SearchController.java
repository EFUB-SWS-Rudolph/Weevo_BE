package com.rudolph.Weevo.member.controller;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.member.dto.response.SearchResponseDto;
import com.rudolph.Weevo.member.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
     private final SearchService searchService;

     @GetMapping
    public ResponseEntity<SearchResponseDto> search(
             @RequestParam String keyword,
             @AuthenticationPrincipal CustomUserPrincipal user
             ){
         SearchResponseDto response = searchService.searchAll(keyword, user.getMemberId());
         return ResponseEntity.ok(response);
    }
}
