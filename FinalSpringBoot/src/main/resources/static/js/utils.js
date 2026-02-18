// Toast Notifications
const Toast = {
    container: null,

    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    show(message, type = 'info', title = null, duration = 4000) {
        this.init();

        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        const titles = {
            success: title || 'Éxito',
            error: title || 'Error',
            warning: title || 'Advertencia',
            info: title || 'Información'
        };

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <div class="toast-icon">${icons[type]}</div>
            <div class="toast-content">
                <div class="toast-title">${titles[type]}</div>
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close" onclick="Toast.close(this.parentElement)">×</button>
        `;

        this.container.appendChild(toast);

        if (duration > 0) {
            setTimeout(() => this.close(toast), duration);
        }

        return toast;
    },

    close(toast) {
        toast.classList.add('hiding');
        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
        }, 300);
    },

    success(message, title = null) {
        return this.show(message, 'success', title);
    },

    error(message, title = null) {
        return this.show(message, 'error', title);
    },

    warning(message, title = null) {
        return this.show(message, 'warning', title);
    },

    info(message, title = null) {
        return this.show(message, 'info', title);
    }
};

// Loading Spinner
const Spinner = {
    overlay: null,

    init() {
        if (!this.overlay) {
            this.overlay = document.createElement('div');
            this.overlay.className = 'spinner-overlay';
            this.overlay.innerHTML = '<div class="spinner"></div>';
            document.body.appendChild(this.overlay);
        }
    },

    show() {
        this.init();
        this.overlay.classList.add('active');
    },

    hide() {
        if (this.overlay) {
            this.overlay.classList.remove('active');
        }
    }
};

// Confirmación de acciones (modal personalizado)
function confirmAction(message, confirmText = 'Confirmar', cancelText = 'Cancelar') {
    return new Promise((resolve) => {
        // Crear overlay
        const overlay = document.createElement('div');
        overlay.className = 'confirm-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            animation: fadeIn 0.2s ease-in;
        `;
        
        // Crear modal
        const modal = document.createElement('div');
        modal.className = 'confirm-modal';
        modal.style.cssText = `
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            max-width: 400px;
            width: 90%;
            animation: slideDown 0.3s ease-out;
        `;
        
        modal.innerHTML = `
            <div style="margin-bottom: 20px; font-size: 16px; color: #333; line-height: 1.5;">
                ${message}
            </div>
            <div style="display: flex; gap: 10px; justify-content: flex-end;">
                <button class="btn-cancel" style="
                    padding: 10px 20px;
                    border: 1px solid #ccc;
                    background: white;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
                    transition: all 0.2s;
                ">${cancelText}</button>
                <button class="btn-confirm" style="
                    padding: 10px 20px;
                    border: none;
                    background: #dc3545;
                    color: white;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 500;
                    transition: all 0.2s;
                ">${confirmText}</button>
            </div>
        `;
        
        // Agregar estilos de animación si no existen
        if (!document.getElementById('confirm-modal-styles')) {
            const style = document.createElement('style');
            style.id = 'confirm-modal-styles';
            style.textContent = `
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                @keyframes fadeOut {
                    from { opacity: 1; }
                    to { opacity: 0; }
                }
                @keyframes slideDown {
                    from { 
                        transform: translateY(-50px);
                        opacity: 0;
                    }
                    to { 
                        transform: translateY(0);
                        opacity: 1;
                    }
                }
                .btn-cancel:hover {
                    background: #f8f9fa !important;
                    border-color: #999 !important;
                }
                .btn-confirm:hover {
                    background: #bb2d3b !important;
                }
            `;
            document.head.appendChild(style);
        }
        
        overlay.appendChild(modal);
        document.body.appendChild(overlay);
        
        // Función para cerrar
        const closeModal = (result) => {
            overlay.style.animation = 'fadeOut 0.2s ease-out';
            setTimeout(() => {
                if (overlay.parentNode) {
                    overlay.parentNode.removeChild(overlay);
                }
                resolve(result);
            }, 200);
        };
        
        // Event listeners
        modal.querySelector('.btn-confirm').addEventListener('click', () => closeModal(true));
        modal.querySelector('.btn-cancel').addEventListener('click', () => closeModal(false));
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) closeModal(false);
        });
        
        // ESC para cancelar
        const escHandler = (e) => {
            if (e.key === 'Escape') {
                closeModal(false);
                document.removeEventListener('keydown', escHandler);
            }
        };
        document.addEventListener('keydown', escHandler);
    });
}

// Validaciones
const Validators = {
    // Validar email
    email(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    // Validar contraseña fuerte (mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número)
    password(password) {
        if (password.length < 8) {
            return { valid: false, message: 'La contraseña debe tener al menos 8 caracteres' };
        }
        if (!/[A-Z]/.test(password)) {
            return { valid: false, message: 'Debe contener al menos una mayúscula' };
        }
        if (!/[a-z]/.test(password)) {
            return { valid: false, message: 'Debe contener al menos una minúscula' };
        }
        if (!/[0-9]/.test(password)) {
            return { valid: false, message: 'Debe contener al menos un número' };
        }
        return { valid: true };
    },

    // Algoritmo de Luhn para validar tarjetas
    creditCard(cardNumber) {
        // Eliminar espacios y guiones
        const cleaned = cardNumber.replace(/[\s-]/g, '');
        
        if (!/^\d{13,19}$/.test(cleaned)) {
            return false;
        }

        let sum = 0;
        let isEven = false;

        for (let i = cleaned.length - 1; i >= 0; i--) {
            let digit = parseInt(cleaned[i]);

            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            isEven = !isEven;
        }

        return sum % 10 === 0;
    },

    // Validar CVV
    cvv(cvv) {
        return /^\d{3,4}$/.test(cvv);
    },

    // Validar fecha de expiración (MM/YY o MM/YYYY)
    expirationDate(date) {
        const regex = /^(0[1-9]|1[0-2])\/(\d{2}|\d{4})$/;
        if (!regex.test(date)) {
            return { valid: false, message: 'Formato inválido (use MM/YY)' };
        }

        const [month, year] = date.split('/');
        const expYear = year.length === 2 ? parseInt('20' + year) : parseInt(year);
        const expMonth = parseInt(month);

        const now = new Date();
        const currentYear = now.getFullYear();
        const currentMonth = now.getMonth() + 1;

        if (expYear < currentYear || (expYear === currentYear && expMonth < currentMonth)) {
            return { valid: false, message: 'La tarjeta ha expirado' };
        }

        return { valid: true };
    },

    // Validar IBAN básico
    iban(iban) {
        const cleaned = iban.replace(/[\s-]/g, '').toUpperCase();
        
        // Formato básico: 2 letras del país + 2 dígitos de control + resto
        if (!/^[A-Z]{2}\d{2}[A-Z0-9]+$/.test(cleaned)) {
            return false;
        }

        // Validar longitud según país (España: 24, Alemania: 22, Francia: 27, etc.)
        const lengths = {
            'ES': 24, 'DE': 22, 'FR': 27, 'IT': 27, 'PT': 25,
            'GB': 22, 'IE': 22, 'NL': 18, 'BE': 16
        };

        const countryCode = cleaned.substring(0, 2);
        const expectedLength = lengths[countryCode];

        if (expectedLength && cleaned.length !== expectedLength) {
            return false;
        }

        return cleaned.length >= 15 && cleaned.length <= 34;
    },

    // Validar username (alfanumérico, 3-20 caracteres)
    username(username) {
        if (username.length < 3 || username.length > 20) {
            return { valid: false, message: 'El usuario debe tener entre 3 y 20 caracteres' };
        }
        if (!/^[a-zA-Z0-9_]+$/.test(username)) {
            return { valid: false, message: 'Solo letras, números y guión bajo' };
        }
        return { valid: true };
    }
};

// Helper para mostrar error en campo
function showFieldError(input, message) {
    input.classList.add('input-error');
    input.classList.remove('input-success');
    
    let errorDiv = input.nextElementSibling;
    if (!errorDiv || !errorDiv.classList.contains('error-message')) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        input.parentNode.insertBefore(errorDiv, input.nextSibling);
    }
    
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
}

// Helper para mostrar éxito en campo
function showFieldSuccess(input) {
    input.classList.remove('input-error');
    input.classList.add('input-success');
    
    const errorDiv = input.nextElementSibling;
    if (errorDiv && errorDiv.classList.contains('error-message')) {
        errorDiv.classList.remove('show');
    }
}

// Helper para limpiar validación
function clearFieldValidation(input) {
    input.classList.remove('input-error', 'input-success');
    
    const errorDiv = input.nextElementSibling;
    if (errorDiv && errorDiv.classList.contains('error-message')) {
        errorDiv.classList.remove('show');
    }
}

// Formatear número de tarjeta automáticamente
function formatCreditCard(input) {
    let value = input.value.replace(/\s/g, '');
    let formatted = value.match(/.{1,4}/g);
    input.value = formatted ? formatted.join(' ') : value;
}

// Formatear fecha de expiración automáticamente
function formatExpirationDate(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 2) {
        input.value = value.substring(0, 2) + (value.length > 2 ? '/' + value.substring(2, 4) : '');
    } else {
        input.value = value;
    }
}

// Convertir Flash Messages de Thymeleaf a Toast
document.addEventListener('DOMContentLoaded', function() {
    // Buscar mensajes flash de Thymeleaf y convertirlos a toast
    const successMsg = document.querySelector('[th\\:if="${mensaje}"]');
    const errorMsg = document.querySelector('[th\\:if="${error}"]');
    
    if (successMsg && successMsg.textContent.trim()) {
        Toast.success(successMsg.textContent.trim());
        successMsg.style.display = 'none';
    }
    
    if (errorMsg && errorMsg.textContent.trim()) {
        Toast.error(errorMsg.textContent.trim());
        errorMsg.style.display = 'none';
    }
});
