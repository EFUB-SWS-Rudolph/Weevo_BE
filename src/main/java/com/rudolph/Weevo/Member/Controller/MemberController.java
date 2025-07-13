package com.rudolph.Weevo.Member.Controller;

import com.rudolph.Weevo.Member.DTO.request.InfoRequest;
import com.rudolph.Weevo.Member.DTO.response.InfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@Tag(name = "User", description = "회원 관련 api")
public class MemberController {

    //최초 회원가입 시 추가정보 입력
    @Operation(
            summary = "최초 회원가입시 추가정보 입력",
            description = "소셜 로그인 후 최초 회원가입이라면 추가정보를 등록합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InfoResponse.class)
            )
    )
    @PostMapping("/info")
    public InfoResponse signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 추가정보 dto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InfoRequest.class)
                    )
            )
            @RequestBody InfoRequest request
    ){
        //실제 로직
        return new InfoResponse("회원가입 성공!");
    }
}
