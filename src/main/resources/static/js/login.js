lucide.createIcons();

document.addEventListener("DOMContentLoaded", function () {
    const forms = document.querySelectorAll(".custom-validate-form");
    forms.forEach(form => {
        form.addEventListener("submit", function (e) {
            let isValid = true;
            const inputs = form.querySelectorAll("input, select, textarea");

            inputs.forEach(input => {
                let existingError = input.parentElement.nextElementSibling;
                if (existingError && existingError.classList.contains("error-msg")) {
                    existingError.remove();
                }

                input.classList.remove("border-rose-500", "bg-rose-50");

                if (!input.validity.valid) {
                    isValid = false;

                    input.classList.remove("border-gray-200");
                    input.classList.add("border-rose-500", "bg-rose-50");

                    let errorText = input.getAttribute("title") || "Vui lòng kiểm tra lại thông tin.";
                    if (input.validity.valueMissing) {
                        errorText = input.getAttribute("title") || "Trường này không được để trống.";
                    }

                    let errorHtml = `<span class="error-msg text-[10px] text-rose-500 font-semibold block mt-1.5 ml-1 flex items-center gap-1"><i data-lucide="info" class="w-3 h-3"></i> ${errorText}</span>`;
                    input.parentElement.insertAdjacentHTML("afterend", errorHtml);
                }
            });

            if (!isValid) {
                e.preventDefault();
                if(typeof lucide !== 'undefined') lucide.createIcons();

                // Logic mới: Ẩn thông báo server khi bấm submit mà form trống
                const errorAlert = document.getElementById('error-alert');
                if (errorAlert) errorAlert.classList.add('hidden');

                const logoutAlert = document.getElementById('logout-alert');
                if (logoutAlert) logoutAlert.classList.add('hidden');
            }
        });

        form.querySelectorAll("input, select, textarea").forEach(input => {
            input.addEventListener("input", function () {
                let existingError = input.parentElement.nextElementSibling;
                if (existingError && existingError.classList.contains("error-msg")) {
                    existingError.remove();
                }

                input.classList.remove("border-rose-500", "bg-rose-50");
                input.classList.add("border-gray-200");
            });
        });
    });
});