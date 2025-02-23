package org.bmserver.docuhelperauth.security

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

@Component
class CustomOAuth2SuccessHandler(
    private val jwtUtil: JwtUtil,
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?,
    ): Mono<Void> {
        val exchange: ServerWebExchange = webFilterExchange?.exchange ?: return Mono.empty()
        val response = exchange.response

        val oauth2User = authentication?.principal as? OAuth2User ?: return Mono.empty()
        val userEmail = oauth2User.attributes.getOrDefault("email", "") as String

        if (userEmail.isBlank()) {
            return response.setComplete()
        }

        val jwtToken = jwtUtil.generateJwt(userEmail)

        addAuthorizationHeader(response, jwtToken)
        addJwtCookie(response, jwtToken)

        response.headers.location = URI("/")
        response.statusCode = HttpStatus.FOUND

        return response.setComplete()
    }

    private fun addAuthorizationHeader(
        response: ServerHttpResponse,
        jwtToken: String,
    ) {
        response.headers.add("Authorization", "Bearer $jwtToken")
    }

    private fun addJwtCookie(
        response: ServerHttpResponse,
        jwtToken: String,
    ) {
        val responseCookie =
            ResponseCookie
                .from("JWT_TOKEN", jwtToken)
                .httpOnly(true)
                .secure(true) // HTTPS에서만 전송 → 보안 강화
                .path("/") // 모든 경로에서 사용 가능
                .sameSite("Strict") // CSRF 공격 방어
                .maxAge(24 * 60 * 60) // 24시간 유지
                .build()

        response.addCookie(responseCookie)
    }
}
