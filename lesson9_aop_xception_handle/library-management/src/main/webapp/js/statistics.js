document.addEventListener('DOMContentLoaded', async () => {
    await loadAllStatistics();
});

async function loadAllStatistics() {
    await Promise.all([
        loadHealthReport(),
        loadTopBooks(),
        loadTopBorrowers(),
        loadCategoryStats(),
        loadLowStockBooks(),
        loadActivityStats()
    ]);
}

async function loadHealthReport() {
    showLoading('healthReport');

    try {
        const result = await API.getHealthReport();
        const reportDiv = document.getElementById('healthReport');

        if (result.success) {
            const data = result.data;

            let healthClass = 'excellent';
            let statusText = data.healthStatus;
            if (statusText.includes('Good')) {
                healthClass = 'good';
                statusText = 'Tốt - Tỷ lệ sẵn có vừa phải';
            } else if (statusText.includes('Excellent')) {
                statusText = 'Xuất sắc - Tỷ lệ sẵn có cao';
            } else if (statusText.includes('Fair')) {
                healthClass = 'fair';
                statusText = 'Khá - Tỷ lệ sẵn có thấp';
            } else if (statusText.includes('Poor')) {
                healthClass = 'poor';
                statusText = 'Kém - Tỷ lệ sẵn có rất thấp';
            }

            let html = `
                <div class="health-status ${healthClass}">
                    ${statusText}
                </div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-top: 1rem;">
                    <div>
                        <strong>Tổng đầu sách:</strong> ${data.totalBookTitles}
                    </div>
                    <div>
                        <strong>Tổng bản sao:</strong> ${data.totalCopies}
                    </div>
                    <div>
                        <strong>Còn lại:</strong> ${data.availableCopies}
                    </div>
                    <div>
                        <strong>Tỷ lệ sẵn có:</strong> ${data.availabilityRate}
                    </div>
                </div>
            `;

            if (data.booksNeedingRestock > 0) {
                html += `
                    <div style="margin-top: 1rem; padding: 1rem; background: #fef3c7; border-radius: 4px;">
                        <strong>⚠️ Sách cần nhập thêm:</strong> ${data.booksNeedingRestock}
                        <ul style="margin-top: 0.5rem; padding-left: 1.5rem;">
                            ${data.lowStockBooks.map(book => `<li>${book}</li>`).join('')}
                        </ul>
                    </div>
                `;
            }

            reportDiv.innerHTML = html;
        } else {
            showError('healthReport', 'Không thể tải báo cáo');
        }
    } catch (error) {
        console.error('Lỗi tải báo cáo:', error);
        showError('healthReport', 'Lỗi tải dữ liệu');
    }
}

async function loadTopBooks() {
    showLoading('topBooks');

    try {
        const result = await API.getTopBooks(10);
        const listDiv = document.getElementById('topBooks');

        if (result.success && result.data.length > 0) {
            let html = '';
            result.data.forEach((book, index) => {
                html += `
                    <div class="list-item">
                        <div>
                            <div class="title">${index + 1}. ${book.title}</div>
                            <div style="font-size: 0.85rem; color: #6b7280;">
                                của ${book.author} | ${book.category}
                            </div>
                        </div>
                        <span class="badge">${book.borrowCount} lượt</span>
                    </div>
                `;
            });
            listDiv.innerHTML = html;
        } else {
            listDiv.innerHTML = '<p class="loading">Không có dữ liệu</p>';
        }
    } catch (error) {
        console.error('Lỗi tải sách:', error);
        showError('topBooks', 'Lỗi tải dữ liệu');
    }
}

async function loadTopBorrowers() {
    showLoading('topBorrowers');

    try {
        const result = await API.getTopBorrowers(10);
        const listDiv = document.getElementById('topBorrowers');

        if (result.success && result.data.length > 0) {
            let html = '';
            result.data.forEach((borrower, index) => {
                html += `
                    <div class="list-item">
                        <div>
                            <div class="title">${index + 1}. ${borrower.borrowerName}</div>
                            <div style="font-size: 0.85rem; color: #6b7280;">
                                Đang mượn: ${borrower.currentBorrows}
                            </div>
                        </div>
                        <span class="badge">${borrower.totalBorrows} lượt</span>
                    </div>
                `;
            });
            listDiv.innerHTML = html;
        } else {
            listDiv.innerHTML = '<p class="loading">Không có dữ liệu</p>';
        }
    } catch (error) {
        console.error('Lỗi tải người mượn:', error);
        showError('topBorrowers', 'Lỗi tải dữ liệu');
    }
}

async function loadCategoryStats() {
    showLoading('categoryStats');

    try {
        const result = await API.getCategoryStats();
        const statsDiv = document.getElementById('categoryStats');

        if (result.success && result.data.length > 0) {
            let html = '';
            result.data.forEach(category => {
                html += `
                    <div class="category-card">
                        <h3>${category.category}</h3>
                        <div class="stat-row">
                            <span>Tổng đầu sách:</span>
                            <strong>${category.totalTitles}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Tổng bản sao:</span>
                            <strong>${category.totalCopies}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Còn lại:</span>
                            <strong style="color: var(--success-color);">${category.availableCopies}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Đang mượn:</span>
                            <strong style="color: var(--primary-color);">${category.borrowedCopies}</strong>
                        </div>
                    </div>
                `;
            });
            statsDiv.innerHTML = html;
        } else {
            statsDiv.innerHTML = '<p class="loading">Không có dữ liệu thể loại</p>';
        }
    } catch (error) {
        console.error('Lỗi tải thống kê thể loại:', error);
        showError('categoryStats', 'Lỗi tải dữ liệu');
    }
}

async function loadLowStockBooks() {
    showLoading('lowStockBooks');

    try {
        const result = await API.getLowStockBooks();
        const booksDiv = document.getElementById('lowStockBooks');

        if (result.success && result.data.length > 0) {
            let html = '';
            result.data.forEach(book => {
                const outOfStock = book.availableQuantity === 0;
                html += `
                    <div class="book-card" style="border-left: 4px solid ${outOfStock ? 'var(--danger-color)' : 'var(--warning-color)'};">
                        <h3>${book.title}</h3>
                        <p class="author">của ${book.author}</p>
                        <span class="category">${book.category || 'Chưa phân loại'}</span>
                        <p class="availability ${outOfStock ? 'unavailable' : 'available'}">
                            ${outOfStock ? '⚠️ Hết hàng' : '⚠️ Sắp hết'}
                            (${book.availableQuantity}/${book.totalQuantity})
                        </p>
                    </div>
                `;
            });
            booksDiv.innerHTML = html;
        } else {
            booksDiv.innerHTML = '<p class="loading" style="color: var(--success-color);">✓ Tất cả sách đều còn đủ!</p>';
        }
    } catch (error) {
        console.error('Lỗi tải sách sắp hết:', error);
        showError('lowStockBooks', 'Lỗi tải dữ liệu');
    }
}

async function loadActivityStats() {
    showLoading('activityStats');

    try {
        const result = await API.getActivityStats();
        const statsDiv = document.getElementById('activityStats');

        if (result.success) {
            const data = result.data;

            let html = '';
            for (const [action, count] of Object.entries(data)) {
                html += `
                    <div class="activity-item">
                        <div class="action">${action.replace(/_/g, ' ')}</div>
                        <div class="count">${count}</div>
                    </div>
                `;
            }
            statsDiv.innerHTML = html;
        } else {
            statsDiv.innerHTML = '<p class="loading">Không có dữ liệu hoạt động</p>';
        }
    } catch (error) {
        console.error('Lỗi tải thống kê hoạt động:', error);
        showError('activityStats', 'Lỗi tải dữ liệu');
    }
}
