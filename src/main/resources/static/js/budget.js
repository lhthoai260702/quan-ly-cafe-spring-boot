/*<![CDATA[*/
let hasExpenseError = /*[[${hasExpenseError != null ? hasExpenseError : false}]]*/ false;
if (hasExpenseError) {
    document.getElementById('addExpenseModal').classList.remove('hidden');
}
/*]]>*/

/**
 * Hàm định dạng tiền tệ dùng chung cho cả form Thêm và Sửa
 * * @param {HTMLElement} input - Thẻ input hiển thị số tiền
 * @param {string} hiddenInputId - ID của thẻ input ẩn chứa số nguyên
 */
function formatCurrency(input, hiddenInputId) {
    let value = input.value.replace(/\D/g, ''); // Bỏ mọi ký tự không phải số
    document.getElementById(hiddenInputId).value = value;

    if (value !== '') {
        input.value = Number(value).toLocaleString('en-US');
    } else {
        input.value = '';
    }
}

/**
 * Mở hộp thoại (Modal) xem chi tiết
 * * @param {string} dateKey - Mã nhóm ngày
 * @param {string} dateStr - Chuỗi ngày hiển thị
 */
function openDetailModal(dateKey, dateStr) {
    document.getElementById('modal-date-title').textContent = dateStr;

    let hiddenData = document.getElementById('detail-data-' + dateKey);
    let detailContent = document.getElementById('modal-detail-content');

    if (hiddenData) {
        detailContent.innerHTML = hiddenData.innerHTML;
    } else {
        detailContent.innerHTML = '<p class="text-center text-gray-500 italic py-4">Không có dữ liệu chi tiết.</p>';
    }

    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
    document.getElementById('detailModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại (Modal) sửa thông tin chi tiêu
 * * @param {HTMLElement} button - Nút được click chứa các thuộc tính data-*
 */
function openEditModal(button) {
    document.getElementById('editExpenseId').value = button.getAttribute('data-id');
    document.getElementById('editTenKhoanChi').value = button.getAttribute('data-name');
    document.getElementById('editNgayChi').value = button.getAttribute('data-date');

    // Format tiền cho form Edit
    let amount = button.getAttribute('data-amount');
    document.getElementById('realSoTienEdit').value = amount;
    document.getElementById('displaySoTienEdit').value = Number(amount).toLocaleString('en-US');

    document.getElementById('editExpenseModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại (Modal) xác nhận xóa chi tiêu
 * * @param {number|string} id - Mã khoản chi cần xóa
 */
function openDeleteModal(id) {
    document.getElementById('deleteExpenseId').value = id;
    document.getElementById('deleteExpenseModal').classList.remove('hidden');
}

// Cấu hình CSS thanh cuộn (Scrollbar)
document.head.insertAdjacentHTML('beforeend', `<style>
    .custom-scrollbar::-webkit-scrollbar { width: 4px; }
    .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
</style>`);

// Khởi tạo các sự kiện khi trang đã tải xong
document.addEventListener('DOMContentLoaded', function () {
    // Tự động format tiền nếu có lỗi Backend trả về
    let realSoTienAdd = document.getElementById('realSoTienAdd');
    let displaySoTienAdd = document.getElementById('displaySoTienAdd');

    if (realSoTienAdd && displaySoTienAdd && realSoTienAdd.value) {
        displaySoTienAdd.value = Number(realSoTienAdd.value).toLocaleString('en-US');
    }

    // Client Validation Form UX
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll('input:not([type="hidden"]), select, textarea');

            inputs.forEach(function (input) {
                let existingError = input.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) {
                    existingError.remove();
                }

                input.classList.remove('border-rose-500', 'border-blue-500');

                if (!input.validity.valid) {
                    isValid = false;
                    input.classList.remove('border-gray-300');
                    input.classList.add(form.action.includes('edit') ? 'border-blue-500' : 'border-rose-500');

                    let errorText = input.getAttribute('title') || 'Vui lòng kiểm tra lại.';
                    if (input.validity.valueMissing) {
                        errorText = 'Trường này không được để trống.';
                    }

                    let errorClass = form.action.includes('edit') ? 'text-blue-500' : 'text-rose-500';
                    let errorHtml = `<span class="error-msg text-[10px] ${errorClass} font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${errorText}</span>`;
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

        // Tự động xóa lỗi khi người dùng bắt đầu nhập lại
        form.querySelectorAll('input:not([type="hidden"]), select, textarea').forEach(function (input) {
            input.addEventListener('input', function () {
                let existingError = input.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) {
                    existingError.remove();
                }
                input.classList.remove('border-rose-500', 'border-blue-500');
                input.classList.add('border-gray-300');
            });
        });
    });
});