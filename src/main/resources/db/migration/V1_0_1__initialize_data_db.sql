START TRANSACTION;

-- Insert data into `role` table
INSERT IGNORE INTO `role` (`name`, `display_name`) VALUES
    ('ROLE_SUPER_ADMIN', 'Super Admin'),
    ('ROLE_USER', 'User');

-- Insert data into `gender` table
INSERT IGNORE INTO `gender` (`name`, `display_name`) VALUES
    ('MALE', 'Male'),
    ('FEMALE', 'Female');

COMMIT;