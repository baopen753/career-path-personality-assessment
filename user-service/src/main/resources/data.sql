-- Insert roles
INSERT INTO roles (role_name) VALUES
                                  ('STUDENT'),
                                  ('ADMIN'),
                                  ('PARENT'),
                                  ('SYSTEM_ADMIN'),
                                  ('EVENT_MANAGER');

-- Insert users (password = '12345' hashed)
INSERT INTO users (role_id, status, email, password) VALUES
                                                         (2, true, 'admin@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (1, true, 'student1@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (3, true, 'parent@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (4, true, 'sysadmin@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (5, true, 'eventmgr@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (1, true, 'student2@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (1, false, 'student3@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (3, false, 'parent2@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (2, true, 'admin2@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi'),
                                                         (5, true, 'eventmgr2@example.com', '$2a$10$Dow1Q/zAN3uqoj1HGLgBFeox6lHoR2UhlROUQDO8O5z9zUvbcWFCi');

-- Insert user profiles
INSERT INTO user_profile (
    birth_day, user_id, account_type, school, address,
    full_name, gender, image_url, phone_number
) VALUES
      ('1990-01-01', 1, 'PREMIUM', 'University A', '123 Main St', 'Alice Admin', 'FEMALE', 'http://img.com/alice.jpg', '1234567890'),
      ('2002-05-10', 2, 'STANDARD', 'High School A', '456 Pine Rd', 'Bob Student', 'MALE', 'http://img.com/bob.jpg', '2345678901'),
      ('1978-07-15', 3, 'STANDARD', 'Parent Academy', '789 Oak Ave', 'Carol Parent', 'OTHER', 'http://img.com/carol.jpg', '3456789012'),
      ('1985-12-20', 4, 'PREMIUM', 'IT Institute', '135 Cedar Blvd', 'Dave SysAdmin', 'MALE', 'http://img.com/dave.jpg', '4567890123'),
      ('1993-09-09', 5, 'STANDARD', 'Event College', '246 Elm St', 'Eve Manager', 'FEMALE', 'http://img.com/eve.jpg', '5678901234'),
      ('2001-04-30', 6, 'STANDARD', 'High School B', '357 Maple Dr', 'Frank Student', 'MALE', 'http://img.com/frank.jpg', '6789012345'),
      ('2000-11-11', 7, 'STANDARD', 'High School C', '468 Willow Ln', 'Grace Student', 'FEMALE', 'http://img.com/grace.jpg', '7890123456'),
      ('1980-06-25', 8, 'PREMIUM', 'Parent Institute', '579 Birch Ct', 'Henry Parent', 'MALE', 'http://img.com/henry.jpg', '8901234567'),
      ('1988-03-17', 9, 'PREMIUM', 'Admin School', '680 Aspen Way', 'Ivy Admin', 'FEMALE', 'http://img.com/ivy.jpg', '9012345678'),
      ('1992-08-05', 10, 'STANDARD', 'Event Academy', '791 Poplar St', 'Jack Manager', 'MALE', 'http://img.com/jack.jpg', '0123456789');
