CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE user_connections (
    user_id BIGINT NOT NULL,
    connected_user_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, connected_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (connected_user_id) REFERENCES users(id)
);


CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    description TEXT,
    amount DECIMAL(15,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);