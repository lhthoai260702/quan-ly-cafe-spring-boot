/*<![CDATA[*/

/**
 * JS: Quản lý Thực Đơn - Universal Modal & Type-ahead Autocomplete
 */

// Định dạng tiền tệ
function formatCurrency(inputElement, hiddenInputId) {
    let value = inputElement.value.replace(/\D/g, '');
    if (value === '') {
        inputElement.value = '';
        document.getElementById(hiddenInputId).value = '0';
        return;
    }
    inputElement.value = parseInt(value, 10).toLocaleString('en-US');
    document.getElementById(hiddenInputId).value = value;
}

// Chuyển đổi trạng thái "Loại Món Khác"
function toggleLoaiMonKhac() {
    const selectEl = document.getElementById('univ_loaiMon_select');
    const inputEl = document.getElementById('univ_loaiMon_khac');

    if (selectEl.value === 'Khác') {
        inputEl.classList.remove('hidden');
        inputEl.required = true;
        inputEl.name = 'loaiMon';
        selectEl.removeAttribute('name');
    } else {
        inputEl.classList.add('hidden');
        inputEl.required = false;
        inputEl.value = '';
        selectEl.name = 'loaiMon';
        inputEl.removeAttribute('name');
    }
}

// Mở rộng chi tiết nguyên liệu (Accordion ngoài bảng chính)
function toggleIngredientRow(maThucDon) {
    const row = document.getElementById('ingredient-view-' + maThucDon);
    const contentDiv = document.getElementById('ingredient-content-' + maThucDon);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');
        if (contentDiv.innerHTML.includes('Đang tải')) {
            fetch('/api/menu/ingredients/' + maThucDon)
                .then(res => res.json())
                .then(data => {
                if (data.length === 0) {
                    contentDiv.innerHTML = '<span class="text-rose-500 italic">Món này chưa thiết lập công thức nguyên liệu.</span>';
                } else {
                    let html = '<ul class="list-disc list-inside space-y-1 mt-1">';
                    data.forEach(item => {
                        html += `<li><b>${item.tenHangHoa}</b>: ${item.khoiLuong} <span class="text-[10px] uppercase font-bold bg-[#e2e3e1]/50 px-1 rounded">${item.donViTinh || '-'}</span></li>`;
                    });
                    html += '</ul>';
                    contentDiv.innerHTML = html;
                }
            })
                .catch(error => {
                console.error('Lỗi khi tải công thức:', error);
                contentDiv.innerHTML = '<span class="text-rose-500 italic">Lỗi khi tải dữ liệu nguyên liệu.</span>';
            });
        }
    } else {
        row.classList.add('hidden');
    }
}

// -----------------------------------------------------
// LOGIC TYPE-AHEAD COMBOBOX THÔNG MINH
// -----------------------------------------------------
function filterCombobox(input) {
    const filter = input.value.toLowerCase().trim();
    const combobox = input.closest('.custom-combobox');
    const list = combobox.querySelector('.combobox-list');
    const items = list.querySelectorAll('.combobox-item');
    const noMatchMsg = list.querySelector('.no-match-msg');

    // Xóa ID ẩn và đơn vị nếu user gõ sửa văn bản (để tránh lưu mã cũ sai)
    const hiddenId = combobox.querySelector('.ingredient-hidden-id');
    const unitSpan = input.closest('.ingredient-row').querySelector('.ingredient-unit');
    hiddenId.value = '';
    unitSpan.innerText = '-';

    let hasMatch = false;

    items.forEach(li => {
        const name = li.getAttribute('data-name').toLowerCase();
        if (name.includes(filter)) {
            li.style.display = '';
            hasMatch = true;
        } else {
            li.style.display = 'none';
        }
    });

    if (!hasMatch && filter !== '') {
        noMatchMsg.classList.remove('hidden');
        noMatchMsg.style.display = 'flex';
        items.forEach(li => li.style.display = 'none');
    } else {
        noMatchMsg.classList.add('hidden');
        noMatchMsg.style.display = 'none';
    }
}

function openCombobox(input) {
    const list = input.closest('.custom-combobox').querySelector('.combobox-list');
    list.classList.remove('hidden');
    filterCombobox(input);
}

function closeCombobox(input) {
    setTimeout(() => {
        const list = input.closest('.custom-combobox').querySelector('.combobox-list');
        if (list) list.classList.add('hidden');
    }, 200);
}

function selectComboboxOption(li) {
    const combobox = li.closest('.custom-combobox');
    const searchInput = combobox.querySelector('.ingredient-search');
    const hiddenId = combobox.querySelector('.ingredient-hidden-id');
    const unitSpan = li.closest('.ingredient-row').querySelector('.ingredient-unit');

    searchInput.value = li.getAttribute('data-name');
    hiddenId.value = li.getAttribute('data-id');
    unitSpan.innerText = li.getAttribute('data-unit') || '-';

    combobox.querySelector('.combobox-list').classList.add('hidden');

    // Tẩy viền đỏ và dọn rác báo lỗi ngay khi user chọn xong
    searchInput.classList.remove('border-rose-500', 'bg-rose-50');
    let form = combobox.closest('form');
    if (form) {
        form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
        // Khôi phục kích thước khung trắng về bình thường
        form.querySelectorAll('.ingredient-row').forEach(r => r.style.paddingBottom = '');
    }
}

// -----------------------------------------------------
// QUẢN LÝ DÒNG NGUYÊN LIỆU ĐỘNG
// -----------------------------------------------------
function addIngredientRow(containerId, maHangHoa = '', tenHangHoa = '', khoiLuong = '', donVi = '') {
    const container = document.getElementById(containerId);
    const template = document.getElementById('ingredientRowTemplate');
    const clone = template.content.cloneNode(true);

    if (maHangHoa !== '') {
        clone.querySelector('.ingredient-hidden-id').value = maHangHoa;
        clone.querySelector('.ingredient-search').value = tenHangHoa;
        clone.querySelector('.ingredient-qty').value = khoiLuong;
        if (donVi !== '') {
            clone.querySelector('.ingredient-unit').innerText = donVi;
        }
    }

    container.appendChild(clone);
    if (typeof lucide !== 'undefined') lucide.createIcons();
    updateIngredientNames(containerId);
}

function updateIngredientNames(containerId) {
    const container = document.getElementById(containerId);
    const rows = container.querySelectorAll('.ingredient-row');

    rows.forEach((row, index) => {
        const hiddenId = row.querySelector('.ingredient-hidden-id');
        const qtyInput = row.querySelector('.ingredient-qty');
        hiddenId.name = `ingredients[${index}].maHangHoa`;
        qtyInput.name = `ingredients[${index}].khoiLuong`;
    });
}

// -----------------------------------------------------
// UNIVERSAL MODAL (GỘP THÊM / SỬA LÀM 1)
// -----------------------------------------------------
function openUniversalModal(action, id, name, price, category) {
    const form = document.getElementById('universalForm');
    const modal = document.getElementById('universalModal');
    const title = document.getElementById('univ_title');
    const btnText = document.getElementById('univ_btn_text');

    // Inputs
    const fId = document.getElementById('univ_maThucDon');
    const fName = document.getElementById('univ_tenMon');
    const fPriceDisp = document.getElementById('univ_giaTien_display');
    const fPriceReal = document.getElementById('univ_giaTienHienTai');
    const fCatSel = document.getElementById('univ_loaiMon_select');
    const fCatKhac = document.getElementById('univ_loaiMon_khac');
    const container = document.getElementById('univ_ingredients_container');

    // Reset Lỗi cũ
    form.querySelectorAll('.error-msg').forEach(e => e.remove());
    form.querySelectorAll('.border-rose-500').forEach(e => {
        e.classList.remove('border-rose-500', 'bg-rose-50');
        e.classList.add('border-[#e2e3e1]');
    });

    if (action === 'ADD') {
        title.innerHTML = '<i data-lucide="plus-circle" class="w-5 h-5 text-[#553722]"></i> Thêm món mới';
        btnText.innerText = 'Lưu món mới';
        form.action = form.getAttribute('data-action-add');

        fId.value = '';
        fName.value = '';
        fPriceDisp.value = '';
        fPriceReal.value = '0';
        fCatSel.value = '';
        toggleLoaiMonKhac();
        container.innerHTML = '';

        // Mở sẵn 1 dòng nguyên liệu trống
        addIngredientRow('univ_ingredients_container');

    } else if (action === 'EDIT') {
        title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5 text-[#553722]"></i> Cập nhật món';
        btnText.innerText = 'Lưu thay đổi';
        form.action = form.getAttribute('data-action-edit');

        fId.value = id;
        fName.value = name;
        fPriceDisp.value = price;
        formatCurrency(fPriceDisp, 'univ_giaTienHienTai');

        // Cấu hình lại Input Loại Món
        fCatSel.name = 'loaiMon';
        fCatKhac.removeAttribute('name');
        fCatKhac.classList.add('hidden');
        fCatKhac.value = '';

        if (category && category !== 'null') {
            let optionExists = Array.from(fCatSel.options).some(opt => opt.value === category);
            if (optionExists && category !== 'Khác') {
                fCatSel.value = category;
                toggleLoaiMonKhac();
            } else {
                fCatSel.value = 'Khác';
                toggleLoaiMonKhac();
                fCatKhac.value = category === 'Khác' ? '' : category;
            }
        } else {
            fCatSel.value = '';
            toggleLoaiMonKhac();
        }

        // Nạp công thức từ Database thông qua API
        container.innerHTML = '<span class="text-xs italic text-gray-500 flex items-center gap-1"><i data-lucide="loader-2" class="w-3 h-3 animate-spin"></i> Đang tải công thức...</span>';
        if (typeof lucide !== 'undefined') lucide.createIcons();

        fetch('/api/menu/ingredients/' + id)
            .then(res => res.json())
            .then(data => {
            container.innerHTML = '';
            if(data.length === 0) {
                addIngredientRow('univ_ingredients_container');
            } else {
                data.forEach(item => {
                    // Gọi API trả về data chuẩn xác tên đơn vị (ĐVSD hoặc ĐVT)
                    addIngredientRow('univ_ingredients_container', item.maHangHoa, item.tenHangHoa, item.khoiLuong, item.donViTinh || '-');
                });
            }
        })
            .catch(error => {
            console.error('Lỗi API Công thức:', error);
            container.innerHTML = '<span class="text-rose-500 text-[11px] font-bold italic">Không thể tải cấu hình nguyên liệu, vui lòng F5.</span>';
        });
    }

    if (typeof lucide !== 'undefined') lucide.createIcons();
    modal.classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_maThucDon').value = id;
    document.getElementById('delete_monName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}

// -----------------------------------------------------
// Bắt sự kiện Validate mượt mà cho form (tránh pop-up gốc)
// -----------------------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll('input[required]:not([disabled]), select[required]:not([disabled])');

            // Quét sạch TẤT CẢ thông báo lỗi và thu gọn ô trắng về kích thước cũ
            form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
            form.querySelectorAll('.ingredient-row').forEach(row => {
                row.style.paddingBottom = ''; // Thu hồi phần dãn nở
            });

            inputs.forEach(function (input) {
                // Trả lại viền mặc định
                input.classList.remove('border-rose-500', 'bg-rose-50');

                let val = input.value ? input.value.trim() : '';

                // Bắt lỗi: Nếu trống
                if (!val) {
                    isValid = false;
                    if (input.classList.contains('ingredient-hidden-id')) {
                        let searchInput = input.closest('.custom-combobox').querySelector('.ingredient-search');
                        searchInput.classList.remove('border-[#e2e3e1]');
                        searchInput.classList.add('border-rose-500', 'bg-rose-50');
                        showError(searchInput, 'Vui lòng chọn từ danh sách.');
                    } else if (input.type !== 'hidden') {
                        input.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]');
                        input.classList.add('border-rose-500', 'bg-rose-50');
                        showError(input, 'Trường này không được bỏ trống.');
                    }
                }
                // Bắt lỗi: Nếu là ô số lượng nguyên liệu mà <= 0
                else if (input.classList.contains('ingredient-qty')) {
                    if (isNaN(val) || parseFloat(val) <= 0) {
                        isValid = false;
                        input.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]');
                        input.classList.add('border-rose-500', 'bg-rose-50');
                        showError(input, 'Số lượng phải > 0');
                    }
                }
            });

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') lucide.createIcons();
            }
        });

        // Tẩy xóa màu báo lỗi ngay khi người dùng gõ/chọn lại
        form.addEventListener('input', function(e) {
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'SELECT') {
                e.target.classList.remove('border-rose-500', 'bg-rose-50');
                e.target.classList.add('border-[#e2e3e1]');

                let row = e.target.closest('.ingredient-row');
                if (row) {
                    let search = row.querySelector('.ingredient-search');
                    if (search) {
                        search.classList.remove('border-rose-500', 'bg-rose-50');
                        search.classList.add('border-[#e2e3e1]');
                    }
                }

                // Dọn rác lỗi và dọn dẹp khoảng trống thừa
                form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
                form.querySelectorAll('.ingredient-row').forEach(r => {
                    r.style.paddingBottom = ''; // Thu hồi phần dãn nở
                });
            }
        });
    });

    // Hàm tiện ích in thông báo chữ đỏ
    function showError(inputElement, message) {
        // ĐỐI VỚI VÙNG NGUYÊN LIỆU (Thành phần & Công thức)
        if (inputElement.classList.contains('ingredient-qty') || inputElement.classList.contains('ingredient-search')) {

            // Tìm đúng wrapper của từng ô
            let wrapper = inputElement.closest('.custom-combobox') || inputElement.parentElement;

            // Thả thẻ lỗi dạng Absolute bám chặt vào đáy
            if (wrapper) {
                wrapper.classList.add('relative');
                // Sửa thành w-max để thẻ mở rộng ôm trọn text lỗi
                let errorHtml = `<span class="error-msg absolute top-full left-1 mt-1 text-[11px] text-rose-500 font-bold flex items-center gap-1 z-10 w-max"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> ${message}</span>`;
                wrapper.insertAdjacentHTML('beforeend', errorHtml);
            }

            // MỞ RỘNG BIÊN TRONG CỦA Ô TRẮNG XUỐNG DƯỚI ĐỂ ÔM LẤY CHỮ
            let row = inputElement.closest('.ingredient-row');
            if (row) {
                row.style.paddingBottom = '2.5rem'; // Khoảng 40px dãn thêm bên trong để chứa thẻ lỗi
            }
        }
        // ĐỐI VỚI CÁC Ô FORM BÌNH THƯỜNG (Tên món, Giá bán...)
        else {
            let errorHtml = `<span class="error-msg text-[11px] text-rose-500 font-bold flex items-center gap-1 mt-1.5 ml-1"><i data-lucide="info" class="w-3.5 h-3.5"></i> ${message}</span>`;
            if (inputElement.parentElement.classList.contains('relative')) {
                inputElement.parentElement.insertAdjacentHTML('afterend', errorHtml);
            } else {
                inputElement.insertAdjacentHTML('afterend', errorHtml);
            }
        }
    }
});
/*]]>*/