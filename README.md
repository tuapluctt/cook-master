
## Công nghệ sử dụng

*   **Framework:** Spring Boot (Phiên bản 3.x)
*   **Database:** MySQL (Hoặc PostgreSQL, H2 tùy cấu hình)
*   **Bảo mật:** Spring Security, JWT (JSON Web Tokens)
*   **Lưu trữ ảnh:** AWS S3 (Amazon Simple Storage Service)
*   **Build Tool:** Maven
*   **Mapping:** MapStruct (Tùy chọn, nếu bạn dùng)
*   **Logging:** SLF4J

## Tính năng chính

*   **Xác thực người dùng:** Đăng ký, đăng nhập, làm mới token, đăng xuất sử dụng JWT.
*   **Quản lý người dùng:** Lấy thông tin người dùng, cập nhật hồ sơ (bao gồm ảnh đại diện - tùy chọn).
*   **Quản lý Công thức (Recipe):**
    *   Tạo mới công thức (trạng thái nháp).
    *   Xem danh sách công thức (có thể có phân trang, lọc).
    *   Xem chi tiết công thức (bao gồm nguyên liệu, các bước, ảnh).
    *   Cập nhật thông tin công thức (tiêu đề, mô tả, thời gian nấu, khẩu phần, trạng thái publish/draft).
    *   Xóa công thức.
    *   Tải lên/Cập nhật ảnh đại diện cho công thức.
*   **Quản lý Nguyên liệu (Ingredient):**
    *   Thêm nguyên liệu vào công thức.
    *   Cập nhật thông tin nguyên liệu.
    *   Xóa nguyên liệu khỏi công thức.
*   **Quản lý Bước thực hiện (Recipe Step):**
    *   Thêm bước thực hiện vào công thức.
    *   Cập nhật mô tả bước.
    *   Xóa bước khỏi công thức.
    *   Quản lý thứ tự các bước.
*   **Quản lý Ảnh Bước (Step Image):**
    *   Tải lên/Thêm ảnh minh họa cho từng bước.
    *   Cập nhật ảnh của bước.
    *   Xóa ảnh khỏi bước.
    *   Quản lý thứ tự hiển thị ảnh.
*   **Phân quyền:** Role-based Access Control.

## Yêu cầu hệ thống

*   JDK 21.
*   Maven 3.6 trở lên.
*   Cơ sở dữ liệu MySQL.
*   Tài khoản AWS và cấu hình S3 bucket (nếu sử dụng tính năng lưu trữ ảnh).
