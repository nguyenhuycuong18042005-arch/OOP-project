#Hệ Thống Quản Lý Thư Viện (Library Management System - LMS)
Ứng dụng quản lý thư viện được viết bằng ngôn ngữ Java, sử dụng mô hình hướng đối tượng (OOP). Ứng dụng cung cấp giao diện Console (Command Line Interface) để thực hiện các nghiệp vụ quản lý sách, độc giả và mượn trả sách.
Ứng dụng được tạo ra để phụ vụ bài tập lớn môn OOP - Lập trình hướng đối tượng

## Tính năng chính
*   **Quản Lý Thủ Thư (Admin)**:
    *   Đăng nhập hệ thống (Mặc định: `admin` / `admin123`).
*   **Quản Lý Sách (Book Management)**:
    *   Thêm đầu sách mới (ISBN, tiêu đề, tác giả...).
    *   Quản lý sách vật lý (Các bản in cụ thể trên kệ).
    *   Tìm kiếm sách theo Tiêu đề, ISBN, Tác giả.
*   **Quản Lý Độc Giả (Reader Management)**:
    *   Đăng ký độc giả mới.
    *   Theo dõi thẻ thư viện và thời hạn.
    *   Xem lịch sử mượn sách.
*   **Quản Lý Mượn / Trả (Loan Management)**:
    *   Tạo phiếu mượn sách (Kiểm tra điều kiện thẻ, số lượng giới hạn).
    *   Xử lý trả sách và tính tiền phạt (nếu quá hạn).
*   **Báo Cáo Thống Kê**:
    *   Thống kê sách đang mượn/sẵn sàng.
    *   Top sách được mượn nhiều nhất.
    *   Top độc giả tích cực.
 
## Công Nghệ Sử Dụng

*   **Ngôn Ngữ**: Java 17
*   **Build Tool**: Maven (Dùng để build project)
*   **Lưu Trữ Dữ Liệu**: JSON Files (sử dụng thư viện `Gson` của Google).
*   **Giao Diện**: Console Application (JavaFX).

## Cài đặt và chạy ứng dụng
1. **Cài đặt thư viện dependencies**
mvn clean install
2. **Chạy ứng dụng**
mvn javaFX:run

## Tác giả
Mai Tiến Hoàng
Nguyễn Văn Phát
Trần Chí Cường
Vũ Xuân Kỳ
Nguyễn Huy Cường

  
