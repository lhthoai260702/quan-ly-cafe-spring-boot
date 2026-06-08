/*<![CDATA[*/
var hasAddError = /*[[${hasAddError != null ? hasAddError : false}]]*/ false;
var hasEditError = /*[[${hasEditError != null ? hasEditError : false}]]*/ false;

if (hasAddError) document.getElementById('addModal').classList.remove('hidden');
if (hasEditError) document.getElementById('editModal').classList.remove('hidden');
/*]]>*/

// Tiện ích Format Tiền Tệ
function formatMoney(valueStr) {
    let clean = valueStr.replace(/\D/g, '');
    if (!clean) return '';
    return parseInt(clean, 10).toLocaleString('en-US');
}

// Logic xử lý khi người dùng Gõ vào ô Mức giảm
function handleInputDiscount(displayEl, hiddenEl, typeEl) {
    let rawVal = displayEl.value.replace(/\D/g, '');

    if (typeEl.value === 'Phần trăm') {
        if (rawVal !== '' && parseInt(rawVal) > 100) rawVal = '100';
        if (rawVal !== '' && parseInt(rawVal) < 1) rawVal = '1';
        displayEl.value = rawVal;
        hiddenEl.value = rawVal;
    } else {
        displayEl.value = formatMoney(rawVal);
        hiddenEl.value = rawVal;
    }
}

function updatePromoValidation(typeEl, displayEl, hiddenEl) {
    let currentVal = hiddenEl.value || displayEl.value.replace(/\D/g, '');
    if (typeEl.value === 'Phần trăm') {
        displayEl.maxLength = 3;
        displayEl.setAttribute('title', 'Mức giảm phần trăm phải từ 1 đến 100');
        if (currentVal && parseInt(currentVal) > 100) currentVal = '100';
        displayEl.value = currentVal;
        hiddenEl.value = currentVal;
    } else {
        displayEl.removeAttribute('maxlength');
        displayEl.setAttribute('title', 'Mức giảm tiền mặt phải là số hợp lệ');
        displayEl.value = formatMoney(currentVal);
        hiddenEl.value = currentVal;
    }

    let existingError = displayEl.parentElement.querySelector(".error-msg, .server-error-msg");
    if (existingError) existingError.remove();
    displayEl.classList.remove("border-rose-500", "bg-rose-50");
    displayEl.classList.add("border-[#e2e3e1]");
}

function openEditModal(id, name, start, end, type, val, desc) {
    document.getElementById('edit_id').value = id;
    document.getElementById('edit_name').value = name;
    document.getElementById('edit_start').value = start;
    document.getElementById('edit_end').value = end;
    document.getElementById('edit_type').value = type;
    document.getElementById('edit_val_hidden').value = val;
    document.getElementById('edit_desc').value = desc && desc !== 'null' ? desc : '';

    updatePromoValidation(
        document.getElementById('edit_type'),
        document.getElementById('edit_val_display'),
        document.getElementById('edit_val_hidden')
    );
    document.getElementById('editModal').classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_id').value = id;
    document.getElementById('delete_name').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

// Khóa ngày kết thúc tự động dựa trên ngày bắt đầu
function bindDateValidation(startId, endId) {
    const startEl = document.getElementById(startId);
    const endEl = document.getElementById(endId);
    if (startEl && endEl) {
        startEl.addEventListener('change', function () {
            if (this.value) {
                let d = new Date(this.value);
                d.setDate(d.getDate() + 1); // Ngày kết thúc min = Ngày bắt đầu + 1
                endEl.min = d.toISOString().split('T')[0];

                // Nếu đang có ngày kết thúc lỗi thì tự động báo hoặc làm rỗng
                if (endEl.value && new Date(endEl.value) <= new Date(this.value)) {
                    endEl.value = '';
                }
            }
        });
    }
}

document.addEventListener("DOMContentLoaded", function () {

    // 1. Tự động Set ngày hôm nay cho Form Thêm
    const addStartDate = document.getElementById('add_start_date');
    if (addStartDate && !addStartDate.value) {
        const today = new Date();
        today.setMinutes(today.getMinutes() - today.getTimezoneOffset());
        addStartDate.value = today.toISOString().split('T')[0];
    }

    // Gắn sự kiện chặn ngày cho 2 form
    bindDateValidation('add_start_date', 'add_end_date');
    bindDateValidation('edit_start', 'edit_end');

    // 2. Gắn sự kiện cho Form Thêm
    const addType = document.getElementById('add_type');
    const addValDisplay = document.getElementById('add_val_display');
    const addValHidden = document.getElementById('add_val_hidden');

    if (addType && addValDisplay && addValHidden) {
        updatePromoValidation(addType, addValDisplay, addValHidden);

        addType.addEventListener('change', () => updatePromoValidation(addType, addValDisplay, addValHidden));
        addValDisplay.addEventListener('input', () => handleInputDiscount(addValDisplay, addValHidden, addType));
    }

    // 3. Gắn sự kiện cho Form Sửa
    const editType = document.getElementById('edit_type');
    const editValDisplay = document.getElementById('edit_val_display');
    const editValHidden = document.getElementById('edit_val_hidden');

    if (editType && editValDisplay && editValHidden) {
        editType.addEventListener('change', () => updatePromoValidation(editType, editValDisplay, editValHidden));
        editValDisplay.addEventListener('input', () => handleInputDiscount(editValDisplay, editValHidden, editType));
    }

    // 4. Validate chung cho toàn bộ thẻ Input
    const forms = document.querySelectorAll(".custom-validate-form");
    forms.forEach(form => {
        form.addEventListener("submit", function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll("input[required], select[required], textarea[required]");

            inputs.forEach(input => {
                let existingError = input.parentElement.querySelector(".error-msg, .server-error-msg");
                if (existingError) existingError.remove();
                input.classList.remove("border-rose-500", "bg-rose-50");

                if (!input.value.trim() || !input.validity.valid) {
                    isValid = false;
                    input.classList.remove("border-[#e2e3e1]");
                    input.classList.add("border-rose-500", "bg-rose-50");

                    let errorText = input.getAttribute("title") || "Trường này không được để trống.";
                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${errorText}</span>`;
                    input.insertAdjacentHTML("afterend", errorHtml);
                }
            });

            // Kiểm tra Logic Ngày kết thúc > Ngày bắt đầu
            let startInput = form.querySelector('input[name="ngayBatDau"]');
            let endInput = form.querySelector('input[name="ngayKetThuc"]');
            if (startInput && endInput && startInput.value && endInput.value) {
                let startDate = new Date(startInput.value);
                let endDate = new Date(endInput.value);

                if (endDate <= startDate) {
                    isValid = false;
                    endInput.classList.remove("border-[#e2e3e1]");
                    endInput.classList.add("border-rose-500", "bg-rose-50");

                    let existingError = endInput.parentElement.querySelector(".error-msg, .server-error-msg");
                    if (existingError) existingError.remove();

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> Ngày kết thúc phải lớn hơn ngày bắt đầu.</span>`;
                    endInput.insertAdjacentHTML("afterend", errorHtml);
                }
            }

            if (!isValid) {
                e.preventDefault();
                if(typeof lucide !== 'undefined') lucide.createIcons();
            }
        });

        form.querySelectorAll("input, select, textarea").forEach(input => {
            input.addEventListener("input", function () {
                let existingError = input.parentElement.querySelector(".error-msg, .server-error-msg");
                if (existingError) existingError.remove();
                input.classList.remove("border-rose-500", "bg-rose-50");
                input.classList.add("border-[#e2e3e1]");
            });
        });
    });
});