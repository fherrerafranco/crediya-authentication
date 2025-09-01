CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    identity_document VARCHAR(255),
    phone VARCHAR(50),
    role_id VARCHAR(255),
    base_salary DECIMAL(12, 2) NOT NULL CHECK (base_salary >= 0 AND base_salary <= 15000000),
    birth_date VARCHAR(50),
    address TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);