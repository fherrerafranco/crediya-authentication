-- Crediya Authentication Database Initial Migration
-- Created: 2025-09-01
-- Description: Initial schema for user authentication microservice
-- Matches UserEntity R2DBC mapping exactly

-- Create users table matching UserEntity structure
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    identity_document VARCHAR(255),
    phone VARCHAR(50),
    role_id VARCHAR(255),
    base_salary DECIMAL(12, 2) NOT NULL,
    birth_date VARCHAR(50),
    address TEXT,
    
    -- Business constraints from domain model
    CONSTRAINT users_email_check CHECK (
        email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    ),
    CONSTRAINT users_salary_range CHECK (
        base_salary >= 0 AND base_salary <= 15000000
    )
);

-- Create unique constraint on email (case insensitive for uniqueness)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique 
ON users (LOWER(email));

-- Performance indexes for common queries
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_identity_document ON users(identity_document) 
WHERE identity_document IS NOT NULL;

-- Grant permissions to the application user
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO crediya_user;

-- Add comments for documentation
COMMENT ON TABLE users IS 'Core users table for authentication microservice - matches UserEntity R2DBC mapping';
COMMENT ON COLUMN users.user_id IS 'Primary key, mapped to UserEntity.id';
COMMENT ON COLUMN users.first_name IS 'User first name, mapped to UserEntity.firstName';
COMMENT ON COLUMN users.last_name IS 'User last name, mapped to UserEntity.lastName';
COMMENT ON COLUMN users.email IS 'User email, must be unique - mapped to UserEntity.email';
COMMENT ON COLUMN users.identity_document IS 'User identity document, mapped to UserEntity.identityDocument';
COMMENT ON COLUMN users.phone IS 'User phone number, mapped to UserEntity.phone';
COMMENT ON COLUMN users.role_id IS 'User role identifier, mapped to UserEntity.roleId';
COMMENT ON COLUMN users.base_salary IS 'User base salary (0-15,000,000), mapped to UserEntity.baseSalary';
COMMENT ON COLUMN users.birth_date IS 'User birth date as string, mapped to UserEntity.birthDate';
COMMENT ON COLUMN users.address IS 'User address, mapped to UserEntity.address';