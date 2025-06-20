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

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {
        Long userId = userService.registerUser(userDto);
        return ResponseEntity.ok("회원가입 성공! userId = " + userId);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        UserDto userInfo = userService.login(userDto);
        System.out.println("안녕하세요");
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/{userId}") // ✅ RESTful 스타일
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long userId) {
        UserDto user = userService.getUserInfo(userId);
        return ResponseEntity.ok(user);
    }

    // user 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long userId,
            @ModelAttribute UserDto userDto) { // ✅ multipart/form-data를 받을 경우 @RequestBody → @ModelAttribute

        userService.updateUserInfo(userId, userDto);
        return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
    }
}
