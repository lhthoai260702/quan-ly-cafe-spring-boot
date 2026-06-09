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
 * Định dạng số điện thoại (tự động thêm khoảng trắng)
 * @param {HTMLElement} input - Thẻ input chứa số điện thoại
 */
function formatPhone(input) {
    let val = input.value.replace(/\D/g, '');

    if (val.length > 10) {
        val = val.substring(0, 10);
    }

    if (val.length > 6) {
        input.value = val.replace(/(\d{3})(\d{3})(\d{1,4})/, '$1 $2 $3');
    } else if (val.length > 3) {
        input.value = val.replace(/(\d{3})(\d{1,3})/, '$1 $2');
    } else {
        input.value = val;
    }
}

/**
 * Định dạng tiền lương (thêm dấu phẩy phân cách hàng nghìn)
 * @param {HTMLElement} input - Thẻ input chứa lương
 */
function formatSalary(input) {
    let val = input.value.replace(/\D/g, '');

    if (val !== '') {
        input.value = Number(val).toLocaleString('en-US');
    } else {
        input.value = '';
    }
}

/**
 * Chuyển đổi hiển thị văn bản (cắt ngắn hoặc hiển thị đầy đủ)
 * @param {HTMLElement} element - Thẻ chứa đoạn văn bản
 */
function toggleText(element) {
    if (element.classList.contains('truncate')) {
        element.classList.remove('truncate');
        element.classList.add('whitespace-normal', 'break-all');
    } else {
        element.classList.add('truncate');
        element.classList.remove('whitespace-normal', 'break-all');
    }
}

/**
 * Mở hộp thoại sửa thông tin nhân viên
 * @param {string|number} id - Mã nhân viên
 * @param {string} name - Họ tên nhân viên
 * @param {string} phone - Số điện thoại
 * @param {string} address - Địa chỉ
 * @param {string|number} roleId - Mã chức vụ
 * @param {string|number} salary - Mức lương
 */
function openEditModal(id, name, phone, address, roleId, salary) {
    document.getElementById('edit_maNhanVien').value = id;
    document.getElementById('edit_hoTen').value = name;

    let phoneInput = document.getElementById('edit_soDienThoai');
    phoneInput.value = phone;
    formatPhone(phoneInput);

    document.getElementById('edit_diaChi').value = address;

    let luongInput = document.getElementById('edit_luong');
    if (luongInput) {
        let parsedSalary = salary ? Math.round(Number(salary)) : '';
        luongInput.value = parsedSalary;
        formatSalary(luongInput);
    }

    let roleSelect = document.getElementById('edit_maChucVu');
    if (roleId) {
        let roleVal = roleId.toString().trim();
        let optionExists = Array.from(roleSelect.options).some(function(opt) {
            return opt.value === roleVal;
        });

        if (optionExists) {
            roleSelect.value = roleVal;
        } else {
            roleSelect.value = '';
        }
    }

    document.getElementById('editModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại xóa nhân viên
 * @param {string|number} id - Mã nhân viên
 * @param {string} name - Họ tên nhân viên
 */
function openDeleteModal(id, name) {
    document.getElementById('delete_maNhanVien').value = id;
    document.getElementById('delete_empName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

/**
 * Khởi tạo kiểm tra tính hợp lệ của Form (Client Validation)
 */
function initFormValidation() {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll('input, select');

            inputs.forEach(function (input) {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) {
                    existingError.remove();
                }
                input.classList.remove('border-rose-500', 'bg-rose-50');

                if (!input.validity.valid) {
                    isValid = false;
                    input.classList.remove('border-transparent');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let errorText = input.getAttribute('title') || 'Vui lòng nhập trường này hợp lệ.';
                    if (input.validity.valueMissing) {
                        errorText = 'Trường này không được để trống.';
                    }

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${errorText}</span>`;
                    input.insertAdjacentHTML('afterend', errorHtml);
                }
            });

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') {
                    lucide.createIcons();
                }
            } else {
                // Xóa định dạng trước khi gửi form về Backend
                let phoneInput = form.querySelector('input[name="soDienThoai"]');
                if (phoneInput) {
                    phoneInput.value = phoneInput.value.replace(/\D/g, '');
                }

                let salaryInput = form.querySelector('input[name="luong"]');
                if (salaryInput) {
                    salaryInput.value = salaryInput.value.replace(/\D/g, '');
                }
            }
        });

        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại
        form.querySelectorAll('input, select').forEach(function (input) {
            input.addEventListener('input', function () {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) {
                    existingError.remove();
                }
                input.classList.remove('border-rose-500', 'bg-rose-50');
                input.classList.add('border-transparent');
            });
        });
    });
}

/**
 * Khởi tạo tải trang qua AJAX không cần reload (PushState)
 */
function initAjaxNavigation() {
    // Lắng nghe click cho tất cả thẻ A có class ajax-link
    const ajaxLinks = document.querySelectorAll('.ajax-link');
    ajaxLinks.forEach(function (link) {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            fetchPage(this.getAttribute('href'));
        });
    });

    // Lắng nghe submit cho Form Tìm kiếm
    const ajaxForm = document.querySelector('.ajax-form');
    if (ajaxForm) {
        ajaxForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            const params = new URLSearchParams(formData);
            let url = this.getAttribute('action');

            if (params.toString()) {
                url += '?' + params.toString();
            }
            fetchPage(url);
        });
    }
}

/**
 * Tải nội dung trang qua AJAX và thay thế khung <main>
 * @param {string} url - Đường dẫn cần tải dữ liệu
 */
function fetchPage(url) {
    const mainContent = document.querySelector('main');

    // Hiệu ứng mờ đi một chút khi đang load dữ liệu
    mainContent.style.transition = 'opacity 0.2s ease';
    mainContent.style.opacity = '0.4';

    fetch(url)
        .then(function (response) {
        return response.text();
    })
        .then(function (html) {
        // Phân tích HTML trả về để chỉ lấy khung <main>
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const newMain = doc.querySelector('main');

        if (newMain) {
            mainContent.innerHTML = newMain.innerHTML;
            mainContent.style.opacity = '1'; // Phục hồi độ sáng

            // Cập nhật lại đường dẫn trên trình duyệt để khi F5 không bị mất dữ liệu lọc
            window.history.pushState({path: url}, '', url);

            // Kích hoạt lại Event Listener cho các Element mới xuất hiện
            initAjaxNavigation();
            initFormValidation();

            if (typeof lucide !== 'undefined') {
                lucide.createIcons();
            }
        }
    })
        .catch(function (error) {
        console.error('Lỗi khi tải dữ liệu:', error);
        mainContent.style.opacity = '1';
    });
}

// Đảm bảo nút Back/Forward trên trình duyệt cũng hoạt động hoàn hảo mà không chớp trang
window.addEventListener('popstate', function() {
    fetchPage(window.location.href);
});

// Kích hoạt tất cả khi tải trang lần đầu
document.addEventListener('DOMContentLoaded', function () {
    initFormValidation();
    initAjaxNavigation();
});