CREATE TABLE IF NOT EXISTS admin_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT admin_users_role_check CHECK (role IN ('ADMIN'))
);

CREATE TABLE IF NOT EXISTS ad_requests (
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
    CONSTRAINT ad_requests_status_check CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE IF NOT EXISTS dashboard_settings (
    setting_key VARCHAR(120) PRIMARY KEY,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP NOT NULL
);
