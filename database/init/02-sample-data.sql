-- Sample data for testing the authentication microservice
-- This file will only insert data if the table is empty

-- Insert sample users only if the table is empty
INSERT INTO users (
    user_id,
    first_name,
    last_name,
    email,
    identity_document,
    phone,
    role_id,
    base_salary,
    birth_date,
    address
) 
SELECT * FROM (VALUES
    ('user-1', 'John', 'Doe', 'john.doe@crediya.com', '12345678', '+1234567890', '1', 75000.00, '1990-01-15', '123 Main St, City, State'),
    ('user-2', 'Jane', 'Smith', 'jane.smith@crediya.com', '87654321', '+0987654321', '2', 85000.50, '1985-05-22', '456 Oak Ave, City, State'),
    ('user-3', 'Admin', 'User', 'admin@crediya.com', '11111111', '+1111111111', '2', 120000.00, '1980-03-10', '789 Pine St, City, State'),
    ('user-4', 'Test', 'Manager', 'manager@crediya.com', '22222222', '+2222222222', '3', 95000.75, '1992-11-30', '321 Elm St, City, State')
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