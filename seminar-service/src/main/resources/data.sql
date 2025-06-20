-- -- Insert Users with different roles
-- INSERT INTO users (id, username, password, role, email, full_name, phone, address, status) VALUES
-- (1, 'admin1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'ADMIN', 'admin1@example.com', 'Admin User', '0123456789', 'Admin Address', true),
-- (2, 'event1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'EVENT_MANAGER', 'event1@example.com', 'Event Manager', '0123456788', 'Event Address', true),
-- (3, 'student1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'STUDENT', 'student1@example.com', 'Student User', '0123456787', 'Student Address', true),
-- (4, 'parent1', '$2a$10$rDkPvvAFV6GgJjXpYWJhUOQZxJZxJZxJZxJZxJZxJZxJZxJZxJZx', 'PARENT', 'parent1@example.com', 'Parent User', '0123456786', 'Parent Address', true);

-- Insert Seminars
INSERT INTO seminars (title, description, duration, price, meeting_url, form_url, status, status_approve, slot, image_url, create_by) VALUES
('Web Development Workshop', 'Learn modern web development techniques and best practices.', 180, 50.0, 'https://meet.google.com/web-dev', 'https://forms.google.com/web-dev', 'PENDING', 'APPROVED', 30, 'https://example.com/images/web-dev.jpg', 3),
('Data Science Seminar', 'Introduction to data science and machine learning concepts.', 240, 75.0, 'https://meet.google.com/data-sci', 'https://forms.google.com/data-sci', 'ONGOING', 'APPROVED', 25, 'https://example.com/images/data-sci.jpg', 3),
('AI Workshop', 'Explore artificial intelligence and its applications.', 180, 60.0, 'https://meet.google.com/ai-workshop', 'https://forms.google.com/ai', 'PENDING', 'APPROVED', 20, 'https://example.com/images/ai.jpg', 3);

-- Insert Seminar Tickets
INSERT INTO seminar_tickets (seminar_id, user_id, description, starting_time, booking_time, status) VALUES
((SELECT id FROM seminars WHERE title = 'Web Development Workshop'), 3, 'Student ticket for Web Development Workshop', '2024-06-20 09:00:00', '2024-06-13 10:00:00', true),
((SELECT id FROM seminars WHERE title = 'Data Science Seminar'), 4, 'Parent ticket for Data Science Seminar', '2024-06-21 14:00:00', '2024-06-13 11:00:00', true),
((SELECT id FROM seminars WHERE title = 'AI Workshop'), 4, 'Parent ticket for AI Workshop', '2024-06-22 10:00:00', '2024-06-13 12:00:00', true);