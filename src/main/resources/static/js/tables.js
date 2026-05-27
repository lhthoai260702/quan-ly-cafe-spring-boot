let currentSelectedTableId = null;

function handleTableClick(element) {
    const maBan = element.getAttribute('data-id');
    const tenBan = element.getAttribute('data-ten');
    const tinhTrang = element.getAttribute('data-tinhtrang');

    currentSelectedTableId = maBan;

    // Reset style các card khác
    const cards = document.getElementsByClassName('table-card');
    for (let card of cards) {
        card.classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    element.classList.add('border-[#553722]', 'ring-2', 'ring-[#553722]/50');

    // Cập nhật thông tin lên thanh công cụ
    document.getElementById('selectedTableId').innerText = maBan;
    document.getElementById('selectedTableName').innerText = tenBan.toUpperCase();

    const badge = document.getElementById('selectedTableBadge');
    badge.innerText = tinhTrang;
    badge.className = "text-[9px] px-1.5 py-0.5 font-bold uppercase tracking-wider rounded-md text-white";

    const iconBox = document.getElementById('barIconBox');
    iconBox.className = "p-3 rounded-xl text-white flex items-center justify-center ";

    // 1. Cập nhật màu sắc Icon dựa trên tình trạng
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

    // 2. Kích hoạt đúng nút theo nghiệp vụ từng loại bàn
    updateActionButtons(tinhTrang);

    document.getElementById('bottomActionBar').classList.remove('hidden');
}

// Thay thế hoàn toàn hàm toggleActionButtons cũ bằng hàm này
function updateActionButtons(tinhTrang) {
    // Lấy các nút cần vô hiệu hoá/kích hoạt
    const btnThanhToan = document.getElementById('btnThanhToan');
    const btnGop = document.getElementById('btnGop');
    const btnTach = document.getElementById('btnTach');
    const btnChuyen = document.getElementById('btnChuyen');
    const btnDatBan = document.getElementById('btnDatBan');

    // Cấu trúc hàm con giúp làm mờ và vô hiệu hoá nút
    const setButtonDisabled = (btn, isDisabled) => {
        if (!btn) return;
        if (isDisabled) {
            btn.classList.add('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        } else {
            btn.classList.remove('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        }
    };

    // Áp dụng Logic nghiệp vụ
    if (tinhTrang === 'Trống') {
        setButtonDisabled(btnThanhToan, true); // Trống thì không thể thanh toán
        setButtonDisabled(btnGop, true);       // Không thể gộp
        setButtonDisabled(btnTach, true);      // Không thể tách
        setButtonDisabled(btnChuyen, true);    // Không thể chuyển đi
        setButtonDisabled(btnDatBan, false);   // SÁNG: Có thể đặt bàn
    } else if (tinhTrang === 'Đã đặt trước') {
        setButtonDisabled(btnThanhToan, true); // Chưa gọi món nên chưa thanh toán
        setButtonDisabled(btnGop, true);
        setButtonDisabled(btnTach, true);
        setButtonDisabled(btnChuyen, true);
        setButtonDisabled(btnDatBan, true);    // Đã đặt rồi không đặt đè lên nữa
    } else if (tinhTrang === 'Đang sử dụng') {
        setButtonDisabled(btnThanhToan, false); // SÁNG: Đã có bill thì được thanh toán
        setButtonDisabled(btnGop, false);       // SÁNG: Được phép gộp
        setButtonDisabled(btnTach, false);      // SÁNG: Được phép tách
        setButtonDisabled(btnChuyen, false);    // SÁNG: Được phép chuyển
        setButtonDisabled(btnDatBan, true);     // Đang ngồi không thể đặt giữ chỗ
    }
}

function closeBottomBar() {
    document.getElementById('bottomActionBar').classList.add('hidden');
    const cards = document.getElementsByClassName('table-card');
    for (let card of cards) {
        card.classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    currentSelectedTableId = null;
}

function executeAction(actionType) {
    if (!currentSelectedTableId) return;
    const tableName = document.getElementById('selectedTableName').innerText;
    const tableStatus = document.getElementById('selectedTableBadge').innerText;

    switch(actionType) {
        case 'xem':
        case 'thanhtoan':
            fetch(`/tables/${currentSelectedTableId}/order-details`)
                .then(response => response.text())
                .then(html => {
                    document.getElementById('orderModalContainer').innerHTML = html;
                    document.getElementById('orderModalOverlay').classList.remove('hidden');
                    setTimeout(() => document.getElementById('orderModalBox').classList.remove('scale-95', 'opacity-0'), 20);
                })
                .catch(() => showCustomError("Không thể kết nối đến máy chủ!"));
            break;

        case 'chuyen':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể chuyển bàn đang có khách!');
                return;
            }
            document.getElementById('transferFromTableId').value = currentSelectedTableId;
            document.getElementById('transferFromTableName').innerText = tableName;
            document.getElementById('transferModal').classList.remove('hidden');
            break;

        case 'gop':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể gộp bàn đang có khách!');
                return;
            }
            document.getElementById('mergeModal').classList.remove('hidden');
            break;

        case 'tach':
            if (tableStatus !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể tách hóa đơn bàn đang có khách!');
                return;
            }
            document.getElementById('splitFromTableId').value = currentSelectedTableId;
            document.getElementById('splitFromTableName').innerText = tableName;
            fetch(`/tables/${currentSelectedTableId}/items`)
                .then(response => response.json())
                .then(items => {
                    const tbody = document.getElementById('splitItemsTableBody');
                    tbody.innerHTML = items.length === 0 ? `<tr><td colspan="3" class="p-4 text-center text-gray-400 italic">Bàn trống!</td></tr>` : '';
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
                showCustomError("Lỗi", "Bàn này không thể đặt trước!");
                return;
            }
            openBookingModal(currentSelectedTableId, tableName);
            break;

        case 'chonmon':
            // Lấy mã bàn và tên bàn hiện tại truyền vào form ẩn
            document.getElementById('orderTableId').value = currentSelectedTableId;
            document.getElementById('orderTableNameDisplay').innerText = document.getElementById('selectedTableName').innerText;
            // Hiển thị modal lên
            document.getElementById('addOrderModal').classList.remove('hidden');
            break;

        case 'inan':
            // Hiển thị modal cài đặt in ấn
            document.getElementById('printSettingsModal').classList.remove('hidden');
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

function openBookingModal(tableId, tableName) {
    document.getElementById('bookingTableId').value = tableId;
    document.getElementById('bookingTableName').innerText = tableName;
    const now = new Date();
    document.getElementById('bookingDate').value = now.toISOString().split('T')[0];
    document.getElementById('bookingTime').value = now.toTimeString().slice(0,5);
    document.getElementById('bookingModal').classList.remove('hidden');
}

function closeOrderModal() {
    document.getElementById('addOrderModal').classList.add('hidden');
}

function tangSoLuong(btn) {
    let input = btn.previousElementSibling;
    input.value = parseInt(input.value) + 1;
}

function giamSoLuong(btn) {
    let input = btn.nextElementSibling;
    if (parseInt(input.value) > 0) {
        input.value = parseInt(input.value) - 1;
    }
}

function closeViewOrderModal() {
    const modalBox = document.getElementById('orderModalBox');
    modalBox.classList.add('scale-95', 'opacity-0');

    setTimeout(() => {
        document.getElementById('orderModalOverlay').classList.add('hidden');
    }, 300);
}

document.getElementById('orderModalOverlay').addEventListener('click', function(e) {
    if (e.target === this) {
        closeViewOrderModal();
    }
});

// Hàm đóng Modal In ấn
function closePrintSettingsModal() {
    document.getElementById('printSettingsModal').classList.add('hidden');
}

// Hàm xử lý lưu cấu hình (Mô phỏng lưu thành công)
function savePrintSettings() {
    // Em có thể nâng cấp viết hàm lưu vào LocalStorage hoặc gửi qua Session sau, hiện tại ta thông báo trực quan:
    alert("Đã lưu cấu hình thiết bị in ấn của quầy thành công!");
    closePrintSettingsModal();
}