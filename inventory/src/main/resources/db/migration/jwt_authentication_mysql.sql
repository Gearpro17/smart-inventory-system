CREATE TABLE user_accounts (
    username VARCHAR(80) NOT NULL PRIMARY KEY,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL
);
