/*<![CDATA[*/

/**
 * Định dạng tiền tệ (thêm dấu phẩy phân cách hàng nghìn)
 * @param {HTMLElement} inputElement - Thẻ input hiển thị
 * @param {string} hiddenInputId - ID của thẻ input ẩn chứa giá trị thực
 */
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

/**
 * Ẩn/hiện ô nhập tay "Loại món khác" khi người dùng tương tác với Select
 * @param {string} mode - Chế độ thao tác ('add' hoặc 'edit')
 */
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

/**
 * Hiển thị/Ẩn danh sách nguyên liệu của một món ăn (Accordion)
 * @param {string|number} maThucDon - Mã thực đơn
 */
function toggleIngredientRow(maThucDon) {
    const row = document.getElementById('ingredient-view-' + maThucDon);
    const contentDiv = document.getElementById('ingredient-content-' + maThucDon);

    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');

        if (contentDiv.innerHTML.includes('Đang tải')) {
            fetch('/api/menu/ingredients/' + maThucDon)
                .then(function (res) {
                return res.json();
            })
                .then(function (data) {
                if (data.length === 0) {
                    contentDiv.innerHTML = '<span class="text-rose-500">Món này chưa thiết lập công thức nguyên liệu.</span>';
                } else {
                    let html = '<ul class="list-disc list-inside space-y-1">';
                    data.forEach(function (item) {
                        html += `<li><b>${item.tenHangHoa}</b>: ${item.khoiLuong} ${item.donViTinh || ''}</li>`;
                    });
                    html += '</ul>';
                    contentDiv.innerHTML = html;
                }
            })
                .catch(function (error) {
                console.error('Lỗi khi tải nguyên liệu:', error);
                contentDiv.innerHTML = '<span class="text-rose-500">Lỗi khi tải dữ liệu nguyên liệu.</span>';
            });
        }
    } else {
        row.classList.add('hidden');
    }
}

/**
 * Lọc dữ liệu hiển thị bên trong Custom Combobox
 * @param {HTMLElement} input - Thẻ input tìm kiếm của Combobox
 */
function filterCombobox(input) {
    const filter = input.value.toLowerCase().trim();
    const combobox = input.closest('.custom-combobox');
    const list = combobox.querySelector('.combobox-list');
    const items = list.querySelectorAll('.combobox-item');
    const noMatchMsg = list.querySelector('.no-match-msg');

    let hasMatch = false;

    items.forEach(function (li) {
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
        items.forEach(function (li) {
            li.style.display = '';
        });
    } else {
        noMatchMsg.classList.add('hidden');
        noMatchMsg.style.display = 'none';
    }
}

/**
 * Mở Custom Combobox
 * @param {HTMLElement} input - Thẻ input được focus
 */
function openCombobox(input) {
    const list = input.closest('.custom-combobox').querySelector('.combobox-list');
    list.classList.remove('hidden');
    filterCombobox(input);
}

/**
 * Đóng Custom Combobox (Có độ trễ để kịp nhận sự kiện click chuột)
 * @param {HTMLElement} input - Thẻ input bị mất focus (blur)
 */
function closeCombobox(input) {
    setTimeout(function () {
        const list = input.closest('.custom-combobox').querySelector('.combobox-list');
        if (list) {
            list.classList.add('hidden');
        }
    }, 200);
}

/**
 * Xử lý khi người dùng chọn một mục trong Custom Combobox
 * @param {HTMLElement} li - Thẻ <li> chứa dữ liệu được chọn
 */
function selectComboboxOption(li) {
    const combobox = li.closest('.custom-combobox');
    const searchInput = combobox.querySelector('.ingredient-search');
    const hiddenId = combobox.querySelector('.ingredient-hidden-id');

    searchInput.value = li.getAttribute('data-name');
    hiddenId.value = li.getAttribute('data-id');
    combobox.querySelector('.combobox-list').classList.add('hidden');

    searchInput.classList.remove('border-rose-500', 'bg-rose-50');

    let errorMsg = searchInput.parentElement.parentElement.querySelector('.error-msg');
    if (errorMsg) {
        errorMsg.remove();
    }
}

/**
 * Thêm một dòng (row) nhập nguyên liệu động vào giao diện
 * @param {string} containerId - ID của khung chứa các dòng nguyên liệu
 * @param {string|number} maHangHoa - Mã hàng hóa (Mặc định: rỗng)
 * @param {string} tenHangHoa - Tên hàng hóa hiển thị (Mặc định: rỗng)
 * @param {string|number} khoiLuong - Khối lượng định lượng (Mặc định: rỗng)
 */
function addIngredientRow(containerId, maHangHoa = '', tenHangHoa = '', khoiLuong = '') {
    const container = document.getElementById(containerId);
    const template = document.getElementById('ingredientRowTemplate');
    const clone = template.content.cloneNode(true);

    if (maHangHoa !== '') {
        clone.querySelector('.ingredient-hidden-id').value = maHangHoa;
        clone.querySelector('.ingredient-search').value = tenHangHoa;
        clone.querySelector('.ingredient-qty').value = khoiLuong;
    }

    container.appendChild(clone);

    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
    updateIngredientNames(containerId);
}

/**
 * Cập nhật lại chỉ số (index) của thuộc tính name cho các dòng nguyên liệu động
 * Để Spring Boot có thể nhận dạng dưới dạng List (Ví dụ: ingredients[0].maHangHoa)
 * @param {string} containerId - ID của khung chứa
 */
function updateIngredientNames(containerId) {
    const container = document.getElementById(containerId);
    const rows = container.querySelectorAll('.ingredient-row');

    rows.forEach(function (row, index) {
        const hiddenId = row.querySelector('.ingredient-hidden-id');
        const qtyInput = row.querySelector('.ingredient-qty');
        hiddenId.name = `ingredients[${index}].maHangHoa`;
        qtyInput.name = `ingredients[${index}].khoiLuong`;
    });
}

/**
 * Mở hộp thoại (Modal) sửa thông tin Thực Đơn
 * @param {string|number} id - Mã thực đơn
 * @param {string} name - Tên món
 * @param {string|number} price - Giá tiền
 * @param {string} category - Loại món
 */
function openEditModal(id, name, price, category) {
    document.getElementById('edit_maThucDon').value = id;
    document.getElementById('edit_tenMon').value = name;

    let displayInput = document.getElementById('edit_giaTien_display');
    displayInput.value = price;
    formatCurrency(displayInput, 'edit_giaTienHienTai');

    // Xử lý thông minh cho Loại Món
    let categorySelect = document.getElementById('edit_loaiMon_select');
    let categoryInput = document.getElementById('edit_loaiMon_khac');

    // Reset trạng thái mặc định
    categorySelect.name = 'loaiMon';
    categoryInput.removeAttribute('name');
    categoryInput.classList.add('hidden');
    categoryInput.value = '';

    if (category && category !== 'null') {
        // Kiểm tra xem database trả về có trùng với các loại có sẵn trong Select không
        let optionExists = Array.from(categorySelect.options).some(function (opt) {
            return opt.value === category;
        });

        if (optionExists && category !== 'Khác') {
            // Trùng loại đã có
            categorySelect.value = category;
            toggleLoaiMonKhac('edit');
        } else {
            // Nếu là loại món mới (hoặc Database ghi chữ 'Khác')
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
        .then(function (res) {
        return res.json();
    })
        .then(function (data) {
        container.innerHTML = '';
        data.forEach(function (item) {
            addIngredientRow('edit_ingredients_container', item.maHangHoa, item.tenHangHoa, item.khoiLuong);
        });
    })
        .catch(function (error) {
        console.error('Lỗi khi tải công thức:', error);
        container.innerHTML = '<span class="text-rose-500 text-xs italic">Không thể tải công thức nguyên liệu.</span>';
    });

    document.getElementById('editModal').classList.remove('hidden');
}

/**
 * Mở hộp thoại (Modal) xác nhận xóa món
 * @param {string|number} id - Mã thực đơn
 * @param {string} name - Tên món
 */
function openDeleteModal(id, name) {
    document.getElementById('delete_maThucDon').value = id;
    document.getElementById('delete_monName').textContent = name;
    document.getElementById('deleteModal').classList.remove('hidden');
}
/*]]>*/