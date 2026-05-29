# ☕ Café Harmony - Hệ Thống Quản Lý Quán Cà Phê

## 🚀 Tính Năng Nổi Bật

### 🔐 1. Hệ Thống Phân Quyền Chi Tiết (Role-Based Access Control)
* **Quản Lý (ROLE_ADMIN):** Toàn quyền điều hành hệ thống, quản trị tài nguyên chiến lược, tài chính, nhân sự và cấu hình dữ liệu.
* **Nhân Viên Bán Hàng (ROLE_USER):** Chỉ tiếp cận các phân hệ cơ bản phục vụ trực tiếp tại quầy.

#### 📊 Ma trận phân quyền chi tiết:
| Phân hệ chức năng | Quản lý (ADMIN) | Nhân viên (USER) | Ghi chú nghiệp vụ |
| :--- | :---: | :---: | :--- |
| **Trang chủ & Tổng quan** | ✅ Có | ✅ Có | Xem thông tin chung |
| **Trang cá nhân** | ✅ Có | ✅ Có | Xem và tự cập nhật thông tin cá nhân |
| **Sơ đồ bàn & Bán hàng** | ✅ Có | ✅ Có | Gọi món, chuyển bàn, tách/gộp bàn, in bill, thanh toán |
| **Quản lý Nhân sự** | ✅ Có | ❌ Không | Xem danh sách, thêm/sửa/xóa nhân viên và chức vụ |
| **Quản lý Thực đơn** | ✅ Có | ❌ Không | Thêm món mới, thay đổi giá, cấu hình định lượng món |
| **Quản lý Thiết bị** | ✅ Có | ❌ Không | Quản lý tài sản cố định, máy móc của quán |
| **Quản lý Kho hàng** | ✅ Có | ❌ Không | Nhập/xuất nguyên vật liệu, kiểm kê tồn kho hàng hóa |
| **Quản lý Marketing** | ✅ Có | ❌ Không | Tạo lập, điều chỉnh các chương trình khuyến mãi |
| **Quản lý Ngân sách** | ✅ Có | ❌ Không | Quản lý thu chi tổng, phê duyệt các khoản chi tiêu |
| **Quản lý Dữ liệu** | ✅ Có | ❌ Không | Sao lưu (Backup) và Phục hồi (Restore) dữ liệu |
| **Thống kê & Báo cáo** | ✅ Có | ❌ Không | Xem biểu đồ doanh thu, báo cáo số liệu kinh doanh |

### 🛒 2. Nghiệp Vụ Quản Lý Bán Hàng (POS)
* Quản lý trực quan sơ đồ bàn theo trạng thái (Trống, Đang sử dụng, Đã đặt trước).
* Thao tác nhanh: Gọi món, thêm/bớt số lượng, ghi chú món ăn.
* Xử lý bàn linh hoạt: Chuyển bàn hoặc gộp/tách hóa đơn linh hoạt khi khách yêu cầu.
* Áp dụng mã khuyến mãi tự động hoặc thủ công trực tiếp khi thanh toán.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

* **Backend:** Java 17+, Spring Boot 3.x, Spring Data JPA, Spring Security 6.x
* **Database:** PostgreSQL
* **Frontend:** Thymeleaf, Tailwind CSS, Thymeleaf Extras Spring Security 6, Lucide Icons
* **Build Tool:** Maven

---

## 📂 Cấu Trúc Dự Án (Project Structure)

```text
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── quanlycafe
│   │   │           └── cafe_management
│   │   │               ├── CafeManagementApplication.java
│   │   │               ├── config/              # Cấu hình hệ thống tổng thể
│   │   │               ├── controller/          # Các bộ điều hướng ứng dụng (Web Controllers)
│   │   │               ├── dto/                 # Data Transfer Objects (ChiTietGoiMon, ThuChi,...)
│   │   │               ├── entity/              # Thực thể ánh xạ cơ sở dữ liệu JPA (Ban, HoaDon, NhanVien,...)
│   │   │               ├── repository/          # Tầng tương tác dữ liệu Spring Data JPA
│   │   │               ├── security/            # Cấu hình Spring Security, CustomUserDetailsService
│   │   │               ├── service/             # Tầng xử lý logic nghiệp vụ (Business Logic Layer)
│   │   │               ├── utils/               # Các class tiện ích bổ trợ
│   │   │               └── validation/          # Logic kiểm tra tính hợp lệ dữ liệu nhập vào
│   │   └── resources
│   │       ├── application.properties           # File cấu hình biến môi trường và kết nối Database
│   │       ├── static/                          # Tài nguyên tĩnh
│   │       │   ├── css/style.css                # CSS custom biên dịch cùng Tailwind
│   │       │   ├── images/                      # Hệ thống biểu tượng, logo hình ảnh quán
│   │       │   └── js/                          # Mã xử lý Javascript tương tác tại bàn
│   │       └── templates/                       # Giao diện Thymeleaf HTML
│   │           ├── fragments/                   # Các thành phần tái sử dụng (sidebar, header, hoadon)
│   │           ├── admin/                       # Các trang quản trị dành cho ADMIN
│   │           ├── employee/                    # Các cấu phần dành cho nhân sự
│   │           └── *.html                       # Các trang tổng quan (home, profile, tables, login,...)
