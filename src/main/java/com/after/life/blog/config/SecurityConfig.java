package com.after.life.blog.config;


import com.after.life.blog.bean.User;
import com.after.life.blog.except.LoginUserNotFoundException;
import com.after.life.blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;


/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-09 17:07
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends AbstractHttpConfigurer<OAuth2AuthorizationServerConfigurer, HttpSecurity>{
//    @Resource
//    @Lazy
//    private JwtFilter jwtFilter;
    @Lazy
    @Resource
    private UserService userService;




    /**
     * 获取code 参数解析
     * @return
     */
    @Bean
    public OAuth2AuthorizationCodeRequestAuthenticationConverter oAuth2AuthorizationCodeRequestAuthenticationConverter() {
        return new OAuth2AuthorizationCodeRequestAuthenticationConverter();
    }

    /**
     * code -> token 参数解析
     * @return
     */
    @Bean
    public OAuth2AuthorizationCodeAuthenticationConverter oAuth2AuthorizationCodeAuthenticationConverter() {
        return new OAuth2AuthorizationCodeAuthenticationConverter();
    }

    /**
     * 刷新token 参数解析
     * @return
     */
    @Bean
    public OAuth2RefreshTokenAuthenticationConverter oAuth2RefreshTokenAuthenticationConverter() {
        return new OAuth2RefreshTokenAuthenticationConverter();
    }

    /**
     * 生成code
     * @return
     */
    @Bean
    public OAuth2AuthorizationCodeRequestAuthenticationProvider oAuth2AuthorizationConsentAuthenticationProvider() {
        return new OAuth2AuthorizationCodeRequestAuthenticationProvider(
                registeredClientRepository(),
                inMemoryOAuth2AuthorizationService(),
                inMemoryOAuth2AuthorizationConsentService()
        );
    }


    @Bean
    public InMemoryOAuth2AuthorizationConsentService inMemoryOAuth2AuthorizationConsentService() {
        return new InMemoryOAuth2AuthorizationConsentService();
    }

    /**
     * 认证体存储
     * @return
     */
    @Bean
    public InMemoryOAuth2AuthorizationService inMemoryOAuth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }



    /**
     * code -> access token
     * @return
     */
    @Bean
    public OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider() {
        OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider(
                inMemoryOAuth2AuthorizationService(),
                jwtGenerator(jwkSource())
        );
        return oAuth2AuthorizationCodeAuthenticationProvider;
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> jwtGenerator(JWKSource<SecurityContext> jwkSource) {
        return new DelegatingOAuth2TokenGenerator(
                new JwtGenerator(new NimbusJwtEncoder(jwkSource)), new OAuth2RefreshTokenGenerator()
        );
    }



    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * refresh token
     * @return
     */
    @Bean
    public OAuth2RefreshTokenAuthenticationProvider oAuth2RefreshTokenAuthenticationProvider() {
        return new OAuth2RefreshTokenAuthenticationProvider(
                inMemoryOAuth2AuthorizationService(),
                jwtGenerator(jwkSource())
        );
    }


    /**
     * code -> token 之前，解析客户端参数
     * @return
     */
    @Bean
    public ClientSecretPostAuthenticationConverter clientSecretPostAuthenticationConverter() {
        return new ClientSecretPostAuthenticationConverter();
    }


    /**
     * code -> token之前, 客户端认证
     * @return
     */
    @Bean
    public ClientSecretAuthenticationProvider clientSecretAuthenticationProvider() {
        ClientSecretAuthenticationProvider clientSecretAuthenticationProvider = new ClientSecretAuthenticationProvider(registeredClientRepository(), inMemoryOAuth2AuthorizationService());
//        clientSecretAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return clientSecretAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/common/**").permitAll()
                        .requestMatchers("/api/v1/note/index").permitAll()
                        .requestMatchers("/api/v1/note/get").permitAll()
                        .requestMatchers("/api/v1/note/search").permitAll()
//                        .requestMatchers("/oauth2/authorize").permitAll()
                        .requestMatchers("/oauth2/access-token").permitAll()
                        .requestMatchers("/oauth2/refresh-token").permitAll()
                        .requestMatchers("/oauth2/resource/test").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwt2Filter(), JwtFilter.class);
        AuthorizationServerContextFilter authorizationServerContextFilter = new AuthorizationServerContextFilter(authorizationServerSettings());
        http.addFilterAfter(postProcess(authorizationServerContextFilter), SecurityContextHolderFilter.class);
        return http.build();
    }
    @Resource
    private JwtUtils jwtUtils;
    public JwtFilter jwtFilter() {
        return new JwtFilter(userDetailsService(), jwtUtils);
    }
    @Bean
    public Oauth2JwtFilter jwt2Filter() {
        return new Oauth2JwtFilter(jwtAuthenticationProvider());
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtDecoder(jwkSource()));
    }




    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("{bcrypt}$2a$10$hq.QtQsH9oNh.0YL0Pcfw.b/gS9ImjFYYni9mol/JAGqQ1Gfx/s4u")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethods((set) -> {
                    set.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    set.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
                })
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:5173")
                .postLogoutRedirectUri("http://127.0.0.1:5173")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("user.info")
                .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofHours(12))
                        .authorizationCodeTimeToLive(Duration.ofHours(1))
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(oidcClient);
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User one = userService.getOne(new LambdaQueryWrapper<User>()
                        .and(e -> {
                            e.eq(User::getEmail, username)
                                    .or().eq(User::getUsername, username);
                        })
                        .last("limit 1"));
                if(one == null) throw new LoginUserNotFoundException();
                return one;
            }
        };
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
