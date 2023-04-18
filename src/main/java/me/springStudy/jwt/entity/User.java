package me.springStudy.jwt.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity                       // database 테이블과 1:1로 mapping 되는 객체
@Table(name = "`USER`")       // 테이블명을 user 로 지정,  ``가 들어간 이유는 없으면 에러남, 예약어이기 때문인가?
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany     // user - authority 테이블의 다대다 관계 설정
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}