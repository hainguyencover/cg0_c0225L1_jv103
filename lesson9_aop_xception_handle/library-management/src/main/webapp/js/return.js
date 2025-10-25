document.addEventListener('DOMContentLoaded', async () => {
    await loadReturnHistory();
});

async function loadReturnHistory() {
    showLoading('returnHistory');

    try {
        const result = await API.getReturnedBooks();
        const historyDiv = document.getElementById('returnHistory');

        if (result.success && result.data.length > 0) {
            const recentReturns = result.data.slice(0, 10);

            let html = '';
            recentReturns.forEach(record => {
                html += `
                    <div class="borrow-item">
                        <h4>${record.bookTitle}</h4>
                        <p><strong>Người mượn:</strong> ${record.borrowerName}</p>
                        <p><strong>Mã:</strong> ${formatBorrowCode(record.borrowCode)}</p>
                        <p><strong>Ngày mượn:</strong> ${formatDate(record.borrowDate)}</p>
                        <p><strong>Ngày trả:</strong> ${formatDate(record.returnDate)}</p>
                    </div>
                `;
            });
            historyDiv.innerHTML = html;
        } else {
            historyDiv.innerHTML = '<p class="loading">Không có lịch sử trả sách</p>';
        }
    } catch (error) {
        console.error('Lỗi tải lịch sử:', error);
        showError('returnHistory', 'Lỗi tải dữ liệu');
    }
}

async function validateCode() {
    const borrowCode = document.getElementById('borrowCode').value.trim();

    if (!borrowCode) {
        alert('Vui lòng nhập mã mượn');
        return;
    }

    if (borrowCode.length !== 5 || !/^\d+$/.test(borrowCode)) {
        alert('Mã mượn phải là 5 chữ số');
        return;
    }

    const validationDiv = document.getElementById('codeValidation');
    validationDiv.innerHTML = '<p class="loading">Đang kiểm tra mã...</p>';
    validationDiv.style.display = 'block';

    try {
        const result = await API.validateBorrowCode(borrowCode);

        if (result.success) {
            const record = result.data;

            if (result.canReturn) {
                validationDiv.className = 'result-box success';
                validationDiv.innerHTML = `
                    <h3>✓ Mã Hợp Lệ</h3>
                    <p><strong>Sách:</strong> ${record.bookTitle}</p>
                    <p><strong>Người mượn:</strong> ${record.borrowerName}</p>
                    <p><strong>Ngày mượn:</strong> ${formatDate(record.borrowDate)}</p>
                    <p style="color: #065f46; margin-top: 1rem;">
                        Sách này có thể được trả. Nhấn nút "Trả Sách" bên dưới.
                    </p>
                `;
            } else {
                validationDiv.className = 'result-box error';
                validationDiv.innerHTML = `
                    <h3>⚠️ Đã Trả Rồi</h3>
                    <p>${result.message}</p>
                    <p><strong>Sách:</strong> ${record.bookTitle}</p>
                    <p><strong>Ngày trả:</strong> ${formatDate(record.returnDate)}</p>
                `;
            }
        } else {
            validationDiv.className = 'result-box error';
            validationDiv.innerHTML = `
                <h3>✗ Mã Không Hợp Lệ</h3>
                <p>${result.error || 'Không tìm thấy mã mượn'}</p>
            `;
        }
    } catch (error) {
        console.error('Lỗi kiểm tra mã:', error);
        validationDiv.className = 'result-box error';
        validationDiv.innerHTML = `
            <h3>✗ Lỗi</h3>
            <p>Lỗi kết nối máy chủ</p>
        `;
    }
}

async function handleReturn(event) {
    event.preventDefault();

    const borrowCode = document.getElementById('borrowCode').value.trim();

    if (!borrowCode) {
        alert('Vui lòng nhập mã mượn');
        return;
    }

    if (borrowCode.length !== 5 || !/^\d+$/.test(borrowCode)) {
        alert('Mã mượn phải là 5 chữ số');
        return;
    }

    if (!confirm(`Bạn có chắc muốn trả sách với mã ${borrowCode}?`)) {
        return;
    }

    const submitBtn = event.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang xử lý...';

    const resultDiv = document.getElementById('returnResult');
    resultDiv.innerHTML = '<p class="loading">Đang xử lý trả sách...</p>';
    resultDiv.style.display = 'block';

    try {
        const result = await API.returnBook(borrowCode);

        if (result.success) {
            resultDiv.className = 'result-box success';
            resultDiv.innerHTML = `
                <h3>✓ Trả Sách Thành Công!</h3>
                <p>${result.message}</p>
                <div style="margin-top: 1rem; padding: 1rem; background: #f3f4f6; border-radius: 4px;">
                    <p><strong>Sách:</strong> ${result.bookTitle}</p>
                    <p><strong>Người mượn:</strong> ${result.borrowerName}</p>
                    <p><strong>Ngày mượn:</strong> ${formatDate(result.borrowDate)}</p>
                    <p><strong>Ngày trả:</strong> ${formatDate(result.returnDate)}</p>
                    <p><strong>Mã:</strong> ${formatBorrowCode(result.borrowCode)}</p>
                </div>
                <p style="margin-top: 1rem;">Cảm ơn bạn đã sử dụng thư viện!</p>
            `;

            document.getElementById('returnForm').reset();
            document.getElementById('codeValidation').style.display = 'none';

            await loadReturnHistory();
        } else {
            resultDiv.className = 'result-box error';
            resultDiv.innerHTML = `
                <h3>✗ Trả Sách Thất Bại</h3>
                <p>${result.error || 'Không thể trả sách'}</p>
                <p style="margin-top: 0.5rem;">Vui lòng kiểm tra mã mượn và thử lại.</p>
            `;
        }
    } catch (error) {
        console.error('Lỗi trả sách:', error);
        resultDiv.className = 'result-box error';
        resultDiv.innerHTML = `
            <h3>✗ Lỗi</h3>
            <p>Lỗi kết nối máy chủ. Vui lòng thử lại.</p>
        `;
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '🔄 Trả Sách';
    }
}

// Tự động format mã mượn (chỉ số)
document.getElementById('borrowCode')?.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '');
});
