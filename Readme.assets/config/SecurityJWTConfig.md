# I. Spring Security, JWT 설정
## 1. build.gradle 설정
Spring Security와 JWT 의존성을 추가합니다.

```java
dependencies {
	// spring security
	implementation 'org.springframework.boot:spring-boot-starter-security'

	// jwt 0.12.3
	implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
	implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
	implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'
}
```

## 2. application-properteis 설정
accessToken과 refreshToken의 만료 시간을 각각 1분, 1주일로 정하였으며 <br>
JWT를 생성하고 검증할 때 사용할 비밀 키를 설정합니다.

```java
spring.jwt.access.expirationTime=60000
spring.jwt.refresh.expirationTime=2592000000
spring.jwt.secret=**************************************************
```

## 3. CustomSecurityConfig
애플리케이션의 보안 설정을 정의한 클래스입니다. <br>
JWT를 사용해 인증을 수행하고, 특정 URL에 대해 접근 권한을 설정합니다.

### 핵심 설정
- **CORS와 CSRF 비활성화**: REST API 기반의 애플리케이션에서는 주로 사용하지 않기 때문에 비활성화하였습니다.
- **세션 관리**: ```SessionCreatonPolicy.STATELESS```로 설정하여 서버가 세션을 사용하지 않고 모든 요청을 독립적으로 처리하게 하였습니다.
- **JWT 필터 추가**: ```JwtAuthFilter```를 스프링 시큐리티 ```filterChain```에 추가하여 JWT를 기반으로 사용자를 인증하게 하였습니다.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // CSRF, CORS
    http.csrf((csrf) -> csrf.disable());
    http.cors(Customizer.withDefaults());

    // 세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 x
    http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS));

    // FormLogin, BasicHttp 비활성화
    http.formLogin((form) -> form.disable());
    http.httpBasic(AbstractHttpConfigurer::disable);

    // JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
    http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

    http.exceptionHandling((exceptionHandling) -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler));

    // 권한 규칙 작성
    http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(PERMIT_ALL_LIST).permitAll()
            .requestMatchers(PERMIT_USER_LIST).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .requestMatchers(PERMIT_LEVEL_2_LIST).hasAnyAuthority("ROLE_ADMIN", "LEVEL_2", "LEVEL_3", "LEVEL_4", "LEVEL_5")
            .requestMatchers(PERMIT_LEVEL_3_LIST).hasAnyAuthority("ROLE_ADMIN", "LEVEL_3", "LEVEL_4", "LEVEL_5")
            .requestMatchers(PERMIT_ADMIN_LIST).hasRole("ADMIN")
            .anyRequest().authenticated()
    );

    return http.build();
}
```
<br>

- **BCryptPasswordEncoder 빈 등록**: ```BCryptPasswordEncoder```를 빈으로 등록하여 비밀번호를 해시화하여 인코딩할 수 있게 하였습니다.

```java
@Bean
public BCryptPasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}
```
<br>

- **권한 설정**: 회원 정책, 이용 가능 정책과 회원의 정보에 따라 접근을 허용하거나 제한하였습니다.
  - 비회원: 게시글, 댓글 조회만 가능
  - 회원
    - 게시글, 댓글, 쪽지, 신고 등 CRUD 모두 가능
    - 단, LEVEL_1 회원은 만남 서비스 조회만 가능
    - LEVEL_2 이상 회원: 만남 서비스 중 셀프 소개팅, 번개 모임 CRUD 가능
    - LEVEL_3 이상 회원: 만남 서비스 중 정기 모임 CRUD 가능
    - 관리자 페이지: 관리자만 CRUD 가능

```java
private static final String[] PERMIT_ALL_LIST = {

        "api/member/email/**", "api/member/nickname/**", "api/member/signUp/**",
        "api/member/sendEmail/**", "api/member/updatePw/**", "/api/member/info/**",
        "/api/member/sendJoinCode/**", "/api/member/joinCode/**",
        "/api/auth/login", "/api/oauth/**", "/api/auth/token/**",

        "/api/member-profile/*",

        "/api/board/cs/notices/**", "/api/board/notices/**", "/api/board/best/**", "/api/board",
        "/api/board/*", "/api/board/*/translate", "/api/board/*/summary",
        "/api/board/search", "/api/board/autocomplete", "/api/board/ranking",
        "/api/board/cs/notice", "/api/board/notices", "/api/board/weekly/best",

        "/api/replies/*", "/api/likes/status", "/api/report/add/**",

        "/api/matching/list/*", "/api/matching/increaseViewCount", "/api/admin/myBanner",
        "/api/matchingReply/*/replies",

        "/ws/**"
};

private static final String[] PERMIT_USER_LIST = {
        "/api/member/delete", "/api/message/**", "/api/my/**",

        "/api/member-profile/modify",

        "/api/board/add/**", "/api/board/modify/**",
        "/api/board/delete/**", "/api/board/*/modify", "/api/board/best/**",

        "/api/replies/add/**", "/api/replies/delete/**", "/api/likes/toggle",

        "/api/report/add",

        "/api/matching/regularly/list", "/api/matching/temporary/list",
        "/api/matching/self/list", "/api/matching/user/*",
        "/api/memberMatching/list/*", "/api/memberMatching/pending/*"
};

private static final String[] PERMIT_ADMIN_LIST = {
        "/api/admin/**"
};

private static final String[] PERMIT_LEVEL_2_LIST = {
        "/api/matching/temporary", "/api/matching/temporary/**",
        "/api/matching/self", "/api/matching/self/**",
        "/api/matching/*/update", "/api/matching/*/delete",
        "/api/memberMatching/**",
        "/api/matchingReply/*"
};

private static final String[] PERMIT_LEVEL_3_LIST = {
        "/api/matching/regularly"
};
```
<br>
---

# II. JWT 유틸리티
## JWTUtil
JWT를 생성하고 관리하는 클래스입니다.

### 핵심 기능
- **JWT 생성**: 사용자의 정보를 바탕으로 JWT를 생성합니다. 이 토큰은 사용자 ID, 이메일, 닉네임, 권한 레벨 등의 정보를 포함합니다.

```java
/**
* Access Token 생성
* @param member
* @return Access Token String
*/
public String createAccessToken(CustomUserInfoDTO member) {
	return createToken(member, accessTokenExpTime);
}

public String createRefreshToken(CustomUserInfoDTO member) {
	String refreshToken = createToken(member, refreshTokenExpTime);
	RefreshToken token = new RefreshToken(member.getId(), refreshToken);
	refreshTokenRepository.save(token);
	return refreshToken;
}

/**
* JWT 생성
* @param member
* @param expireTime
* @return JWT String
*/
private String createToken(CustomUserInfoDTO member, long expireTime) {
	Claims claims = Jwts.claims();
	claims.put("id", member.getId());
	claims.put("email", member.getEmail());
	claims.put("nickname", member.getNickname());
	claims.put("isActive", member.getIsActive());
	claims.put("memberType", member.getMemberType());
	claims.put("socialType", member.getSocialType());
	claims.put("level", member.getLevel());
	claims.put("point", member.getPoint());
	claims.put("nextPoint", member.getNextPoint());
	
	ZonedDateTime now = ZonedDateTime.now();
	ZonedDateTime tokenValidity = now.plusSeconds(expireTime);
	
	return Jwts.builder()
		.setClaims(claims)
		.setIssuedAt(Date.from(now.toInstant()))
		.setExpiration(Date.from(tokenValidity.toInstant()))
		.signWith(key, SignatureAlgorithm.HS256)
		.compact();
}
```

- **JWT 검증**: 클라이언트로부터 받은 JWT가 유효한지, 위변조되지 않았는지 검증합니다.

```java
/**
* JWT 검증
* @param token
* @return IsValidate
*/
public boolean validateToken(String token) {
	try {
	    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	    return true;
	} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
	    log.info("Invalid JWT Token", e);
	} catch (ExpiredJwtException e) {
	    log.info("Expired JWT Token", e);
	} catch (UnsupportedJwtException e) {
	    log.info("Unsupported JWT Token", e);
	} catch (IllegalArgumentException e) {
	    log.info("JWT claims string is empty.", e);
	}
	return false;
}
```

- **토큰에서 정보 추출**: JWT에서 사용자 정보를 추출하여 이후의 요청 처리에 사용합니다.

```java
/**
* Token에서 User ID 추출
* @param token
* @return User ID
*/
public String getEmail(String token) {
	return parseClaims(token).get("email", String.class);
}

/**
* JWT Claims 추출
* @param accessToken
* @return JWT Claims
*/
public Claims parseClaims(String accessToken) {
	try {
	    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
	} catch(ExpiredJwtException e) {
	    return e.getClaims();
	}
}
```

- **RefreshToken 처리**: 만료된 액세스 토큰을 갱신하기 위한 리프레시 토큰도 관리합니다.

```java

```

<br>
---

# III. 사용자 인증 서비스
## CustomUserDetailsService

### 핵심 기능
- **사용자 정보 로드**: 이메일을 통해 사용자 정보를 DB에서 로드합니다.

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        CustomUserInfoDTO dto = mapper.memberToCustomUserInfoDTO(member);

        return new CustomUserDetails(dto);
    }
}
```

- **사용자 정보 매핑**: 로드한 사용자 정보를 ```CustomUserInfoDTO```로 변환하여 반환합니다. 이 정보는 JWT를 생성하고 검증하는 과정에 사용됩니다.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomUserInfoDTO {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Boolean isActive;
    private int point;
    private int nextPoint;
    private String level;
    private String memberType;
    private String socialType;

    public <T> CustomUserInfoDTO(T id, T email, T memberType, T level) {
    }
}
```

- **CustomUserDetails**: ```CustomUserInfoDTO```를 기반으로 Spring Security가 요구하는 ```UserDetails``` 인터페이스를 구현하는 객체를 생성하여 반환합니다.

```java
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfoDTO member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + member.getMemberType().toString());
        roles.add(member.getLevel().toString());

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
  ```

<br>
---

# IV. JWT 필터
## JWTAuthFilter
클라이언트의 요청에서 JWT를 추출하고 이를 검증하여 인증된 사용자로 간주하게 하는 클래스입니다.

<br>
---

# V. Custom Handler 클래스
## 1. CustomAccessDeniedHandler
사용자가 권한이 없는 리소스에 접근하려고 할 때 403 Forbidden 에러를 반환하는 클래스입니다.

## 2. AuthenticationEntryPoint
인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 401 Unauthorized 에러를 반환하는 클래스입니다.
