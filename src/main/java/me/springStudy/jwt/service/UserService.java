package me.springStudy.jwt.service;

import java.util.Collections;
import java.util.Optional;
import me.springStudy.jwt.dto.UserDto;
import me.springStudy.jwt.entity.Authority;
import me.springStudy.jwt.entity.User;
import me.springStudy.jwt.excpetion.DuplicateMemberException;
import me.springStudy.jwt.excpetion.NotFoundMemberException;
import me.springStudy.jwt.repository.UserRepository;
import me.springStudy.jwt.utils.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * (1) 파라미터로 받은 userDto를 기준으로 해당 유저로 저장되어있는지 찾아보고, 없을 경우 다음 로직 실행
     * (2) 권한정보 생성, 생성된 권한정보와 함게 User 생성하여 저장
     */
    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }



    /** username 기준으로 정보를 Get */
    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }


    /** 현재 Security Context 에 저장된 username 정보만 Get */
    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}