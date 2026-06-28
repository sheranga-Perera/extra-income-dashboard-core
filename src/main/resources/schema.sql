DROP TABLE IF EXISTS dashboard_settings CASCADE;
DROP TABLE IF EXISTS ad_requests CASCADE;
DROP TABLE IF EXISTS admin_users CASCADE;

CREATE TABLE admin_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT admin_users_role_check CHECK (role IN ('ADMIN')),
    CONSTRAINT admin_users_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE ad_requests (
    id UUID PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(255),
    ad_title VARCHAR(255) NOT NULL,
    ad_description VARCHAR(255) NOT NULL,
    ad_type VARCHAR(255) NOT NULL,
    ad_goal VARCHAR(255),
    media_url TEXT,
    media_content TEXT,
    media_notes TEXT,
    cta VARCHAR(255),
    views_per_day INTEGER,
    minutes_per_day INTEGER,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT ad_requests_status_check CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT ad_requests_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE dashboard_settings (
    setting_key VARCHAR(120) PRIMARY KEY,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT dashboard_settings_deleted_check CHECK (deleted IN (0, 1))
);
