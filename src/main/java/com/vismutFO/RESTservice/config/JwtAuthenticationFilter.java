package com.vismutFO.RESTservice.config;

import java.io.IOException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vismutFO.RESTservice.services.JwtService;
import com.vismutFO.RESTservice.services.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    private final MeterRegistry meterRegistry;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            userName = jwtService.extractUserName(jwt);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!");
            Counter timeExpiredCounter = Counter.builder("timeExpiredAttempts")
                .tag("title", "timeExpiredAttempts")
                .description("a number of attempts go to someone else's profile with expired jwt")
                .register(meterRegistry);
            timeExpiredCounter.increment();
            throw e;
        }
        if (StringUtils.isNotEmpty(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService()
                    .loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        try {
            filterChain.doFilter(request, response);
        }
        catch (io.jsonwebtoken.ExpiredJwtException e) {
            if (jwtService.extractClaimFromHeader(authHeader, "type").equals("DISPOSABLE")) {
                System.out.println("!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!!!");
                Counter timeExpiredCounter = Counter.builder("timeExpiredAttempts")
                        .tag("title", "timeExpiredAttempts")
                        .description("a number of attempts go to someone else's profile with expired jwt")
                        .register(meterRegistry);
                timeExpiredCounter.increment();
            }
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}