package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.UserDto;
import com.artrithm.backendapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto dto) {
        Long userId = userService.registerUser(dto);
        return ResponseEntity.ok("회원가입 성공! userId = " + userId);
    }

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto dto) {
        UserDto userInfo = userService.login(dto);
        return ResponseEntity.ok(userInfo);
    }

    // ✅ 사용자 정보 조회 (userId 파라미터 방식)
    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(@RequestParam Long userId) {
        UserDto user = userService.getUserInfo(userId);
        return ResponseEntity.ok(user);
    }
}
