Mở command tool:

Đi vào thư mục /docker-compose

Explain: Bên trong folder chứa 2 file compose: docker-compose-infas, docker-compose-microservice

Chạy lệnh sau với quyền (root linux/administrator window):
    Khởi tạo container: docker compose -f docker-compose-infras.yml -f docker-compose-microservice.yml up --build
    Kill container : docker compose -f docker-compose-infras.yml -f docker-compose-microservice.yml down -v