package org.bmserver.docuhelperauth.security

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OauthRestController {
    @GetMapping("/user")
    fun getUser(
        @AuthenticationPrincipal principal: OAuth2User,
    ): Map<String, Any?> = mapOf("name" to principal.attributes["name"], "email" to principal.attributes["email"])
}
