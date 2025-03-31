package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.entity.LoginDto;
import com.artrithm.backendapi.entity.User;
import com.artrithm.backendapi.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        User result = null;
        result = userService.createUser(user);
        return result;
    }

    @GetMapping
    public List<User> getAllUsers(HttpSession session) {
        List<User> result = null;
        result = userService.getAllUsers();
        return result;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable long id) {
        User result = null;
        result = userService.getUserById(id);
        return result;
    }

    @GetMapping("/delete/{id}")
    public User deleteUserById(@PathVariable long id) {
        User result = null;
        result = userService.deleteUserById(id);
        return result;
    }
    @PostMapping("/update/{id}")
    public User updateUser(@RequestBody User userDetails, @PathVariable long id) {
        User result = null;
        result = userService.updateUser(id,userDetails);
        return result;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto, HttpSession session) {
        User user = userService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        if (user != null) {
            session.setAttribute("userId", user.getId());
            return user.getUsername()+" 로그인 성공";
        } else {
            return "로그인 실패";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "로그아웃";
    }

    @GetMapping("/session-check")
    public String checkLogin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId != null) {
            return "로그인 상태 userId = " + userId;
        } else {
            return "로그인 안됨 (세션 없음)";
        }
    }

}
