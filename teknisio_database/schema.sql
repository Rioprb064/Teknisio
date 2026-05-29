-- Table: users (Mencakup Customer dan Teknisi)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    role VARCHAR(50) NOT NULL, -- 'CUSTOMER', 'TECHNICIAN'
    profile_picture_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: services_category
CREATE TABLE services_category (
    id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL, -- misal: 'AC', 'Fridge', 'Washing Machine'
    icon_url TEXT,
    description TEXT
);

-- Table: orders
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID REFERENCES users(id),
    technician_id UUID REFERENCES users(id),
    category_id INT REFERENCES services_category(id),
    damage_description TEXT,
    damage_photo_url TEXT,
    order_date DATE NOT NULL,
    timeslot VARCHAR(50) NOT NULL, -- 'Morning 8-11 AM', 'Afternoon 12-3 PM', 'Evening 4-6 PM'
    estimated_price DECIMAL(10, 2),
    status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'ACCEPTED', 'ON_THE_WAY', 'REPAIRED', 'CANCELLED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: chat_messages
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id),
    sender_id UUID REFERENCES users(id),
    receiver_id UUID REFERENCES users(id),
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
