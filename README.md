# ☕ Café Harmony - Hệ Thống Quản Lý Quán Cà Phê Toàn Diện

Café Harmony là giải pháp phần mềm quản lý quán cà phê toàn diện được xây dựng trên nền tảng **Java Spring Boot** kết
hợp cơ sở dữ liệu quan hệ **PostgreSQL**. Hệ thống tuân thủ mô hình **MVC truyền thống (Server-Side Rendering)** sử dụng
**Thymeleaf Engine** kết hợp giao diện tối giản, hiện đại của **Tailwind CSS**. Hệ thống được thiết kế tối ưu phục vụ
hai nhóm đối tượng chính: Nhà quản lý (Admin) điều hành chiến lược và Nhân viên (User) vận hành trực tiếp tại quầy POS.

---

## 🚀 Tính Năng Nổi Bật & Phân Hệ Nghiệp Vụ

### 🔐 1. Cơ Chế Phân Quyền Dựa Trên Vai Trò (Role-Based Access Control - RBAC)

Hệ thống tích hợp chặt chẽ lớp bảo mật **Spring Security 6.x**, chia làm 2 phân quyền cốt lõi:

* **Quản Lý (`ROLE_ADMIN`):** Toàn quyền kiểm soát hệ thống, phê duyệt tài chính, cấu hình định lượng thực đơn, quản trị
  kho hàng, tài sản cố định và sao lưu dữ liệu hệ thống.
* **Nhân Viên (`ROLE_USER`):** Giới hạn quyền truy cập, chỉ thao tác các nghiệp vụ bán hàng tại bàn (POS), quản lý hóa
  đơn cục bộ và cập nhật trang cá nhân.

#### 📊 Ma trận phân quyền & Điều hướng luồng (Controller Matrix)

| Phân hệ chức năng         | Quản lý (ADMIN) | Nhân viên (USER) | Nghiệp vụ chi tiết                                                    | Controller xử lý      |
|:--------------------------|:---------------:|:----------------:|:----------------------------------------------------------------------|:----------------------|
| **Tổng quan (Dashboard)** |      ✅ Có       |       ✅ Có       | Thống kê số liệu nhanh, biểu đồ doanh thu theo ngày/tháng.            | `HomeController`      |
| **Sơ đồ bàn & Gọi món**   |      ✅ Có       |       ✅ Có       | Quản lý trạng thái bàn, đặt bàn, chuyển/tách/gộp hóa đơn POS.         | `TablesController`    |
| **Hồ sơ cá nhân**         |      ✅ Có       |       ✅ Có       | Xem thông tin cá nhân, tự đổi mật khẩu, cập nhật ảnh đại diện.        | `ProfileController`   |
| **Quản lý Nhân sự**       |      ✅ Có       |     ❌ Không      | Quản lý thông tin nhân sự, lọc theo chức vụ, bảng lương thực tế.      | `EmployeeController`  |
| **Quản lý Thực đơn**      |      ✅ Có       |     ❌ Không      | Thêm món, phân loại, thay đổi giá, cấu hình định lượng nguyên liệu.   | `MenuController`      |
| **Quản lý Kho hàng**      |      ✅ Có       |     ❌ Không      | Nhập kho hàng hóa, kiểm kho nguyên vật liệu, kiểm kê tồn kho.         | `InventoryController` |
| **Quản lý Thiết bị**      |      ✅ Có       |     ❌ Không      | Theo dõi tài sản cố định, công cụ dụng cụ, tình trạng vận hành.       | `EquipmentController` |
| **Quản lý Marketing**     |      ✅ Có       |     ❌ Không      | Thiết lập chiến dịch khuyến mãi (Theo % hoặc số tiền cố định).        | `MarketingController` |
| **Quản lý Ngân sách**     |      ✅ Có       |     ❌ Không      | Lập phiếu chi, thống kê dòng tiền thu chi (Thu ngân + Chi phí ngoài). | `BudgetController`    |
| **Quản lý Dữ liệu**       |      ✅ Có       |     ❌ Không      | Phân hệ sao lưu (Backup) và phục hồi (Restore) an toàn hệ thống.      | `DataController`      |
| **Báo cáo thống kê**      |      ✅ Có       |     ❌ Không      | Xuất báo cáo doanh thu tài chính theo bộ lọc thời gian động.          | `ReportController`    |

---

### 🛒 2. Nghiệp Vụ Bán Hàng Trực Quầy (Point of Sale - POS)

* **Quản lý sơ đồ bàn động:** Hiển thị trực quan trạng thái bàn theo thời gian thực (Trống, Đang sử dụng, Đã đặt trước)
  thông qua mã màu chuyên nghiệp.
* **Xử lý hóa đơn linh hoạt:** Hỗ trợ gọi món, thêm/bớt số lượng, ghi chú món ăn trực tiếp cho bar/bếp. Người dùng có
  thể chuyển bàn hoặc gộp/tách hóa đơn tức thì theo yêu cầu của khách hàng.
* **Tính toán tự động chiết khấu:** Tích hợp bộ mã khuyến mãi (`KhuyenMai`), tự động áp dụng giảm giá trực tiếp vào hóa
  đơn theo cấu hình phần trăm (%) hoặc số tiền giảm (VNĐ) khi thanh toán.

---

### 🛡️ 3. Kiến Trúc An Toàn Dữ Liệu & Quy Chuẩn Lập Trình

* **Cơ chế Xóa Mềm (Soft Delete):** 100% bảng danh mục lõi trong cơ sở dữ liệu được tích hợp cột `flag_delete` (0: Hoạt
  động, 1: Đã xóa). Khi thực hiện thao tác xóa từ giao diện, hệ thống sẽ cập nhật trạng thái thành 1 thay vì xóa vật lý
  khỏi ổ đĩa, giúp bảo toàn tính toàn vẹn dữ liệu lịch sử và phục vụ việc tra cứu hóa đơn/phiếu nhập kho cũ.
* **Form Validation nghiêm ngặt:** Sử dụng `jakarta.validation` kết hợp custom validator (`PhoneNumberValidator`) kiểm
  tra định dạng dữ liệu đầu vào chặt chẽ từ phía Backend (giới hạn ký tự theo độ rộng cột Database, lọc ký tự đặc biệt ở
  ô nhập Lương, Số điện thoại).
* **Java Code Convention:** Mã nguồn tuân thủ tuyệt đối quy chuẩn lập trình Java quốc tế: Nhóm import phân tầng (Java
  core -> Thư viện thứ ba -> Code dự án), Header Javadoc đầy đủ cho class, phương thức có đầy đủ mô tả `@param` và
  `@return`.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

* **Backend Framework:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security 6.x
* **Database Client:** PostgreSQL (Quản lý ràng buộc chặt chẽ qua cơ chế `ON DELETE SET NULL` và `ON DELETE CASCADE`).
* **Frontend Engine:** Thymeleaf UI Template, Thymeleaf Extras Spring Security 6 (Phân tách hiển thị UI động dựa trên
  Authentication).
* **Styling & Icons:** Tailwind CSS (Tối ưu thiết kế Responsive đa nền tảng PC/Mobile), Lucide Icons vỡ mảnh cao cấp.
* **Build Tool & Utilities:** Maven, Lombok (Giảm thiểu Boilerplate code).

---

## 📂 Cấu Trúc Thư Mục Dự Án (Project Structure)

Dự án được phân tách module rõ ràng theo đúng kiến trúc Layered Architecture (Kiến trúc phân tầng):

```text
├── Dockerfile                           # Dockerfile cấu hình đóng gói môi trường chạy ứng dụng
├── pom.xml                              # Khai báo các thư viện phụ thuộc (Dependencies Maven)
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── quanlycafe
│   │   │           └── cafe_management
│   │   │               ├── CafeManagementApplication.java  # File kích hoạt khởi chạy Spring Boot
│   │   │               ├── controller/         # Tầng tiếp nhận Request và điều hướng giao diện (Web Controllers)
│   │   │               ├── dto/                # Data Transfer Objects (Hứng dữ liệu Form, áp Validation)
│   │   │               ├── entity/             # Thực thể ORM ánh xạ trực tiếp sang bảng PostgreSQL qua JPA
│   │   │               ├── repository/         # Tầng giao tiếp Database (Kế thừa Spring Data JpaRepository)
│   │   │               ├── security/           # Cấu hình WebSecurityConfig, mã hóa mật khẩu BCrypt
│   │   │               ├── service/            # Tầng xử lý logic nghiệp vụ trung tâm (Business Logic Layer)
│   │   │               └── validation/         # Lớp xử lý kiểm tra định dạng và bắt lỗi dữ liệu tùy chỉnh
│   │   └── resources
│   │       ├── application.properties          # File cấu hình cấu hình môi trường, cổng Port, kết nối CSDL
│   │       ├── static/                         # Thư mục chứa các tài nguyên tĩnh
│   │       │   ├── css/style.css               # Tệp tin CSS cấu hình giao diện
│   │       │   ├── images/                     # Ảnh hệ thống, logo thương hiệu và thư mục upload avatar
│   │       │   └── js/                         # JavaScript xử lý DOM/AJAX được chia module theo từng trang
│   │       └── templates/                      # Thư mục chứa tệp tin giao diện Thymeleaf HTML
│   │           ├── fragments/                  # Các cấu phần dùng chung (sidebar.html, header.html, hoadon.html)
│   │           └── *.html                      # Trang tính năng độc lập (home, employees, tables, menu, inventory,...)