/*<![CDATA[*/
let currentSelectedTableId = null;

/**
 * Xử lý sự kiện khi người dùng nhấp (click) vào một bàn cụ thể trên sơ đồ
 * @param {HTMLElement} element - Thẻ chứa dữ liệu của bàn được click
 */
function handleTableClick(element) {
    const maBan = element.getAttribute('data-id');
    const tenBan = element.getAttribute('data-ten');
    const tinhTrang = element.getAttribute('data-tinhtrang');

    currentSelectedTableId = maBan;

    // Reset kiểu dáng của các thẻ bàn khác
    const cards = document.getElementsByClassName('table-card');
    for (let i = 0; i < cards.length; i++) {
        cards[i].classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }

    // Nổi bật thẻ bàn đang được chọn
    element.classList.add('border-[#553722]', 'ring-2', 'ring-[#553722]/50');

    // Cập nhật thông tin lên thanh công cụ bên dưới
    document.getElementById('selectedTableId').innerText = maBan;
    document.getElementById('selectedTableName').innerText = tenBan.toUpperCase();

    const badge = document.getElementById('selectedTableBadge');
    badge.innerText = tinhTrang;
    badge.className = 'text-[9px] px-1.5 py-0.5 font-bold uppercase tracking-wider rounded-md text-white';

    const iconBox = document.getElementById('barIconBox');
    iconBox.className = 'p-3 rounded-xl text-white flex items-center justify-center ';

    // 1. Cập nhật màu sắc Icon dựa trên tình trạng của bàn
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

    // 2. Kích hoạt đúng các nút chức năng theo nghiệp vụ từng loại bàn
    updateActionButtons(tinhTrang);

    document.getElementById('bottomActionBar').classList.remove('hidden');
}

/**
 * Cập nhật trạng thái bật/tắt (Enable/Disable) của các nút chức năng theo logic nghiệp vụ
 * @param {string} tinhTrang - Tình trạng hiện tại của bàn
 */
function updateActionButtons(tinhTrang) {
    const btnThanhToan = document.getElementById('btnThanhToan');
    const btnGop = document.getElementById('btnGop');
    const btnTach = document.getElementById('btnTach');
    const btnChuyen = document.getElementById('btnChuyen');
    const btnDatBan = document.getElementById('btnDatBan');

    // Hàm phụ trợ giúp làm mờ và vô hiệu hoá nút bấm
    const setButtonDisabled = function (btn, isDisabled) {
        if (!btn) {
            return;
        }
        if (isDisabled) {
            btn.classList.add('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        } else {
            btn.classList.remove('opacity-40', 'pointer-events-none', 'cursor-not-allowed');
        }
    };

    // Áp dụng Logic nghiệp vụ bán hàng
    if (tinhTrang === 'Trống') {
        setButtonDisabled(btnThanhToan, true); // Trống thì không thể thanh toán
        setButtonDisabled(btnGop, true);       // Không thể gộp
        setButtonDisabled(btnTach, true);      // Không thể tách
        setButtonDisabled(btnChuyen, true);    // Không thể chuyển đi
        setButtonDisabled(btnDatBan, false);   // Có thể đặt bàn
    } else if (tinhTrang === 'Đã đặt trước') {
        setButtonDisabled(btnThanhToan, true); // Chưa gọi món nên chưa thanh toán
        setButtonDisabled(btnGop, true);
        setButtonDisabled(btnTach, true);
        setButtonDisabled(btnChuyen, true);
        setButtonDisabled(btnDatBan, true);    // Đã đặt rồi không đặt đè lên nữa
    } else if (tinhTrang === 'Đang sử dụng') {
        setButtonDisabled(btnThanhToan, false); // Đã có bill thì được thanh toán
        setButtonDisabled(btnGop, false);       // Được phép gộp
        setButtonDisabled(btnTach, false);      // Được phép tách
        setButtonDisabled(btnChuyen, false);    // Được phép chuyển
        setButtonDisabled(btnDatBan, true);     // Đang ngồi không thể đặt giữ chỗ
    }
}

/**
 * Đóng thanh công cụ hành động phía dưới màn hình
 */
function closeBottomBar() {
    document.getElementById('bottomActionBar').classList.add('hidden');
    const cards = document.getElementsByClassName('table-card');

    for (let i = 0; i < cards.length; i++) {
        cards[i].classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    currentSelectedTableId = null;
}

/**
 * Điều hướng xử lý khi người dùng chọn một hành động từ thanh công cụ
 * @param {string} actionType - Loại hành động (ví dụ: 'xem', 'chuyen', 'gop'...)
 */
function executeAction(actionType) {
    if (!currentSelectedTableId) {
        return;
    }

    const tableName = document.getElementById('selectedTableName').innerText;
    const tableStatus = document.getElementById('selectedTableBadge').innerText;

    switch (actionType) {
        case 'xem':
        case 'thanhtoan':
            fetch(`/tables/${currentSelectedTableId}/order-details`)
                .then(function (response) {
                return response.text();
            })
                .then(function (html) {
                document.getElementById('orderModalContainer').innerHTML = html;
                document.getElementById('orderModalOverlay').classList.remove('hidden');
                setTimeout(function () {
                    document.getElementById('orderModalBox').classList.remove('scale-95', 'opacity-0');
                }, 20);
            })
                .catch(function () {
                showCustomError('Không thể kết nối đến máy chủ!');
            });
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
                .then(function (response) {
                return response.json();
            })
                .then(function (items) {
                const tbody = document.getElementById('splitItemsTableBody');
                tbody.innerHTML = items.length === 0 ? '<tr><td colspan="3" class="p-4 text-center text-gray-400 italic">Bàn trống!</td></tr>' : '';

                items.forEach(function (item) {
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
                showCustomError('Lỗi', 'Bàn này không thể đặt trước!');
                return;
            }
            openBookingModal(currentSelectedTableId, tableName);
            break;

        case 'chonmon':
            document.getElementById('orderTableId').value = currentSelectedTableId;
            document.getElementById('orderTableNameDisplay').innerText = document.getElementById('selectedTableName').innerText;
            document.getElementById('addOrderModal').classList.remove('hidden');
            break;

        case 'inan':
            document.getElementById('printSettingsModal').classList.remove('hidden');
            break;
    }
}

/**
 * Các hàm tiện ích để đóng/mở Modal giao diện
 */
function showCustomError(message) {
    document.getElementById('customErrorMessage').innerText = message;
    document.getElementById('customErrorModal').classList.remove('hidden');
}

function closeCustomError() {
    document.getElementById('customErrorModal').classList.add('hidden');
}

function closeTransferModal() {
    document.getElementById('transferModal').classList.add('hidden');
}

function closeMergeModal() {
    document.getElementById('mergeModal').classList.add('hidden');
}

function closeSplitModal() {
    document.getElementById('splitModal').classList.add('hidden');
}

function closeBookingModal() {
    document.getElementById('bookingModal').classList.add('hidden');
}

function openBookingModal(tableId, tableName) {
    document.getElementById('bookingTableId').value = tableId;
    document.getElementById('bookingTableName').innerText = tableName;

    const now = new Date();
    document.getElementById('bookingDate').value = now.toISOString().split('T')[0];
    document.getElementById('bookingTime').value = now.toTimeString().slice(0, 5);
    document.getElementById('bookingModal').classList.remove('hidden');
}

function closeOrderModal() {
    document.getElementById('addOrderModal').classList.add('hidden');
}

/**
 * Tăng số lượng món ăn khi gọi món
 * @param {HTMLElement} btn - Nút '+' được bấm
 */
function tangSoLuong(btn) {
    const input = btn.previousElementSibling;
    input.value = parseInt(input.value, 10) + 1;
}

/**
 * Giảm số lượng món ăn khi gọi món
 * @param {HTMLElement} btn - Nút '-' được bấm
 */
function giamSoLuong(btn) {
    const input = btn.nextElementSibling;
    if (parseInt(input.value, 10) > 0) {
        input.value = parseInt(input.value, 10) - 1;
    }
}

/**
 * Đóng Modal xem chi tiết hóa đơn
 */
function closeViewOrderModal() {
    const modalBox = document.getElementById('orderModalBox');
    modalBox.classList.add('scale-95', 'opacity-0');

    setTimeout(function () {
        document.getElementById('orderModalOverlay').classList.add('hidden');
    }, 300);
}

// Lắng nghe sự kiện click ra bên ngoài để đóng Modal xem hóa đơn
document.getElementById('orderModalOverlay').addEventListener('click', function (e) {
    if (e.target === this) {
        closeViewOrderModal();
    }
});

function closePrintSettingsModal() {
    document.getElementById('printSettingsModal').classList.add('hidden');
}

function savePrintSettings() {
    alert('Đã lưu cấu hình thiết bị in ấn của quầy thành công!');
    closePrintSettingsModal();
}

/**
 * Xử lý tính toán động hiển thị Tiền khi người dùng thay đổi lựa chọn Khuyến Mãi
 */
document.addEventListener('change', function (e) {
    if (e.target && e.target.id === 'khuyenMaiSelect') {
        const selectedOption = e.target.options[e.target.selectedIndex];
        const tongTienGocElem = document.getElementById('tongTienGoc');
        const tongTienCuoiElem = document.getElementById('tongTienCuoi');
        const giamGiaTextElem = document.getElementById('giamGiaText');

        if (!tongTienGocElem || !tongTienCuoiElem || !giamGiaTextElem) {
            return;
        }

        const tongGoc = parseFloat(tongTienGocElem.getAttribute('data-goc')) || 0;

        if (selectedOption.value === '') {
            // Khôi phục hiển thị ban đầu nếu không chọn KM
            tongTienCuoiElem.innerText = new Intl.NumberFormat('vi-VN').format(tongGoc) + ' VNĐ';
            tongTienGocElem.classList.add('hidden');
            giamGiaTextElem.classList.add('hidden');
        } else {
            // Lấy giá trị khuyến mãi và tính toán
            const loaiKm = selectedOption.getAttribute('data-loai').toLowerCase();
            const giaTriGiam = parseFloat(selectedOption.getAttribute('data-giatri'));
            let tienGiam = 0;

            if (loaiKm.includes('phần')) {
                tienGiam = tongGoc * (giaTriGiam / 100);
            } else {
                tienGiam = giaTriGiam;
            }

            // Ép tổng tiền về 0 nếu tiền giảm lớn hơn tiền gốc
            const tongCuoi = Math.max(tongGoc - tienGiam, 0);

            // Cập nhật giao diện thanh toán
            tongTienGocElem.classList.remove('hidden');
            giamGiaTextElem.classList.remove('hidden');
            giamGiaTextElem.innerText = 'Đã giảm: -' + new Intl.NumberFormat('vi-VN').format(tienGiam) + ' VNĐ';
            tongTienCuoiElem.innerText = new Intl.NumberFormat('vi-VN').format(tongCuoi) + ' VNĐ';
        }
    }
});

/**
 * Bắt lỗi từ server và hiển thị bằng Modal thân thiện khi trang tải xong
 */
document.addEventListener('DOMContentLoaded', function () {
    const errorMsg = /*[[${errorMsg}]]*/ null;
    if (errorMsg) {
        showCustomError(errorMsg);
    }
});

/**
 * Định dạng tự động số điện thoại có khoảng trắng (VD: 0901 234 567)
 * @param {HTMLElement} input - Thẻ input SĐT
 */
function formatPhoneNumber(input) {
    let val = input.value.replace(/\D/g, ''); // Bỏ các ký tự không phải số
    if (val.length > 7) {
        input.value = val.replace(/(\d{4})(\d{3})(\d+)/, '$1 $2 $3').trim();
    } else if (val.length > 4) {
        input.value = val.replace(/(\d{4})(\d+)/, '$1 $2').trim();
    } else {
        input.value = val;
    }
}
/*]]>*/