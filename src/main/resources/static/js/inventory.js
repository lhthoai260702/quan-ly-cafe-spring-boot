/**
 * Định dạng tiền tệ (chỉ nhận số, tự động thêm dấu phẩy)
 * @param {HTMLElement} inputElement - Thẻ input nhập tiền
 * @param {string} hiddenInputId - ID của thẻ input ẩn chứa giá trị thực
 */
function formatCurrency(inputElement, hiddenInputId) {
    let rawValue = inputElement.value.replace(/\D/g, '');

    if (rawValue.length > 10) {
        rawValue = rawValue.substring(0, 10);
    }

    document.getElementById(hiddenInputId).value = rawValue;
    inputElement.value = rawValue ? new Intl.NumberFormat('en-US').format(rawValue) : '';
}

/**
 * Định dạng số lượng (hỗ trợ nhập số thập phân tùy theo đơn vị tính)
 * @param {HTMLElement} inputElement - Thẻ input nhập số lượng
 * @param {string} hiddenInputId - ID của thẻ input ẩn
 * @param {string} unitElementId - ID của thẻ chứa đơn vị tính
 */
function formatQuantity(inputElement, hiddenInputId, unitElementId) {
    let allowDecimal = true;

    if (unitElementId) {
        const unitEl = document.getElementById(unitElementId);
        const unitText = unitEl ? (unitEl.tagName === 'SELECT' ? unitEl.options[unitEl.selectedIndex].text : unitEl.value).toLowerCase() : '';

        if (unitText) {
            const fractionalUnits = ['kg', 'gram', 'lít', 'ml'];
            allowDecimal = fractionalUnits.some(u => unitText === u || unitText.includes(u));
            inputElement.placeholder = allowDecimal ? 'Ví dụ: 1,5' : 'Ví dụ: 10 (Chỉ nhập số nguyên)';
        }
    }

    let val = inputElement.value;

    if (allowDecimal) {
        val = val.replace(/[^0-9,]/g, '');
        if (val.startsWith(',')) {
            val = '0' + val;
        }

        const parts = val.split(',');
        if (parts.length > 2) {
            val = parts[0] + ',' + parts.slice(1).join('').replace(/,/g, '');
        }

        inputElement.value = val;
        document.getElementById(hiddenInputId).value = val.replace(',', '.');
    } else {
        val = val.split(',')[0].replace(/[^0-9]/g, '');
        inputElement.value = val;
        document.getElementById(hiddenInputId).value = val;
    }
}

/**
 * Thiết lập ngày mặc định là ngày hôm nay cho form thêm mới
 */
function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    const addDateInput = document.getElementById('add_ngayNhap');

    if (addDateInput && !addDateInput.value) {
        addDateInput.value = today;
    }
}

/**
 * Mở hộp thoại nhập hàng
 * @param {number|string} id - Mã hàng hóa
 * @param {string} name - Tên hàng hóa
 * @param {string} unitText - Đơn vị tính
 */
function openImportModal(id, name, unitText) {
    document.getElementById('import_id').value = id;
    document.getElementById('import_name').textContent = name;
    document.getElementById('import_ngayNhap').value = new Date().toISOString().split('T')[0];
    document.getElementById('import_unit_text').value = unitText || '';

    const displayInput = document.getElementById('import_soLuong_display');
    if (displayInput) {
        displayInput.value = '';
        formatQuantity(displayInput, 'import_soLuong_real', 'import_unit_text');
    }
    document.getElementById('import_soLuong_real').value = '';

    // Reset Đơn giá
    const donGiaDisplay = document.getElementById('import_donGia_display');
    if (donGiaDisplay) {
        donGiaDisplay.value = '';
        formatCurrency(donGiaDisplay, 'import_donGia_real');
    }

    document.getElementById('importModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại sửa hàng hóa
 * @param {number|string} id - Mã hàng hóa
 * @param {string} name - Tên hàng hóa
 * @param {number|string} unitId - Mã đơn vị tính
 */
function openEditModal(id, name, unitId) {
    document.getElementById('edit_id').value = id;
    document.getElementById('edit_name').value = name;
    document.getElementById('edit_unit').value = unitId;
    document.getElementById('editModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại xóa hàng hóa
 * @param {number|string} id - Mã hàng hóa
 * @param {string} name - Tên hàng hóa
 */
function openDeleteModal(id, name) {
    document.getElementById('delete_id').value = id;
    document.getElementById('delete_name').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại xóa lịch sử nhập hàng
 * @param {number|string} maDonNhap - Mã đơn nhập
 */
function openDeleteHistoryModal(maDonNhap) {
    document.getElementById('delete_hist_id').value = maDonNhap;
    document.getElementById('deleteHistoryModal').classList.remove('hidden');
}

// ==========================================
// TÍNH NĂNG MỚI: MỞ RỘNG DÒNG LỊCH SỬ NHẬP
// ==========================================

/**
 * Hiển thị/Ẩn danh sách lịch sử nhập của một hàng hóa
 * @param {number|string} maHangHoa - Mã hàng hóa
 */
function toggleHistoryRow(maHangHoa) {
    const row = document.getElementById('history-view-' + maHangHoa);
    const contentDiv = document.getElementById('history-content-' + maHangHoa);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');

        // Gọi API lấy dữ liệu
        fetch(`/inventory/api/history/${maHangHoa}`)
            .then(response => response.json())
            .then(data => {
            if (data.length === 0) {
                contentDiv.innerHTML = '<span class="italic text-gray-500">Chưa có dữ liệu nhập hàng.</span>';
                return;
            }

            let html = `
                    <table class="w-full text-left bg-white rounded-lg overflow-hidden shadow-sm mt-1 mb-2 border border-[#e2e3e1]/60">
                        <thead class="bg-[#f4f4f2] text-[10px] uppercase text-[#50453e] font-bold border-b border-[#e2e3e1]/60">
                            <tr>
                                <th class="p-3 pl-4 w-32">Ngày nhập</th>
                                <th class="p-3 text-center">SL Nhập</th>
                                <th class="p-3 text-right">Đơn giá</th>
                                <th class="p-3 text-right">Thành tiền</th>
                                <th class="p-3 text-center w-24">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-[#e2e3e1]/40 text-sm">`;

            data.forEach(item => {
                const dateStr = new Date(item.ngayNhap).toLocaleDateString('vi-VN');
                const rawDate = item.ngayNhap.split('T')[0];
                const formattedQty = String(item.soLuong).replace('.', ',');

                // Format tiền tệ chuẩn như bảng chính
                const formattedDonGia = new Intl.NumberFormat('vi-VN').format(item.donGia) + ' VNĐ';
                const formattedTongTien = new Intl.NumberFormat('vi-VN').format(item.tongTien) + ' VNĐ';

                html += `
                        <tr class="hover:bg-[#f9f9f7]/50 transition-colors">
                            <td class="p-3 pl-4 text-[#1a1c1b]">${dateStr}</td>
                            <td class="p-3 text-center font-bold text-emerald-700">${formattedQty}</td>
                            <td class="p-3 text-right font-bold text-[#553722]">${formattedDonGia}</td>
                            <td class="p-3 text-right font-bold text-amber-600">${formattedTongTien}</td>
                            <td class="p-3 text-center flex justify-center items-center gap-1.5">
                                <button type="button"
                                        onclick="openEditHistoryModal(${item.maDonNhap}, '${rawDate}', '${formattedQty}', ${item.donGia})"
                                        class="p-1.5 text-[#553722] hover:bg-[#ffdcc6]/50 rounded-lg transition-colors" title="Sửa phiếu nhập">
                                    <i data-lucide="pen-line" class="w-4 h-4"></i>
                                </button>
                                <button type="button"
                                        onclick="openDeleteHistoryModal(${item.maDonNhap})"
                                        class="p-1.5 text-rose-500 hover:text-rose-700 hover:bg-rose-100 rounded-lg transition-colors" title="Xóa phiếu nhập">
                                    <i data-lucide="trash-2" class="w-4 h-4"></i>
                                </button>
                            </td>
                        </tr>`;
            });
            html += '</tbody></table>';
            contentDiv.innerHTML = html;

            // Kích hoạt lại icon Lucide cho bảng mới load
            if (typeof lucide !== 'undefined') {
                lucide.createIcons();
            }
        })
            .catch(error => {
            console.error('Lỗi tải lịch sử:', error);
            contentDiv.innerHTML = '<span class="text-rose-500 font-medium">Lỗi tải dữ liệu.</span>';
        });
    } else {
        row.classList.add('hidden');
    }
}

/**
 * Mở hộp thoại sửa lịch sử phiếu nhập
 * @param {number|string} maDonNhap - Mã đơn nhập
 * @param {string} rawDate - Ngày nhập định dạng YYYY-MM-DD
 * @param {string} qtyStr - Số lượng hiển thị
 * @param {number|string} donGia - Đơn giá gốc
 */
function openEditHistoryModal(maDonNhap, rawDate, qtyStr, donGia) {
    document.getElementById('hist_maDonNhap').value = maDonNhap;
    document.getElementById('hist_ngayNhap').value = rawDate;

    const qtyDisplay = document.getElementById('hist_soLuong_display');
    qtyDisplay.value = qtyStr;
    formatQuantity(qtyDisplay, 'hist_soLuong_real', null);

    const donGiaDisplay = document.getElementById('hist_donGia_display');
    donGiaDisplay.value = donGia;
    formatCurrency(donGiaDisplay, 'hist_donGia_real');

    document.getElementById('editHistoryModal').classList.remove('hidden');
}

document.addEventListener('DOMContentLoaded', function () {
    setDefaultDate();
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});