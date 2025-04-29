package org.bmserver.docuhelperauth.security.user

import org.springframework.data.annotation.Id
import java.util.UUID

class Member(
    @Id var uuid: UUID?,
    val email: String,
    val role: Role = Role.FREE,
)
