package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.Verification

interface VerifyService {
    fun generate(): Verification
}