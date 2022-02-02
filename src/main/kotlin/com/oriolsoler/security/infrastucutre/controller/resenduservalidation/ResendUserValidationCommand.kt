package com.oriolsoler.security.infrastucutre.controller.resenduservalidation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


class ResendUserValidationCommand @JsonCreator constructor(@JsonProperty("email") val email: String)