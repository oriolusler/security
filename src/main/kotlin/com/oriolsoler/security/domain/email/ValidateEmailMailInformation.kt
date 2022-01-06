package com.oriolsoler.security.domain.email


class ValidateEmailMailInformation(
    from: String,
    to: String,
    validation: String,
    subject: String = "Este es su código de confirmación: %s",
    body: String = "Para confirmar tu correo, simplemente vuelve a la ventana del explorador" +
            " en la que comenzaste a crear tu cuenta de NeverEatAlone e introduce este código: %s"

) :
    MailInformation(
        from,
        to,
        String.format(subject, validation),
        String.format(body, validation)
    )