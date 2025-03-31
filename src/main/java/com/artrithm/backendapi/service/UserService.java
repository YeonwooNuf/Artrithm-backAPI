package com.artrithm.backendapi.service;

import com.artrithm.backendapi.entity.User;
import com.artrithm.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        User result = null;
        result = userRepository.save(user);
        return result;
    }
    public List<User> getAllUsers() {
        List<User> result = null;
        result = userRepository.findAll();
        return result;
    }
    public User getUserById(Long id) {
        User result = null;
        result = userRepository.findById(id).orElse(result);
        return result;
    }

    public User updateUser(Long id, User userDetails){
        User result = null;
        result = userRepository.findById(id).map(user -> {
            if(userDetails.getUsername() != null)
                user.setUsername(userDetails.getUsername());
            if(userDetails.getPassword() != null)
                user.setPassword(userDetails.getPassword());
            if(userDetails.getEmail() != null)
                user.setEmail(userDetails.getEmail());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User Not Found"));
        return result;
    }

    public User deleteUserById(Long id) {
        User result = null;
        userRepository.deleteById(id);
        return result;
    }

    public User authenticate(String name, String password ){
        User user = userRepository.findByUsername(name).orElseThrow(()->new RuntimeException("사용자를 찾을 수 없습니다."));
        if(user.getPassword().equals(password)){
            return user;
        }else{
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}
