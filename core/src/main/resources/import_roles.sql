-- Create the default development administrator.
INSERT INTO users (username, password, email, loggedin)
VALUES (
           'admin',
           'REPLACE_WITH_PASSWORD_HASH',
           'admin@example.com',
           false
       )
    ON CONFLICT (username) DO NOTHING;

-- Assign the default USER and ADMIN roles.
INSERT INTO user_roles (user_id, role)
SELECT u.id, r.role
FROM users u
         CROSS JOIN (VALUES ('USER'), ('ADMIN')) AS r(role)
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role = r.role
);
