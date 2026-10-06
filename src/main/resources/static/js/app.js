// ═══════════════════════════════════════════════
// DisciplineBot — Frontend JavaScript
// ═══════════════════════════════════════════════

// Live clock
function updateClock() {
    const el = document.getElementById('liveTime');
    if (!el) return;
    const now = new Date();
    const time = now.toLocaleTimeString('en-IN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
        timeZone: 'Asia/Kolkata'
    });
    el.textContent = `🕐 IST ${time}`;
}
setInterval(updateClock, 1000);
updateClock();

// Auto-hide toast after 5 seconds
document.addEventListener('DOMContentLoaded', () => {
    const toast = document.getElementById('toast');
    if (toast) {
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.5s ease';
            setTimeout(() => toast.remove(), 500);
        }, 4000);
    }

    // Animate progress bars on load
    const fills = document.querySelectorAll('.progress-bar-fill, .habit-progress-fill');
    fills.forEach(fill => {
        const target = fill.style.width;
        fill.style.width = '0%';
        setTimeout(() => { fill.style.width = target; }, 200);
    });
});

// Test WhatsApp connection
async function testWhatsApp() {
    showNotification('📱 Sending test Telegram message...', 'info');
    try {
        const response = await fetch('/api/test/whatsapp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: '🤖 DisciplineBot Test! Your coach is watching. No excuses today.' })
        });
        const data = await response.json();
        if (data.sent) {
            showNotification('✅ Telegram message sent! Check your phone.', 'success');
        } else {
            showNotification('⚠️ Could not send. Check Telegram config in application.properties', 'error');
        }
    } catch (e) {
        showNotification('❌ Error: ' + e.message, 'error');
    }
}

// Send daily summary now
async function sendSummaryNow() {
    showNotification('📊 Generating and sending summary...', 'info');
    try {
        const response = await fetch('/api/send/summary', { method: 'POST' });
        const data = await response.json();
        if (data.sent) {
            showNotification(`✅ Summary sent! Grade: ${data.grade}`, 'success');
        } else {
            showNotification('⚠️ Failed to send summary. Check Telegram config.', 'error');
        }
    } catch (e) {
        showNotification('❌ Error: ' + e.message, 'error');
    }
    return false;
}

// Send summary button handler
function sendSummary(event) {
    event.preventDefault();
    sendSummaryNow();
    return false;
}

// Show toast notification
function showNotification(message, type = 'info') {
    // Remove existing
    document.querySelectorAll('.toast-dynamic').forEach(t => t.remove());

    const toast = document.createElement('div');
    toast.className = `toast toast-dynamic toast-${type === 'success' ? 'success' : 'error'}`;
    toast.innerHTML = `<span>${message}</span>`;
    toast.style.cssText = `
        position: fixed;
        top: 80px;
        right: 24px;
        z-index: 9999;
        padding: 14px 20px;
        border-radius: 10px;
        font-size: 0.9rem;
        font-weight: 500;
        backdrop-filter: blur(10px);
        max-width: 380px;
        animation: slideIn 0.4s cubic-bezier(0.34,1.56,0.64,1);
        font-family: 'Inter', sans-serif;
    `;

    if (type === 'success') {
        toast.style.background = 'rgba(16,185,129,0.15)';
        toast.style.border = '1px solid rgba(16,185,129,0.4)';
        toast.style.color = '#6ee7b7';
    } else if (type === 'info') {
        toast.style.background = 'rgba(6,182,212,0.15)';
        toast.style.border = '1px solid rgba(6,182,212,0.4)';
        toast.style.color = '#67e8f9';
    } else {
        toast.style.background = 'rgba(239,68,68,0.15)';
        toast.style.border = '1px solid rgba(239,68,68,0.4)';
        toast.style.color = '#fca5a5';
    }

    document.body.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.4s ease';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// Add hover ripple effect to habit cards
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.habit-card').forEach(card => {
        card.addEventListener('mousemove', (e) => {
            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            card.style.background = `radial-gradient(circle at ${x}px ${y}px, rgba(99,102,241,0.06), transparent 60%), linear-gradient(145deg, #111827 0%, #0d1120 100%)`;
        });
        card.addEventListener('mouseleave', () => {
            card.style.background = '';
        });
    });
});
