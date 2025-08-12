package com.koa.RingDong.domain.mandalart.controller;

import com.koa.RingDong.domain.mandalart.service.MandalartService;
import com.koa.RingDong.domain.mandalart.dto.UpdateMainBlockRequest;
import com.koa.RingDong.global.dto.ApiResponse;
import com.koa.RingDong.domain.mandalart.dto.MainBlockResponse;
import com.koa.RingDong.global.security.oauth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mandalart")
public class MandalartController {

    private final MandalartService mandalartService;

    @Operation(summary = "만다라트 전체 9x9 생성")
    @PostMapping()
    public ResponseEntity<ApiResponse<MainBlockResponse>> createMandalart(
            @RequestBody @Valid UpdateMainBlockRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        MainBlockResponse response = mandalartService.createMandalart(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("만다라트 전체 9x9 생성", response));
    }

    @Operation(summary = "만다라트 전체 9x9 조회")
    @GetMapping()
    public ResponseEntity<ApiResponse<MainBlockResponse>> getMandalart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        MainBlockResponse response = mandalartService.getMandalart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("만다라트 전체 9x9 조회", response));
    }

    @Operation(summary = "만다라트 전체 9x9 수정")
    @PatchMapping()
    public ResponseEntity<ApiResponse<MainBlockResponse>> updateMandalart(
            @RequestBody @Valid UpdateMainBlockRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MainBlockResponse response = mandalartService.updateMandalart(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("만다라트 전체 9x9 수정", response));
    }
}
