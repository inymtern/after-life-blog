package com.after.life.blog.config;

import cn.hutool.core.collection.ListUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-25 13:56
 */
public class Oauth2JwtFilter extends OncePerRequestFilter {


    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public Oauth2JwtFilter(JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if(!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwtToken;
        String servletPath = request.getServletPath();
        if(!servletPath.contains("oauth2")) {
            filterChain.doFilter(request, response);
            return;
        }
        if(servletPath.contains("oauth2/authorize")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7);
        try {
//            Jwt jwt = jwtDecoder.decode(jwtToken);
////            String subject = jwt.getSubject();
//            Map<String, Object> claims = jwt.getClaims();
//            // username
//            String subject = (String)claims.get("sub");
//            // client_id
//            List<String> audList = (List<String>)claims.get("aud");
//            // scope
//            List<String> scopeList = (List<String>)claims.get("scope");
//            String iss = (String)claims.get("iss");
//            // 过期时间
//            Instant exp = (Instant)claims.get("exp");
//            // 签发时间
//            Instant iat = (Instant)claims.get("iat");
            BearerTokenAuthenticationToken bearerTokenAuthenticationToken = new BearerTokenAuthenticationToken(jwtToken);
            Authentication authenticate = jwtAuthenticationProvider.authenticate(bearerTokenAuthenticationToken);
//            RegisteredClient client = registeredClientRepository.findByClientId(audList.get(0));
//            assert client != null;
//            OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = new OAuth2ClientAuthenticationToken(client, CLIENT_SECRET_POST, null);
            SecurityContextHolder.getContext().setAuthentication(authenticate);

//            new JwtAuthenticationToken()
//            userEmail = jwtDecoder.extractUsername(jwtToken);
//            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//                if(jwtUtils.isTokenValid(jwtToken, userDetails)) {
//                    UsernamePasswordAuthenticationToken authenticationToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                }
//            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(502);
        }
    }
}
