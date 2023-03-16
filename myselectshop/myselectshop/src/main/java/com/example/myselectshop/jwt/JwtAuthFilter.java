package com.example.myselectshop.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.myselectshop.dto.SecurityExceptionDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // Filter를 상속 받는다. 무조건 이 필터를 사용해야하는건 아님. ex. UsernamePasswordAuthenticationFilter, AbstractAuthenticationProcessingFilter(기본 Basic 필터) 등 있다.

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request); // resolveToken - 토큰을 가져오는 함수.

        if(token != null) {     // 게시글 작성과 같은 인증이 필요한 요청이 들어왔을때 토큰이 있는지 없는지 확인 후 인증 처리 // 토큰이 requestHeader에 있냐 없냐로 분기처리. ->
                                // 해주는 이유: 모든 URI가 permitAll로 허가가 된게 아님. 로그인 회원가입 하는 부분은 인증이 필요 없음. 이러한 URI 인증 부분은 토큰이 Header에 들어있지 않아서 이렇게 분기처리 안해주면 토큰 검증하는 부분에서 exception 발생. 그래서 분기처리를 해줌.
            if(!jwtUtil.validateToken(token)){ // validateToken으로 검증.
                jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            Claims info = jwtUtil.getUserInfoFromToken(token); // 토큰이 정상적으로 있다면,  claims 객체에 user 정보를 가져옴.
            setAuthentication(info.getSubject());
        }
        filterChain.doFilter(request,response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext(); // SecurityContext 생성
        Authentication authentication = jwtUtil.createAuthentication(username);  // Authentication 인증 객체에 넣기.
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context); // SecurityContextHolder에 넣음.
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) { // 토큰에 대한 오류가 발생시, 이 메소드를 통해 client에게 커스터마이징 한 exception 처리 값을 알려줌.
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg)); // ObjectMapper()로 변환.
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}