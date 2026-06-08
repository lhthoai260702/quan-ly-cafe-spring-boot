function formatCurrency(inputElement, hiddenInputId) {
    let rawValue = inputElement.value.replace(/\D/g, '');
    if (rawValue.length > 10) rawValue = rawValue.substring(0, 10);
    document.getElementById(hiddenInputId).value = rawValue;
    inputElement.value = rawValue ? new Intl.NumberFormat('en-US').format(rawValue) : '';
}

function formatQuantity(inputElement, hiddenInputId, unitElementId) {
    let allowDecimal = true;
    if (unitElementId) {
        let unitEl = document.getElementById(unitElementId);
        let unitText = unitEl ? (unitEl.tagName === 'SELECT' ? unitEl.options[unitEl.selectedIndex].text : unitEl.value).toLowerCase() : '';
        if (unitText) {
            let fractionalUnits = ['kg', 'lít', 'lit', 'l', 'g', 'ml', 'm', 'gam'];
            allowDecimal = fractionalUnits.some(u => unitText === u || unitText.includes(u));
            inputElement.placeholder = allowDecimal ? "Ví dụ: 1,5" : "Ví dụ: 10 (Chỉ nhập số nguyên)";
        }
    }
    let val = inputElement.value;
    if (allowDecimal) {
        val = val.replace(/[^0-9,]/g, '');
        if (val.startsWith(',')) val = '0' + val;
        let parts = val.split(',');
        if (parts.length > 2) val = parts[0] + ',' + parts.slice(1).join('').replace(/,/g, '');
        inputElement.value = val;
        document.getElementById(hiddenInputId).value = val.replace(',', '.');
    } else {
        val = val.split(',')[0].replace(/[^0-9]/g, '');
        inputElement.value = val;
        document.getElementById(hiddenInputId).value = val;
    }
}

function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    const addDateInput = document.getElementById('add_ngayNhap');
    if(addDateInput && !addDateInput.value) addDateInput.value = today;
}

function openImportModal(id, name, unitText) {
    document.getElementById('import_id').value = id;
    document.getElementById('import_name').textContent = name;
    document.getElementById('import_ngayNhap').value = new Date().toISOString().split('T')[0];
    document.getElementById('import_unit_text').value = unitText || '';

    let displayInput = document.getElementById('import_soLuong_display');
    if (displayInput) { displayInput.value = ''; formatQuantity(displayInput, 'import_soLuong_real', 'import_unit_text'); }
    document.getElementById('import_soLuong_real').value = '';

    // MỚI: Reset Đơn giá
    let donGiaDisplay = document.getElementById('import_donGia_display');
    if (donGiaDisplay) { donGiaDisplay.value = ''; formatCurrency(donGiaDisplay, 'import_donGia_real'); }

    document.getElementById('importModal').classList.remove('hidden');
}

function openEditModal(id, name, unitId) {
    document.getElementById('edit_id').value = id;
    document.getElementById('edit_name').value = name;
    document.getElementById('edit_unit').value = unitId;
    document.getElementById('editModal').classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_id').value = id;
    document.getElementById('delete_name').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

function openDeleteHistoryModal(maDonNhap) {
    document.getElementById('delete_hist_id').value = maDonNhap;
    document.getElementById('deleteHistoryModal').classList.remove('hidden');
}

// ==========================================
// TÍNH NĂNG MỚI: MỞ RỘNG DÒNG LỊCH SỬ NHẬP
// ==========================================
function toggleHistoryRow(maHangHoa) {
    let row = document.getElementById('history-view-' + maHangHoa);
    let contentDiv = document.getElementById('history-content-' + maHangHoa);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');

        // Gọi API lấy dữ liệu
        fetch(`/inventory/api/history/${maHangHoa}`)
            .then(response => response.json())
            .then(data => {
            if(data.length === 0) {
                contentDiv.innerHTML = `<span class="italic text-gray-500">Chưa có dữ liệu nhập hàng.</span>`;
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
                let dateStr = new Date(item.ngayNhap).toLocaleDateString('vi-VN');
                let rawDate = item.ngayNhap.split('T')[0];
                let formattedQty = String(item.soLuong).replace('.', ',');

                // Format tiền tệ chuẩn như bảng chính
                let formattedDonGia = new Intl.NumberFormat('vi-VN').format(item.donGia) + ' đ';
                let formattedTongTien = new Intl.NumberFormat('vi-VN').format(item.tongTien) + ' đ';

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
            html += `</tbody></table>`;
            contentDiv.innerHTML = html;

            // Kích hoạt lại icon Lucide cho bảng mới load
            if(typeof lucide !== 'undefined') lucide.createIcons();
        })
            .catch(error => {
            contentDiv.innerHTML = `<span class="text-rose-500 font-medium">Lỗi tải dữ liệu.</span>`;
        });
    } else {
        row.classList.add('hidden');
    }
}

function openEditHistoryModal(maDonNhap, rawDate, qtyStr, donGia) {
    document.getElementById('hist_maDonNhap').value = maDonNhap;
    document.getElementById('hist_ngayNhap').value = rawDate;

    let qtyDisplay = document.getElementById('hist_soLuong_display');
    qtyDisplay.value = qtyStr;
    formatQuantity(qtyDisplay, 'hist_soLuong_real', null);

    let donGiaDisplay = document.getElementById('hist_donGia_display');
    donGiaDisplay.value = donGia;
    formatCurrency(donGiaDisplay, 'hist_donGia_real');

    document.getElementById('editHistoryModal').classList.remove('hidden');
}

document.addEventListener("DOMContentLoaded", function () {
    setDefaultDate();
    lucide.createIcons();
});