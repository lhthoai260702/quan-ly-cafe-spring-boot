let currentSelectedTableId = null;

function handleTableClick(element) {
    const maBan = element.getAttribute('data-id');
    const tenBan = element.getAttribute('data-ten');
    const tinhTrang = element.getAttribute('data-tinhtrang');

    currentSelectedTableId = maBan;

    const cards = document.getElementsByClassName('table-card');
    for (let card of cards) {
        card.classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    element.classList.add('border-[#553722]', 'ring-2', 'ring-[#553722]/50');

    document.getElementById('selectedTableId').innerText = maBan;
    document.getElementById('selectedTableName').innerText = tenBan.toUpperCase();

    const badge = document.getElementById('selectedTableBadge');
    badge.innerText = tinhTrang;
    badge.className = "text-[10px] px-2 py-0.5 font-bold uppercase tracking-wider rounded-md text-white";

    const iconBox = document.getElementById('barIconBox');
    iconBox.className = "p-3 rounded-xl text-white ";

    if (tinhTrang === 'Đang sử dụng') {
        badge.classList.add('bg-amber-500');
        iconBox.classList.add('bg-amber-500');
        toggleActionButtons(false);
    } else if (tinhTrang === 'Đã đặt trước') {
        badge.classList.add('bg-blue-500');
        iconBox.classList.add('bg-blue-500');
        toggleActionButtons(true);
    } else {
        badge.classList.add('bg-gray-400');
        iconBox.classList.add('bg-gray-400');
        toggleActionButtons(true);
    }

    document.getElementById('bottomActionBar').classList.remove('hidden');
}

function toggleActionButtons(isDisabled) {
    const btns = [document.getElementById('btnGop'), document.getElementById('btnTach'), document.getElementById('btnThanhToan')];
    btns.forEach(btn => isDisabled ? btn.classList.add('opacity-50', 'pointer-events-none') : btn.classList.remove('opacity-50', 'pointer-events-none'));
}

function closeBottomBar() {
    document.getElementById('bottomActionBar').classList.add('hidden');
    const cards = document.getElementsByClassName('table-card');
    for (let card of cards) {
        card.classList.remove('border-[#553722]', 'ring-2', 'ring-[#553722]/50');
    }
    currentSelectedTableId = null;
}

function closeOrderModal() {
    const overlay = document.getElementById('orderModalOverlay');
    const box = document.getElementById('orderModalBox');
    box.classList.add('scale-95', 'opacity-0');
    setTimeout(() => overlay.classList.add('hidden'), 150);
}

function executeAction(actionType) {
    if (!currentSelectedTableId) return;
    const tableName = document.getElementById('selectedTableName').innerText;

    switch(actionType) {
        case 'xem':
            fetch(`/tables/${currentSelectedTableId}/order-details`)
                .then(response => response.text())
                .then(html => {
                    document.getElementById('orderModalContainer').innerHTML = html;
                    document.getElementById('orderModalOverlay').classList.remove('hidden');
                    setTimeout(() => document.getElementById('orderModalBox').classList.remove('scale-95', 'opacity-0'), 20);
                })
                .catch(err => showCustomError("Không thể kết nối đến máy chủ!"));
            break;

        case 'chuyen':
            if (document.getElementById('selectedTableBadge').innerText !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể chuyển những bàn ĐANG CÓ KHÁCH!');
                return;
            }
            document.getElementById('transferFromTableId').value = currentSelectedTableId;
            document.getElementById('transferFromTableName').innerText = tableName;
            document.getElementById('transferModal').classList.remove('hidden');
            break;

        case 'thanhtoan':
            showCustomError(`Tính năng đang phát triển: Mở hóa đơn thanh toán cho [${tableName}]`);
            break;

        case 'gop':
            if (document.getElementById('selectedTableBadge').innerText !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể gộp những bàn ĐANG CÓ KHÁCH!');
                return;
            }
            const radios = document.getElementsByName('denMaBan');
            for(let i=0; i<radios.length; i++) {
                if(radios[i].value == currentSelectedTableId) {
                    radios[i].checked = true;
                    break;
                }
            }
            document.getElementById('mergeModal').classList.remove('hidden');
            break;

        case 'tach':
            if (document.getElementById('selectedTableBadge').innerText !== 'ĐANG SỬ DỤNG') {
                showCustomError('Lỗi: Chỉ có thể tách hóa đơn đối với những bàn ĐANG CÓ KHÁCH!');
                return;
            }

            document.getElementById('splitFromTableId').value = currentSelectedTableId;
            document.getElementById('splitFromTableName').innerText = tableName;

            // Gọi AJAX Fetch API để kéo danh sách món ăn của bàn này về dạng JSON
            fetch(`/tables/${currentSelectedTableId}/items`)
                .then(response => response.json())
                .then(items => {
                    const tbody = document.getElementById('splitItemsTableBody');
                    tbody.innerHTML = ''; // Làm sạch popup trước khi chèn mới

                    if (items.length === 0) {
                        tbody.innerHTML = `<tr><td colspan="3" class="p-4 text-center text-gray-400 italic">Bàn này chưa có món ăn nào để tách!</td></tr>`;
                        return;
                    }

                    items.forEach(item => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td class="p-3 font-semibold text-gray-800">
                                ${item.tenmon}
                                <input type="hidden" name="mathucdonList" value="${item.mathucdon}">
                            </td>
                            <td class="p-3 text-center font-bold text-gray-500">${item.soluong}</td>
                            <td class="p-3 text-center">
                                <input type="number" name="soluongTachList" value="0" min="0" max="${item.soluong}"
                                       class="w-24 px-2 py-1 border border-gray-200 rounded-lg text-center font-extrabold text-gray-700 focus:outline-none focus:border-[#553722] focus:ring-1 focus:ring-[#553722]">
                            </td>
                        `;
                        tbody.appendChild(row);
                    });

                    // Bật hiển thị Popup Tách lên màn hình
                    document.getElementById('splitModal').classList.remove('hidden');
                })
                .catch(err => showCustomError("Không thể lấy danh sách món ăn của bàn từ máy chủ!"));
            break;

        default:
            showCustomError(`Đang bảo trì tính năng: ${actionType}`);
    }
}

function closeTransferModal() {
    document.getElementById('transferModal').classList.add('hidden');
}

function showCustomError(message) {
    document.getElementById('customErrorMessage').innerText = message;
    document.getElementById('customErrorModal').classList.remove('hidden');
}

function closeCustomError() {
    document.getElementById('customErrorModal').classList.add('hidden');
}

function closeMergeModal() {
    document.getElementById('mergeModal').classList.add('hidden');
}

function closeSplitModal() {
    document.getElementById('splitModal').classList.add('hidden');
}