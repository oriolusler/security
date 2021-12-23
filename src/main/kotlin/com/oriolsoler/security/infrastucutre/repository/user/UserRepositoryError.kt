package com.oriolsoler.security.infrastucutre.repository.user

class UserRepositoryError(message: String?) : Exception("User repository error: $message")