-- Roles and Authentication Schema
-- Created: 2025-09-04
-- Description: Adds roles table and authentication support

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator'),
('ADVISOR', 'Financial advisor'), 
('CUSTOMER', 'Customer user');

-- Update users table for authentication
ALTER TABLE users 
    ALTER COLUMN role_id TYPE INTEGER USING role_id::integer,
    ADD COLUMN password_hash VARCHAR(255);

-- Add foreign key constraint
ALTER TABLE users 
    ADD CONSTRAINT fk_users_role_id 
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_users_role_fk ON users(role_id);

-- Grant permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON roles TO crediya_user;
GRANT USAGE ON SEQUENCE roles_role_id_seq TO crediya_user;

-- Comments for documentation
COMMENT ON TABLE roles IS 'User roles for authorization';
COMMENT ON COLUMN roles.role_id IS 'Sequential role identifier';
COMMENT ON COLUMN roles.name IS 'Role name (ADMIN, ADVISOR, CUSTOMER)';
COMMENT ON COLUMN users.role_id IS 'Foreign key to roles table';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password for authentication';