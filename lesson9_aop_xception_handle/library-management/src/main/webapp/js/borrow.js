let availableBooks = [];

document.addEventListener('DOMContentLoaded', async () => {
    await loadAvailableBooks();
    await loadCurrentBorrows();

    // Setup book select change handler
    document.getElementById('bookSelect').addEventListener('change', updateBookInfo);
});

async function loadAvailableBooks() {
    try {
        const result = await API.getAllBooks();

        if (result.success) {
            availableBooks = result.data.filter(book => book.availableQuantity > 0);

            const select = document.getElementById('bookSelect');
            select.innerHTML = '<option value="">-- Select a book --</option>';

            availableBooks.forEach(book => {
                const option = document.createElement('option');
                option.value = book.bookId;
                option.textContent = `${book.title} (${book.availableQuantity} available)`;
                option.dataset.title = book.title;
                option.dataset.author = book.author;
                option.dataset.available = book.availableQuantity;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading books:', error);
    }
}

async function loadCurrentBorrows() {
    showLoading('currentBorrows');

    try {
        const result = await API.getCurrentBorrows();
        const borrowsDiv = document.getElementById('currentBorrows');

        if (result.success && result.data.length > 0) {
            let html = '';
            result.data.forEach(borrow => {
                html += `
                    <div class="borrow-item">
                        <h4>${borrow.bookTitle}</h4>
                        <p><strong>Người mượn:</strong> ${borrow.borrowerName}</p>
                        <p><strong>Mã:</strong> ${formatBorrowCode(borrow.borrowCode)}</p>
                        <p><strong>Ngày mượn:</strong> ${formatDate(borrow.borrowDate)}</p>
                    </div>
                `;
            });
            borrowsDiv.innerHTML = html;
        } else {
            borrowsDiv.innerHTML = '<p class="loading">Không có sách đang được mượn</p>';
        }
    } catch (error) {
        console.error('Lỗi tải danh sách:', error);
        showError('currentBorrows', 'Lỗi tải dữ liệu');
    }
}

function updateBookInfo() {
    const select = document.getElementById('bookSelect');
    const selectedOption = select.options[select.selectedIndex];

    if (select.value) {
        document.getElementById('selectedTitle').textContent = selectedOption.dataset.title;
        document.getElementById('selectedAuthor').textContent = selectedOption.dataset.author;
        document.getElementById('selectedAvailable').textContent = selectedOption.dataset.available;
        document.getElementById('bookInfo').style.display = 'block';
    } else {
        document.getElementById('bookInfo').style.display = 'none';
    }
}

async function handleBorrow(event) {
    event.preventDefault();

    const bookId = document.getElementById('bookSelect').value;
    const borrowerName = document.getElementById('borrowerName').value.trim();

    if (!bookId) {
        alert('Vui lòng chọn sách');
        return;
    }

    if (!borrowerName) {
        alert('Vui lòng nhập tên của bạn');
        return;
    }

    const submitBtn = event.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang xử lý...';

    try {
        const result = await API.borrowBook(bookId, borrowerName);

        const resultDiv = document.getElementById('borrowResult');

        if (result.success) {
            resultDiv.className = 'result-box success';
            resultDiv.innerHTML = `
                <h3>✓ Mượn Thành Công!</h3>
                <p>${result.message}</p>
                <p><strong>Mã Mượn Của Bạn:</strong> <span style="font-size: 1.5rem; font-weight: bold; color: #2563eb;">${result.borrowCode}</span></p>
                <p style="color: #991b1b; margin-top: 1rem;">
                    ⚠️ Vui lòng lưu mã này! Bạn sẽ cần nó để trả sách.
                </p>
            `;
            resultDiv.style.display = 'block';

            document.getElementById('borrowForm').reset();
            document.getElementById('bookInfo').style.display = 'none';

            await loadAvailableBooks();
            await loadCurrentBorrows();
        } else {
            resultDiv.className = 'result-box error';
            resultDiv.innerHTML = `
                <h3>✗ Lỗi</h3>
                <p>${result.error || 'Không thể mượn sách'}</p>
            `;
            resultDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Lỗi mượn sách:', error);
        const resultDiv = document.getElementById('borrowResult');
        resultDiv.className = 'result-box error';
        resultDiv.innerHTML = `
            <h3>✗ Lỗi</h3>
            <p>Lỗi kết nối máy chủ. Vui lòng thử lại.</p>
        `;
        resultDiv.style.display = 'block';
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '📖 Mượn Sách';
    }
}
