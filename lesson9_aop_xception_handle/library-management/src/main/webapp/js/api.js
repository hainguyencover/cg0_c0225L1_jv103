// API Configuration
// Tự động detect context path
const API_BASE_URL = window.location.origin + '/library-management';

console.log('[API] Base URL:', API_BASE_URL);

// API Helper Functions
const API = {
    // Books API
    async getAllBooks() {
        console.log('[API] Calling getAllBooks...');
        const response = await fetch(`${API_BASE_URL}/books`);
        const data = await response.json();
        console.log('[API] getAllBooks response:', data);
        return data;
    },

    async getBookById(id) {
        const response = await fetch(`${API_BASE_URL}/books?id=${id}`);
        return await response.json();
    },

    async searchBooks(keyword) {
        const response = await fetch(`${API_BASE_URL}/books?action=search&keyword=${encodeURIComponent(keyword)}`);
        return await response.json();
    },

    async getBooksByCategory(category) {
        const response = await fetch(`${API_BASE_URL}/books?category=${encodeURIComponent(category)}`);
        return await response.json();
    },

    async getCategories() {
        const response = await fetch(`${API_BASE_URL}/books?action=categories`);
        return await response.json();
    },

    // Borrow API
    async borrowBook(bookId, borrowerName) {
        const response = await fetch(`${API_BASE_URL}/borrow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `bookId=${bookId}&borrowerName=${encodeURIComponent(borrowerName)}`
        });
        return await response.json();
    },

    async getAllBorrowRecords() {
        const response = await fetch(`${API_BASE_URL}/borrow`);
        return await response.json();
    },

    async getBorrowRecordByCode(code) {
        const response = await fetch(`${API_BASE_URL}/borrow?code=${code}`);
        return await response.json();
    },

    async getCurrentBorrows() {
        const response = await fetch(`${API_BASE_URL}/borrow?status=BORROWED`);
        return await response.json();
    },

    async getReturnedBooks() {
        const response = await fetch(`${API_BASE_URL}/borrow?status=RETURNED`);
        return await response.json();
    },

    // Return API
    async validateBorrowCode(code) {
        const response = await fetch(`${API_BASE_URL}/return?code=${code}`);
        return await response.json();
    },

    async returnBook(borrowCode) {
        const response = await fetch(`${API_BASE_URL}/return`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `borrowCode=${borrowCode}`
        });
        return await response.json();
    },

    // Statistics API
    async getOverview() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=overview`);
        return await response.json();
    },

    async getDashboard() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=dashboard`);
        return await response.json();
    },

    async getTopBooks(limit = 10) {
        const response = await fetch(`${API_BASE_URL}/statistics?action=topBooks&limit=${limit}`);
        return await response.json();
    },

    async getTopBorrowers(limit = 10) {
        const response = await fetch(`${API_BASE_URL}/statistics?action=topBorrowers&limit=${limit}`);
        return await response.json();
    },

    async getCategoryStats() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=categories`);
        return await response.json();
    },

    async getHealthReport() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=health`);
        return await response.json();
    },

    async getLowStockBooks() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=lowStock`);
        return await response.json();
    },

    async getActivityStats() {
        const response = await fetch(`${API_BASE_URL}/statistics?action=activity`);
        return await response.json();
    }
};

// Utility Functions
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<p class="loading">Loading...</p>';
    }
}

function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<p class="error">${message}</p>`;
    }
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
}

function formatBorrowCode(code) {
    return `<span class="code">${code}</span>`;
}
