let currentSelectedTableId = null;

function handleTableClick(element) {
    const maBan = element.getAttribute('data-id');
    const tenBan = element.getAttribute('data-ten');
    const tinhTrang = element.getAttribute('data-tinhtrang');

    currentSelectedTableId = maBan;

    // Reset kiểu dáng
    const cards = document.getElementsByClassName('table-card');
    for (let i = 0; i < cards.length; i++) {
        cards[i].classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }

    element.classList.add('border-[#553722]', 'ring-2', 'ring-[#553722]/50');

    // 🚀 1. Lấy mã hóa đơn từ input ẩn do Backend sinh ra (nếu có)
    const hiddenInvoice = element.querySelector('.hidden-invoice-id');
    const maHoaDon = hiddenInvoice ? hiddenInvoice.value : null;

    // Cập nhật thông tin thanh công cụ chính
    document.getElementById('selectedTableId').innerText = maBan;
    document.getElementById('selectedTableName').innerText = tenBan.toUpperCase();

    // 🚀 2. Cập nhật dòng hiển thị phụ: Bổ sung Mã Hóa Đơn màu nổi bật
    const extraInfoElem = document.getElementById('selectedTableExtraInfo');
    if (maHoaDon) {
        extraInfoElem.innerHTML = `Mã bàn: #<span id="selectedTableId">${maBan}</span> <span class="mx-1">•</span> <span class="text-amber-600 font-extrabold">HĐ: #${maHoaDon}</span>`;
    } else {
        extraInfoElem.innerHTML = `Mã bàn: #<span id="selectedTableId">${maBan}</span>`;
    }

    const badge = document.getElementById('selectedTableBadge');
    badge.innerText = tinhTrang;
    badge.className = 'text-[9px] px-1.5 py-0.5 font-bold uppercase tracking-wider rounded-md text-white';

    const iconBox = document.getElementById('barIconBox');
    iconBox.className = 'p-3 rounded-xl text-white flex items-center justify-center ';

    if (tinhTrang === 'Đang sử dụng') {
        badge.classList.add('bg-amber-500');
        iconBox.classList.add('bg-amber-500');
    } else if (tinhTrang === 'Đã đặt trước') {
        badge.classList.add('bg-blue-500');
        iconBox.classList.add('bg-blue-500');
    } else {
        badge.classList.add('bg-gray-400');
        iconBox.classList.add('bg-gray-400');
    }

    updateActionButtons(tinhTrang);
    document.getElementById('bottomActionBar').classList.remove('hidden');
}

function updateActionButtons(tinhTrang) {
    const btnThanhToan = document.getElementById('btnThanhToan');
    const btnGop = document.getElementById('btnGop');
    const btnTach = document.getElementById('btnTach');
    const btnChuyen = document.getElementById('btnChuyen');
    const btnDatBan = document.getElementById('btnDatBan');
    const btnHuyBan = document.getElementById('btnHuyBan'); // NÚT HỦY BÀN

    const setButtonDisabled = function (btn, isDisabled) {
        if (!btn) return;
        if (isDisabled) {
            // Làm mờ và tắt khả năng click
            btn.classList.add('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        } else {
            // Bật lại khả năng click
            btn.classList.remove('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        }
    };

    if (tinhTrang === 'Trống') {
        setButtonDisabled(btnThanhToan, true);
        setButtonDisabled(btnGop, true);
        setButtonDisabled(btnTach, true);
        setButtonDisabled(btnChuyen, true);
        setButtonDisabled(btnDatBan, false);
        setButtonDisabled(btnHuyBan, true);     // Trống thì TẮT/LÀM MỜ NÚT HỦY
    } else if (tinhTrang === 'Đã đặt trước') {
        setButtonDisabled(btnThanhToan, true);
        setButtonDisabled(btnGop, true);
        setButtonDisabled(btnTach, true);
        setButtonDisabled(btnChuyen, true);
        setButtonDisabled(btnDatBan, true);
        setButtonDisabled(btnHuyBan, false);    // BẬT NÚT HỦY
    } else if (tinhTrang === 'Đang sử dụng') {
        setButtonDisabled(btnThanhToan, false);
        setButtonDisabled(btnGop, false);
        setButtonDisabled(btnTach, false);
        setButtonDisabled(btnChuyen, false);
        setButtonDisabled(btnDatBan, true);
        setButtonDisabled(btnHuyBan, false);    // BẬT NÚT HỦY
    }
}

function closeBottomBar() {
    document.getElementById('bottomActionBar').classList.add('hidden');
    const cards = document.getElementsByClassName('table-card');

    for (let i = 0; i < cards.length; i++) {
        cards[i].classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    currentSelectedTableId = null;
}

function executeAction(actionType) {
    if (!currentSelectedTableId && actionType !== 'inan') return;
    const tableName = document.getElementById('selectedTableName') ? document.getElementById('selectedTableName').innerText : "";
    const tableStatus = document.getElementById('selectedTableBadge') ? document.getElementById('selectedTableBadge').innerText : "";

    switch (actionType) {
        case 'xem':
        case 'thanhtoan':
            fetch(`/tables/${currentSelectedTableId}/order-details`)
                .then(response => response.text())
                .then(html => {
                document.getElementById('orderModalContainer').innerHTML = html;
                document.getElementById('orderModalOverlay').classList.remove('hidden');
                setTimeout(() => {
                    document.getElementById('orderModalBox').classList.remove('scale-95', 'opacity-0');
                }, 20);
            })
                .catch(() => showCustomError('Không thể kết nối đến máy chủ!'));
            break;
        case 'chuyen':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể chuyển bàn đang có khách!'); return;
            }
            document.getElementById('transferFromTableId').value = currentSelectedTableId;
            document.getElementById('transferFromTableName').innerText = tableName;
            document.getElementById('transferModal').classList.remove('hidden');
            break;
        case 'gop':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể gộp bàn đang có khách!'); return;
            }

            // 1. Reset sạch trạng thái các ô chọn trong Modal Gộp Bàn về mặc định
            const tuMaBanCheckboxes = document.querySelectorAll('input[name="tuMaBanList"]');
            const denMaBanRadios = document.querySelectorAll('input[name="denMaBan"]');

            tuMaBanCheckboxes.forEach(cb => {
                cb.checked = false;
                cb.onclick = null; // Gỡ bỏ hàm chặn click cũ
                cb.parentElement.classList.remove('opacity-60', 'bg-gray-100', 'pointer-events-none');
            });

            denMaBanRadios.forEach(radio => {
                radio.checked = false;
            });

            // 2. Tự động chọn và khóa bàn hiện tại
            if (currentSelectedTableId) {
                // A. Tick sẵn ô Bàn Đích đến (Radio) và vẫn cho phép thay đổi sang bàn khác
                const currentTableRadio = document.querySelector(`input[name="denMaBan"][value="${currentSelectedTableId}"]`);
                if (currentTableRadio) currentTableRadio.checked = true;

                // B. Tick sẵn ô Bàn Cần Gộp (Checkbox) và khóa cứng không cho bỏ tick
                const currentTableCheckbox = document.querySelector(`input[name="tuMaBanList"][value="${currentSelectedTableId}"]`);
                if (currentTableCheckbox) {
                    currentTableCheckbox.checked = true;
                    // Chặn hành động click bỏ tick
                    currentTableCheckbox.onclick = function() { return false; };
                    // Làm mờ và tắt sự kiện chuột trên thẻ cha để nhân viên biết ô này đã bị khóa cứng
                    currentTableCheckbox.parentElement.classList.add('opacity-60', 'bg-gray-100', 'pointer-events-none');
                }
            }

            document.getElementById('mergeModal').classList.remove('hidden');
            break;
        case 'tach':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể tách hóa đơn bàn đang có khách!'); return;
            }
            document.getElementById('splitFromTableId').value = currentSelectedTableId;
            document.getElementById('splitFromTableName').innerText = tableName;

            fetch(`/tables/${currentSelectedTableId}/items`)
                .then(response => response.json())
                .then(items => {
                const tbody = document.getElementById('splitItemsTableBody');
                tbody.innerHTML = items.length === 0 ? '<tr><td colspan="3" class="p-4 text-center text-gray-400 italic">Bàn trống!</td></tr>' : '';

                items.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `<td class="p-3 font-semibold">${item.tenmon}<input type="hidden" name="mathucdonList" value="${item.mathucdon}"></td>
                                         <td class="p-3 text-center">${item.soluong}</td>
                                         <td class="p-3 text-center"><input type="number" name="soluongTachList" value="0" min="0" max="${item.soluong}" class="w-16 border rounded text-center"></td>`;
                    tbody.appendChild(row);
                });
                document.getElementById('splitModal').classList.remove('hidden');
            });
            break;
        case 'datban':
            if (tableStatus !== 'TRỐNG') {
                showCustomError('Lỗi', 'Bàn này không thể đặt trước!'); return;
            }
            openBookingModal(currentSelectedTableId, tableName);
            break;
        case 'chonmon':
            document.getElementById('orderTableId').value = currentSelectedTableId;
            document.getElementById('orderTableNameDisplay').innerText = tableName;

            // Xử lý an toàn chuỗi trạng thái (xóa khoảng trắng thừa và in hoa)
            let currentStatus = tableStatus.trim().toUpperCase();

            if (currentStatus === 'ĐÃ ĐẶT TRƯỚC') {
                document.getElementById('confirmCustomerTable').innerText = tableName;
                document.getElementById('confirmCustomerTypeModal').classList.remove('hidden');
            } else {
                document.getElementById('orderLoaiKhach').value = 'binhthuong';
                proceedToOpenOrderModal(currentStatus);
            }
            break;
        case 'inan':
            document.getElementById('printSettingsModal').classList.remove('hidden');
            break;
        case 'huyban': // MỞ POPUP XÁC NHẬN HỦY BÀN VỚI THÔNG BÁO ĐỘNG
            if (tableStatus === 'TRỐNG') {
                showCustomError('Bàn trống không thể hủy!'); return;
            }

            document.getElementById('cancelTableIdInput').value = currentSelectedTableId;
            document.getElementById('cancelTableName').innerText = tableName;

            // Lấy element chứa câu thông báo phụ
            const subMessage = document.getElementById('cancelTableSubMessage');

            // Đổi thông báo tùy theo tình trạng bàn
            if (tableStatus === 'ĐÃ ĐẶT TRƯỚC') {
                subMessage.innerText = 'Khách hàng sẽ bị mất chỗ. Thông tin đặt bàn sẽ bị xóa!';
                subMessage.className = 'font-semibold text-orange-600 mt-1 block'; // Màu cam cảnh báo nhẹ
            } else if (tableStatus === 'ĐANG SỬ DỤNG') {
                subMessage.innerText = 'Toàn bộ thực đơn và hóa đơn chưa thanh toán sẽ bị xóa hoàn toàn!';
                subMessage.className = 'font-semibold text-red-600 mt-1 block'; // Màu đỏ nguy hiểm
            }

            document.getElementById('cancelTableModal').classList.remove('hidden');
            break;
    }
}

function showCustomError(message) {
    document.getElementById('customErrorMessage').innerText = message;
    document.getElementById('customErrorModal').classList.remove('hidden');
}
function closeCustomError() { document.getElementById('customErrorModal').classList.add('hidden'); }
function closeTransferModal() { document.getElementById('transferModal').classList.add('hidden'); }
function closeMergeModal() { document.getElementById('mergeModal').classList.add('hidden'); }
function closeSplitModal() { document.getElementById('splitModal').classList.add('hidden'); }
function closeBookingModal() { document.getElementById('bookingModal').classList.add('hidden'); }
function closeOrderModal() { document.getElementById('addOrderModal').classList.add('hidden'); }
function closePrintSettingsModal() { document.getElementById('printSettingsModal').classList.add('hidden'); }
function closeCancelModal() { document.getElementById('cancelTableModal').classList.add('hidden'); }

function openBookingModal(tableId, tableName) {
    document.getElementById('bookingTableId').value = tableId;
    document.getElementById('bookingTableName').innerText = tableName;
    const now = new Date();
    document.getElementById('bookingDate').value = now.toISOString().split('T')[0];
    document.getElementById('bookingTime').value = now.toTimeString().slice(0, 5);
    document.getElementById('bookingModal').classList.remove('hidden');
}

function tangSoLuong(btn) {
    const input = btn.previousElementSibling;
    if(input.readOnly) return;

    let currentVal = parseInt(input.value, 10);
    if (isNaN(currentVal)) currentVal = 0;

    // Giới hạn không cho ấn nút cộng vượt quá 999
    if (currentVal < 999) {
        input.value = currentVal + 1;
    }
}

function giamSoLuong(btn) {
    const input = btn.nextElementSibling;
    if(input.readOnly) return;

    let currentVal = parseInt(input.value, 10);
    if (isNaN(currentVal)) currentVal = 0;

    if (currentVal > 0) {
        input.value = currentVal - 1;
    }
}

function closeViewOrderModal() {
    const modalBox = document.getElementById('orderModalBox');
    if(modalBox) modalBox.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        const overlay = document.getElementById('orderModalOverlay');
        if(overlay) overlay.classList.add('hidden');
    }, 300);
}

function formatSoLuongGoiMon(input) {
    // 1. Chỉ giữ lại các ký tự số
    let val = input.value.replace(/\D/g, '');

    // 2. Giới hạn tối đa 3 ký tự
    if (val.length > 3) {
        val = val.slice(0, 3);
    }

    // 3. Xử lý số 0 ở đầu (để tránh nhập "005")
    if (val !== '' && parseInt(val, 10) >= 0) {
        val = parseInt(val, 10).toString();
    }

    // 4. Nếu người dùng xóa sạch trơn, gán lại về 0
    if (val === '') {
        val = '0';
    }

    input.value = val;
}

document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.getElementById('orderModalOverlay');
    if(overlay) {
        overlay.addEventListener('click', function (e) {
            if (e.target === this) closeViewOrderModal();
        });
    }
});

function savePrintSettings() {
    alert('Đã lưu cấu hình thiết bị in ấn của quầy thành công!');
    closePrintSettingsModal();
}

document.addEventListener('change', function (e) {
    if (e.target && e.target.id === 'khuyenMaiSelect') {
        const selectedOption = e.target.options[e.target.selectedIndex];
        const tongTienGocElem = document.getElementById('tongTienGoc');
        const tongTienCuoiElem = document.getElementById('tongTienCuoi');
        const giamGiaTextElem = document.getElementById('giamGiaText');

        if (!tongTienGocElem || !tongTienCuoiElem || !giamGiaTextElem) return;

        const tongGoc = parseFloat(tongTienGocElem.getAttribute('data-goc')) || 0;

        if (selectedOption.value === '') {
            tongTienCuoiElem.innerText = new Intl.NumberFormat('vi-VN').format(tongGoc) + ' VNĐ';
            tongTienGocElem.classList.add('hidden');
            giamGiaTextElem.classList.add('hidden');
        } else {
            const loaiKm = selectedOption.getAttribute('data-loai').toLowerCase();
            const giaTriGiam = parseFloat(selectedOption.getAttribute('data-giatri'));
            let tienGiam = loaiKm.includes('phần') ? tongGoc * (giaTriGiam / 100) : giaTriGiam;

            const tongCuoi = Math.max(tongGoc - tienGiam, 0);

            tongTienGocElem.classList.remove('hidden');
            giamGiaTextElem.classList.remove('hidden');
            giamGiaTextElem.innerText = 'Đã giảm: -' + new Intl.NumberFormat('vi-VN').format(tienGiam) + ' VNĐ';
            tongTienCuoiElem.innerText = new Intl.NumberFormat('vi-VN').format(tongCuoi) + ' VNĐ';
        }

        if (typeof tinhTienThoi === 'function') {
            tinhTienThoi();
        }
    }
});

function formatPhoneNumber(input) {
    // 1. Chỉ giữ lại các ký tự là số
    let val = input.value.replace(/\D/g, '');

    // 2. Chặn không cho nhập quá 10 số
    if (val.length > 10) {
        val = val.slice(0, 10);
    }

    // 3. Tự động định dạng cách dãn (XXXX XXX XXX)
    if (val.length > 7) {
        input.value = val.replace(/(\d{4})(\d{3})(\d{0,3})/, '$1 $2 $3').trim();
    } else if (val.length > 4) {
        input.value = val.replace(/(\d{4})(\d{0,3})/, '$1 $2').trim();
    } else {
        input.value = val;
    }
}

/**
 * Kiểm tra form Đặt Bàn (Bắt buộc đủ 10 số SĐT)
 */
function validateBookingForm(form) {
    let isValid = true;

    // 1. Xóa hết các thông báo lỗi cũ
    form.querySelectorAll('.error-msg').forEach(msg => msg.remove());

    // Lấy tất cả các ô nhập liệu bắt buộc
    const inputs = form.querySelectorAll('input[required]');

    inputs.forEach(function (input) {
        // Trả lại viền mặc định trước khi kiểm tra
        input.classList.remove('border-rose-500', 'bg-rose-50');
        input.classList.add('border-gray-200');

        let val = input.value ? input.value.trim() : '';

        // Lỗi 1: Bỏ trống
        if (!val) {
            isValid = false;
            showInlineError(input, 'Trường này không được bỏ trống.');
        }
        // Lỗi 2: Số điện thoại không đủ 10 số
        else if (input.name === 'sdtKhachHang') {
            const digitCount = val.replace(/\D/g, '').length;
            if (digitCount !== 10) {
                isValid = false;
                showInlineError(input, 'Vui lòng nhập đúng và đủ 10 số.');
            }
        }
    });

    // Nếu hợp lệ, tự động làm sạch khoảng trắng của SĐT trước khi gửi lên Backend
    if (isValid) {
        const phoneInput = form.querySelector('input[name="sdtKhachHang"]');
        if (phoneInput) phoneInput.value = phoneInput.value.replace(/\D/g, '');
    }

    return isValid; // Trả về false sẽ chặn form submit
}

// Hàm phụ trợ vẽ dòng chữ lỗi màu đỏ
function showInlineError(inputElement, message) {
    inputElement.classList.remove('border-gray-200');
    inputElement.classList.add('border-rose-500', 'bg-rose-50');

    // Dùng FontAwesome cho đồng bộ với file tables.html
    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold flex items-center gap-1 mt-1.5 ml-1 block"><i class="fa-solid fa-circle-info shrink-0"></i> ${message}</span>`;
    inputElement.insertAdjacentHTML('afterend', errorHtml);
}

// Tự động xóa lỗi và trả lại viền bình thường khi người dùng bắt đầu gõ lại
document.addEventListener('input', function(e) {
    if (e.target.closest('#bookingForm')) {
        e.target.classList.remove('border-rose-500', 'bg-rose-50');
        e.target.classList.add('border-gray-200');
        let wrapper = e.target.parentElement;
        wrapper.querySelectorAll('.error-msg').forEach(msg => msg.remove());
    }
});

/**
 * Kiểm tra form Gọi món trước khi gửi lên Server
 * Nếu không có món nào > 0 thì chặn lại và hiện thông báo
 */
function validateOrderForm(form) {
    const inputs = form.querySelectorAll('input[name="soLuong"]');
    let totalQty = 0;

    for (let i = 0; i < inputs.length; i++) {
        let val = parseInt(inputs[i].value, 10);
        if (!isNaN(val)) {
            totalQty += val;
        }
    }

    if (totalQty === 0) {
        showCustomError('Vui lòng chọn ít nhất 1 món (số lượng lớn hơn 0) trước khi xác nhận order!');
        return false; // Chặn, không cho submit form về Backend
    }

    return true; // Cho phép submit
}

/* ======================================================================
   CÁC HÀM XỬ LÝ COMBOBOX CUSTOM (Giống hệt bên trang Nhân Viên / Menu)
   ====================================================================== */

function toggleCombobox(element) {
    const dropdown = element.nextElementSibling;
    const icon = element.querySelector('i');

    // Đóng tất cả các dropdown khác đang mở
    document.querySelectorAll('.combobox-dropdown:not(.hidden)').forEach(d => {
        if (d !== dropdown) {
            d.classList.add('hidden');
            d.classList.remove('opacity-100');
            const prevIcon = d.previousElementSibling.querySelector('i');
            if(prevIcon) prevIcon.style.transform = 'rotate(0deg)';
        }
    });

    if (dropdown.classList.contains('hidden')) {
        dropdown.classList.remove('hidden');
        setTimeout(() => dropdown.classList.add('opacity-100'), 10);
        icon.style.transform = 'rotate(180deg)';
        // Tự động focus vào ô tìm kiếm
        const searchInput = dropdown.querySelector('input[type="text"]');
        if(searchInput) searchInput.focus();
    } else {
        dropdown.classList.remove('opacity-100');
        setTimeout(() => dropdown.classList.add('hidden'), 200);
        icon.style.transform = 'rotate(0deg)';
    }
}

function selectComboboxOption(liElement) {
    const value = liElement.getAttribute('data-value');
    const text = liElement.innerText;

    const wrapper = liElement.closest('.custom-combobox');
    const targetInputId = wrapper.getAttribute('data-target-input');
    const displayText = wrapper.querySelector('.combobox-selected-text');

    // Gán text hiển thị
    displayText.innerText = text;
    displayText.classList.remove('text-gray-400');
    displayText.classList.add('text-gray-900', 'font-semibold');

    // Gán value ngầm vào thẻ input hidden để Submit Form
    document.getElementById(targetInputId).value = value;

    // Đóng dropdown
    toggleCombobox(wrapper.firstElementChild);
}

function filterCombobox(inputElement) {
    const keyword = inputElement.value.toLowerCase().trim();
    const dropdown = inputElement.closest('.combobox-dropdown');
    const items = dropdown.querySelectorAll('ul li:not(.no-result-msg)');
    const noResultMsg = dropdown.querySelector('.no-result-msg');
    let hasResult = false;

    items.forEach(item => {
        const text = item.innerText.toLowerCase();
        // Lọc không phân biệt hoa thường
        if (text.includes(keyword)) {
            item.style.display = 'block';
            hasResult = true;
        } else {
            item.style.display = 'none';
        }
    });

    if (hasResult) {
        noResultMsg.style.display = 'none';
    } else {
        noResultMsg.style.display = 'block';
    }
}

// Click ra ngoài thì tự động đóng tất cả combobox
document.addEventListener('click', function(event) {
    if (!event.target.closest('.custom-combobox')) {
        document.querySelectorAll('.combobox-dropdown:not(.hidden)').forEach(d => {
            d.classList.remove('opacity-100');
            setTimeout(() => d.classList.add('hidden'), 200);
            const icon = d.previousElementSibling.querySelector('i');
            if(icon) icon.style.transform = 'rotate(0deg)';
        });
    }
});

function filterOrderMenu(searchInput) {
    const keyword = searchInput.value.toLowerCase().trim();
    const items = document.querySelectorAll('.order-item-card');

    items.forEach(item => {
        const nameElement = item.querySelector('.order-item-name');
        if (nameElement) {
            const name = nameElement.innerText.toLowerCase();
            // Nếu tên món chứa từ khóa -> Hiển thị (dùng '' để lấy lại CSS display:flex gốc)
            // Nếu không chứa -> Ẩn đi (display:none)
            if (name.includes(keyword)) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        }
    });
}

/**
 * 🚀 Hàm tính Tiền thối lại trực tiếp khi gõ (Đồng bộ format dấu phẩy Kho hàng)
 */
function tinhTienThoi() {
    const input = document.getElementById('tienKhachDua');
    const tienThoiElem = document.getElementById('tienThoiLai');
    const tongTienCuoiElem = document.getElementById('tongTienCuoi');

    if (!input || !tienThoiElem || !tongTienCuoiElem) return;

    // Lấy Tổng tiền cuối cùng trên bill
    let tongTienStr = tongTienCuoiElem.innerText.replace(/\D/g, '');
    let tongTien = parseInt(tongTienStr, 10) || 0;

    // Chỉ giữ lại số từ ô nhập tiền khách đưa
    let valStr = input.value.replace(/\D/g, '');

    // Xử lý 1: Nếu để trống -> Mặc định là 0 VNĐ (Không hiện chữ CK nữa)
    if (valStr === '') {
        input.value = '';
        tienThoiElem.innerText = '0 VNĐ';
        tienThoiElem.className = 'text-[11px] font-bold text-gray-400';
        return;
    }

    // Xử lý 2: Ép kiểu sang số nguyên và loại bỏ số 0 ở đầu (VD: gõ 005 -> 5)
    let tienKhach = parseInt(valStr, 10);

    // Nếu cố tình nhập số 0
    if (tienKhach === 0) {
        input.value = '0';
        tienThoiElem.innerText = 'Chưa đủ tiền thanh toán!';
        tienThoiElem.className = 'text-[11px] font-bold text-red-500';
        return;
    }

    // Tự động định dạng dấu phẩy (,) chuẩn en-US giống hệt phân hệ Kho
    input.value = new Intl.NumberFormat('en-US').format(tienKhach);

    // Xử lý 3: Tính tiền thối và cảnh báo
    let tienThoi = tienKhach - tongTien;

    if (tienThoi < 0) {
        tienThoiElem.innerText = 'Khách đưa thiếu: ' + new Intl.NumberFormat('en-US').format(Math.abs(tienThoi)) + ' VNĐ';
        tienThoiElem.className = 'text-[11px] font-bold text-red-500';
    } else {
        tienThoiElem.innerText = new Intl.NumberFormat('en-US').format(tienThoi) + ' VNĐ';
        tienThoiElem.className = 'text-[11px] font-bold text-emerald-600';
    }
}

/**
 * 🚀 Hàm kiểm tra chặn Submit nếu nhập thiếu tiền
 */
function validateCheckoutForm(form) {
    const input = document.getElementById('tienKhachDua');
    const tongTienCuoiElem = document.getElementById('tongTienCuoi');

    // Chỉ kiểm tra nếu thu ngân có nhập tiền (Không để trống)
    if (input && input.value.trim() !== '') {
        let tienKhach = parseInt(input.value.replace(/\D/g, ''), 10) || 0;
        let tongTien = parseInt(tongTienCuoiElem.innerText.replace(/\D/g, ''), 10) || 0;

        if (tienKhach > 0 && tienKhach < tongTien) {
            showCustomError('Lỗi: Số tiền khách đưa (' + new Intl.NumberFormat('en-US').format(tienKhach) + ' VNĐ) nhỏ hơn tổng hóa đơn!');
            return false; // Chặn không cho gửi lên Backend
        }
    }

    return true; // Cho phép thanh toán
}

function proceedToOpenOrderModal(tableStatus) {
    const searchInput = document.getElementById('searchOrderMenu');
    if (searchInput) {
        searchInput.value = '';
        filterOrderMenu(searchInput);
    }
    const allInputs = document.querySelectorAll('#addOrderModal input[name="soLuong"]');
    allInputs.forEach(input => input.value = 0);

    if (tableStatus === 'ĐANG SỬ DỤNG') {
        fetch(`/tables/${currentSelectedTableId}/items`)
            .then(response => response.json())
            .then(items => {
            items.forEach(item => {
                const hiddenInput = document.querySelector(`#addOrderModal input[name="maThucDon"][value="${item.mathucdon}"]`);
                if (hiddenInput) {
                    const qtyInput = hiddenInput.parentElement.querySelector('input[name="soLuong"]');
                    if (qtyInput) qtyInput.value = item.soluong;
                }
            });
            document.getElementById('addOrderModal').classList.remove('hidden');
        }).catch(err => {
            document.getElementById('addOrderModal').classList.remove('hidden');
        });
    } else {
        document.getElementById('addOrderModal').classList.remove('hidden');
    }
}

function selectCustomerType(type) {
    document.getElementById('orderLoaiKhach').value = type;
    document.getElementById('confirmCustomerTypeModal').classList.add('hidden');
    // Mở popup order (Bàn vẫn đang mang nhãn ĐÃ ĐẶT TRƯỚC nên sẽ mở bill trống)
    proceedToOpenOrderModal('ĐÃ ĐẶT TRƯỚC');
}

function closeConfirmCustomerTypeModal() {
    document.getElementById('confirmCustomerTypeModal').classList.add('hidden');
}