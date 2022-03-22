package com.oriolsoler.security.infrastucutre.controller.login.firebase

import com.fasterxml.jackson.annotation.JsonProperty

class LoginFirebaseRequestCommand(@JsonProperty("firebaseToken") val firebaseToken: String)