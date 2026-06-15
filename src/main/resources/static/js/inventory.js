/**
 * JS: Quản Lý Tồn Kho - Cấu trúc Universal Modal (Tối ưu UI/UX)
 */

function formatCurrency(inputElement, hiddenInputId) {
    let rawValue = inputElement.value.replace(/\D/g, '');
    if (rawValue.length > 10) rawValue = rawValue.substring(0, 10);
    document.getElementById(hiddenInputId).value = rawValue;
    inputElement.value = rawValue ? new Intl.NumberFormat('en-US').format(rawValue) : '';
}

function formatQuantity(inputElement, hiddenInputId, unitElementId) {
    let val = inputElement.value.replace(/[^0-9,]/g, '');
    if (val.startsWith(',')) val = '0' + val;
    const parts = val.split(',');
    if (parts.length > 2) val = parts[0] + ',' + parts.slice(1).join('').replace(/,/g, '');

    inputElement.value = val;
    document.getElementById(hiddenInputId).value = val.replace(',', '.');
}

/**
 * MỘT MODAL ĐA NĂNG ĐẢM NHIỆM 4 HÀNH ĐỘNG KHÁC NHAU
 * action: 'ADD' | 'EDIT_ITEM' | 'EDIT_HISTORY' | 'IMPORT'
 */
function openUniversalModal(action, id, name, unitId, useUnitId, date, qty, price) {
    const form = document.getElementById('universalForm');
    const modal = document.getElementById('universalModal');
    const title = document.getElementById('univ_modal_title');
    const btnText = document.getElementById('univ_btn_text');
    const btnSubmit = document.getElementById('univ_submit_btn');
    const header = document.getElementById('univ_modal_header');

    // Box Giao Diện
    const secDinhDanh = document.getElementById('section_dinhDanh');
    const secNhapHang = document.getElementById('section_nhapHang');
    const lblImportName = document.getElementById('import_item_label');
    const displayImportName = document.getElementById('import_name_display');

    // DOM Từng Phần (Containers) để dễ ẩn hiện
    const fDateContainer = document.getElementById('univ_date_container');
    const fQtyContainer = document.getElementById('univ_qty_container');
    const fPriceContainer = document.getElementById('univ_price_container');
    const lblUnivQty = document.getElementById('univ_qty_label');

    // DOM Inputs
    const fId = document.getElementById('univ_id');
    const fName = document.getElementById('univ_name');
    const fUnit = document.getElementById('univ_unit');
    const fDate = document.getElementById('univ_date');
    const fQtyDisp = document.getElementById('univ_qty_display');
    const fQtyReal = document.getElementById('univ_qty_real');
    const fPriceDisp = document.getElementById('univ_price_display');
    const fPriceReal = document.getElementById('univ_price_real');

    // 1. Reset lỗi Validation cũ & Tẩy màu chữ cũ của ô Số lượng
    form.querySelectorAll('.error-msg').forEach(e => e.remove());
    form.querySelectorAll('.border-rose-500').forEach(e => {
        e.classList.remove('border-rose-500', 'bg-rose-50');
        e.classList.add('border-[#e2e3e1]', 'focus:border-[#553722]');
    });
    fQtyDisp.classList.remove('text-emerald-700', 'text-[#553722]');

    // 2. Mở toàn bộ thẻ để sẵn sàng gán dữ liệu
    [fName, fUnit, fDate, fQtyDisp, fPriceDisp].forEach(el => el.setAttribute('required', 'required'));
    secDinhDanh.classList.remove('hidden');
    secNhapHang.classList.remove('hidden', 'border-t', 'pt-4', 'mt-2');
    secNhapHang.classList.add('border-t', 'pt-4', 'mt-2');

    fDateContainer.classList.remove('hidden');
    fQtyContainer.classList.remove('hidden');
    fPriceContainer.classList.remove('hidden');
    lblImportName.classList.add('hidden');
    header.className = "px-5 py-4 border-b border-[#e2e3e1]/60 flex justify-between items-center bg-[#f9f9f7]";
    title.className = "text-lg font-bold text-[#1a1c1b] flex items-center gap-2";

    // 3. Truyền dữ liệu Base
    fId.value = id || '';
    fName.value = name || 'Khởi tạo Dummy';
    fUnit.value = unitId || (fUnit.options.length > 0 ? fUnit.options[0].value : '');
    document.getElementById('univ_useUnit').value = useUnitId || '';
    fDate.value = date || new Date().toISOString().split('T')[0];
    fQtyDisp.value = qty || '1';
    fQtyReal.value = qty ? String(qty).replace(',', '.') : '1';
    fPriceDisp.value = price || '1';
    fPriceReal.value = price || '1';

    if(qty) formatQuantity(fQtyDisp, 'univ_qty_real', null);
    if(price) formatCurrency(fPriceDisp, 'univ_price_real');

    // QUY TẮC BẮT LỖI SỐ LƯỢNG (Regex Pattern)
    // Loại 1: Số > 0 (Chặn 0, 0,0, 00, v.v)
    const strictlyPositivePattern = '^(?!0+(?:,0+)?$)\\d+(?:,\\d+)?$';
    // Loại 2: Số >= 0 (Cho phép 0)
    const positiveOrZeroPattern = '^\\d+(?:,\\d+)?$';

    // 4. Biến Hình Giao Diện Theo Action
    if (action === 'ADD') {
        title.innerHTML = '<i data-lucide="package-plus" class="w-5 h-5"></i> Khai báo hàng mới';
        btnText.innerText = 'Lưu hàng hóa';
        lblUnivQty.innerHTML = 'Số lượng khởi tạo <span class="text-rose-500">*</span>';
        fQtyDisp.classList.add('text-[#553722]');
        form.action = form.getAttribute('data-action-add');
        btnSubmit.className = "px-5 py-2.5 rounded-xl text-sm font-bold text-white bg-[#553722] hover:bg-[#6f4e37] shadow-md transition-all flex items-center gap-2";
        fId.value = ''; fName.value = ''; fQtyDisp.value = ''; fQtyReal.value = ''; fPriceDisp.value = ''; fPriceReal.value = '';

        // ADD: Bắt buộc lớn hơn 0
        fQtyDisp.setAttribute('pattern', strictlyPositivePattern);
        fQtyDisp.setAttribute('title', 'Số lượng phải lớn hơn 0');

    } else if (action === 'EDIT_ITEM') {
        title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5"></i> Chỉnh sửa hàng hóa';
        btnText.innerText = 'Lưu thay đổi';
        lblUnivQty.innerHTML = 'Số lượng tồn kho <span class="text-rose-500">*</span>';
        fQtyDisp.classList.add('text-[#553722]');
        form.action = form.getAttribute('data-action-edit');
        btnSubmit.className = "px-5 py-2.5 rounded-xl text-sm font-bold text-white bg-[#553722] hover:bg-[#6f4e37] shadow-md transition-all flex items-center gap-2";

        fDateContainer.classList.add('hidden');
        fPriceContainer.classList.add('hidden');
        [fDate, fPriceDisp].forEach(el => el.removeAttribute('required'));

        // EDIT_ITEM (Tồn kho): Cho phép bằng 0
        fQtyDisp.setAttribute('pattern', positiveOrZeroPattern);
        fQtyDisp.setAttribute('title', 'Số lượng không được để trống (có thể nhập 0)');

    } else if (action === 'EDIT_HISTORY') {
        title.innerHTML = '<i data-lucide="file-edit" class="w-5 h-5"></i> Chỉnh sửa phiếu nhập';
        btnText.innerText = 'Cập nhật';
        lblUnivQty.innerHTML = 'Số lượng nhập <span class="text-rose-500">*</span>';
        fQtyDisp.classList.add('text-emerald-700');
        form.action = form.getAttribute('data-action-history');
        btnSubmit.className = "px-5 py-2.5 rounded-xl text-sm font-bold text-white bg-[#553722] hover:bg-[#6f4e37] shadow-md transition-all flex items-center gap-2";

        secDinhDanh.classList.add('hidden');
        secNhapHang.classList.remove('border-t', 'pt-4', 'mt-2');
        [fName, fUnit].forEach(el => el.removeAttribute('required'));

        // EDIT_HISTORY (Đơn nhập): Bắt buộc lớn hơn 0
        fQtyDisp.setAttribute('pattern', strictlyPositivePattern);
        fQtyDisp.setAttribute('title', 'Số lượng nhập phải lớn hơn 0');

    } else if (action === 'IMPORT') {
        title.innerHTML = '<i data-lucide="arrow-down-to-line" class="w-5 h-5"></i> Nhập thêm hàng';
        btnText.innerText = 'Xác nhận Nhập';
        lblUnivQty.innerHTML = 'Số lượng nhập <span class="text-rose-500">*</span>';
        fQtyDisp.classList.add('text-emerald-700');
        form.action = form.getAttribute('data-action-import');

        header.className = "px-5 py-4 border-b border-emerald-100 flex justify-between items-center bg-emerald-50";
        title.className = "text-lg font-bold text-emerald-800 flex items-center gap-2";
        btnSubmit.className = "px-5 py-2.5 rounded-xl text-sm font-bold text-white bg-emerald-600 hover:bg-emerald-700 shadow-md transition-all flex items-center gap-2";

        secDinhDanh.classList.add('hidden');
        secNhapHang.classList.remove('border-t', 'pt-4', 'mt-2');
        lblImportName.classList.remove('hidden');
        displayImportName.innerText = name;

        [fName, fUnit].forEach(el => el.removeAttribute('required'));
        fQtyDisp.value = ''; fQtyReal.value = ''; fPriceDisp.value = ''; fPriceReal.value = '';

        // IMPORT: Bắt buộc lớn hơn 0
        fQtyDisp.setAttribute('pattern', strictlyPositivePattern);
        fQtyDisp.setAttribute('title', 'Số lượng nhập phải lớn hơn 0');
    }

    if (typeof lucide !== 'undefined') lucide.createIcons();
    modal.classList.remove('hidden');
}

/**
 * Dành riêng cho việc giữ nguyên màn hình khi Form bị Validation Backend báo lỗi.
 */
function restoreUniversalModal(action) {
    openUniversalModal(action);
    const fQtyDisp = document.getElementById('univ_qty_display');
    const fPriceDisp = document.getElementById('univ_price_display');
    if (fQtyDisp.value && !fQtyDisp.value.includes(',')) formatQuantity(fQtyDisp, 'univ_qty_real', null);
    if (fPriceDisp.value && !fPriceDisp.value.includes('.')) formatCurrency(fPriceDisp, 'univ_price_real');
}

// Logic hiển thị lịch sử nhập hàng (Giữ nguyên)
function toggleHistoryRow(maHangHoa) {
    const row = document.getElementById('history-view-' + maHangHoa);
    const contentDiv = document.getElementById('history-content-' + maHangHoa);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');
        fetch(`/inventory/api/history/${maHangHoa}`)
            .then(response => response.json())
            .then(data => {
            if (data.length === 0) {
                contentDiv.innerHTML = '<span class="italic text-gray-500">Chưa có dữ liệu nhập hàng.</span>';
                return;
            }

            // Đã tinh chỉnh lại tỷ lệ độ rộng (width) cho từng cột để bảng cân đối và dễ nhìn hơn
            let html = `
                    <table class="w-full text-left bg-white rounded-lg overflow-hidden shadow-sm mt-1 mb-2 border border-[#e2e3e1]/60 table-fixed">
                        <thead class="bg-[#f4f4f2] text-[10px] uppercase text-[#50453e] font-bold">
                            <tr>
                                <th class="p-2 pl-4 w-32">Ngày nhập</th>
                                <th class="p-2 text-center w-24">SL Nhập</th>
                                <th class="p-2 text-right w-36">Đơn giá</th>
                                <th class="p-2 text-right w-40">Thành tiền</th>
                                <th class="p-2 text-center w-24">Thao tác</th>
                            </tr>
                        </thead><tbody class="divide-y divide-[#e2e3e1]/40 text-sm">`;

            data.forEach(item => {
                const dateStr = new Date(item.ngayNhap).toLocaleDateString('vi-VN');
                const rawDate = item.ngayNhap.split('T')[0];
                const formattedQty = String(item.soLuong).replace('.', ',');
                const formattedDonGia = new Intl.NumberFormat('en-US').format(item.donGia);
                const formattedTongTien = new Intl.NumberFormat('en-US').format(item.tongTien);

                html += `
                        <tr class="hover:bg-[#f9f9f7]/50">
                            <td class="p-2 pl-4 truncate">${dateStr}</td>
                            <td class="p-2 text-center font-bold text-emerald-700 truncate">${formattedQty}</td>
                            <td class="p-2 text-right font-bold text-[#553722] truncate">${formattedDonGia}đ</td>
                            <td class="p-2 text-right font-bold text-amber-600 truncate">${formattedTongTien}đ</td>
                            <td class="p-2 text-center flex justify-center gap-1.5">
                                <button type="button" onclick="openUniversalModal('EDIT_HISTORY', ${item.maDonNhap}, null, null, null, '${rawDate}', '${formattedQty}', ${item.donGia})" class="p-1 text-[#553722] hover:bg-[#ffdcc6]/50 rounded"><i data-lucide="pen-line" class="w-4 h-4"></i></button>
                                <button type="button" onclick="openDeleteHistoryModalCustom(${item.maDonNhap})" class="p-1 text-rose-500 hover:bg-rose-100 rounded"><i data-lucide="trash-2" class="w-4 h-4"></i></button>
                            </td>
                        </tr>`;
            });
            html += '</tbody></table>';
            contentDiv.innerHTML = html;
            if (typeof lucide !== 'undefined') lucide.createIcons();
        });
    } else {
        row.classList.add('hidden');
    }
}

// Logic Validation Inline (Tránh bong bóng trình duyệt)
document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            // Bổ sung lọc những thẻ nào không bị ẩn (Tức là đang hiển thị cho action tương ứng)
            const inputs = form.querySelectorAll('input[required]:not([disabled]), select[required]:not([disabled])');

            inputs.forEach(function (input) {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) existingError.remove();

                input.classList.remove('border-rose-500', 'bg-rose-50');

                let val = input.value ? input.value.trim() : '';

                if (!val) {
                    isValid = false;
                    showError(input, 'Trường này không được để trống.');
                }
                // NÂNG CẤP: Bắt lỗi bằng Regex theo thuộc tính Pattern được gán bằng JS
                else if (input.hasAttribute('pattern')) {
                    let regex = new RegExp(input.getAttribute('pattern'));
                    if (!regex.test(val)) {
                        isValid = false;
                        showError(input, input.getAttribute('title') || 'Giá trị không hợp lệ.');
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

        form.querySelectorAll('input:not([type="hidden"]), select').forEach(function (input) {
            input.addEventListener('input', function () {
                let existingError = input.parentElement.querySelector('.error-msg');
                if (existingError) existingError.remove();
                input.classList.remove('border-rose-500', 'bg-rose-50');
                input.classList.add('border-[#e2e3e1]', 'focus:border-[#553722]');
            });
        });
    });

    // Hàm tiện ích in thông báo chữ đỏ
    function showError(inputElement, message) {
        if (inputElement.type !== 'hidden') {
            inputElement.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]', 'focus:border-emerald-500');
            inputElement.classList.add('border-rose-500', 'bg-rose-50');
            let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${message}</span>`;
            inputElement.insertAdjacentHTML('afterend', errorHtml);
        }
    }
});

// ============================================================================
// XỬ LÝ LỖI VALIDATION TỪ SERVER TRẢ VỀ
// ============================================================================
document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra xem biến SERVER_FLAGS từ HTML truyền sang có tồn tại không
    if (typeof SERVER_FLAGS !== 'undefined') {
        if (SERVER_FLAGS.hasAddError) restoreUniversalModal('ADD');
        else if (SERVER_FLAGS.hasEditError) restoreUniversalModal('EDIT_ITEM');
        else if (SERVER_FLAGS.hasImportError) restoreUniversalModal('IMPORT');
        else if (SERVER_FLAGS.hasEditHistoryError) restoreUniversalModal('EDIT_HISTORY');
    }
});

// ============================================================================
// CÁC HÀM MỞ MODAL XÓA (DELETE)
// ============================================================================
function openDeleteModalCustom(id, name) {
    document.getElementById('delete_id').value = id;
    document.getElementById('delete_name').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

function openDeleteHistoryModalCustom(id) {
    document.getElementById('delete_hist_id').value = id;
    document.getElementById('deleteHistoryModal').classList.remove('hidden');
}