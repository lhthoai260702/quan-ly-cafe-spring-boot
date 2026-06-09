/**
 * Mô phỏng quá trình sao lưu dữ liệu hệ thống
 */
function simulateBackup() {
    const btn = document.getElementById('btn-backup');
    const container = document.getElementById('backup-progress-container');
    const bar = document.getElementById('backup-progress-bar');
    const text = document.getElementById('backup-progress-text');

    btn.disabled = true;
    btn.classList.add('opacity-50', 'cursor-not-allowed');
    container.classList.remove('hidden');

    let progress = 0;

    const interval = setInterval(() => {
        progress += Math.floor(Math.random() * 10) + 5;

        if (progress >= 100) {
            progress = 100;
            clearInterval(interval);

            setTimeout(() => {
                alert('Sao lưu thành công! File đã được lưu vào máy tính của bạn.');
                container.classList.add('hidden');
                bar.style.width = '0%';
                text.innerText = '0%';
                btn.disabled = false;
                btn.classList.remove('opacity-50', 'cursor-not-allowed');
            }, 500);
        }

        bar.style.width = progress + '%';
        text.innerText = progress + '%';
    }, 300);
}

/**
 * Mô phỏng quá trình phục hồi dữ liệu từ file backup (.sql)
 */
function simulateRestore() {
    const fileInput = document.getElementById('restore-file');

    if (!fileInput.files.length) {
        alert('Vui lòng chọn một file (.sql) trước khi phục hồi!');
        return;
    }

    if (!confirm('CẢNH BÁO: Bạn có chắc chắn muốn ghi đè toàn bộ dữ liệu hệ thống? Thao tác này không thể hoàn tác!')) {
        return;
    }

    const btn = document.getElementById('btn-restore');
    const container = document.getElementById('restore-progress-container');
    const bar = document.getElementById('restore-progress-bar');
    const text = document.getElementById('restore-progress-text');

    btn.disabled = true;
    btn.classList.add('opacity-50', 'cursor-not-allowed');
    container.classList.remove('hidden');

    let progress = 0;

    const interval = setInterval(() => {
        progress += Math.floor(Math.random() * 8) + 2;

        if (progress >= 100) {
            progress = 100;
            clearInterval(interval);

            setTimeout(() => {
                alert('Phục hồi dữ liệu thành công! Hệ thống sẽ tự động tải lại.');
                location.reload();
            }, 600);
        }

        bar.style.width = progress + '%';
        text.innerText = progress + '%';
    }, 400);
}