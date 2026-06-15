/*<![CDATA[*/

/** * 1. XỬ LÝ LỖI TỪ SERVER (Giữ form mở, Phục hồi dữ liệu & Highlight lỗi)
 */
let hasAddError = /*[[${hasAddError != null ? hasAddError : false}]]*/ false;
let hasEditError = /*[[${hasEditError != null ? hasEditError : false}]]*/ false;
let serverErrorMsg = /*[[${errorMsg != null ? errorMsg : ''}]]*/ '';

// Hứng dữ liệu form đang gõ dở từ Controller truyền xuống
let addFormData = /*[[${addForm}]]*/ null;
let editFormData = /*[[${editForm}]]*/ null;

document.addEventListener('DOMContentLoaded', function() {
    if (hasAddError || hasEditError) {
        const modal = document.getElementById('universalModal');
        const title = document.getElementById('univ_title');
        const btnText = document.getElementById('univ_btn_text');
        const accountSection = document.getElementById('univ_account_section');
        const form = document.getElementById('universalForm');

        modal.classList.remove('hidden');

        // LOGIC PHỤC HỒI LẠI DỮ LIỆU ĐÃ GÕ
        let formData = hasAddError ? addFormData : editFormData;
        if (formData) {
            if (hasEditError) document.getElementById('univ_maNhanVien').value = formData.maNhanVien || '';
            document.getElementById('univ_hoTen').value = formData.hoTen || '';

            let phoneInput = document.getElementById('univ_soDienThoai');
            phoneInput.value = formData.soDienThoai || '';
            formatPhone(phoneInput); // Tự động định dạng lại số điện thoại

            document.getElementById('univ_diaChi').value = formData.diaChi || '';
            document.getElementById('univ_tenDangNhap').value = formData.tenDangNhap || '';

            if (formData.maChucVu) {
                document.getElementById('univ_maChucVu').value = formData.maChucVu;
            }

            if (formData.luong) {
                document.getElementById('univ_luong_real').value = formData.luong;
                document.getElementById('univ_luong_display').value = parseInt(formData.luong, 10).toLocaleString('en-US');
            }
        }

        // Cấu hình lại giao diện Form cho đúng Action
        if (hasAddError) {
            title.innerHTML = '<i data-lucide="user-plus" class="w-5 h-5 text-[#553722]"></i> Tuyển nhân sự mới';
            btnText.innerText = 'Lưu';
            accountSection.classList.remove('hidden');
            form.action = form.getAttribute('data-action-add');
        } else if (hasEditError) {
            title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5 text-[#553722]"></i> Cập nhật hồ sơ';
            btnText.innerText = 'Lưu';
            accountSection.classList.remove('hidden');
            accountSection.querySelector('h4').innerText = 'Cập nhật Tài khoản (Bỏ trống mật khẩu nếu không đổi)';
            form.action = form.getAttribute('data-action-edit');

            const fPassword = document.getElementById('univ_matKhau');
            fPassword.disabled = false;
            fPassword.required = false;
            fPassword.placeholder = 'Nhập mật khẩu mới...';
        }

        // 🔴 LOGIC KIỂM TRA LỖI TỪ BACKEND VÀ BÔI ĐỎ CHÍNH XÁC Ô LỖI
        if (serverErrorMsg) {
            let lowerMsg = serverErrorMsg.toLowerCase();

            // Lỗi liên quan đến Username
            if (lowerMsg.includes('tên đăng nhập')) {
                const usernameInput = document.getElementById('univ_tenDangNhap');
                if (usernameInput) {
                    usernameInput.classList.remove('border-transparent', 'border-[#e2e3e1]');
                    usernameInput.classList.add('border-rose-500', 'bg-rose-50');

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> ${serverErrorMsg}</span>`;
                    usernameInput.insertAdjacentHTML('afterend', errorHtml);
                }
            }
            // Lỗi liên quan đến Mật khẩu
            else if (lowerMsg.includes('mật khẩu')) {
                const passwordInput = document.getElementById('univ_matKhau');
                if (passwordInput) {
                    passwordInput.classList.remove('border-transparent', 'border-[#e2e3e1]');
                    passwordInput.classList.add('border-rose-500', 'bg-rose-50');

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> ${serverErrorMsg}</span>`;
                    passwordInput.insertAdjacentHTML('afterend', errorHtml);
                }
            }
        }
    }
});

/** * 2. CÔNG CỤ FORMAT (Tiền tệ, SĐT, Text)
 */
function formatPhone(input) {
    let val = input.value.replace(/\D/g, '');
    if (val.length > 10) val = val.substring(0, 10);
    if (val.length > 6) input.value = val.replace(/(\d{3})(\d{3})(\d{1,4})/, '$1 $2 $3');
    else if (val.length > 3) input.value = val.replace(/(\d{3})(\d{1,3})/, '$1 $2');
    else input.value = val;
}

function formatCurrency(inputElement, hiddenInputId) {
    let value = inputElement.value.replace(/\D/g, '');

    // Ép chặt giới hạn 9 chữ số bằng Javascript
    if (value.length > 9) {
        value = value.substring(0, 9);
    }

    if (value === '') {
        inputElement.value = '';
        document.getElementById(hiddenInputId).value = '0';
        return;
    }
    inputElement.value = parseInt(value, 10).toLocaleString('en-US');
    document.getElementById(hiddenInputId).value = value;
}

function toggleText(element) {
    if (element.classList.contains('truncate')) {
        element.classList.remove('truncate');
        element.classList.add('whitespace-normal', 'break-all');
    } else {
        element.classList.add('truncate');
        element.classList.remove('whitespace-normal', 'break-all');
    }
}

/** * 3. LOGIC MODAL & VALIDATION
 */
function openUniversalModal(action, id, name, phone, address, role, salary, username) {
    const form = document.getElementById('universalForm');
    const modal = document.getElementById('universalModal');
    const title = document.getElementById('univ_title');
    const btnText = document.getElementById('univ_btn_text');

    const fId = document.getElementById('univ_maNhanVien');
    const fName = document.getElementById('univ_hoTen');
    const fPhone = document.getElementById('univ_soDienThoai');
    const fAddress = document.getElementById('univ_diaChi');
    const fRole = document.getElementById('univ_maChucVu');
    const fSalaryDisp = document.getElementById('univ_luong_display');
    const fSalaryReal = document.getElementById('univ_luong_real');

    const accountSection = document.getElementById('univ_account_section');
    const fUsername = document.getElementById('univ_tenDangNhap');
    const fPassword = document.getElementById('univ_matKhau');

    form.querySelectorAll('.error-msg').forEach(e => e.remove());
    form.querySelectorAll('.border-rose-500').forEach(e => {
        e.classList.remove('border-rose-500', 'bg-rose-50');
        e.classList.add('border-transparent');
    });

    if (action === 'ADD') {
        title.innerHTML = '<i data-lucide="user-plus" class="w-5 h-5 text-[#553722]"></i> Tuyển nhân sự mới';
        btnText.innerText = 'Lưu';
        form.action = form.getAttribute('data-action-add');

        fId.value = '';
        fId.disabled = true;
        fName.value = '';
        fPhone.value = '';
        fAddress.value = '';
        fRole.value = '';
        fSalaryDisp.value = '';
        fSalaryReal.value = '0';

        accountSection.querySelector('h4').innerText = 'Thông tin tài khoản & Chức vụ';

        fUsername.disabled = false;
        fUsername.required = true;
        fUsername.value = '';
        fPassword.disabled = false;
        fPassword.required = true;
        fPassword.value = '';
        fPassword.placeholder = '';

    } else if (action === 'EDIT') {
        title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5 text-[#553722]"></i> Cập nhật hồ sơ';
        btnText.innerText = 'Lưu';
        form.action = form.getAttribute('data-action-edit');

        fId.value = id;
        fId.disabled = false;
        fName.value = name;

        fPhone.value = phone;
        formatPhone(fPhone);

        fAddress.value = address;

        if (role) {
            let roleVal = role.toString().trim();
            let optionExists = Array.from(fRole.options).some(opt => opt.value === roleVal);
            fRole.value = optionExists ? roleVal : '';
        }

        if (salary && salary !== 'null') {
            fSalaryReal.value = salary;
            fSalaryDisp.value = parseInt(salary, 10).toLocaleString('en-US');
        } else {
            fSalaryReal.value = '0';
            fSalaryDisp.value = '0';
        }

        accountSection.classList.remove('hidden');
        accountSection.querySelector('h4').innerText = 'Cập nhật Tài khoản (Bỏ trống mật khẩu nếu không đổi)';

        fUsername.disabled = false;
        fUsername.required = true;
        fUsername.value = username ? username : '';

        fPassword.disabled = false;
        fPassword.required = false;
        fPassword.value = '';
        fPassword.placeholder = 'Nhập mật khẩu mới...';
    }

    if (typeof lucide !== 'undefined') lucide.createIcons();
    modal.classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_maNhanVien').value = id;
    document.getElementById('delete_empName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

function initFormValidation() {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        const newForm = form.cloneNode(true);
        form.parentNode.replaceChild(newForm, form);

        newForm.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = newForm.querySelectorAll('input[required]:not([disabled]), select[required]:not([disabled])');

            newForm.querySelectorAll('.error-msg').forEach(msg => msg.remove());

            inputs.forEach(function (input) {
                input.classList.remove('border-rose-500', 'bg-rose-50');
                let val = input.value ? input.value.trim() : '';

                // 1. Bắt lỗi bỏ trống
                if (!val) {
                    isValid = false;
                    input.classList.remove('border-transparent', 'border-[#e2e3e1]', 'focus:border-[#553722]');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> Trường này không được bỏ trống.</span>`;
                    input.insertAdjacentHTML('afterend', errorHtml);
                }
                // 🔴 2. BẮT LỖI LƯƠNG BẰNG 0
                else if (input.id === 'univ_luong_display' && val === '0') {
                    isValid = false;
                    input.classList.remove('border-transparent', 'border-[#e2e3e1]', 'focus:border-[#553722]');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> Mức lương không được bằng 0.</span>`;
                    input.insertAdjacentHTML('afterend', errorHtml);
                }
                // 3. Bắt lỗi nhập thiếu ký tự (Ví dụ Mật khẩu ít hơn 6 ký tự)
                else if (input.hasAttribute('minlength') && val.length < parseInt(input.getAttribute('minlength'))) {
                    isValid = false;
                    input.classList.remove('border-transparent', 'border-[#e2e3e1]', 'focus:border-[#553722]');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let min = input.getAttribute('minlength');
                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> Vui lòng nhập tối thiểu ${min} ký tự.</span>`;
                    input.insertAdjacentHTML('afterend', errorHtml);
                }
            });

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') lucide.createIcons();
            } else {
                let phoneInput = newForm.querySelector('input[name="soDienThoai"]');
                if (phoneInput) {
                    phoneInput.value = phoneInput.value.replace(/\D/g, '');
                }
            }
        });

        newForm.addEventListener('input', function(e) {
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'SELECT') {
                e.target.classList.remove('border-rose-500', 'bg-rose-50');
                e.target.classList.add('border-transparent');
                let wrapper = e.target.parentElement;
                wrapper.querySelectorAll('.error-msg').forEach(msg => msg.remove());
            }
        });
    });
}

/**
 * 4. LOGIC AJAX NAVIGATION (Chuyển trang không giật lác)
 */
function initAjaxNavigation() {
    const ajaxLinks = document.querySelectorAll('.ajax-link');
    ajaxLinks.forEach(function (link) {
        const newLink = link.cloneNode(true);
        link.parentNode.replaceChild(newLink, link);

        newLink.addEventListener('click', function(e) {
            e.preventDefault();
            fetchPage(this.getAttribute('href'));
        });
    });

    const ajaxForm = document.querySelector('.ajax-form');
    if (ajaxForm) {
        const newAjaxForm = ajaxForm.cloneNode(true);
        ajaxForm.parentNode.replaceChild(newAjaxForm, ajaxForm);

        newAjaxForm.addEventListener('submit', function(e) {
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

function fetchPage(url) {
    const mainContent = document.querySelector('main');
    mainContent.style.transition = 'opacity 0.2s ease';
    mainContent.style.opacity = '0.4';

    fetch(url)
        .then(response => response.text())
        .then(html => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const newMain = doc.querySelector('main');

        if (newMain) {
            mainContent.innerHTML = newMain.innerHTML;
            mainContent.style.opacity = '1';
            window.history.pushState({path: url}, '', url);

            initAjaxNavigation();
            initFormValidation();

            if (typeof lucide !== 'undefined') {
                lucide.createIcons();
            }
        }
    })
        .catch(error => {
        console.error('Lỗi khi tải dữ liệu:', error);
        mainContent.style.opacity = '1';
    });
}

window.addEventListener('popstate', function() {
    fetchPage(window.location.href);
});

// Kích hoạt tất cả khi tải trang lần đầu
document.addEventListener('DOMContentLoaded', function () {
    initFormValidation();
    initAjaxNavigation();
});

/*]]>*/