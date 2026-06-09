/**
 * Đọc file ảnh từ thẻ input và hiển thị trực tiếp lên giao diện (Preview)
 * @param {Event} event - Sự kiện change khi người dùng chọn file
 */
function previewAvatar(event) {
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('avatarPreview').src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

/**
 * Khởi tạo bắt lỗi Form (Client Validation) khi trang đã tải xong
 */
document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll('input, select, textarea');

            inputs.forEach(function (input) {
                // Bỏ qua kiểm tra lỗi đối với các thẻ đang bị khóa (disabled)
                if (input.disabled) {
                    return;
                }

                let existingError = input.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) {
                    existingError.remove();
                }

                input.classList.remove('border-rose-500', 'bg-rose-50');

                if (!input.validity.valid) {
                    isValid = false;

                    // Đổi màu viền và nền sang đỏ báo lỗi
                    input.classList.remove('border-[#e2e3e1]', 'bg-white');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let errorText = input.getAttribute('title') || 'Vui lòng kiểm tra lại thông tin.';

                    if (input.validity.valueMissing) {
                        errorText = 'Trường này không được để trống.';
                    } else if (input.validity.tooShort || input.validity.patternMismatch) {
                        errorText = input.getAttribute('title') || 'Giá trị không hợp lệ.';
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
            }
        });

        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại dữ liệu
        form.querySelectorAll('input, select, textarea').forEach(function (input) {
            input.addEventListener('input', function () {
                let existingError = input.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) {
                    existingError.remove();
                }

                input.classList.remove('border-rose-500', 'bg-rose-50');
                input.classList.add('border-[#e2e3e1]', 'bg-white');
            });
        });
    });
});