/*<![CDATA[*/

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

function formatQuantity(input) {
    let val = input.value;
    val = val.replace(/,/g, '.');
    val = val.replace(/[^0-9.]/g, '');
    const parts = val.split('.');
    if (parts.length > 2) {
        val = parts[0] + '.' + parts.slice(1).join('');
    }
    input.value = val;
}

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

function handleInputCombobox(input) {
    const combobox = input.closest('.custom-combobox');
    const hiddenId = combobox.querySelector('.ingredient-hidden-id');
    const unitSpan = input.closest('.ingredient-row').querySelector('.ingredient-unit');

    hiddenId.value = '';
    unitSpan.innerText = '-';

    filterCombobox(input);
}

function clearCombobox(btn) {
    const combobox = btn.closest('.custom-combobox');
    const searchInput = combobox.querySelector('.ingredient-search');

    searchInput.value = '';
    handleInputCombobox(searchInput);

    openCombobox(searchInput);
    searchInput.focus();
}

function filterCombobox(input) {
    const filter = input.value.toLowerCase().trim();
    const combobox = input.closest('.custom-combobox');
    const list = combobox.querySelector('.combobox-list');
    const items = list.querySelectorAll('.combobox-item');
    const noMatchMsg = list.querySelector('.no-match-msg');

    // 🚀 BƯỚC 1: Thu thập tất cả các ID nguyên liệu đã được chọn ở CÁC DÒNG KHÁC
    const currentHiddenInput = combobox.querySelector('.ingredient-hidden-id');
    const selectedIds = new Set();
    document.querySelectorAll('#univ_ingredients_container .ingredient-hidden-id').forEach(hiddenInput => {
        // Nếu ô đó có giá trị và KHÔNG PHẢI là ô hiện tại đang thao tác
        if (hiddenInput.value && hiddenInput !== currentHiddenInput) {
            selectedIds.add(hiddenInput.value);
        }
    });

    let hasMatch = false;

    // 🚀 BƯỚC 2: Vòng lặp kiểm tra từng món trong danh sách đổ xuống
    items.forEach(li => {
        const name = li.getAttribute('data-name').toLowerCase();
        const id = li.getAttribute('data-id');

        // Nếu nguyên liệu này đã được chọn ở dòng khác -> Ẩn nó đi ngay lập tức
        if (selectedIds.has(id)) {
            li.style.display = 'none';
        }
        // Nếu chưa được chọn và tên khớp với từ khóa tìm kiếm -> Hiển thị
        else if (name.includes(filter)) {
            li.style.display = '';
            hasMatch = true;
        }
        // Không khớp từ khóa -> Ẩn
        else {
            li.style.display = 'none';
        }
    });

    // 🚀 BƯỚC 3: Xử lý thông báo khi danh sách trống
    if (!hasMatch) {
        // Nếu người dùng chưa gõ tìm kiếm mà danh sách trống trơn -> Báo là đã chọn hết
        if (filter === '') {
            noMatchMsg.innerHTML = '<i data-lucide="info" class="w-3 h-3"></i> Đã chọn hết nguyên liệu kho';
        } else {
            noMatchMsg.innerHTML = '<i data-lucide="info" class="w-3 h-3"></i> Không tìm thấy';
        }

        noMatchMsg.classList.remove('hidden');
        noMatchMsg.style.display = 'flex';
        if (typeof lucide !== 'undefined') lucide.createIcons(); // Render lại icon

        items.forEach(li => li.style.display = 'none');
    } else {
        noMatchMsg.classList.add('hidden');
        noMatchMsg.style.display = 'none';
    }
}

function openCombobox(input) {
    const combobox = input.closest('.custom-combobox');
    const list = combobox.querySelector('.combobox-list');

    list.classList.remove('hidden');
    filterCombobox(input);

    // 🚀 BÍ QUYẾT: Tính toán tọa độ và dùng 'fixed' để thoát khỏi overflow của Popup
    const rect = input.getBoundingClientRect();
    list.style.position = 'fixed';
    list.style.left = rect.left + 'px';
    list.style.width = rect.width + 'px';
    list.style.zIndex = '99999';

    // 🚀 NÂNG CẤP: Nếu danh sách dài chạm đáy màn hình, tự động lật ngược lên trên!
    const dropdownHeight = 192; // Tương đương max-h-48 (12rem = 192px / Khoảng 5 món)
    if (rect.bottom + dropdownHeight > window.innerHeight) {
        list.style.top = 'auto';
        list.style.bottom = (window.innerHeight - rect.top + 4) + 'px';
        list.classList.remove('mt-1');
        list.classList.add('mb-1', 'shadow-[0_-10px_40px_-10px_rgba(0,0,0,0.15)]'); // Đổ bóng ngược
    } else {
        list.style.bottom = 'auto';
        list.style.top = (rect.bottom + 4) + 'px';
        list.classList.remove('mb-1', 'shadow-[0_-10px_40px_-10px_rgba(0,0,0,0.15)]');
        list.classList.add('mt-1');
    }
}

// Đóng combobox nếu người dùng lăn chuột cuộn form (để tránh danh sách bị bay lơ lửng sai vị trí)
document.getElementById('universalForm').addEventListener('scroll', function() {
    const activeInput = document.activeElement;
    if (activeInput && activeInput.classList.contains('ingredient-search')) {
        activeInput.blur(); // Tự động đóng danh sách khi cuộn
    }
});

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

    searchInput.classList.remove('border-rose-500', 'bg-rose-50');
    let form = combobox.closest('form');
    if (form) {
        form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
        form.querySelectorAll('.ingredient-row').forEach(r => r.style.paddingBottom = '');
    }
}

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

function openUniversalModal(action, id, name, price, category) {
    const form = document.getElementById('universalForm');
    const modal = document.getElementById('universalModal');
    const title = document.getElementById('univ_title');
    const btnText = document.getElementById('univ_btn_text');

    const fId = document.getElementById('univ_maThucDon');
    const fName = document.getElementById('univ_tenMon');
    const fPriceDisp = document.getElementById('univ_giaTien_display');
    const fPriceReal = document.getElementById('univ_giaTienHienTai');
    const fCatSel = document.getElementById('univ_loaiMon_select');
    const fCatKhac = document.getElementById('univ_loaiMon_khac');
    const container = document.getElementById('univ_ingredients_container');

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
        fId.disabled = true;

        fName.value = '';
        fPriceDisp.value = '';
        fPriceReal.value = '0';
        fCatSel.value = '';
        toggleLoaiMonKhac();
        container.innerHTML = '';

        addIngredientRow('univ_ingredients_container');

    } else if (action === 'EDIT') {
        title.innerHTML = '<i data-lucide="pen-line" class="w-5 h-5 text-[#553722]"></i> Cập nhật món';
        btnText.innerText = 'Lưu thay đổi';
        form.action = form.getAttribute('data-action-edit');

        fId.value = id;
        fId.disabled = false;

        fName.value = name;
        fPriceDisp.value = price;
        formatCurrency(fPriceDisp, 'univ_giaTienHienTai');

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

document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('.custom-validate-form');

    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let isValid = true;

            form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
            form.querySelectorAll('.ingredient-row').forEach(row => {
                row.style.paddingBottom = '';
            });

            const basicInputs = form.querySelectorAll('input[required]:not([disabled]), select[required]:not([disabled])');
            basicInputs.forEach(function (input) {
                input.classList.remove('border-rose-500', 'bg-rose-50');
                let val = input.value ? input.value.trim() : '';

                if (!val) {
                    isValid = false;
                    input.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]');
                    input.classList.add('border-rose-500', 'bg-rose-50');
                    showError(input, 'Trường này không được bỏ trống.');
                }
            });

            const ingredientRows = form.querySelectorAll('.ingredient-row');
            ingredientRows.forEach(function (row) {
                const hiddenIdInput = row.querySelector('.ingredient-hidden-id');
                const searchInput = row.querySelector('.ingredient-search');
                const qtyInput = row.querySelector('.ingredient-qty');

                searchInput.classList.remove('border-rose-500', 'bg-rose-50');
                qtyInput.classList.remove('border-rose-500', 'bg-rose-50');

                let idVal = hiddenIdInput.value ? hiddenIdInput.value.trim() : '';
                let qtyVal = qtyInput.value ? qtyInput.value.trim() : '';
                let searchVal = searchInput.value ? searchInput.value.trim() : '';

                if (!idVal && !searchVal && !qtyVal) {
                }
                else if (!idVal && searchVal !== '') {
                    isValid = false;
                    searchInput.classList.remove('border-[#e2e3e1]');
                    searchInput.classList.add('border-rose-500', 'bg-rose-50');
                    showError(searchInput, 'Vui lòng chọn từ danh sách.');
                }
                else if (idVal && !qtyVal) {
                    isValid = false;
                    qtyInput.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]');
                    qtyInput.classList.add('border-rose-500', 'bg-rose-50');
                    showError(qtyInput, 'Vui lòng nhập số lượng.');
                }
                else if (!idVal && qtyVal) {
                    isValid = false;
                    searchInput.classList.remove('border-[#e2e3e1]');
                    searchInput.classList.add('border-rose-500', 'bg-rose-50');
                    showError(searchInput, 'Vui lòng chọn nguyên liệu.');
                }
                else if (idVal && qtyVal) {
                    if (isNaN(qtyVal) || parseFloat(qtyVal) <= 0 || qtyVal === '.') {
                        isValid = false;
                        qtyInput.classList.remove('border-[#e2e3e1]', 'focus:border-[#553722]');
                        qtyInput.classList.add('border-rose-500', 'bg-rose-50');
                        showError(qtyInput, 'Số lượng phải > 0');
                    }
                }
            });

            if (!isValid) {
                e.preventDefault();
                if (typeof lucide !== 'undefined') lucide.createIcons();
            } else {
                ingredientRows.forEach(function (row) {
                    const hiddenIdInput = row.querySelector('.ingredient-hidden-id');
                    const qtyInput = row.querySelector('.ingredient-qty');
                    if (!hiddenIdInput.value.trim() && !qtyInput.value.trim()) {
                        row.remove();
                    }
                });
                updateIngredientNames('univ_ingredients_container');
            }
        });

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

                form.querySelectorAll('.error-msg').forEach(msg => msg.remove());
                form.querySelectorAll('.ingredient-row').forEach(r => {
                    r.style.paddingBottom = '';
                });
            }
        });
    });

    function showError(inputElement, message) {
        if (inputElement.classList.contains('ingredient-qty') || inputElement.classList.contains('ingredient-search')) {

            let wrapper = inputElement.closest('.custom-combobox') || inputElement.parentElement;

            if (wrapper) {
                wrapper.classList.add('relative');
                let errorHtml = `<span class="error-msg absolute top-full left-1 mt-1 text-[11px] text-rose-500 font-bold flex items-center gap-1 z-10 w-max"><i data-lucide="info" class="w-3.5 h-3.5 shrink-0"></i> ${message}</span>`;
                wrapper.insertAdjacentHTML('beforeend', errorHtml);
            }

            let row = inputElement.closest('.ingredient-row');
            if (row) {
                row.style.paddingBottom = '2.5rem';
            }
        }
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