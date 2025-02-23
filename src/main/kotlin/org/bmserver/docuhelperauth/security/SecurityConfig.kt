package org.bmserver.docuhelperauth.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler,
) {
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it.anyExchange().permitAll()
            }.oauth2Login { oauth2 -> oauth2.authenticationSuccessHandler(customOAuth2SuccessHandler) } // OAuth2 로그인 활성화
            .build()
}
