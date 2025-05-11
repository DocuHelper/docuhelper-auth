package org.bmserver.docuhelperauth.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.bmserver.docuhelperauth.security.user.Member
import org.bmserver.docuhelperauth.security.user.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import java.time.Instant
import java.util.Date
import java.time.temporal.ChronoUnit
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class OauthRestController(
    private val jwtUtil: JwtUtil,
    private val authService: AuthService
) {

    @GetMapping("/user")
    fun getUser(
        @AuthenticationPrincipal principal: OAuth2User,
    ): Map<String, Any?> = mapOf("name" to principal.attributes["name"], "email" to principal.attributes["email"])

    @PostMapping("/auth/verify/docuhelper")
    fun verifyDocuhelperJwtToken(@RequestBody userToken: UserToken):ResponseEntity<Void> {
        jwtUtil.valid(userToken.token)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/auth/verify/google")
    fun googleLogin(@RequestBody userToken: UserToken): Mono<Map<String, String>> {

        return authService.getEmailByGoogleToken(userToken.token)
            .let {
                authService.generateJwtToken(it).map { token ->
                    mapOf("token" to token)
                }
            }
    }
}

class UserToken(
    val token: String
)