# Database Setup for Crediya Authentication Microservice

This directory contains the PostgreSQL database setup for the Crediya Authentication microservice.

## Quick Start

### Start PostgreSQL with Docker Compose

```bash
# From the project root directory
docker-compose up -d postgres

# Check if the database is healthy
docker-compose ps
```

### Optional: Start with PgAdmin for database management

```bash
# Start PostgreSQL with PgAdmin
docker-compose --profile admin up -d

# Access PgAdmin at http://localhost:8080
# Email: admin@crediya.com
# Password: admin123
```

## Database Configuration

### Connection Details
- **Host**: localhost
- **Port**: 5432
- **Database**: crediya_auth
- **Username**: crediya_user
- **Password**: crediya_pass

### Spring Boot Configuration
Add these properties to your `application.yml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/crediya_auth
    username: crediya_user
    password: crediya_pass
    pool:
      initial-size: 10
      max-size: 20
      max-idle-time: 30m
      validation-query: SELECT 1
```

## Database Schema

### Users Table
The `users` table is designed to match exactly the `UserEntity` R2DBC mapping:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | VARCHAR(255) | PRIMARY KEY | User identifier |
| first_name | VARCHAR(255) | nullable | User first name |
| last_name | VARCHAR(255) | nullable | User last name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | User email |
| identity_document | VARCHAR(255) | nullable | Identity document |
| phone | VARCHAR(50) | nullable | Phone number |
| role_id | VARCHAR(255) | nullable | Role identifier |
| base_salary | DECIMAL(12,2) | NOT NULL, 0-15,000,000 | Base salary |
| birth_date | VARCHAR(50) | nullable | Birth date |
| address | TEXT | nullable | User address |

### Business Constraints
- **Email**: Must be valid email format and unique (case-insensitive)
- **Base Salary**: Must be between 0 and 15,000,000
- **Indexes**: Performance indexes on email, role_id, and identity_document

## Sample Data

The database includes sample test users:

1. **John Doe** (user-1) - john.doe@crediya.com - Role: 1
2. **Jane Smith** (user-2) - jane.smith@crediya.com - Role: 2  
3. **Admin User** (user-3) - admin@crediya.com - Role: 2
4. **Test Manager** (user-4) - manager@crediya.com - Role: 3

## Database Management

### Connecting with psql
```bash
# Connect to the database
docker exec -it crediya-postgres psql -U crediya_user -d crediya_auth

# Common queries
\dt                          # List tables
\d users                     # Describe users table
SELECT * FROM users;         # View all users
```

### Backup and Restore
```bash
# Create backup
docker exec crediya-postgres pg_dump -U crediya_user crediya_auth > backup.sql

# Restore backup
docker exec -i crediya-postgres psql -U crediya_user crediya_auth < backup.sql
```

### Reset Database
```bash
# Stop and remove containers with volumes
docker-compose down -v

# Start fresh
docker-compose up -d postgres
```

## Monitoring

### Health Check
The PostgreSQL container includes a health check:
```bash
docker-compose ps
```

### Database Performance
```sql
-- Check table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public';

-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read
FROM pg_stat_user_indexes 
WHERE schemaname = 'public';
```

## Troubleshooting

### Common Issues

1. **Port 5432 already in use**
   ```bash
   # Change port in docker-compose.yml
   ports:
     - "5433:5432"
   ```

2. **Permission denied**
   ```bash
   # Reset volumes
   docker-compose down -v
   docker volume prune
   ```

3. **Connection refused**
   ```bash
   # Wait for health check
   docker-compose logs postgres
   ```

### Logs
```bash
# View PostgreSQL logs
docker-compose logs -f postgres

# View PgAdmin logs  
docker-compose logs -f pgadmin
```