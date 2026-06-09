/*<![CDATA[*/
var hasExpenseError = /*[[${hasExpenseError != null ? hasExpenseError : false}]]*/ false;
if (hasExpenseError) {
    document.getElementById('addExpenseModal').classList.remove('hidden');
}
/*]]>*/

// Hàm Format Tiền dùng chung cho cả Thêm và Sửa
function formatCurrency(input, hiddenInputId) {
    let value = input.value.replace(/\D/g, ''); // Bỏ mọi ký tự chữ
    document.getElementById(hiddenInputId).value = value;
    if (value !== '') {
        input.value = Number(value).toLocaleString('en-US');
    } else {
        input.value = '';
    }
}

function openDetailModal(dateKey, dateStr) {
    document.getElementById('modal-date-title').textContent = dateStr;
    let hiddenData = document.getElementById('detail-data-' + dateKey);
    let detailContent = document.getElementById('modal-detail-content');
    if (hiddenData) detailContent.innerHTML = hiddenData.innerHTML;
    else detailContent.innerHTML = '<p class="text-center text-gray-500 italic py-4">Không có dữ liệu chi tiết.</p>';
    if (typeof lucide !== 'undefined') lucide.createIcons();
    document.getElementById('detailModal').classList.remove('hidden');
}

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

function openDeleteModal(id) {
    document.getElementById('deleteExpenseId').value = id;
    document.getElementById('deleteExpenseModal').classList.remove('hidden');
}

document.head.insertAdjacentHTML("beforeend", `<style>
            .custom-scrollbar::-webkit-scrollbar { width: 4px; }
            .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
            .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
        </style>`);

// Tự động format tiền nếu có lỗi Backend trả về
document.addEventListener("DOMContentLoaded", function () {
    let realSoTienAdd = document.getElementById('realSoTienAdd');
    let displaySoTienAdd = document.getElementById('displaySoTienAdd');
    if (realSoTienAdd && displaySoTienAdd && realSoTienAdd.value) {
        displaySoTienAdd.value = Number(realSoTienAdd.value).toLocaleString('en-US');
    }

    // Client Validation Form UX
    const forms = document.querySelectorAll(".custom-validate-form");
    forms.forEach(form => {
        form.addEventListener("submit", function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll("input:not([type='hidden']), select, textarea");

            inputs.forEach(input => {
                let existingError = input.parentElement.querySelector(".error-msg, .server-error-msg");
                if (existingError) existingError.remove();
                input.classList.remove("border-rose-500", "border-blue-500");

                if (!input.validity.valid) {
                    isValid = false;
                    input.classList.remove("border-gray-300");
                    input.classList.add(form.action.includes('edit') ? "border-blue-500" : "border-rose-500");

                    let errorText = input.getAttribute("title") || "Vui lòng kiểm tra lại.";
                    if (input.validity.valueMissing) errorText = "Trường này không được để trống.";

                    let errorClass = form.action.includes('edit') ? "text-blue-500" : "text-rose-500";
                    let errorHtml = `<span class="error-msg text-[10px] ${errorClass} font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${errorText}</span>`;
                    input.insertAdjacentHTML("afterend", errorHtml);
                }
            });

            if (!isValid) {
                e.preventDefault();
                if(typeof lucide !== 'undefined') lucide.createIcons();
            }
        });

        form.querySelectorAll("input:not([type='hidden']), select, textarea").forEach(input => {
            input.addEventListener("input", function () {
                let existingError = input.parentElement.querySelector(".error-msg, .server-error-msg");
                if (existingError) existingError.remove();
                input.classList.remove("border-rose-500", "border-blue-500");
                input.classList.add("border-gray-300");
            });
        });
    });
});