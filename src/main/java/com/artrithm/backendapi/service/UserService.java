package com.artrithm.backendapi.service;

<<<<<<< Updated upstream
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
=======
import com.artrithm.backendapi.dto.UserDto;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long registerUser(UserDto userDto) {
        // 중복 체크
        if (userRepository.existsByLoginId(userDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByPhoneNumber(userDto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // 사용자 저장
        User user = User.builder()
                .loginId(userDto.getLoginId())
                .password(encodedPassword)
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .build();

        return userRepository.save(user).getId();
    }

    public UserDto login(UserDto userDto) {
        User user = userRepository.findByLoginId(userDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
>>>>>>> Stashed changes
    }
}
