package io.mkrzywanski.chat.app.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.core.SecuritySocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    RSocketMessageHandler messageHandler(final RSocketStrategies strategies) {
        final var handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

//    @Bean
//    RSocketServerCustomizer ss(SecuritySocketAcceptorInterceptor interceptor) {
//        return (server) -> server.interceptors((registry) -> registry.forSocketAcceptor(interceptor));
//    }

//
//    @Bean
//    MapReactiveUserDetailsService authentication() {
//        //This is NOT intended for production use (it is intended for getting started experience only)
//        final UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user1")
//                .password("pass")
//                .roles("USER")
//                .build();
//
//        final UserDetails user2 = User.withDefaultPasswordEncoder()
//                .username("user2")
//                .password("pass")
//                .roles("NONE")
//                .build();
//
//        return new MapReactiveUserDetailsService(user, user2);
//    }

    @Bean
    PayloadSocketAcceptorInterceptor authorization(final RSocketSecurity security) {
        security.authorizePayload(authorize ->
                authorize
                        .setup().permitAll()
                        .anyExchange().authenticated())
                .jwt(Customizer.withDefaults());
//                .simpleAuthentication(Customizer.withDefaults());
        return security.build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders
                .fromIssuerLocation(issuer);
    }
}
