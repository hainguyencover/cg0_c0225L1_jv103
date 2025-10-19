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
            if (data.healthStatus.includes('Good')) healthClass = 'good';
            else if (data.healthStatus.includes('Fair')) healthClass = 'fair';
            else if (data.healthStatus.includes('Poor')) healthClass = 'poor';

            let html = `
                <div class="health-status ${healthClass}">
                    ${data.healthStatus}
                </div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-top: 1rem;">
                    <div>
                        <strong>Total Book Titles:</strong> ${data.totalBookTitles}
                    </div>
                    <div>
                        <strong>Total Copies:</strong> ${data.totalCopies}
                    </div>
                    <div>
                        <strong>Available:</strong> ${data.availableCopies}
                    </div>
                    <div>
                        <strong>Availability Rate:</strong> ${data.availabilityRate}
                    </div>
                </div>
            `;

            if (data.booksNeedingRestock > 0) {
                html += `
                    <div style="margin-top: 1rem; padding: 1rem; background: #fef3c7; border-radius: 4px;">
                        <strong>⚠️ Books Needing Restock:</strong> ${data.booksNeedingRestock}
                        <ul style="margin-top: 0.5rem; padding-left: 1.5rem;">
                            ${data.lowStockBooks.map(book => `<li>${book}</li>`).join('')}
                        </ul>
                    </div>
                `;
            }

            reportDiv.innerHTML = html;
        } else {
            showError('healthReport', 'Failed to load health report');
        }
    } catch (error) {
        console.error('Error loading health report:', error);
        showError('healthReport', 'Error loading data');
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
                                by ${book.author} | ${book.category}
                            </div>
                        </div>
                        <span class="badge">${book.borrowCount} borrows</span>
                    </div>
                `;
            });
            listDiv.innerHTML = html;
        } else {
            listDiv.innerHTML = '<p class="loading">No data available</p>';
        }
    } catch (error) {
        console.error('Error loading top books:', error);
        showError('topBooks', 'Error loading data');
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
                                Currently borrowing: ${borrower.currentBorrows}
                            </div>
                        </div>
                        <span class="badge">${borrower.totalBorrows} total</span>
                    </div>
                `;
            });
            listDiv.innerHTML = html;
        } else {
            listDiv.innerHTML = '<p class="loading">No data available</p>';
        }
    } catch (error) {
        console.error('Error loading top borrowers:', error);
        showError('topBorrowers', 'Error loading data');
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
                            <span>Total Titles:</span>
                            <strong>${category.totalTitles}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Total Copies:</span>
                            <strong>${category.totalCopies}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Available:</span>
                            <strong style="color: var(--success-color);">${category.availableCopies}</strong>
                        </div>
                        <div class="stat-row">
                            <span>Borrowed:</span>
                            <strong style="color: var(--primary-color);">${category.borrowedCopies}</strong>
                        </div>
                    </div>
                `;
            });
            statsDiv.innerHTML = html;
        } else {
            statsDiv.innerHTML = '<p class="loading">No category data available</p>';
        }
    } catch (error) {
        console.error('Error loading category stats:', error);
        showError('categoryStats', 'Error loading data');
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
                        <p class="author">by ${book.author}</p>
                        <span class="category">${book.category || 'Uncategorized'}</span>
                        <p class="availability ${outOfStock ? 'unavailable' : 'available'}">
                            ${outOfStock ? '⚠️ Out of Stock' : '⚠️ Low Stock'}
                            (${book.availableQuantity}/${book.totalQuantity})
                        </p>
                    </div>
                `;
            });
            booksDiv.innerHTML = html;
        } else {
            booksDiv.innerHTML = '<p class="loading" style="color: var(--success-color);">✓ All books are well stocked!</p>';
        }
    } catch (error) {
        console.error('Error loading low stock books:', error);
        showError('lowStockBooks', 'Error loading data');
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
            statsDiv.innerHTML = '<p class="loading">No activity data available</p>';
        }
    } catch (error) {
        console.error('Error loading activity stats:', error);
        showError('activityStats', 'Error loading data');
    }
}
