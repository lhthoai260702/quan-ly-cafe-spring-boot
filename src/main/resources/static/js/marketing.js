/*<![CDATA[*/

/**
 * 1. FORMAT CÔNG CỤ (Tiền tệ, Mức giảm)
 */
function formatMoney(valueStr) {
    let clean = valueStr.replace(/\D/g, '');
    if (!clean) return '';
    return parseInt(clean, 10).toLocaleString('en-US');
}

function handleInputDiscount(displayEl, hiddenEl, typeEl) {
    let rawVal = displayEl.value.replace(/\D/g, '').replace(/^0+/, '');

    if (typeEl.value === 'Phần trăm') {
        if (rawVal !== '' && parseInt(rawVal, 10) > 100) rawVal = '100';
        displayEl.value = rawVal;
        hiddenEl.value = rawVal;
    } else {
        displayEl.value = formatMoney(rawVal);
        hiddenEl.value = rawVal;
    }
}

function updatePromoValidation(typeEl, displayEl, hiddenEl) {
    let currentVal = hiddenEl.value || displayEl.value.replace(/\D/g, '');
    currentVal = currentVal.replace(/^0+/, '');

    if (typeEl.value === 'Phần trăm') {
        displayEl.maxLength = 3;
        displayEl.setAttribute('title', 'Mức giảm phần trăm phải từ 1 đến 100');
        if (currentVal && parseInt(currentVal, 10) > 100) currentVal = '100';
        displayEl.value = currentVal;
        hiddenEl.value = currentVal;
    } else {
        displayEl.removeAttribute('maxlength');
        displayEl.setAttribute('title', 'Mức giảm tiền mặt phải lớn hơn 0');
        displayEl.value = formatMoney(currentVal);
        hiddenEl.value = currentVal;
    }

    let existingError = displayEl.parentElement.querySelector('.error-msg, .server-error-msg');
    if (existingError) existingError.remove();

    displayEl.classList.remove('border-rose-500', 'bg-rose-50');
    displayEl.classList.add('border-[#e2e3e1]');
}

/**
 * 2. LOGIC MODAL GỘP (Thêm/Sửa)
 */
function openUniversalModal(action, id, name, start, end, type, val, desc) {
    const form = document.getElementById('universalForm');
    const modal = document.getElementById('universalModal');
    const title = document.getElementById('univ_title');
    const btnText = document.getElementById('univ_btn_text');

    const fId = document.getElementById('univ_id');
    const fName = document.getElementById('univ_name');
    const fStart = document.getElementById('univ_start');
    const fEnd = document.getElementById('univ_end');
    const fType = document.getElementById('univ_type');
    const fValDisplay = document.getElementById('univ_val_display');
    const fValHidden = document.getElementById('univ_val_hidden');
    const fDesc = document.getElementById('univ_desc');

    // Xóa mọi lỗi viền đỏ cũ
    form.querySelectorAll('.error-msg, .server-error-msg').forEach(e => e.remove());
    form.querySelectorAll('.border-rose-500').forEach(e => {
        e.classList.remove('border-rose-500', 'bg-rose-50');
        e.classList.add('border-[#e2e3e1]');
    });

    if (action === 'ADD') {
        title.innerHTML = '<i data-lucide="ticket" class="w-5 h-5 text-[#553722] mr-2"></i> Tạo Khuyến Mãi Mới';
        btnText.innerText = 'Lưu chương trình';
        form.action = form.getAttribute('data-action-add');

        fId.value = '';
        fId.disabled = true;

        fName.value = '';

        // Tự động gán ngày hôm nay cho trường bắt đầu
        const today = new Date();
        today.setMinutes(today.getMinutes() - today.getTimezoneOffset());
        fStart.value = today.toISOString().split('T')[0];

        fEnd.value = '';
        fEnd.min = ''; // Reset chặn ngày cũ
        fType.value = 'Phần trăm';
        fValHidden.value = '';
        fValDisplay.value = '';
        fDesc.value = '';

    } else if (action === 'EDIT') {
        title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5 text-[#553722] mr-2"></i> Cập nhật Khuyến Mãi';
        btnText.innerText = 'Lưu thay đổi';
        form.action = form.getAttribute('data-action-edit');

        fId.value = id;
        fId.disabled = false;

        fName.value = name;
        fStart.value = start;
        fEnd.value = end;
        fType.value = type;
        fValHidden.value = val;
        fDesc.value = (desc && desc !== 'null') ? desc : '';

        // Cập nhật Min ngày kết thúc
        if (start) {
            let d = new Date(start);
            d.setDate(d.getDate() + 1);
            fEnd.min = d.toISOString().split('T')[0];
        }
    }

    // Gọi hàm render để định dạng số/tỷ lệ hiển thị đúng format ngay khi mở
    updatePromoValidation(fType, fValDisplay, fValHidden);

    if (typeof lucide !== 'undefined') lucide.createIcons();
    modal.classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_id').value = id;
    document.getElementById('delete_name').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

/**
 * 3. KHỞI TẠO CÁC SỰ KIỆN (VALIDATION TRỰC TIẾP)
 */
document.addEventListener('DOMContentLoaded', function () {

    // Xử lý giữ form nếu Backend trả về lỗi
    let hasAddError = /*[[${hasAddError != null ? hasAddError : false}]]*/ false;
    let hasEditError = /*[[${hasEditError != null ? hasEditError : false}]]*/ false;
    if (hasAddError) openUniversalModal('ADD');
    if (hasEditError) openUniversalModal('EDIT');

    const fType = document.getElementById('univ_type');
    const fValDisplay = document.getElementById('univ_val_display');
    const fValHidden = document.getElementById('univ_val_hidden');
    const fStart = document.getElementById('univ_start');
    const fEnd = document.getElementById('univ_end');

    // Gắn sự kiện chặn ngày kết thúc
    if (fStart && fEnd) {
        fStart.addEventListener('change', function () {
            if (this.value) {
                let d = new Date(this.value);
                d.setDate(d.getDate() + 1);
                fEnd.min = d.toISOString().split('T')[0];

                // Nếu đang có ngày kết thúc lỗi thì tự động xóa rỗng
                if (fEnd.value && new Date(fEnd.value) <= new Date(this.value)) {
                    fEnd.value = '';
                }
            }
        });
    }

    // Sự kiện gõ mức giảm (Phần trăm / VND)
    if (fType && fValDisplay && fValHidden) {
        fType.addEventListener('change', function () {
            updatePromoValidation(fType, fValDisplay, fValHidden);
        });
        fValDisplay.addEventListener('input', function () {
            handleInputDiscount(fValDisplay, fValHidden, fType);
        });
    }

    const forms = document.querySelectorAll('.custom-validate-form');
    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll('input[required]:not([disabled]), select[required]:not([disabled])');

            inputs.forEach(function (input) {
                let existingError = input.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) existingError.remove();
                input.classList.remove('border-rose-500', 'bg-rose-50');

                if (!input.value.trim()) {
                    isValid = false;
                    input.classList.remove('border-[#e2e3e1]');
                    input.classList.add('border-rose-500', 'bg-rose-50');

                    let errorText = input.getAttribute('title') || 'Trường này không được để trống.';
                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> ${errorText}</span>`;
                    input.insertAdjacentHTML('afterend', errorHtml);
                }
            });

            // Kiểm tra Logic Ngày kết thúc > Ngày bắt đầu
            if (fStart && fEnd && fStart.value && fEnd.value) {
                let startDate = new Date(fStart.value);
                let endDate = new Date(fEnd.value);

                if (endDate <= startDate) {
                    isValid = false;
                    fEnd.classList.remove('border-[#e2e3e1]');
                    fEnd.classList.add('border-rose-500', 'bg-rose-50');

                    let existingError = fEnd.parentElement.querySelector('.error-msg, .server-error-msg');
                    if (existingError) existingError.remove();

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> Ngày kết thúc phải lớn hơn ngày bắt đầu.</span>`;
                    fEnd.insertAdjacentHTML('afterend', errorHtml);
                }
            }

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') lucide.createIcons();
            }
        });

        // Tự động gỡ viền đỏ khi người dùng nhập lại
        form.addEventListener('input', function (e) {
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'SELECT' || e.target.tagName === 'TEXTAREA') {
                let existingError = e.target.parentElement.querySelector('.error-msg, .server-error-msg');
                if (existingError) existingError.remove();

                e.target.classList.remove('border-rose-500', 'bg-rose-50');
                e.target.classList.add('border-[#e2e3e1]');
            }
        });
    });
});
/*]]>*/