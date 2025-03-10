package org.bmserver.docuhelperauth.security.user

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface MemberRepository : R2dbcRepository<Member, UUID> {
    fun findMemberByEmail(email: String): Mono<Member>
}
