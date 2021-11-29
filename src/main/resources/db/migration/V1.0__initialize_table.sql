CREATE TABLE SECURITY_USER(
    id       UUID,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    phone    VARCHAR(15)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE SECURITY_USER_ROLE
(
    user_id UUID,
    role    VARCHAR(20),
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES SECURITY_USER (id)
);