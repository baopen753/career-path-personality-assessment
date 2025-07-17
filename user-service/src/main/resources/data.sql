-- Insert roles
INSERT INTO roles (role_name)
VALUES ('STUDENT'),
       ('ADMIN'),
       ('PARENT'),
       ('SYSTEM_ADMIN'),
       ('EVENT_MANAGER');

-- Insert users (password = '12345' hashed)
INSERT INTO users (role_id, status, email, current_package, password)
VALUES (2, true, 'admin@example.com', null, '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (1, true, 'student1@example.com', 'STANDARD', '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (3, true, 'parent@example.com', 'STANDARD', '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (4, true, 'sysadmin@example.com', null, '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (5, true, 'eventmgr@example.com', null, '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (1, true, 'student2@example.com', 'STANDARD', '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (1, false, 'student3@example.com', 'STANDARD', '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (3, false, 'parent2@example.com', 'STANDARD', '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (2, true, 'admin2@example.com', null, '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2'),
       (5, true, 'eventmgr2@example.com', null, '$2a$12$Sqd5lHHmpeuThlWd3xyL2eYsT1WjR72cJKRGv9u27HlEWeyk6idj2');

INSERT INTO packages (package_name, price)
VALUES ('STANDARD', 0),
       ('PREMIUM', 4000);

-- Insert user profiles
INSERT INTO user_profile (birth_day, user_id, school, address, full_name, gender, image_url, phone_number, district_code, province_code)
VALUES
    ('1990-01-01', 1, 'University A', '123 Main St', 'Alice Admin', 'FEMALE', 'http://img.com/alice.jpg', '1234567890', 1, 101),
    ('2002-05-10', 2, 'High School A', '456 Pine Rd', 'Bob Student', 'MALE', 'http://img.com/bob.jpg', '2345678901', 2, 102),
    ('1978-07-15', 3, 'Parent Academy', '789 Oak Ave', 'Carol Parent', 'OTHER', 'http://img.com/carol.jpg', '3456789012', 3, 103),
    ('1985-12-20', 4, 'IT Institute', '135 Cedar Blvd', 'Dave SysAdmin', 'MALE', 'http://img.com/dave.jpg', '4567890123', 4, 104),
    ('1993-09-09', 5, 'Event College', '246 Elm St', 'Eve Manager', 'FEMALE', 'http://img.com/eve.jpg', '5678901234', 5, 105),
    ('2001-04-30', 6, 'High School B', '357 Maple Dr', 'Frank Student', 'MALE', 'http://img.com/frank.jpg', '6789012345', 6, 106),
    ('2000-11-11', 7, 'High School C', '468 Willow Ln', 'Grace Student', 'FEMALE', 'http://img.com/grace.jpg', '7890123456', 7, 107),
    ('1980-06-25', 8, 'Parent Institute', '579 Birch Ct', 'Henry Parent', 'MALE', 'http://img.com/henry.jpg', '8901234567', 8, 108),
    ('1988-03-17', 9, 'Admin School', '680 Aspen Way', 'Ivy Admin', 'FEMALE', 'http://img.com/ivy.jpg', '9012345678', 9, 109),
    ('1992-08-05', 10, 'Event Academy', '791 Poplar St', 'Jack Manager', 'MALE', 'http://img.com/jack.jpg', '0123456789', 10, 110);
