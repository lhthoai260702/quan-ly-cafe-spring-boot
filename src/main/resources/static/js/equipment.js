/*<![CDATA[*/
let hasAddError = /*[[${hasAddError != null ? hasAddError : false}]]*/ false;
let hasEditError = /*[[${hasEditError != null ? hasEditError : false}]]*/ false;

if (hasAddError) {
    document.getElementById('addModal').classList.remove('hidden');
}

if (hasEditError) {
    document.getElementById('editModal').classList.remove('hidden');
}
/*]]>*/

/**
 * Định dạng tiền tệ (loại bỏ chữ, thêm dấu phẩy phân cách hàng nghìn)
 * @param {HTMLElement} input - Thẻ input hiển thị số tiền
 * @param {string} hiddenFieldId - ID của thẻ input ẩn chứa số nguyên để gửi về server
 */
function formatCurrency(input, hiddenFieldId) {
    // Lấy giá trị, dùng regex loại bỏ tất cả ký tự không phải số (\D)
    let rawValue = input.value.replace(/\D/g, '');

    // GIỚI HẠN: Chỉ cho phép tối đa 10 chữ số (Tương ứng DECIMAL(12,2))
    if (rawValue.length > 10) {
        rawValue = rawValue.substring(0, 10);
    }

    // Cập nhật giá trị số thực vào trường hidden để gửi lên server
    document.getElementById(hiddenFieldId).value = rawValue;

    // Format lại hiển thị với dấu phẩy
    if (rawValue !== '') {
        input.value = parseInt(rawValue, 10).toLocaleString('en-US');
    } else {
        input.value = '';
    }
}

/**
 * Mở hộp thoại (Modal) thêm thiết bị mới
 */
function openAddModal() {
    document.getElementById('addModal').classList.remove('hidden');
    const dateInput = document.getElementById('add_ngayMua');

    // Tự động gán ngày hôm nay nếu chưa có giá trị
    if (!dateInput.value) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.value = today;
    }
}

/**
 * Mở hộp thoại (Modal) chỉnh sửa thông tin thiết bị
 * @param {string|number} id - Mã thiết bị
 * @param {string} name - Tên thiết bị
 * @param {string} status - Tình trạng thiết bị
 * @param {string|number} price - Đơn giá mua
 * @param {string} date - Ngày mua
 * @param {string} note - Ghi chú
 */
function openEditModal(id, name, status, price, date, note) {
    document.getElementById('edit_maThietBi').value = id;
    document.getElementById('edit_tenThietBi').value = name;
    document.getElementById('edit_tinhTrang').value = status;

    // Xử lý nạp giá trị hiển thị cho trường Giá tiền
    const priceHidden = document.getElementById('edit_donGiaMua');
    const priceDisplay = document.getElementById('edit_donGia_display');

    if (price && price !== 'null' && price !== '') {
        priceHidden.value = price;
        priceDisplay.value = parseInt(price, 10).toLocaleString('en-US'); // Hiển thị có phẩy
    } else {
        priceHidden.value = '';
        priceDisplay.value = '';
    }

    document.getElementById('edit_ngayMua').value = (date && date !== 'null') ? date : '';
    document.getElementById('edit_ghiChu').value = (note && note !== 'null') ? note : '';
    document.getElementById('editModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại (Modal) xác nhận xóa thiết bị
 * @param {string|number} id - Mã thiết bị
 * @param {string} name - Tên thiết bị
 */
function openDeleteModal(id, name) {
    document.getElementById('delete_maThietBi').value = id;
    document.getElementById('delete_tbName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

/**
 * Khởi tạo các sự kiện khi trang đã tải xong (Validation Form)
 */
document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            // Bổ sung lọc cả thẻ hidden để kiểm tra validate form
            const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');

            inputs.forEach(function (input) {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) {
                    existingError.remove();
                }

                input.classList.remove('border-rose-500', 'bg-rose-50');

                // Do chúng ta dùng input dummy (text) nên cần phải check value của input text hiển thị
                if (!input.value || input.value.trim() === '') {
                    isValid = false;

                    // Tránh viền đỏ vào input hidden
                    if (input.type !== 'hidden') {
                        input.classList.remove('border-[#e2e3e1]');
                        input.classList.add('border-rose-500', 'bg-rose-50');
                        let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> Trường này không được để trống.</span>`;
                        input.insertAdjacentHTML('afterend', errorHtml);
                    }
                }
            });

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') {
                    lucide.createIcons();
                }
            }
        });

        // Xóa thông báo lỗi khi người dùng bắt đầu tương tác lại
        form.querySelectorAll('input:not([type="hidden"]), select, textarea').forEach(function (input) {
            input.addEventListener('input', function () {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) {
                    existingError.remove();
                }

                input.classList.remove('border-rose-500', 'bg-rose-50');
                input.classList.add('border-[#e2e3e1]');
            });
        });
    });
});