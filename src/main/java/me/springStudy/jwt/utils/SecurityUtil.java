package me.springStudy.jwt.utils;

import lombok.extern.slf4j.Slf4j;
import me.springStudy.jwt.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {

    }

    /** Security Context 의 Authentication 객체를 이용해 username 을 리턴
     * 해당 객체가 저장되는 시점은 doFilter 메소드에 setAuthentication 하는 부분 */
    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        System.out.println("authentication.getPrincipal() instanceof : " + authentication.getPrincipal());
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String)
            username = (String) authentication.getPrincipal();

        return Optional.ofNullable(username);
    }
}