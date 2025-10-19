let allBooks = [];

document.addEventListener('DOMContentLoaded', async () => {
    await loadAllBooks();
    await loadCategories();
});

async function loadAllBooks() {
    showLoading('booksList');

    try {
        const result = await API.getAllBooks();

        if (result.success) {
            allBooks = result.data;
            displayBooks(allBooks);
            document.getElementById('bookCount').textContent = allBooks.length;
        } else {
            showError('booksList', 'Failed to load books');
        }
    } catch (error) {
        console.error('Error loading books:', error);
        showError('booksList', 'Error connecting to server');
    }
}

async function loadCategories() {
    try {
        const result = await API.getCategories();

        if (result.success) {
            const select = document.getElementById('categoryFilter');
            result.data.forEach(category => {
                const option = document.createElement('option');
                option.value = category;
                option.textContent = category;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

function displayBooks(books) {
    const booksDiv = document.getElementById('booksList');

    if (books.length === 0) {
        booksDiv.innerHTML = '<p class="loading">No books found</p>';
        return;
    }

    let html = '';
    books.forEach(book => {
        const available = book.availableQuantity > 0;
        html += `
            <div class="book-card" onclick="showBookDetails(${book.bookId})">
                <h3>${book.title}</h3>
                <p class="author">by ${book.author}</p>
                <span class="category">${book.category || 'Uncategorized'}</span>
                <p class="availability ${available ? 'available' : 'unavailable'}">
                    ${available ? '✓ Available' : '✗ Not Available'} 
                    (${book.availableQuantity}/${book.totalQuantity})
                </p>
            </div>
        `;
    });

    booksDiv.innerHTML = html;
    document.getElementById('bookCount').textContent = books.length;
}

async function searchBooks() {
    const keyword = document.getElementById('searchInput').value.trim();

    if (!keyword) {
        alert('Please enter a search keyword');
        return;
    }

    showLoading('booksList');

    try {
        const result = await API.searchBooks(keyword);

        if (result.success) {
            displayBooks(result.data);
        } else {
            showError('booksList', 'Search failed');
        }
    } catch (error) {
        console.error('Error searching books:', error);
        showError('booksList', 'Error performing search');
    }
}

async function filterByCategory() {
    const category = document.getElementById('categoryFilter').value;

    if (!category) {
        displayBooks(allBooks);
        return;
    }

    showLoading('booksList');

    try {
        const result = await API.getBooksByCategory(category);

        if (result.success) {
            displayBooks(result.data);
        } else {
            showError('booksList', 'Filter failed');
        }
    } catch (error) {
        console.error('Error filtering books:', error);
        showError('booksList', 'Error filtering books');
    }
}

async function showBookDetails(bookId) {
    try {
        const result = await API.getBookById(bookId);

        if (result.success) {
            const book = result.data;
            const logs = result.logs || [];

            let html = `
                <div class="book-details">
                    <h3>${book.title}</h3>
                    <p><strong>Author:</strong> ${book.author}</p>
                    <p><strong>Category:</strong> ${book.category || 'N/A'}</p>
                    <p><strong>Total Quantity:</strong> ${book.totalQuantity}</p>
                    <p><strong>Available:</strong> ${book.availableQuantity}</p>
                    <p><strong>Created:</strong> ${formatDate(book.createdAt)}</p>
                    <p><strong>Updated:</strong> ${formatDate(book.updatedAt)}</p>
                </div>
            `;

            if (logs.length > 0) {
                html += '<h3>Action History</h3><div class="log-list">';
                logs.forEach(log => {
                    html += `
                        <div class="log-item">
                            <strong>${log.action}</strong> - 
                            ${log.changeAmount > 0 ? '+' : ''}${log.changeAmount} 
                            (${log.beforeQuantity} → ${log.afterQuantity}) 
                            by ${log.actor} 
                            on ${formatDate(log.timestamp)}
                        </div>
                    `;
                });
                html += '</div>';
            }

            document.getElementById('modalBody').innerHTML = html;
            document.getElementById('bookModal').style.display = 'block';
        }
    } catch (error) {
        console.error('Error loading book details:', error);
        alert('Error loading book details');
    }
}

function closeModal() {
    document.getElementById('bookModal').style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('bookModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}

// Allow Enter key to search
document.getElementById('searchInput')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        searchBooks();
    }
});
