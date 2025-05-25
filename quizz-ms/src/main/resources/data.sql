-- Insert initial data only if tables are empty
DO $$ 
BEGIN
    -- Insert options if table is empty
    IF NOT EXISTS (SELECT 1 FROM options LIMIT 1) THEN
        INSERT INTO options (option_text, score_delta) VALUES
        ('Strongly Agree', 'POSITIVE'),
        ('Agree', 'POSITIVE'),
        ('Neutral', 'NEUTRAL'),
        ('Disagree', 'NEGATIVE'),
        ('Strongly Disagree', 'NEGATIVE');
    END IF;

    -- Insert questions if table is empty
    IF NOT EXISTS (SELECT 1 FROM questions LIMIT 1) THEN
        INSERT INTO questions (text, dimension, order_number) VALUES
        ('I enjoy being the center of attention', 'EXTRAVERSION_INTROVERSION', 1),
        ('I prefer to work alone rather than in a group', 'EXTRAVERSION_INTROVERSION', 2),
        ('I notice small sounds when others do not', 'SENSING_INTUITION', 3),
        ('I often make decisions based on my feelings', 'THINKING_FEELING', 4),
        ('I prefer to have a plan rather than be spontaneous', 'JUDGING_PERCEIVING', 5);
    END IF;
END $$; 