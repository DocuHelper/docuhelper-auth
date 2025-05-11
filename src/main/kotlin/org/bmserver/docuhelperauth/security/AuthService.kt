package org.bmserver.docuhelperauth.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.bmserver.docuhelperauth.security.user.Member
import org.bmserver.docuhelperauth.security.user.MemberRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(
    private var memberRepository: MemberRepository,
    private var jwtUtil: JwtUtil
) {

    fun getEmailByGoogleToken(googleJwtToken: String): String {
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(
                listOf(
                    "686711888253-g0g2rmijtu0k8n8h2v2brvdl6bfd490r.apps.googleusercontent.com",
                    "686711888253-73lv85cibfelr4agi6bjkif339tv9fap.apps.googleusercontent.com"
                )
            )
            .build()
        val googleIdToken = verifier.verify(googleJwtToken)
            ?: throw IllegalArgumentException("Invalid ID token")

        // 2. Extract payload
        val payload = googleIdToken.payload
        val userId = payload.subject

        return payload.email ?: throw IllegalArgumentException("Email not present in token")

    }

    fun generateJwtToken(userEmail: String): Mono<String> {
        return memberRepository
            .findMemberByEmail(userEmail)
            .switchIfEmpty(
                memberRepository.save(
                    Member(uuid = null, email = userEmail),
                ),
            ).map { member ->
                member.uuid?.let {
                    jwtUtil.generateJwt(it, userEmail, member.role)
                } ?: error("Member UUID is null")
            }
    }
}