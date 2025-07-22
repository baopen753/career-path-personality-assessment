This file is useful for building docker containers

1. Chuẩn bị
    + ngrok

2. Setup
    + cài đặt ngrok: https://ngrok.com/downloads
    + Sau khi cài đặt, vô cmd window, đứng ở đường dẫn chứa folder ngrok vừa tải
    + Gõ lệnh:
        1. docker network create swp391
        2. docker run --rm -it --network swd392 -e NGROK_AUTHTOKEN=2afapkwFcXNMiPIvWcwXrWNh8F7_6BvQfVpLQwC8ysQww4UQ6 -p 4040:4040 ngrok/ngrok:3 http localhost:8086

    + Nhìn ở dòng Forwarding: copy toàn bộ đường dẫn bên trái của mũi tên '->'      ví dụ:  https://.....ngrok-free.app       WARNING: ko được tắt terminal của ngrok đang chạy

    + Paste vào file .env , override lại giá trị hiện tại của key 'WEBHOOK_BASE_URI'


3. Compose
    Đi vào thư mục /docker-compose

Mô tả: Bên trong folder chứa 2 file compose: docker-compose-infas, docker-compose-microservice
Chạy lệnh sau với quyền (root linux/administrator window):
    Khởi tạo container:
        1. docker compose -f docker-compose-infras.yml -f docker-compose-microservice.yml up --build -d   (12/12 containers)
        2. docker compose -f docker-compose-payment.yml up --build -d                                     (1/1 container)

    Kill container :
        1. docker compose -f docker-compose-infras.yml -f docker-compose-microservice.yml down -v
        2. docker compose -f docker-compose-payment.yml down -v