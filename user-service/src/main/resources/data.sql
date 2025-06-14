
INSERT INTO users (status, email, password, role) VALUES
                                                      (TRUE,  'alice@example.com',  'alice12345',  'STUDENT'),
                                                      (FALSE, 'bob@example.com',    'bob12345',    'ADMIN'),
                                                      (TRUE,  'carol@example.com',  'carol12345',  'EVENT_MANAGER'),
                                                      (TRUE,  'dave@example.com',   'dave12345',   'PARENT'),
                                                      (FALSE, 'eve@example.com',    'eve12345',    'STUDENT'),
                                                      (TRUE,  'frank@example.com',  'frank12345',  'ADMIN'),
                                                      (FALSE, 'grace@example.com',  'grace12345',  'PARENT'),
                                                      (TRUE,  'heidi@example.com',  'heidi12345',  'EVENT_MANAGER'),
                                                      (TRUE,  'ivan@example.com',   'ivan12345',   'STUDENT'),
                                                      (FALSE, 'judy@example.com',   'judy12345',   'ADMIN');


INSERT INTO user_profile (
    user_id, birth_day, account_type, school, address, full_name, gender, image_url, phone_number
) VALUES
      (1, '2000-01-15', 'STANDARD', 'Greenwood High', '123 Main St', 'Alice Johnson', 'FEMALE', 'https://example.com/alice.jpg', '123-456-7890'),
      (2, '1985-07-30', 'PREMIUM', 'Westview School', '456 Oak Ave', 'Bob Smith', 'MALE', 'https://example.com/bob.jpg', '234-567-8901'),
      (3, '1990-04-10', 'STANDARD', 'Sunnydale Prep', '789 Pine Rd', 'Carol White', 'FEMALE', 'https://example.com/carol.jpg', '345-678-9012'),
      (4, '1975-03-25', 'PREMIUM', 'Hillcrest High', '135 Maple Ln', 'Dave Brown', 'MALE', 'https://example.com/dave.jpg', '456-789-0123'),
      (5, '2002-11-05', 'STANDARD', 'Brookfield Academy', '246 Birch Blvd', 'Eve Davis', 'FEMALE', 'https://example.com/eve.jpg', '567-890-1234'),
      (6, '1980-12-12', 'PREMIUM', 'Lakeside School', '357 Cedar Cir', 'Frank Wilson', 'MALE', 'https://example.com/frank.jpg', '678-901-2345'),
      (7, '1995-08-22', 'STANDARD', 'Oakwood School', '468 Elm Dr', 'Grace Lee', 'FEMALE', 'https://example.com/grace.jpg', '789-012-3456'),
      (8, '1988-06-18', 'PREMIUM', 'Silver Lake HS', '579 Willow Way', 'Heidi Martinez', 'FEMALE', 'https://example.com/heidi.jpg', '890-123-4567'),
      (9, '2001-09-14', 'STANDARD', 'Riverdale School', '680 Spruce St', 'Ivan Clark', 'MALE', 'https://example.com/ivan.jpg', '901-234-5678'),
      (10, '1992-05-09', 'PREMIUM', 'Harmony High', '791 Fir Ln', 'Judy Lewis', 'FEMALE', 'https://example.com/judy.jpg', '012-345-6789');
