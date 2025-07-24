-- Chèn Danh mục
INSERT INTO category (name, description) VALUES
                                             ('Đánh giá Tính cách MBTI', 'Đánh giá Chỉ số Loại hình Myers-Briggs để xác định loại tính cách dựa trên bốn chiều: Hướng ngoại/Hướng nội, Cảm nhận/Trực giác, Tư duy/Cảm xúc, Phán xét/Nhận thức'),
                                             ('Đánh giá Tính cách DISC', 'Đánh giá hành vi DISC để xác định phong cách tính cách dựa trên bốn chiều: Dominance, Influence, Steadiness, Conscientiousness');

-- Chèn Tiêu chuẩn Tính cách cho MBTI (16 loại) - Giữ nguyên
INSERT INTO personality_standard (standard, personality_code, nickname, key_traits, description, career_mapping_personality) VALUES
                                                                                                                                 ('MBTI', 'ENFP', 'The Campaigner', 'Nhiệt tình, Sáng tạo, Tự phát, Hướng về con người', 'Những cá nhân nhiệt tình và sáng tạo, nhìn cuộc sống đầy những khả năng. Họ nhanh chóng kết nối giữa các sự kiện và thông tin, và tự tin tiến hành dựa trên các mô hình họ nhận thấy.', 'Marketing, Báo chí, Truyền thông, Du lịch, Quản trị Kinh doanh, Sư phạm Tiếng Anh, Sư phạm Ngữ văn, Thiết kế Đồ họa, Quan hệ Quốc tế, Thiết kế'),
                                                                                                                                 ('MBTI', 'INFP', 'The Mediator', 'Lý tưởng, Trung thành, Quan tâm, Định hướng giá trị', 'Những người lý tưởng và trung thành với giá trị của họ và với những người quan trọng với họ. Họ muốn cuộc sống bên ngoài phù hợp với giá trị của mình.', 'Sư phạm Ngữ văn, Thiết kế Đồ họa, Y học dự phòng, Triết học, Ngữ văn, Sinh học, Khoa học Môi trường, Thiết kế'),
                                                                                                                                 ('MBTI', 'ENFJ', 'The Protagonist', 'Ấm áp, Đồng cảm, Có trách nhiệm, Hỗ trợ', 'Nồng ấm, đồng cảm, nhạy bén và có trách nhiệm. Rất nhạy cảm với cảm xúc, nhu cầu và động lực của người khác.', 'Giáo dục Tiểu học, Quản trị Kinh doanh, Điều dưỡng, Truyền thông, Báo chí, Quan hệ Quốc tế, Sư phạm Toán học, Sư phạm Ngữ văn, Sư phạm Tiếng Anh, Y khoa, Dược học, Răng - Hàm - Mặt, Thú y, Quản lý'),
                                                                                                                                 ('MBTI', 'INFJ', 'The Advocate', 'Sâu sắc, Truyền cảm hứng, Quyết đoán, Vị tha', 'Tìm kiếm ý nghĩa và kết nối trong ý tưởng, mối quan hệ và tài sản vật chất. Họ muốn hiểu điều gì thúc đẩy con người và có cái nhìn sâu sắc về người khác.', 'Y học dự phòng, Triết học, Ngữ văn, Sư phạm Ngữ văn, Sinh học, Khoa học Môi trường'),
                                                                                                                                 ('MBTI', 'ENTP', 'The Debater', 'Thông minh, Tò mò, Đổi mới, Năng động', 'Nhanh nhẹn, sáng tạo, kích thích, cảnh giác và thẳng thắn. Họ tháo vát trong việc giải quyết các vấn đề mới và thách thức.', 'Luật, Marketing, Báo chí, Khoa học Máy tính, Kỹ thuật Phần mềm, Kỹ thuật Hệ thống Công nghiệp, Kinh tế, Quản trị Kinh doanh, An toàn thông tin, Mạng Máy tính và Truyền thông dữ liệu, Quản lý, Truyền thông'),
                                                                                                                                 ('MBTI', 'INTP', 'The Thinker', 'Khách quan, Phân tích, Lý thuyết, Độc lập', 'Tìm cách phát triển các giải thích logic cho mọi thứ khiến họ quan tâm. Họ lý thuyết và trừu tượng, quan tâm đến ý tưởng hơn là tương tác xã hội.', 'Khoa học Máy tính, Toán học, Vật lý, Kỹ thuật Phần mềm, An toàn thông tin, Kỹ thuật Hóa học, Kỹ thuật Điện - Điện tử, Kỹ thuật Xây dựng, Kỹ thuật Cơ khí, Kỹ thuật Y sinh, Kỹ thuật Dệt, Quản lý Công nghiệp, Công nghệ Thông tin, Kỹ thuật Hệ thống Công nghiệp, Công nghệ Sinh học, Hệ thống Thông tin, Mạng Máy tính và Truyền thông dữ liệu, Kinh tế, Kỹ thuật Hàng không, Công nghệ Kỹ thuật Cơ khí, Triết học, Hóa học, Sinh học'),
                                                                                                                                 ('MBTI', 'ENTJ', 'The Commander', 'Mạnh dạn, Giàu trí tưởng tượng, Ý chí mạnh mẽ, Chiến lược', 'Thẳng thắn, quyết đoán, lãnh đạo tự nhiên. Họ nhanh chóng nhận ra các quy trình và chính sách không hợp lý và kém hiệu quả, và phát triển các hệ thống toàn diện để giải quyết vấn đề tổ chức.', 'Quản lý, Luật, Quản trị Kinh doanh, Quản lý Công nghiệp, Tài chính - Ngân hàng, Kỹ thuật Hệ thống Công nghiệp, Quản trị Luật, Kế toán, Quản lý Hoạt động Bay, Quản trị Kinh doanh Hàng không, Kinh tế, Quản lý Đất đai'),
                                                                                                                                 ('MBTI', 'INTJ', 'The Architect', 'Giàu trí tưởng tượng, Quyết đoán, Tham vọng, Độc lập', 'Có trí óc độc đáo và động lực lớn để thực hiện ý tưởng và đạt được mục tiêu. Họ nhanh chóng nhận ra các mô hình trong các sự kiện bên ngoài và phát triển các góc nhìn giải thích dài hạn.', 'Kiến trúc, Khoa học Máy tính, Kỹ thuật Phần mềm, Toán học, Vật lý, Kế toán, An toàn thông tin, Quản lý, Kỹ thuật Xây dựng, Kỹ thuật Hóa học, Kỹ thuật Cơ khí, Kỹ thuật Điện - Điện tử, Kỹ thuật Y sinh, Kỹ thuật Dệt, Quản lý Công nghiệp, Công nghệ Thông tin, Kỹ thuật Hệ thống Công nghiệp, Công nghệ Sinh học, Tài chính - Ngân hàng, Hệ thống Thông tin, Mạng Máy tính và Truyền thông dữ liệu, Kinh tế, Quản lý Hoạt động Bay, Kỹ thuật Hàng không, Quản trị Kinh doanh Hàng không, Công nghệ Kỹ thuật Cơ khí, Quản trị Luật, Lâm nghiệp, Quản lý Đất đai, Triết học, Hóa học, Sinh học, Khoa học Môi trường, Lịch sử, Quan hệ Quốc tế'),
                                                                                                                                 ('MBTI', 'ESFP', 'The Entertainer', 'Tự phát, Nhiệt tình, Linh hoạt, Hướng về con người', 'Hòa đồng, thân thiện và dễ chấp nhận. Họ tỏa ra sự ấm áp và nhiệt tình, và làm cho mọi thứ thú vị hơn cho người khác bằng sự thích thú của mình.', 'Marketing, Du lịch, Quản trị Kinh doanh, Truyền thông, Báo chí, Sư phạm Tiếng Anh, Sư phạm Ngữ văn, Giáo dục Tiểu học, Điều dưỡng, Thiết kế Đồ họa, Thiết kế, Ngôn ngữ Anh, Ngôn ngữ Trung Quốc'),
                                                                                                                                 ('MBTI', 'ISFP', 'The Adventurer', 'Quyến rũ, Nhạy cảm, Tò mò, Nghệ thuật', 'Trầm lặng, thân thiện, nhạy cảm và tốt bụng. Họ tận hưởng khoảnh khắc hiện tại và những gì đang diễn ra xung quanh.', 'Thiết kế Đồ họa, Thiết kế, Du lịch, Điều dưỡng, Sư phạm Tiếng Anh, Sư phạm Ngữ văn, Giáo dục Tiểu học, Y học dự phòng'),
                                                                                                                                 ('MBTI', 'ESFJ', 'The Consul', 'Ấm áp, Tận tâm, Hài hòa, Hợp tác', 'Nồng ấm, tận tâm và hợp tác. Họ muốn có sự hài hòa trong môi trường của mình và làm việc với quyết tâm để thiết lập điều đó.', 'Điều dưỡng, Giáo dục Tiểu học, Quản lý, Kế toán, Sư phạm Toán học, Sư phạm Ngữ văn, Sư phạm Tiếng Anh, Quản trị Kinh doanh, Y khoa, Dược học, Răng - Hàm - Mặt, Thú y'),
                                                                                                                                 ('MBTI', 'ISFJ', 'The Protector', 'Ấm ấm, Chu đáo, Nhẹ nhàng, Có trách nhiệm', 'Trầm lặng, thân thiện, có trách nhiệm và tận tâm. Họ cam kết và ổn định trong việc đáp ứng nghĩa vụ của mình.', 'Kế toán, Điều dưỡng, Giáo dục Tiểu học, An toàn thông tin, Quản lý, Hệ thống Thông tin, Quản trị Kinh doanh, Quản lý Đất đai, Thú y, Lâm nghiệp, Nông học, Chăn nuôi'),
                                                                                                                                 ('MBTI', 'ESTP', 'The Entrepreneur', 'Tự phát, Năng động, Thực dụng, Thích nghi', 'Linh hoạt và khoan dung, những người giải quyết vấn đề thực dụng. Họ tập trung vào kết quả ngay lập tức và là những người thực hiện năng động.', 'Marketing, Quản lý, Tài chính - Ngân hàng, Quản lý Hoạt động Bay, Quản trị Kinh doanh, Kế toán, Luật, Quản lý Công nghiệp, Kinh tế, Báo chí, Truyền thông, Du lịch'),
                                                                                                                                 ('MBTI', 'ISTP', 'The Virtuoso', 'Mạnh dạn, Thực tế, Thử nghiệm, Thích nghi', 'Khoan dung và linh hoạt, những người quan sát trầm lặng cho đến khi xuất hiện vấn đề, sau đó hành động nhanh chóng để tìm giải pháp khả thi.', 'Khoa học Máy tính, Kỹ thuật Phần mềm, Kỹ thuật Cơ khí, Kỹ thuật Điện - Điện tử, An toàn thông tin, Kỹ thuật Hàng không, Kỹ thuật Xây dựng, Kỹ thuật Hóa học, Kỹ thuật Y sinh, Kỹ thuật Dệt, Quản lý Công nghiệp, Công nghệ Thông tin, Toán học, Vật lý, Hệ thống Thông tin, Mạng Máy tính và Truyền thông dữ liệu, Công nghệ Kỹ thuật Cơ khí, Thiết kế'),
                                                                                                                                 ('MBTI', 'ESTJ', 'The Executive', 'Có tổ chức, Thực tế, Quyết đoán, Truyền thống', 'Thực tế, thực dụng, quyết đoán. Họ nhanh chóng đưa ra quyết định, tổ chức các dự án và con người để hoàn thành công việc.', 'Quản lý, Kinh tế, Luật, Kế toán, Quản trị Kinh doanh, Tài chính - Ngân hàng, Quản lý Công nghiệp, Quản trị Luật, Quản lý Hoạt động Bay, Quản trị Kinh doanh Hàng không, Quản lý Đất đai, Nông học, Chăn nuôi, Lâm nghiệp'),
                                                                                                                                 ('MBTI', 'ISTJ', 'The Logistician', 'Trầm lặng, nghiêm túc, đáng tin cậy', 'Đạt được thành công bằng sự cẩn thận và đáng tin cậy. Họ thực tế, thực dụng, có trách nhiệm.', 'Kế toán, Quản lý, Luật, Khoa học Máy tính, An toàn thông tin, Kỹ thuật Xây dựng, Hệ thống Thông tin, Tài chính - Ngân hàng, Quản trị Kinh doanh, Quản lý Công nghiệp, Kỹ thuật Hóa học, Kỹ thuật Cơ khí, Kỹ thuật Điện - Điện tử, Kỹ thuật Y sinh, Kỹ thuật Dệt, Công nghệ Thông tin, Toán học, Vật lý, Công nghệ Sinh học, Mạng Máy tính và Truyền thông dữ liệu, Kinh tế, Quản lý Hoạt động Bay, Kỹ thuật Hàng không, Quản trị Kinh doanh Hàng không, Công nghệ Kỹ thuật Cơ khí, Quản trị Luật, Nông học, Chăn nuôi, Thú y, Lâm nghiệp, Quản lý Đất đai, Lịch sử');

-- Chèn Tiêu chuẩn Tính cách cho DISC (4 loại) - Giữ nguyên
INSERT INTO personality_standard (standard, personality_code, nickname, key_traits, description, career_mapping_personality) VALUES
                                                                                                                                 ('DISC', 'D', 'Dominance', 'Trực tiếp, Tập trung vào kết quả, Cạnh tranh, Quyết đoán', 'Những cá nhân trực tiếp và quyết đoán, tập trung vào kết quả cuối cùng và vượt qua thử thách. Họ thường nhanh nhẹn và quyết đoán.', 'Quản lý, Luật, Quản trị Kinh doanh, Tài chính - Ngân hàng, Kỹ thuật Hệ thống Công nghiệp, Quản lý Công nghiệp, Kế toán, Quản lý Hoạt động Bay, Quản trị Luật, Kinh tế, Kỹ thuật Xây dựng, Kỹ thuật Hóa học, Kỹ thuật Cơ khí, Kỹ thuật Điện - Điện tử, Khoa học Máy tính, Kỹ thuật Y sinh, Kỹ thuật Dệt, Công nghệ Thông tin, Toán học, Vật lý, Hóa học, Sinh học, Khoa học Môi trường, Quan hệ Quốc tế, Công nghệ Sinh học, Hệ thống Thông tin, Kỹ thuật Phần mềm, Mạng Máy tính và Truyền thông dữ liệu, Y khoa, Dược học, Răng - Hàm - Mặt, Kỹ thuật Hàng không, Quản trị Kinh doanh Hàng không, Công nghệ Kỹ thuật Cơ khí, Nông học, Chăn nuôi, Thú y, Lâm nghiệp, Quản lý Đất đai, An toàn thông tin, Kiến trúc, Báo chí, Truyền thông, Du lịch, Thiết kế'),
                                                                                                                                 ('DISC', 'I', 'Influence', 'Nhiệt tình, Lạc quan, Hướng về con người, Thuyết phục', 'Những cá nhân nhiệt tình và lạc quan, tập trung vào việc ảnh hưởng đến người khác và xây dựng mối quan hệ. Họ định hướng con người và năng động.', 'Marketing, Báo chí, Truyền thông, Du lịch, Quản trị Kinh doanh, Sư phạm Tiếng Anh, Sư phạm Ngữ văn, Giáo dục Tiểu học, Thiết kế Đồ họa, Thiết kế, Ngôn ngữ Anh, Ngôn ngữ Trung Quốc, Quan hệ Quốc tế, Y khoa, Điều dưỡng, Y học dự phòng, Răng - Hàm - Mặt, Thú y, Quản lý'),
                                                                                                                                 ('DISC', 'S', 'Steadiness', 'Kiên nhẫn, Đáng tin cậy, Hỗ trợ, Nhất quán', 'Những cá nhân kiên nhẫn và đáng tin cậy, tập trung vào hợp tác và duy trì sự ổn định. Họ thích môi trường ổn định, có thể dự đoán.', 'Điều dưỡng, Giáo dục Tiểu học, Sư phạm Toán học, Sư phạm Ngữ văn, Sư phạm Tiếng Anh, Kế toán, Quản lý, Quản trị Kinh doanh, Hệ thống Thông tin, Y khoa, Dược học, Răng - Hàm - Mặt, Thú y, Nông học, Chăn nuôi, Lâm nghiệp, Quản lý Đất đai, An toàn thông tin'),
                                                                                                                                 ('DISC', 'C', 'Conscientiousness', 'Chính xác, Phân tích, Có hệ thống, Tập trung vào chất lượng', 'Những cá nhân chính xác và phân tích, tập trung vào chất lượng và cách tiếp cận có hệ thống. Họ thích môi trường có cấu trúc với kỳ vọng rõ ràng.', 'Kế toán, Kỹ thuật Xây dựng, Kỹ thuật Hóa học, Kỹ thuật Cơ khí, Kỹ thuật Điện - Điện tử, Khoa học Máy tính, Kỹ thuật Y sinh, Kỹ thuật Dệt, Quản lý Công nghiệp, Công nghệ Thông tin, Toán học, Vật lý, Hóa học, Sinh học, Khoa học Môi trường, Triết học, Ngữ văn, Lịch sử, Quan hệ Quốc tế, Kỹ thuật Hệ thống Công nghiệp, Công nghệ Sinh học, Tài chính - Ngân hàng, Hệ thống Thông tin, Kỹ thuật Phần mềm, Mạng Máy tính và Truyền thông dữ liệu, Kinh tế, Luật, Quản lý, Y khoa, Dược học, Răng - Hàm - Mặt, Quản lý Hoạt động Bay, Kỹ thuật Hàng không, Quản trị Kinh doanh Hàng không, Công nghệ Kỹ thuật Cơ khí, Quản trị Luật, Nông học, Chăn nuôi, Thú y, Lâm nghiệp, Quản lý Đất đai, An toàn thông tin, Thiết kế, Kiến trúc, Truyền thông, Báo chí, Du lịch, Marketing, Ngôn ngữ Anh, Ngôn ngữ Trung Quốc, Sư phạm Toán học, Sư phạm Ngữ văn, Sư phạm Tiếng Anh, Giáo dục Tiểu học, Điều dưỡng, Y học dự phòng');

-- Chèn Bài kiểm tra - CẬP NHẬT SỐ LƯỢNG CÂU HỎI MBTI
INSERT INTO quiz (title, category_id, description, question_quantity) VALUES
                                                                          ('Đánh giá Loại Tính cách MBTI (40 câu)', 1, 'Bài đánh giá rút gọn gồm 40 câu hỏi để xác định loại tính cách Myers-Briggs của bạn qua bốn chiều', 40),
                                                                          ('Đánh giá Phong cách Hành vi DISC', 2, 'Bài đánh giá toàn diện gồm 24 khối để xác định sở thích phong cách hành vi DISC của bạn', 24);

-- Chèn Câu hỏi MBTI (40 câu hỏi, 10 câu mỗi chiều) - CẬP NHẬT
-- Hướng ngoại (E) vs Hướng nội (I) - Câu hỏi 1-10
INSERT INTO quiz_question (content, order_number, dimension, quiz_id) VALUES
                                                                          ('Tôi cảm thấy tràn đầy năng lượng sau khi dành thời gian với một nhóm người đông', 1, 'E/I', 1),
                                                                          ('Tôi thích suy nghĩ thành tiếng và xử lý ý tưởng bằng cách nói chuyện', 2, 'E/I', 1),
                                                                          ('Tôi thích là trung tâm của sự chú ý tại các buổi tụ họp xã hội', 3, 'E/I', 1),
                                                                          ('Tôi dễ dàng và nhanh chóng kết bạn trong môi trường mới', 4, 'E/I', 1),
                                                                          ('Tôi thích làm việc theo nhóm hơn là làm việc một mình', 5, 'E/I', 1),
                                                                          ('Tôi cảm thấy thoải mái khi bắt đầu cuộc trò chuyện với người lạ', 6, 'E/I', 1),
                                                                          ('Tôi suy nghĩ tốt hơn khi có thể trao đổi ý tưởng với người khác', 7, 'E/I', 1),
                                                                          ('Tôi thích các bữa tiệc và sự kiện xã hội hơn là những buổi tối yên tĩnh ở nhà', 8, 'E/I', 1),
                                                                          ('Tôi có xu hướng hành động trước và suy nghĩ về hậu quả sau', 9, 'E/I', 1),
                                                                          ('Tôi cảm thấy mệt mỏi sau khi dành thời gian dài một mình', 10, 'E/I', 1);

-- Cảm nhận (S) vs Trực giác (N) - Câu hỏi 11-20
INSERT INTO quiz_question (content, order_number, dimension, quiz_id) VALUES
                                                                          ('Tôi thích thông tin cụ thể, thực tế hơn là các lý thuyết trừu tượng', 11, 'S/N', 1),
                                                                          ('Tôi tập trung vào thực tế hiện tại hơn là các khả năng trong tương lai', 12, 'S/N', 1),
                                                                          ('Tôi tin tưởng vào năm giác quan của mình hơn là linh cảm hoặc trực giác', 13, 'S/N', 1),
                                                                          ('Tôi thích hướng dẫn từng bước hơn là các nguyên tắc chung', 14, 'S/N', 1),
                                                                          ('Tôi chú ý và ghi nhớ các chi tiết cụ thể về con người và địa điểm', 15, 'S/N', 1),
                                                                          ('Tôi thích các phương pháp đã được chứng minh hơn là các cách tiếp cận sáng tạo', 16, 'S/N', 1),
                                                                          ('Tôi thích xử lý các sự kiện và trải nghiệm thực tế', 17, 'S/N', 1),
                                                                          ('Tôi thích các ứng dụng thực tế hơn là các khái niệm lý thuyết', 18, 'S/N', 1),
                                                                          ('Tôi tập trung vào cái đang có hơn là cái có thể có', 19, 'S/N', 1),
                                                                          ('Tôi học tốt nhất thông qua trải nghiệm thực tế', 20, 'S/N', 1);

-- Tư duy (T) vs Cảm xúc (F) - Câu hỏi 21-30
INSERT INTO quiz_question (content, order_number, dimension, quiz_id) VALUES
                                                                          ('Tôi đưa ra quyết định dựa trên phân tích logic hơn là giá trị cá nhân', 21, 'T/F', 1),
                                                                          ('Tôi có thể dễ dàng tách cảm xúc khỏi quá trình đưa ra quyết định', 22, 'T/F', 1),
                                                                          ('Tôi thích công bằng và khách quan hơn là quan tâm đến cảm xúc', 23, 'T/F', 1),
                                                                          ('Tôi coi trọng sự thật và độ chính xác hơn là sự hài hòa và khéo léo', 24, 'T/F', 1),
                                                                          ('Tôi có thể đưa ra lời phê bình mà không lo làm tổn thương cảm xúc', 25, 'T/F', 1),
                                                                          ('Tôi phân tích vấn đề một cách logic trước khi xem xét tác động cảm xúc', 26, 'T/F', 1),
                                                                          ('Tôi thích đưa ra quyết định dựa trên sự kiện và dữ liệu', 27, 'T/F', 1),
                                                                          ('Tôi tin rằng đúng đắn quan trọng hơn là được yêu thích', 28, 'T/F', 1),
                                                                          ('Tôi có thể giữ thái độ khách quan và không bị cuốn theo cảm xúc trong các tình huống cảm xúc', 29, 'T/F', 1),
                                                                          ('Tôi coi trọng năng lực và thành tựu hơn là mối quan hệ cá nhân', 30, 'T/F', 1);

-- Phán xét (J) vs Nhận thức (P) - Câu hỏi 31-40
INSERT INTO quiz_question (content, order_number, dimension, quiz_id) VALUES
                                                                          ('Tôi thích mọi thứ được giải quyết và quyết định hơn là để ngỏ', 31, 'J/P', 1),
                                                                          ('Tôi thích lập kế hoạch trước và tuân theo lịch trình', 32, 'J/P', 1),
                                                                          ('Tôi cảm thấy thoải mái hơn khi có một cấu trúc và thói quen rõ ràng', 33, 'J/P', 1),
                                                                          ('Tôi thích hoàn thành một dự án trước khi bắt đầu dự án khác', 34, 'J/P', 1),
                                                                          ('Tôi thích đưa ra quyết định nhanh chóng và tiến hành', 35, 'J/P', 1),
                                                                          ('Tôi cảm thấy căng thẳng khi có quá nhiều lựa chọn hoặc khả năng', 36, 'J/P', 1),
                                                                          ('Tôi thích môi trường ngăn nắp và có trật tự', 37, 'J/P', 1),
                                                                          ('Tôi thích có thời hạn và làm việc đều đặn để đạt được chúng', 38, 'J/P', 1),
                                                                          ('Tôi thích sự kết thúc và hoàn thành hơn là giữ các lựa chọn mở', 39, 'J/P', 1),
                                                                          ('Tôi lập danh sách và thích đánh dấu khi hoàn thành công việc', 40, 'J/P', 1);

-- Chèn Tùy chọn Bài kiểm tra MBTI (3 tùy chọn mỗi câu hỏi) - CẬP NHẬT
-- Giả sử ID câu hỏi được tự động tăng từ 1.
-- Các ID sau đây tương ứng với 40 câu hỏi MBTI đã được chèn ở trên.

-- Cho các câu hỏi E/I (question_id 1-10)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
-- Câu hỏi 1
('Không đồng ý', 'I', -1, 1), ('Trung lập', 'E/I', 0, 1), ('Đồng ý', 'E', 1, 1),
-- Câu hỏi 2
('Không đồng ý', 'I', -1, 2), ('Trung lập', 'E/I', 0, 2), ('Đồng ý', 'E', 1, 2),
-- Câu hỏi 3
('Không đồng ý', 'I', -1, 3), ('Trung lập', 'E/I', 0, 3), ('Đồng ý', 'E', 1, 3),
-- Câu hỏi 4
('Không đồng ý', 'I', -1, 4), ('Trung lập', 'E/I', 0, 4), ('Đồng ý', 'E', 1, 4),
-- Câu hỏi 5
('Không đồng ý', 'I', -1, 5), ('Trung lập', 'E/I', 0, 5), ('Đồng ý', 'E', 1, 5),
-- Câu hỏi 6
('Không đồng ý', 'I', -1, 6), ('Trung lập', 'E/I', 0, 6), ('Đồng ý', 'E', 1, 6),
-- Câu hỏi 7
('Không đồng ý', 'I', -1, 7), ('Trung lập', 'E/I', 0, 7), ('Đồng ý', 'E', 1, 7),
-- Câu hỏi 8
('Không đồng ý', 'I', -1, 8), ('Trung lập', 'E/I', 0, 8), ('Đồng ý', 'E', 1, 8),
-- Câu hỏi 9
('Không đồng ý', 'I', -1, 9), ('Trung lập', 'E/I', 0, 9), ('Đồng ý', 'E', 1, 9),
-- Câu hỏi 10
('Không đồng ý', 'I', -1, 10), ('Trung lập', 'E/I', 0, 10), ('Đồng ý', 'E', 1, 10);

-- Cho các câu hỏi S/N (question_id 11-20)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
-- Câu hỏi 11
('Không đồng ý', 'N', -1, 11), ('Trung lập', 'S/N', 0, 11), ('Đồng ý', 'S', 1, 11),
-- Câu hỏi 12
('Không đồng ý', 'N', -1, 12), ('Trung lập', 'S/N', 0, 12), ('Đồng ý', 'S', 1, 12),
-- Câu hỏi 13
('Không đồng ý', 'N', -1, 13), ('Trung lập', 'S/N', 0, 13), ('Đồng ý', 'S', 1, 13),
-- Câu hỏi 14
('Không đồng ý', 'N', -1, 14), ('Trung lập', 'S/N', 0, 14), ('Đồng ý', 'S', 1, 14),
-- Câu hỏi 15
('Không đồng ý', 'N', -1, 15), ('Trung lập', 'S/N', 0, 15), ('Đồng ý', 'S', 1, 15),
-- Câu hỏi 16
('Không đồng ý', 'N', -1, 16), ('Trung lập', 'S/N', 0, 16), ('Đồng ý', 'S', 1, 16),
-- Câu hỏi 17
('Không đồng ý', 'N', -1, 17), ('Trung lập', 'S/N', 0, 17), ('Đồng ý', 'S', 1, 17),
-- Câu hỏi 18
('Không đồng ý', 'N', -1, 18), ('Trung lập', 'S/N', 0, 18), ('Đồng ý', 'S', 1, 18),
-- Câu hỏi 19
('Không đồng ý', 'N', -1, 19), ('Trung lập', 'S/N', 0, 19), ('Đồng ý', 'S', 1, 19),
-- Câu hỏi 20
('Không đồng ý', 'N', -1, 20), ('Trung lập', 'S/N', 0, 20), ('Đồng ý', 'S', 1, 20);

-- Cho các câu hỏi T/F (question_id 21-30)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
-- Câu hỏi 21
('Không đồng ý', 'F', -1, 21), ('Trung lập', 'T/F', 0, 21), ('Đồng ý', 'T', 1, 21),
-- Câu hỏi 22
('Không đồng ý', 'F', -1, 22), ('Trung lập', 'T/F', 0, 22), ('Đồng ý', 'T', 1, 22),
-- Câu hỏi 23
('Không đồng ý', 'F', -1, 23), ('Trung lập', 'T/F', 0, 23), ('Đồng ý', 'T', 1, 23),
-- Câu hỏi 24
('Không đồng ý', 'F', -1, 24), ('Trung lập', 'T/F', 0, 24), ('Đồng ý', 'T', 1, 24),
-- Câu hỏi 25
('Không đồng ý', 'F', -1, 25), ('Trung lập', 'T/F', 0, 25), ('Đồng ý', 'T', 1, 25),
-- Câu hỏi 26
('Không đồng ý', 'F', -1, 26), ('Trung lập', 'T/F', 0, 26), ('Đồng ý', 'T', 1, 26),
-- Câu hỏi 27
('Không đồng ý', 'F', -1, 27), ('Trung lập', 'T/F', 0, 27), ('Đồng ý', 'T', 1, 27),
-- Câu hỏi 28
('Không đồng ý', 'F', -1, 28), ('Trung lập', 'T/F', 0, 28), ('Đồng ý', 'T', 1, 28),
-- Câu hỏi 29
('Không đồng ý', 'F', -1, 29), ('Trung lập', 'T/F', 0, 29), ('Đồng ý', 'T', 1, 29),
-- Câu hỏi 30
('Không đồng ý', 'F', -1, 30), ('Trung lập', 'T/F', 0, 30), ('Đồng ý', 'T', 1, 30);

-- Cho các câu hỏi J/P (question_id 31-40)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
-- Câu hỏi 31
('Không đồng ý', 'P', -1, 31), ('Trung lập', 'J/P', 0, 31), ('Đồng ý', 'J', 1, 31),
-- Câu hỏi 32
('Không đồng ý', 'P', -1, 32), ('Trung lập', 'J/P', 0, 32), ('Đồng ý', 'J', 1, 32),
-- Câu hỏi 33
('Không đồng ý', 'P', -1, 33), ('Trung lập', 'J/P', 0, 33), ('Đồng ý', 'J', 1, 33),
-- Câu hỏi 34
('Không đồng ý', 'P', -1, 34), ('Trung lập', 'J/P', 0, 34), ('Đồng ý', 'J', 1, 34),
-- Câu hỏi 35
('Không đồng ý', 'P', -1, 35), ('Trung lập', 'J/P', 0, 35), ('Đồng ý', 'J', 1, 35),
-- Câu hỏi 36
('Không đồng ý', 'P', -1, 36), ('Trung lập', 'J/P', 0, 36), ('Đồng ý', 'J', 1, 36),
-- Câu hỏi 37
('Không đồng ý', 'P', -1, 37), ('Trung lập', 'J/P', 0, 37), ('Đồng ý', 'J', 1, 37),
-- Câu hỏi 38
('Không đồng ý', 'P', -1, 38), ('Trung lập', 'J/P', 0, 38), ('Đồng ý', 'J', 1, 38),
-- Câu hỏi 39
('Không đồng ý', 'P', -1, 39), ('Trung lập', 'J/P', 0, 39), ('Đồng ý', 'J', 1, 39),
-- Câu hỏi 40
('Không đồng ý', 'P', -1, 40), ('Trung lập', 'J/P', 0, 40), ('Đồng ý', 'J', 1, 40);

-- =============================================
-- Chèn Câu hỏi & Tùy chọn DISC (Đầy đủ 24 khối)
-- =============================================

-- Chèn Câu hỏi DISC (24 khối, mỗi khối đại diện cho một câu hỏi với 4 lựa chọn)
-- Giả sử ID câu hỏi MBTI cuối cùng là 40, ID câu hỏi DISC sẽ bắt đầu từ 41.
INSERT INTO quiz_question (content, order_number, dimension, quiz_id) VALUES
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 1, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 2, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 3, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 4, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 5, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 6, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 7, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 8, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 9, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 10, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 11, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 12, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 13, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 14, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 15, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 16, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 17, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 18, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 19, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 20, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 21, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 22, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 23, 'DISC', 2),
                                                                          ('Chọn câu hỏi giống bạn NHẤT và câu hỏi ÍT NHẤT giống bạn từ các lựa chọn sau:', 24, 'DISC', 2);

-- Chèn Tùy chọn Bài kiểm tra DISC (4 lựa chọn mỗi câu hỏi, đại diện cho các đặc điểm D, I, S, C)
-- Giả sử ID câu hỏi bắt đầu từ 41 cho DISC
-- Khối 1 (Câu hỏi 41)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi mạnh mẽ và trực tiếp trong giao tiếp', 'D', 2, 41),
                                                                                   ('Tôi nhiệt tình và lạc quan về các dự án', 'I', 2, 41),
                                                                                   ('Tôi kiên nhẫn và hỗ trợ các thành viên trong nhóm', 'S', 2, 41),
                                                                                   ('Tôi cẩn thận và chính xác trong công việc', 'C', 2, 41);
-- Khối 2 (Câu hỏi 42)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi nắm quyền kiểm soát trong các tình huống khó khăn', 'D', 2, 42),
                                                                                   ('Tôi truyền cảm hứng cho người khác bằng năng lượng và tầm nhìn của mình', 'I', 2, 42),
                                                                                   ('Tôi lắng nghe cẩn thận để hiểu nhu cầu của người khác', 'S', 2, 42),
                                                                                   ('Tôi phân tích thông tin kỹ lưỡng trước khi quyết định', 'C', 2, 42);
-- Khối 3 (Câu hỏi 43)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thích môi trường cạnh tranh và các thử thách', 'D', 2, 43),
                                                                                   ('Tôi dễ dàng xây dựng mối quan hệ với người mới', 'I', 2, 43),
                                                                                   ('Tôi mang lại sự ổn định và nhất quán cho đội nhóm', 'S', 2, 43),
                                                                                   ('Tôi tập trung vào chất lượng và làm mọi thứ đúng', 'C', 2, 43);
-- Khối 4 (Câu hỏi 44)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi đưa ra quyết định nhanh chóng dưới áp lực', 'D', 2, 44),
                                                                                   ('Tôi thúc đẩy người khác bằng sự nhiệt tình', 'I', 2, 44),
                                                                                   ('Tôi làm việc đều đặn và đáng tin cậy trong các nhiệm vụ', 'S', 2, 44),
                                                                                   ('Tôi đặt câu hỏi chi tiết để hiểu yêu cầu', 'C', 2, 44);
-- Khối 5 (Câu hỏi 45)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thúc đẩy kết quả và các mục tiêu cuối cùng', 'D', 2, 45),
                                                                                   ('Tôi giao tiếp với năng lượng và sự phấn khởi', 'I', 2, 45),
                                                                                   ('Tôi thể hiện sự quan tâm thực sự đến sức khỏe của người khác', 'S', 2, 45),
                                                                                   ('Tôi thích làm việc với các phương pháp và hệ thống đã được chứng minh', 'C', 2, 45);
-- Khối 6 (Câu hỏi 46)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi sẵn sàng chấp nhận rủi ro để đạt được mục tiêu', 'D', 2, 46),
                                                                                   ('Tôi thích động não và tạo ra ý tưởng mới', 'I', 2, 46),
                                                                                   ('Tôi thích hợp tác hơn là cạnh tranh', 'S', 2, 46),
                                                                                   ('Tôi thích có các quy trình rõ ràng để tuân theo', 'C', 2, 46);
-- Khối 7 (Câu hỏi 47)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi quyết đoán khi bày tỏ ý kiến của mình', 'D', 2, 47),
                                                                                   ('Tôi thích tương tác xã hội và các hoạt động nhóm', 'I', 2, 47),
                                                                                   ('Tôi bình tĩnh và điềm đạm trong các xung đột', 'S', 2, 47),
                                                                                   ('Tôi thích quan sát trước khi tham gia', 'C', 2, 47);
-- Khối 8 (Câu hỏi 48)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi tập trung vào việc đạt được kết quả đo lường được', 'D', 2, 48),
                                                                                   ('Tôi diễn đạt và sống động khi nói chuyện', 'I', 2, 48),
                                                                                   ('Tôi trung thành và đáng tin cậy trong các mối quan hệ', 'S', 2, 48),
                                                                                   ('Tôi có cách tiếp cận hệ thống trong việc giải quyết vấn đề', 'C', 2, 48);
-- Khối 9 (Câu hỏi 49)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thiếu kiên nhẫn với sự không hiệu quả và chậm trễ', 'D', 2, 49),
                                                                                   ('Tôi tự phát và thích nghi với sự thay đổi', 'I', 2, 49),
                                                                                   ('Tôi thích các thói quen và quy trình quen thuộc', 'S', 2, 49),
                                                                                   ('Tôi ngoại giao và khéo léo trong giao tiếp', 'C', 2, 49);
-- Khối 10 (Câu hỏi 50)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi táo bạo và sẵn sàng thách thức hiện trạng', 'D', 2, 50),
                                                                                   ('Tôi lạc quan về các khả năng trong tương lai', 'I', 2, 50),
                                                                                   ('Tôi khiêm tốn và nhún nhường về thành tựu của mình', 'S', 2, 50),
                                                                                   ('Tôi thận trọng khi thực hiện các thay đổi lớn', 'C', 2, 50);
-- Khối 11 (Câu hỏi 51)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi đối mặt trực tiếp và ngay lập tức với các vấn đề', 'D', 2, 51),
                                                                                   ('Tôi thuyết phục người khác bằng sự quyến rũ và nhiệt tình', 'I', 2, 51),
                                                                                   ('Tôi tìm kiếm sự đồng thuận và thống nhất từ nhóm', 'S', 2, 51),
                                                                                   ('Tôi nghiên cứu kỹ lưỡng trước khi đưa ra khuyến nghị', 'C', 2, 51);
-- Khối 12 (Câu hỏi 52)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi định hướng kết quả và tập trung vào mục tiêu', 'D', 2, 52),
                                                                                   ('Tôi định hướng con người và tập trung vào mối quan hệ', 'I', 2, 52),
                                                                                   ('Tôi định hướng quy trình và tập trung vào chi tiết', 'C', 2, 52),
                                                                                   ('Tôi định hướng an toàn và tập trung vào sự ổn định', 'S', 2, 52);
-- Khối 13 (Câu hỏi 53)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thích dẫn dắt hơn là đi theo', 'D', 2, 53),
                                                                                   ('Tôi thích sự đa dạng và thay đổi trong công việc', 'I', 2, 53),
                                                                                   ('Tôi thích làm việc nhóm và hợp tác', 'S', 2, 53),
                                                                                   ('Tôi thích có các tiêu chuẩn và kỳ vọng rõ ràng', 'C', 2, 53);
-- Khối 14 (Câu hỏi 54)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi quyết đoán và định hướng hành động', 'D', 2, 54),
                                                                                   ('Tôi sáng tạo và giàu trí tưởng tượng trong việc giải quyết vấn đề', 'I', 2, 54),
                                                                                   ('Tôi hỗ trợ và khuyến khích người khác', 'S', 2, 54),
                                                                                   ('Tôi logic và phân tích trong suy nghĩ của mình', 'C', 2, 54);
-- Khối 15 (Câu hỏi 55)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi đặt tiêu chuẩn cao và mong đợi sự xuất sắc', 'D', 2, 55),
                                                                                   ('Tôi tập trung vào các khía cạnh tích cực của tình huống', 'I', 2, 55),
                                                                                   ('Tôi kiên nhẫn với quá trình học tập của người khác', 'S', 2, 55),
                                                                                   ('Tôi coi trọng sự chính xác hơn tốc độ khi hoàn thành nhiệm vụ', 'C', 2, 55);
-- Khối 16 (Câu hỏi 56)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thoải mái với xung đột và đối đầu', 'D', 2, 56),
                                                                                   ('Tôi thoải mái khi là trung tâm của sự chú ý', 'I', 2, 56),
                                                                                   ('Tôi thoải mái với thói quen và sự dự đoán', 'S', 2, 56),
                                                                                   ('Tôi thoải mái khi làm việc phía sau hậu trường', 'C', 2, 56);
-- Khối 17 (Câu hỏi 57)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi ưu tiên hiệu quả và năng suất', 'D', 2, 57),
                                                                                   ('Tôi ưu tiên sự đổi mới và sáng tạo', 'I', 2, 57),
                                                                                   ('Tôi ưu tiên sự hài hòa và hợp tác', 'S', 2, 57),
                                                                                   ('Tôi ưu tiên sự chính xác và độ chính xác', 'C', 2, 57);
-- Khối 18 (Câu hỏi 58)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi giao tiếp một cách trực tiếp và thẳng thắn', 'D', 2, 58),
                                                                                   ('Tôi giao tiếp với cảm xúc và đam mê', 'I', 2, 58),
                                                                                   ('Tôi giao tiếp với sự đồng cảm và thấu hiểu', 'S', 2, 58),
                                                                                   ('Tôi giao tiếp với sự kiện và dữ liệu', 'C', 2, 58);
-- Khối 19 (Câu hỏi 59)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi phát triển trong môi trường nhanh, áp lực cao', 'D', 2, 59),
                                                                                   ('Tôi phát triển trong môi trường năng động, tập trung vào con người', 'I', 2, 59),
                                                                                   ('Tôi phát triển trong môi trường ổn định, hỗ trợ', 'S', 2, 59),
                                                                                   ('Tôi phát triển trong môi trường có cấu trúc, tổ chức', 'C', 2, 59);
-- Khối 20 (Câu hỏi 60)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi tập trung vào những gì cần làm ngay bây giờ', 'D', 2, 60),
                                                                                   ('Tôi tập trung vào các cơ hội và khả năng trong tương lai', 'I', 2, 60),
                                                                                   ('Tôi tập trung vào việc duy trì mối quan hệ và tinh thần', 'S', 2, 60),
                                                                                   ('Tôi tập trung vào việc tránh sai lầm và đảm bảo chất lượng', 'C', 2, 60);
-- Khối 21 (Câu hỏi 61)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi được thúc đẩy bởi việc đạt được các mục tiêu đầy thách thức', 'D', 2, 61),
                                                                                   ('Tôi được thúc đẩy bởi sự công nhận và đánh giá cao', 'I', 2, 61),
                                                                                   ('Tôi được thúc đẩy bởi việc giúp người khác thành công', 'S', 2, 61),
                                                                                   ('Tôi được thúc đẩy bởi việc làm mọi thứ đúng cách', 'C', 2, 61);
-- Khối 22 (Câu hỏi 62)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi thích làm việc độc lập với sự giám sát tối thiểu', 'D', 2, 62),
                                                                                   ('Tôi thích làm việc với người khác trong các nhóm hợp tác', 'I', 2, 62),
                                                                                   ('Tôi thích làm việc trong môi trường hỗ trợ, không đe dọa', 'S', 2, 62),
                                                                                   ('Tôi thích làm việc với các hướng dẫn và kỳ vọng rõ ràng', 'C', 2, 62);
-- Khối 23 (Câu hỏi 63)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi xử lý căng thẳng bằng cách kiểm soát và tiến lên', 'D', 2, 63),
                                                                                   ('Tôi xử lý căng thẳng bằng cách nói chuyện với người khác', 'I', 2, 63),
                                                                                   ('Tôi xử lý căng thẳng bằng cách giữ bình tĩnh và kiên nhẫn', 'S', 2, 63),
                                                                                   ('Tôi xử lý căng thẳng bằng cách phân tích tình huống một cách cẩn thận', 'C', 2, 63);
-- Khối 24 (Câu hỏi 64)
INSERT INTO quiz_options (option_text, target_trait, score_value, question_id) VALUES
                                                                                   ('Tôi tin vào việc chấp nhận rủi ro có tính toán để có phần thưởng lớn hơn', 'D', 2, 64),
                                                                                   ('Tôi tin vào việc duy trì các mối quan hệ tích cực trên hết', 'I', 2, 64),
                                                                                   ('Tôi tin vào việc đáng tin cậy và nhất quán', 'S', 2, 64),
                                                                                   ('Tôi tin vào việc cẩn thận và làm đúng ngay lần đầu tiên', 'C', 2, 64);
