document.addEventListener('DOMContentLoaded', async () => {
    await loadReturnHistory();
});

async function loadReturnHistory() {
    showLoading('returnHistory');

    try {
        const result = await API.getReturnedBooks();
        const historyDiv = document.getElementById('returnHistory');

        if (result.success && result.data.length > 0) {
            const recentReturns = result.data.slice(0, 10); // Show 10 most recent

            let html = '';
            recentReturns.forEach(record => {
                html += `
                    <div class="borrow-item">
                        <h4>${record.bookTitle}</h4>
                        <p><strong>Borrower:</strong> ${record.borrowerName}</p>
                        <p><strong>Code:</strong> ${formatBorrowCode(record.borrowCode)}</p>
                        <p><strong>Borrowed:</strong> ${formatDate(record.borrowDate)}</p>
                        <p><strong>Returned:</strong> ${formatDate(record.returnDate)}</p>
                    </div>
                `;
            });
            historyDiv.innerHTML = html;
        } else {
            historyDiv.innerHTML = '<p class="loading">No return history</p>';
        }
    } catch (error) {
        console.error('Error loading return history:', error);
        showError('returnHistory', 'Error loading data');
    }
}

async function validateCode() {
    const borrowCode = document.getElementById('borrowCode').value.trim();

    if (!borrowCode) {
        alert('Please enter a borrow code');
        return;
    }

    if (borrowCode.length !== 5 || !/^\d+$/.test(borrowCode)) {
        alert('Borrow code must be exactly 5 digits');
        return;
    }

    const validationDiv = document.getElementById('codeValidation');
    validationDiv.innerHTML = '<p class="loading">Validating code...</p>';
    validationDiv.style.display = 'block';

    try {
        const result = await API.validateBorrowCode(borrowCode);

        if (result.success) {
            const record = result.data;

            if (result.canReturn) {
                validationDiv.className = 'result-box success';
                validationDiv.innerHTML = `
                    <h3>✓ Valid Code</h3>
                    <p><strong>Book:</strong> ${record.bookTitle}</p>
                    <p><strong>Borrower:</strong> ${record.borrowerName}</p>
                    <p><strong>Borrowed on:</strong> ${formatDate(record.borrowDate)}</p>
                    <p style="color: #065f46; margin-top: 1rem;">
                        This book can be returned. Click "Return Book" button below.
                    </p>
                `;
            } else {
                validationDiv.className = 'result-box error';
                validationDiv.innerHTML = `
                    <h3>⚠️ Already Returned</h3>
                    <p>${result.message}</p>
                    <p><strong>Book:</strong> ${record.bookTitle}</p>
                    <p><strong>Returned on:</strong> ${formatDate(record.returnDate)}</p>
                `;
            }
        } else {
            validationDiv.className = 'result-box error';
            validationDiv.innerHTML = `
                <h3>✗ Invalid Code</h3>
                <p>${result.error || 'Borrow code not found'}</p>
            `;
        }
    } catch (error) {
        console.error('Error validating code:', error);
        validationDiv.className = 'result-box error';
        validationDiv.innerHTML = `
            <h3>✗ Error</h3>
            <p>Error connecting to server</p>
        `;
    }
}

async function handleReturn(event) {
    event.preventDefault();

    const borrowCode = document.getElementById('borrowCode').value.trim();

    if (!borrowCode) {
        alert('Please enter a borrow code');
        return;
    }

    if (borrowCode.length !== 5 || !/^\d+$/.test(borrowCode)) {
        alert('Borrow code must be exactly 5 digits');
        return;
    }

    if (!confirm(`Are you sure you want to return the book with code ${borrowCode}?`)) {
        return;
    }

    const submitBtn = event.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Processing...';

    const resultDiv = document.getElementById('returnResult');
    resultDiv.innerHTML = '<p class="loading">Processing return...</p>';
    resultDiv.style.display = 'block';

    try {
        const result = await API.returnBook(borrowCode);

        if (result.success) {
            resultDiv.className = 'result-box success';
            resultDiv.innerHTML = `
                <h3>✓ Book Returned Successfully!</h3>
                <p>${result.message}</p>
                <div style="margin-top: 1rem; padding: 1rem; background: #f3f4f6; border-radius: 4px;">
                    <p><strong>Book:</strong> ${result.bookTitle}</p>
                    <p><strong>Borrower:</strong> ${result.borrowerName}</p>
                    <p><strong>Borrowed:</strong> ${formatDate(result.borrowDate)}</p>
                    <p><strong>Returned:</strong> ${formatDate(result.returnDate)}</p>
                    <p><strong>Code:</strong> ${formatBorrowCode(result.borrowCode)}</p>
                </div>
                <p style="margin-top: 1rem;">Thank you for using our library!</p>
            `;

            // Reset form
            document.getElementById('returnForm').reset();
            document.getElementById('codeValidation').style.display = 'none';

            // Reload return history
            await loadReturnHistory();
        } else {
            resultDiv.className = 'result-box error';
            resultDiv.innerHTML = `
                <h3>✗ Return Failed</h3>
                <p>${result.error || 'Failed to return book'}</p>
                <p style="margin-top: 0.5rem;">Please check your borrow code and try again.</p>
            `;
        }
    } catch (error) {
        console.error('Error returning book:', error);
        resultDiv.className = 'result-box error';
        resultDiv.innerHTML = `
            <h3>✗ Error</h3>
            <p>Error connecting to server. Please try again.</p>
        `;
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '🔄 Return Book';
    }
}

// Auto-format borrow code input (numbers only)
document.getElementById('borrowCode')?.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '');
});
