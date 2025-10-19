// Custom JavaScript for Customer Manager
document.addEventListener('DOMContentLoaded', function() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // File input preview
    const fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        input.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const fileName = file.name;
                const label = this.nextElementSibling;
                if (label && label.classList.contains('form-text')) {
                    label.textContent = `Đã chọn: ${fileName}`;
                    label.className = 'form-text text-success';
                }
            }
        });
    });

    // Enhanced delete confirmation
    const deleteForms = document.querySelectorAll('form[action*="delete"]');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('BẠN CÓ CHẮC CHẮN MUỐN XÓA KHÁCH HÀNG NÀY?\n\nHành động này không thể hoàn tác!')) {
                e.preventDefault();
            }
        });
    });

    // Add loading state to buttons
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.form.checkValidity()) {
                const originalText = this.innerHTML;
                this.innerHTML = '<span class="loading me-2"></span> Đang xử lý...';
                this.disabled = true;

                // Revert after 5 seconds if still processing (safety)
                setTimeout(() => {
                    this.innerHTML = originalText;
                    this.disabled = false;
                }, 5000);
            }
        });
    });

    // Form validation enhancement
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const requiredFields = this.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('is-invalid');
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            if (!isValid) {
                e.preventDefault();
                this.querySelector('.is-invalid').focus();
            }
        });
    });
});
