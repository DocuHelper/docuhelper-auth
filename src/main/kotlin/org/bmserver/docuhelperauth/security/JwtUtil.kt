package org.bmserver.docuhelperauth.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil {
    final val secretKeySpecAlgorithm = "HmacSHA256"

    @Value("\${spring.security.jwt.secret}")
    lateinit var secret: String

    val secretKey: SecretKey by lazy {
        SecretKeySpec(secret.toByteArray(), secretKeySpecAlgorithm)
    }

    fun generateJwt(
        uuid: UUID,
        email: String,
    ): String =
        Jwts
            .builder()
            .subject(uuid.toString())
            .claims(
                mapOf(
                    "email" to email,
                ),
            ).issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간 후 만료
            .signWith(secretKey) // HMAC 서명 적용
            .compact()

    fun valid(jwt: String): Claims =
        Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwt)
            .payload
}
