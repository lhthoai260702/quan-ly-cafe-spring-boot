/*<![CDATA[*/
// Hàm format tiền tệ
function formatCurrency(inputElement, hiddenInputId) {
    let value = inputElement.value.replace(/\D/g, '');
    if (value === '') {
        inputElement.value = '';
        document.getElementById(hiddenInputId).value = '';
        return;
    }
    inputElement.value = parseInt(value, 10).toLocaleString('en-US');
    document.getElementById(hiddenInputId).value = value;
}

// Logic ẩn/hiện Ô nhập Loại món mới
function toggleLoaiMonKhac(mode) {
    const selectEl = document.getElementById(mode + '_loaiMon_select');
    const inputEl = document.getElementById(mode + '_loaiMon_khac');

    if (selectEl.value === 'Khác') {
        // Hiện ô nhập tay
        inputEl.classList.remove('hidden');
        inputEl.required = true;

        // MẸO: Chuyển name='loaiMon' từ select sang input để Backend đọc dữ liệu mới gõ
        inputEl.name = 'loaiMon';
        selectEl.removeAttribute('name');
    } else {
        // Ẩn ô nhập tay
        inputEl.classList.add('hidden');
        inputEl.required = false;
        inputEl.value = ''; // Xoá dữ liệu thừa

        // Trả name='loaiMon' về lại cho thẻ Select
        selectEl.name = 'loaiMon';
        inputEl.removeAttribute('name');
    }
}

// Logic Accordion: Sổ xuống danh sách nguyên liệu của dòng
function toggleIngredientRow(maThucDon) {
    const row = document.getElementById('ingredient-view-' + maThucDon);
    const contentDiv = document.getElementById('ingredient-content-' + maThucDon);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');
        if (contentDiv.innerHTML.includes('Đang tải')) {
            fetch('/api/menu/ingredients/' + maThucDon)
                .then(res => res.json())
                .then(data => {
                if(data.length === 0) {
                    contentDiv.innerHTML = '<span class="text-rose-500">Món này chưa thiết lập công thức nguyên liệu.</span>';
                } else {
                    let html = '<ul class="list-disc list-inside space-y-1">';
                    data.forEach(item => {
                        html += `<li><b>${item.tenHangHoa}</b>: ${item.khoiLuong} ${item.donViTinh || ''}</li>`;
                    });
                    html += '</ul>';
                    contentDiv.innerHTML = html;
                }
            });
        }
    } else {
        row.classList.add('hidden');
    }
}

// Custom Searchable Combobox Logic
function filterCombobox(input) {
    const filter = input.value.toLowerCase().trim();
    const combobox = input.closest('.custom-combobox');
    const list = combobox.querySelector('.combobox-list');
    const items = list.querySelectorAll('.combobox-item');
    const noMatchMsg = list.querySelector('.no-match-msg');

    let hasMatch = false;

    items.forEach(li => {
        const name = li.getAttribute('data-name').toLowerCase();
        if (name.includes(filter)) {
            li.style.display = "";
            hasMatch = true;
        } else {
            li.style.display = "none";
        }
    });

    if (!hasMatch && filter !== "") {
        noMatchMsg.classList.remove('hidden');
        noMatchMsg.style.display = "flex";
        items.forEach(li => li.style.display = "");
    } else {
        noMatchMsg.classList.add('hidden');
        noMatchMsg.style.display = "none";
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

    searchInput.value = li.getAttribute('data-name');
    hiddenId.value = li.getAttribute('data-id');
    combobox.querySelector('.combobox-list').classList.add('hidden');

    searchInput.classList.remove("border-rose-500", "bg-rose-50");
    let errorMsg = searchInput.parentElement.parentElement.querySelector(".error-msg");
    if (errorMsg) errorMsg.remove();
}

// JS: Thêm dòng nguyên liệu động
function addIngredientRow(containerId, maHangHoa = '', tenHangHoa = '', khoiLuong = '') {
    const container = document.getElementById(containerId);
    const template = document.getElementById('ingredientRowTemplate');
    const clone = template.content.cloneNode(true);

    if(maHangHoa !== '') {
        clone.querySelector('.ingredient-hidden-id').value = maHangHoa;
        clone.querySelector('.ingredient-search').value = tenHangHoa;
        clone.querySelector('.ingredient-qty').value = khoiLuong;
    }

    container.appendChild(clone);
    if(typeof lucide !== 'undefined') lucide.createIcons();
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

// Logic Mở Edit Modal
function openEditModal(id, name, price, category) {
    document.getElementById('edit_maThucDon').value = id;
    document.getElementById('edit_tenMon').value = name;

    let displayInput = document.getElementById('edit_giaTien_display');
    displayInput.value = price;
    formatCurrency(displayInput, 'edit_giaTienHienTai');

    // Xử lý thông minh cho Loại Món
    let categorySelect = document.getElementById('edit_loaiMon_select');
    let categoryInput = document.getElementById('edit_loaiMon_khac');

    // Reset state chuẩn bị dữ liệu
    categorySelect.name = 'loaiMon';
    categoryInput.removeAttribute('name');
    categoryInput.classList.add('hidden');
    categoryInput.value = '';

    if (category && category !== 'null') {
        // Kiểm tra xem database trả về có trùng với các loại có sẵn trong Select không
        let optionExists = Array.from(categorySelect.options).some(opt => opt.value === category);

        if (optionExists && category !== 'Khác') {
            // Trùng loại đã có
            categorySelect.value = category;
            toggleLoaiMonKhac('edit');
        } else {
            // Nếu là loại món mới (hoặc Database ghi chữ Khác)
            categorySelect.value = 'Khác';
            toggleLoaiMonKhac('edit');
            categoryInput.value = category === 'Khác' ? '' : category;
        }
    } else {
        categorySelect.value = '';
        toggleLoaiMonKhac('edit');
    }

    // Gọi AJAX lấy danh sách nguyên liệu và tự vẽ row
    const container = document.getElementById('edit_ingredients_container');
    container.innerHTML = '<span class="text-xs italic text-gray-500">Đang tải công thức...</span>';

    fetch('/api/menu/ingredients/' + id)
        .then(res => res.json())
        .then(data => {
        container.innerHTML = '';
        data.forEach(item => {
            addIngredientRow('edit_ingredients_container', item.maHangHoa, item.tenHangHoa, item.khoiLuong);
        });
    });

    document.getElementById('editModal').classList.remove('hidden');
}

function openDeleteModal(id, name) {
    document.getElementById('delete_maThucDon').value = id;
    document.getElementById('delete_monName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}
/*]]>*/