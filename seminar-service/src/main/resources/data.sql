-- Insert Users with different roles
INSERT INTO users (id, username, password, role, email, full_name, phone, address, status) VALUES
(1, 'admin1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'ADMIN', 'admin1@example.com', 'Admin User', '0123456789', 'Admin Address', true),
(2, 'event1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'EVENT_MANAGER', 'event1@example.com', 'Event Manager', '0123456788', 'Event Address', true),
(3, 'student1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'STUDENT', 'student1@example.com', 'Student User', '0123456787', 'Student Address', true),
(4, 'parent1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'PARENT', 'parent1@example.com', 'Parent User', '0123456786', 'Parent Address', true);

-- Insert Seminars
INSERT INTO seminars (id, title, description, duration, price, meeting_url, form_url, status, status_approve, slot, image_url, create_by) VALUES
(1, 'Web Development Workshop', 'Learn about modern web development', 120, 0, 'https://meet.google.com/web', 'https://forms.google.com/web', 'PENDING', 'PENDING', 20, 'https://example.com/web.jpg', 2),
(2, 'Data Science Seminar', 'Introduction to Data Science', 90, 0, 'https://meet.google.com/data', 'https://forms.google.com/data', 'ONGOING', 'APPROVED', 15, 'https://example.com/data.jpg', 2),
(3, 'AI Workshop', 'Deep dive into AI and Machine Learning', 180, 0, 'https://meet.google.com/ai', 'https://forms.google.com/ai', 'COMPLETED', 'APPROVED', 10, 'https://example.com/ai.jpg', 2);

-- Insert Seminar Tickets
INSERT INTO seminar_tickets (id, seminar_id, user_profile_id, description, starting_time, booking_time, status) VALUES
(1, 1, 3, 'Student ticket for Web Development Workshop', '2024-06-20 09:00:00', '2024-06-13 10:00:00', true),
(2, 2, 4, 'Parent ticket for Data Science Seminar', '2024-06-21 14:00:00', '2024-06-13 11:00:00', true),
(3, 3, 4, 'Parent ticket for AI Workshop', '2024-06-22 10:00:00', '2024-06-13 12:00:00', true);