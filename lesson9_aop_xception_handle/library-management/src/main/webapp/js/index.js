// Load dashboard data on page load
document.addEventListener('DOMContentLoaded', async () => {
    await loadOverview();
    await loadRecentActivity();
});

async function loadOverview() {
    try {
        const result = await API.getOverview();

        if (result.success) {
            const data = result.data;

            document.getElementById('totalBooks').textContent = data.totalBookCopies || 0;
            document.getElementById('availableBooks').textContent = data.availableBooks || 0;
            document.getElementById('borrowedBooks').textContent = data.borrowedBooks || 0;
            document.getElementById('totalBorrows').textContent = data.totalBorrows || 0;
            document.getElementById('currentBorrows').textContent = data.currentBorrows || 0;
            document.getElementById('totalVisits').textContent = data.totalVisits || 0;
        } else {
            showError('statsGrid', 'Không thể tải dữ liệu tổng quan');
        }
    } catch (error) {
        console.error('Lỗi tải tổng quan:', error);
        showError('statsGrid', 'Lỗi kết nối máy chủ');
    }
}

async function loadRecentActivity() {
    try {
        const result = await API.getCurrentBorrows();
        const activityDiv = document.getElementById('recentActivity');

        if (result.success && result.data.length > 0) {
            const recentItems = result.data.slice(0, 5);

            let html = '<div class="activity-list">';
            recentItems.forEach(item => {
                html += `
                    <div class="activity-item-list">
                        📖 <strong>${item.borrowerName}</strong> đã mượn 
                        "<em>${item.bookTitle}</em>" 
                        vào ${formatDate(item.borrowDate)}
                        ${formatBorrowCode(item.borrowCode)}
                    </div>
                `;
            });
            html += '</div>';

            activityDiv.innerHTML = html;
        } else {
            activityDiv.innerHTML = '<p class="loading">Không có hoạt động gần đây</p>';
        }
    } catch (error) {
        console.error('Lỗi tải hoạt động:', error);
        showError('recentActivity', 'Lỗi tải hoạt động');
    }
}
