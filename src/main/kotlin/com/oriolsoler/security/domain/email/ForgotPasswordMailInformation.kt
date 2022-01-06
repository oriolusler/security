package com.oriolsoler.security.domain.email


class ForgotPasswordMailInformation(
    from: String,
    to: String,
    validation: String,
    subject: String = "Este es su código para cambiar la contraseña: %s",
    body: String = "Has solicitado el cambio de la contraseña, " +
            "introduce este código para seguir con el proceso: %s"
) :
    MailInformation(
        from,
        to,
        String.format(subject, validation),
        String.format(body, validation)
    )