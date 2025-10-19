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
            showError('statsGrid', 'Failed to load overview data');
        }
    } catch (error) {
        console.error('Error loading overview:', error);
        showError('statsGrid', 'Error connecting to server');
    }
}

async function loadRecentActivity() {
    try {
        const result = await API.getCurrentBorrows();
        const activityDiv = document.getElementById('recentActivity');

        if (result.success && result.data.length > 0) {
            const recentItems = result.data.slice(0, 5); // Show only 5 recent

            let html = '<div class="activity-list">';
            recentItems.forEach(item => {
                html += `
                    <div class="activity-item-list">
                        📖 <strong>${item.borrowerName}</strong> borrowed 
                        "<em>${item.bookTitle}</em>" 
                        on ${formatDate(item.borrowDate)}
                        ${formatBorrowCode(item.borrowCode)}
                    </div>
                `;
            });
            html += '</div>';

            activityDiv.innerHTML = html;
        } else {
            activityDiv.innerHTML = '<p class="loading">No recent activity</p>';
        }
    } catch (error) {
        console.error('Error loading recent activity:', error);
        showError('recentActivity', 'Error loading activity');
    }
}
