package kr.mashup.ladder.auth.service

import kr.mashup.ladder.auth.dto.request.AuthRequest

interface AuthService {

    fun login(request: AuthRequest): Long

}
