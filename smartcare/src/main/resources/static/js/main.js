/* SmartCare - JavaScript Utilities */

// API Base URL
const API_BASE = '/api/v1';

// Authentication helpers
const AuthService = {
    getToken: () => localStorage.getItem('token'),
    getRole: () => localStorage.getItem('role'),
    getUsername: () => localStorage.getItem('username'),
    
    isAuthenticated: () => !!localStorage.getItem('token'),
    
    setAuth: (token, role, username) => {
        localStorage.setItem('token', token);
        localStorage.setItem('role', role);
        localStorage.setItem('username', username);
    },
    
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('username');
        window.location.href = '/';
    },
    
    hasRole: (role) => localStorage.getItem('role') === role
};

// API helpers
const ApiService = {
    request: async (url, options = {}) => {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        const response = await fetch(`${API_BASE}${url}`, {
            ...options,
            headers
        });
        
        return response.json();
    },
    
    get: (url) => ApiService.request(url),
    
    post: (url, body) => ApiService.request(url, {
        method: 'POST',
        body: JSON.stringify(body)
    }),
    
    put: (url, body) => ApiService.request(url, {
        method: 'PUT',
        body: JSON.stringify(body)
    }),
    
    delete: (url) => ApiService.request(url, {
        method: 'DELETE'
    })
};

// Utility functions
const Utils = {
    formatDate: (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    },
    
    formatTime: (timeString) => {
        return timeString ? timeString.substring(0, 5) : 'N/A';
    },
    
    getStatusBadgeClass: (status) => {
        const statusMap = {
            'PENDING': 'pending',
            'CONFIRMED': 'confirmed',
            'COMPLETED': 'completed',
            'CANCELLED': 'cancelled'
        };
        return statusMap[status] || 'pending';
    },
    
    showAlert: (message, type = 'success') => {
        const alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
        alert(message);
    }
};

// Check authentication on page load
window.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    
    // Pages that don't require authentication
    const publicPages = ['/', '/login', '/register'];
    
    if (!publicPages.includes(currentPath)) {
        if (!AuthService.isAuthenticated()) {
            window.location.href = '/login';
        }
    }
});
