package com.after.life.blog.controller;

import cn.hutool.core.map.MapBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-24 12:05
 */
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthorizationController {

    private final OAuth2AuthorizationCodeRequestAuthenticationConverter oAuth2AuthorizationCodeRequestAuthenticationConverter;
    private final OAuth2AuthorizationCodeAuthenticationConverter oAuth2AuthorizationCodeAuthenticationConverter;
    private final OAuth2RefreshTokenAuthenticationConverter oAuth2RefreshTokenAuthenticationConverter;
    private final OAuth2AuthorizationCodeRequestAuthenticationProvider oAuth2AuthorizationCodeRequestAuthenticationProvider;
    private final OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider;
    private final OAuth2RefreshTokenAuthenticationProvider oAuth2RefreshTokenAuthenticationProvider;
    private final InMemoryOAuth2AuthorizationService inMemoryOAuth2AuthorizationService;
    private final AuthenticationManager authenticationManager;
    private final ClientSecretPostAuthenticationConverter clientSecretPostAuthenticationConverter;
    private final ClientSecretAuthenticationProvider clientSecretAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    /**
     * response_type=code
     * scope=
     * state=
     * client_id=
     * redirect_uri=
     * nonce=
     * @param request
     * @return
     */
    @GetMapping("/oauth2/authorize")
    public ResponseEntity<OAuth2AuthorizationCode> oauth2Authorize(HttpServletRequest request) {
//        OAuth2AuthorizationConsentAuthenticationToken
//        Authentication authenticate1 = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken("root", "root")
//        );
//        SecurityContextHolder.getContext().setAuthentication(authenticate1);
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.err.println(principal);
//        String header = request.getHeader("Authorization");
//        String clientId = request.getParameter("client_id");
        OAuth2AuthorizationCodeRequestAuthenticationToken authentication = (OAuth2AuthorizationCodeRequestAuthenticationToken)oAuth2AuthorizationCodeRequestAuthenticationConverter.convert(request);
        assert authentication != null;
        OAuth2AuthorizationCodeRequestAuthenticationToken authenticate = (OAuth2AuthorizationCodeRequestAuthenticationToken)oAuth2AuthorizationCodeRequestAuthenticationProvider.authenticate(authentication);
        OAuth2AuthorizationCode authorizationCode = authenticate.getAuthorizationCode();
        return ResponseEntity.ok(authorizationCode);
    }

    /**
     * grant_type=authorization_code
     * client_id=
     * client_secret=
     * code=
     * redirect_uri=
     * @param request
     * @return
     */
    @PostMapping("/oauth2/access-token")
    public ResponseEntity<?> oauth2Token(HttpServletRequest request) {
        // 客户端先校验参数
        Authentication clientAuthentication = clientSecretPostAuthenticationConverter.convert(request);

        if(clientAuthentication == null) return ResponseEntity.status(403).body("request params is not valid");
        // 客户端Filter认证
        Authentication authenticate1 = clientSecretAuthenticationProvider.authenticate(clientAuthentication);
        // 设置client认证信息
        SecurityContextHolder.getContext().setAuthentication(authenticate1);
        // 服务端convert校验参数
        OAuth2AuthorizationCodeAuthenticationToken authentication = (OAuth2AuthorizationCodeAuthenticationToken)oAuth2AuthorizationCodeAuthenticationConverter.convert(request);

        if(authentication == null) return ResponseEntity.status(403).body("request params is not valid");
        // 认证生成token
//        OAuth2Authorization auth2Authorization = inMemoryOAuth2AuthorizationService.findById(authentication.getCode());
        OAuth2AccessTokenAuthenticationToken authenticate = (OAuth2AccessTokenAuthenticationToken)oAuth2AuthorizationCodeAuthenticationProvider.authenticate(authentication);
        OAuth2AccessToken accessToken = authenticate.getAccessToken();
        OAuth2RefreshToken refreshToken = authenticate.getRefreshToken();
        Map<Object, Object> build = MapBuilder.create()
                .put("access_token", accessToken)
                .put("refresh_token", refreshToken)
                .build();
        return ResponseEntity.ok(build);
    }

    /**
     * grant_type=refresh_token
     * refresh_token=
     * scope=
     * @param request
     * @return
     */
    @PostMapping("/oauth2/refresh-token")
    public ResponseEntity<?> oauth2RefreshToken(HttpServletRequest request) {
        // 客户端先校验参数
        Authentication clientAuthentication = clientSecretPostAuthenticationConverter.convert(request);
        assert clientAuthentication != null;
        // 客户端Filter认证
        Authentication authenticate1 = clientSecretAuthenticationProvider.authenticate(clientAuthentication);
        // 设置client认证信息
        SecurityContextHolder.getContext().setAuthentication(authenticate1);
        OAuth2RefreshTokenAuthenticationToken authentication = (OAuth2RefreshTokenAuthenticationToken)oAuth2RefreshTokenAuthenticationConverter.convert(request);
        assert authentication != null;
        OAuth2AccessTokenAuthenticationToken authenticate = (OAuth2AccessTokenAuthenticationToken)oAuth2RefreshTokenAuthenticationProvider.authenticate(authentication);
        OAuth2AccessToken accessToken = authenticate.getAccessToken();
        OAuth2RefreshToken refreshToken = authenticate.getRefreshToken();
        Map<Object, Object> build = MapBuilder.create()
                .put("access_token", accessToken)
                .put("refresh_token", refreshToken)
                .build();
        return ResponseEntity.ok(build);
    }
}
