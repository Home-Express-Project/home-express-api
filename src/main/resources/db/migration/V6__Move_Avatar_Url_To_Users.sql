-- 1. Add avatar_url column to users table
ALTER TABLE users ADD COLUMN avatar_url TEXT;

-- 2. Migrate existing avatar data from customers to users
UPDATE users u
JOIN customers c ON u.user_id = c.customer_id
SET u.avatar_url = c.avatar_url
WHERE c.avatar_url IS NOT NULL;

-- 3. Remove avatar_url column from customers table
ALTER TABLE customers DROP COLUMN avatar_url;
