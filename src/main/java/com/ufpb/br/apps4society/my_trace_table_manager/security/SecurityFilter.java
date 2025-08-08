package com.ufpb.br.apps4society.my_trace_table_manager.security;

import com.ufpb.br.apps4society.my_trace_table_manager.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private TokenProvider tokenProvider;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityFilter(TokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isPublicRoute(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenOfRequest(request);

        if (token != null) {
            String userIdStr = tokenProvider.getSubjectByToken(token);
            Long userId = Long.valueOf(userIdStr);

            UserDetails userDetails = userDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(user);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(String method, String path) {
        return
            (method.equals("GET") && path.equals("/v1/user/all")) ||
            (method.equals("POST") && path.equals("/v1/user/login")) ||
            (method.equals("GET") && path.startsWith("/v1/theme")) ||
            (method.equals("POST") && path.startsWith("/v1/trace/check")) ||
            (method.equals("GET") && path.startsWith("/v1/trace")) ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/h2") ||
            path.equals("/");
    }

    private String getTokenOfRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return null;
        }

        return token.substring("Bearer ".length());
    }
}
