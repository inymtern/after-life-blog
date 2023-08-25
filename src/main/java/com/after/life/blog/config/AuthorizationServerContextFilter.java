package com.after.life.blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-24 15:48
 */
final class AuthorizationServerContextFilter extends OncePerRequestFilter {
    private final AuthorizationServerSettings authorizationServerSettings;

    AuthorizationServerContextFilter(AuthorizationServerSettings authorizationServerSettings) {
        Assert.notNull(authorizationServerSettings, "authorizationServerSettings cannot be null");
        this.authorizationServerSettings = authorizationServerSettings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            AuthorizationServerContext authorizationServerContext =
                    new DefaultAuthorizationServerContext(
                            () -> resolveIssuer(this.authorizationServerSettings, request),
                            this.authorizationServerSettings);
            AuthorizationServerContextHolder.setContext(authorizationServerContext);
            filterChain.doFilter(request, response);
        } finally {
            AuthorizationServerContextHolder.resetContext();
        }
    }

    private static String resolveIssuer(AuthorizationServerSettings authorizationServerSettings, HttpServletRequest request) {
        return authorizationServerSettings.getIssuer() != null ?
                authorizationServerSettings.getIssuer() :
                getContextPath(request);
    }

    private static String getContextPath(HttpServletRequest request) {
        // @formatter:off
        return UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build()
                .toUriString();
        // @formatter:on
    }

    private static final class DefaultAuthorizationServerContext implements AuthorizationServerContext {
        private final Supplier<String> issuerSupplier;
        private final AuthorizationServerSettings authorizationServerSettings;

        private DefaultAuthorizationServerContext(Supplier<String> issuerSupplier, AuthorizationServerSettings authorizationServerSettings) {
            this.issuerSupplier = issuerSupplier;
            this.authorizationServerSettings = authorizationServerSettings;
        }

        @Override
        public String getIssuer() {
            return this.issuerSupplier.get();
        }

        @Override
        public AuthorizationServerSettings getAuthorizationServerSettings() {
            return this.authorizationServerSettings;
        }

    }

}
