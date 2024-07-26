package com.luckyvicky.woosan.global.config;

import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.auth.filter.JWTUtil;
import com.luckyvicky.woosan.global.auth.filter.JwtAuthFilter;
import com.luckyvicky.woosan.global.auth.handler.CustomAccessDeniedHandler;
import com.luckyvicky.woosan.global.auth.handler.CustomAuthenticationEntryPoint;
import com.luckyvicky.woosan.global.auth.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class CustomSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTUtil jwtUtil;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

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

            "/api/matching/list", "/api/matching/increaseViewCount", "/api/admin/myBanner",
            "/api/matchingReply/*/replies",

            "/ws/**"
            ,
            "/api/matching/**",
            "/api/memberMatching/**",
            "/api/matchingReply/**"
    };

    private static final String[] PERMIT_USER_LIST = {
            "/api/member/delete", "/api/message/**", "/api/my/**",

            "/api/member-profile/modify",

            "/api/board/add/**", "/api/board/modify/**",
            "/api/board/delete/**", "/api/board/*/modify", "/api/board/best/**",

            "/api/replies/add/**", "/api/replies/delete/**", "/api/likes/toggle",

            "/api/report/add",

//            "/api/matching/regularly/list", "/api/matching/temporary/list",
//            "/api/matching/self/list", "/api/matching/user/*"
    };

    private static final String[] PERMIT_ADMIN_LIST = {
            "/api/admin/**"
    };

    private static final String[] PERMIT_LEVEL_2_LIST = {
            "/api/matching/temporary", "/api/matching/temporary/**",
            "/api/matching/self", "/api/matching/self/**",
            "/api/matching/*/update", "/api/matching/*/delete",
//            "/api/memberMatching/**",
//            "/api/matchingReply/*"
    };

    private static final String[] PERMIT_LEVEL_3_LIST = {
            "/api/matching/regularly"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

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
                .requestMatchers(PERMIT_LEVEL_2_LIST).hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .requestMatchers(PERMIT_LEVEL_3_LIST).hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .requestMatchers(PERMIT_ADMIN_LIST).hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        return http.build();
    }
}

