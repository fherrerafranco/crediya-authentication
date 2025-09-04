-- Sample data for testing the authentication microservice
-- This file will only insert data if the table is empty

-- Insert sample users only if the table is empty
INSERT INTO users (
    user_id,
    first_name,
    last_name,
    email,
    password_hash,
    identity_document,
    phone,
    role_id,
    base_salary,
    birth_date,
    address
) 
SELECT * FROM (VALUES
    ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'John', 'Doe', 'john.doe@crediya.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m', '12345678', '+1234567890', 1, 75000.00, '1990-01-15', '123 Main St, City, State'),
    ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'Jane', 'Smith', 'jane.smith@crediya.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m', '87654321', '+0987654321', 2, 85000.50, '1985-05-22', '456 Oak Ave, City, State'),
    ('550e8400-e29b-41d4-a716-446655440003'::uuid, 'Admin', 'User', 'admin@crediya.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m', '11111111', '+1111111111', 2, 120000.00, '1980-03-10', '789 Pine St, City, State'),
    ('550e8400-e29b-41d4-a716-446655440004'::uuid, 'Test', 'Manager', 'manager@crediya.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m', '22222222', '+2222222222', 3, 95000.75, '1992-11-30', '321 Elm St, City, State')
) AS sample_data
WHERE NOT EXISTS (SELECT 1 FROM users LIMIT 1);

-- Verify the insertion
DO $$
DECLARE
    user_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    RAISE NOTICE 'Total users in database: %', user_count;
END $$;