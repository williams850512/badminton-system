// ============================================================
// 訂單管理 JavaScript
// ============================================================
const API_URL = '/api/orders';
const MEMBER_API_URL = '/api/members';
const PRODUCT_API_URL = '/api/products';

let orderModal, cachedOrders = [], cachedProducts = [], cart = [];
let debounceTimer;

document.addEventListener('DOMContentLoaded', function () {
    orderModal = new bootstrap.Modal(document.getElementById('orderModal'));
    document.getElementById('searchKeyword').addEventListener('keyup', e => {
        if (e.key === 'Enter') searchData();
    });
    // 會員搜尋防抖
    document.getElementById('memberSearch').addEventListener('input', function () {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => searchMember(this.value.trim()), 300);
    });
    document.addEventListener('click', e => {
        const r = document.getElementById('memberSearchResults');
        if (r && !e.target.closest('#memberSearch') && !e.target.closest('#memberSearchResults'))
            r.style.display = 'none';
    });
    loadProducts();
    loadData();
});

// ==================== 載入商品下拉 ====================
async function loadProducts() {
    try {
        const res = await fetch(PRODUCT_API_URL);
        cachedProducts = (await res.json()).filter(p => p.status === 'ACTIVE' && p.stockQty > 0);
        const sel = document.getElementById('selectProduct');
        sel.innerHTML = '<option value="">請選擇商品</option>';
        cachedProducts.forEach(p => {
            sel.innerHTML += `<option value="${p.productId}">${p.productName} (${p.brand || ''}) - $${p.price} [庫存:${p.stockQty}]</option>`;
        });
    } catch (e) { console.error('載入商品失敗', e); }
}

// ==================== 會員搜尋 ====================
async function searchMember(keyword) {
    const div = document.getElementById('memberSearchResults');
    if (keyword.length < 1) { div.style.display = 'none'; return; }
    try {
        const res = await fetch(`${MEMBER_API_URL}/search?keyword=${encodeURIComponent(keyword)}`);
        const members = await res.json();
        if (members.length === 0) {
            div.innerHTML = '<div class="list-group-item text-muted small">找不到符合的會員</div>';
        } else {
            div.innerHTML = members.map(m =>
                `<button type="button" class="list-group-item list-group-item-action py-1"
                    onclick="selectMember(${m.memberId}, '${(m.fullName || '').replace(/'/g, "\\'")}')">
                    <strong>${m.fullName || m.username}</strong>
                    <small class="text-muted ms-1">${m.phone || ''}</small>
                </button>`
            ).join('');
        }
        div.style.display = 'block';
    } catch (e) {
        div.innerHTML = '<div class="list-group-item text-danger small">搜尋失敗</div>';
        div.style.display = 'block';
    }
}

function selectMember(id, name) {
    document.getElementById('memberId').value = id;
    document.getElementById('memberSearch').value = name;
    document.getElementById('memberSelectedInfo').textContent = `✓ ${name} (ID: ${id})`;
    document.getElementById('memberSearchResults').style.display = 'none';
}

// ==================== 購物車操作 ====================
function addToCart() {
    const pid = parseInt(document.getElementById('selectProduct').value);
    const qty = parseInt(document.getElementById('selectQty').value) || 1;
    if (!pid) { alert('請選擇商品！'); return; }

    const product = cachedProducts.find(p => p.productId === pid);
    if (!product) return;

    const existing = cart.find(c => c.productId === pid);
    if (existing) {
        existing.quantity += qty;
    } else {
        cart.push({
            productId: pid,
            name: product.productName,
            price: Math.round(product.price),
            quantity: qty
        });
    }
    document.getElementById('selectQty').value = 1;
    renderCart();
}

function removeFromCart(index) {
    cart.splice(index, 1);
    renderCart();
}

function renderCart() {
    const tbody = document.getElementById('cartBody');
    if (cart.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">尚未加入商品</td></tr>';
        document.getElementById('cartTotal').textContent = '$0';
        return;
    }
    let total = 0;
    tbody.innerHTML = '';
    cart.forEach((item, i) => {
        const sub = item.price * item.quantity;
        total += sub;
        tbody.innerHTML += `<tr>
            <td>${item.name}</td>
            <td>$${item.price.toLocaleString()}</td>
            <td>${item.quantity}</td>
            <td>$${sub.toLocaleString()}</td>
            <td><button class="btn btn-outline-danger btn-sm" onclick="removeFromCart(${i})"><i class="bi bi-x"></i></button></td>
        </tr>`;
    });
    document.getElementById('cartTotal').textContent = `$${total.toLocaleString()}`;
}

// ==================== 載入訂單列表 ====================
async function loadData(keyword) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '<tr><td colspan="7"><div class="spinner-border spinner-border-sm text-primary"></div> 載入中...</td></tr>';
    try {
        let orders = await (await fetch(API_URL)).json();
        if (keyword) {
            const kw = keyword.toLowerCase();
            orders = orders.filter(o => {
                const name = o.member ? (o.member.fullName || o.member.username || '') : '';
                return name.toLowerCase().includes(kw)
                    || (o.paymentType || '').toLowerCase().includes(kw)
                    || (o.status || '').toLowerCase().includes(kw)
                    || String(o.orderId).includes(kw);
            });
        }
        cachedOrders = orders;
        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">目前沒有訂單資料</td></tr>';
            return;
        }
        tbody.innerHTML = '';
        orders.forEach((order, index) => {
            const memberInfo = order.member
                ? `${order.member.fullName || order.member.username} <small class="text-muted">(ID:${order.member.memberId})</small>`
                : '-';
            const orderDate = order.orderDate ? new Date(order.orderDate).toLocaleString('zh-TW') : '-';

            tbody.innerHTML += `<tr>
                <td>${order.orderId}</td>
                <td>${memberInfo}</td>
                <td><strong>$${(order.totalAmount || 0).toLocaleString()}</strong></td>
                <td>${getStatusBadge(order.status)}</td>
                <td>${getPaymentLabel(order.paymentType)}</td>
                <td>${orderDate}</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-info" data-order-id="${order.orderId}"
                                onclick="toggleItems(${order.orderId}, this)">
                            <i class="bi bi-caret-right-fill"></i> 明細
                        </button>
                        <button class="btn btn-outline-primary" onclick="openEditModal(${index})"><i class="bi bi-pencil"></i></button>
                        <button class="btn btn-outline-danger" onclick="deleteOrder(${order.orderId})"><i class="bi bi-trash"></i></button>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown"><i class="bi bi-arrow-repeat"></i></button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#" onclick="changeStatus(${order.orderId},'UNPAID')"><i class="bi bi-clock text-warning"></i> 未付款</a></li>
                                <li><a class="dropdown-item" href="#" onclick="changeStatus(${order.orderId},'PAID')"><i class="bi bi-check-circle text-success"></i> 已付款</a></li>
                                <li><a class="dropdown-item" href="#" onclick="changeStatus(${order.orderId},'SHIPPED')"><i class="bi bi-truck text-info"></i> 已出貨</a></li>
                                <li><a class="dropdown-item" href="#" onclick="changeStatus(${order.orderId},'COMPLETED')"><i class="bi bi-bag-check text-primary"></i> 已完成</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="#" onclick="changeStatus(${order.orderId},'CANCELLED')"><i class="bi bi-x-circle text-danger"></i> 已取消</a></li>
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>`;
        });
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-4">載入失敗：${e.message}</td></tr>`;
    }
}

// ==================== 展開訂單明細 ====================
async function toggleItems(orderId, btn) {
    const existing = document.getElementById(`items-row-${orderId}`);
    if (existing) {
        existing.remove();
        btn.innerHTML = '<i class="bi bi-caret-right-fill"></i> 明細';
        return;
    }
    btn.innerHTML = '<i class="bi bi-caret-down-fill"></i> 收合';
    const row = document.createElement('tr');
    row.id = `items-row-${orderId}`;
    row.innerHTML = `<td colspan="7" style="background:#f8f9fa;padding:16px 24px;">
        <strong><i class="bi bi-list-ul"></i> 訂單明細</strong>
        <div class="spinner-border spinner-border-sm ms-2" id="items-loading-${orderId}"></div>
        <div id="items-content-${orderId}" class="mt-2"></div>
    </td>`;
    btn.closest('tr').after(row);

    try {
        const items = await (await fetch(`${API_URL}/${orderId}/items`)).json();
        document.getElementById(`items-loading-${orderId}`).style.display = 'none';
        const div = document.getElementById(`items-content-${orderId}`);
        if (items.length === 0) {
            div.innerHTML = '<span class="text-muted">此訂單沒有明細</span>';
            return;
        }
        let html = '<table class="table table-sm table-bordered mb-0" style="background:white">';
        html += '<thead><tr><th>商品</th><th>單價</th><th>數量</th><th>小計</th></tr></thead><tbody>';
        items.forEach(it => {
            const pName = it.product ? it.product.productName : '(已刪除)';
            html += `<tr><td>${pName}</td><td>$${it.unitPrice.toLocaleString()}</td>
                <td>${it.quantity}</td><td>$${it.subtotal.toLocaleString()}</td></tr>`;
        });
        html += '</tbody></table>';
        div.innerHTML = html;
    } catch (e) {
        document.getElementById(`items-content-${orderId}`).innerHTML = '<span class="text-danger">載入失敗</span>';
    }
}

// ==================== Badge 工具 ====================
function getStatusBadge(s) {
    const m = {
        UNPAID: '<span class="badge bg-warning text-dark">未付款</span>',
        PAID: '<span class="badge bg-success">已付款</span>',
        SHIPPED: '<span class="badge bg-info text-dark">已出貨</span>',
        COMPLETED: '<span class="badge bg-primary">已完成</span>',
        CANCELLED: '<span class="badge bg-danger">已取消</span>'
    };
    return m[s] || `<span class="badge bg-secondary">${s}</span>`;
}

function getPaymentLabel(p) {
    const m = { CASH: '現金', CREDIT_CARD: '信用卡', TRANSFER: '轉帳', LINE_PAY: 'Line Pay' };
    return m[p] || (p || '<span class="text-muted">-</span>');
}

// ==================== 新增 Modal ====================
function openCreateModal() {
    document.getElementById('modalTitle').textContent = '新增訂單';
    document.getElementById('orderForm').reset();
    document.getElementById('editId').value = '';
    document.getElementById('memberId').value = '';
    document.getElementById('memberSearch').value = '';
    document.getElementById('memberSearch').disabled = false;
    document.getElementById('memberSelectedInfo').textContent = '';
    // 顯示商品選擇區
    document.querySelectorAll('.cart-section').forEach(el => el.style.display = '');
    cart = [];
    renderCart();
    loadProducts();
    orderModal.show();
}

// ==================== 編輯 Modal ====================
function openEditModal(index) {
    const order = cachedOrders[index];
    document.getElementById('modalTitle').textContent = '編輯訂單';
    document.getElementById('editId').value = order.orderId;
    document.getElementById('memberId').value = order.member ? order.member.memberId : '';
    document.getElementById('memberSearch').value = order.member ? (order.member.fullName || order.member.username) : '';
    document.getElementById('memberSearch').disabled = true;
    document.getElementById('memberSelectedInfo').textContent = '';
    document.getElementById('paymentType').value = order.paymentType || '';
    document.getElementById('note').value = order.note || '';
    // 隱藏商品選擇區（編輯時用展開明細管理）
    document.querySelectorAll('.cart-section').forEach(el => el.style.display = 'none');
    cart = [];
    renderCart();
    orderModal.show();
}

// ==================== 儲存訂單 ====================
async function saveOrder() {
    const id = document.getElementById('editId').value;
    const memberId = document.getElementById('memberId').value;
    if (!memberId) { alert('請選擇會員！'); return; }

    try {
        if (id) {
            // 編輯：只更新 paymentType, note（保持原狀態）
            const order = cachedOrders.find(o => o.orderId == id);
            const res = await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    status: order ? order.status : 'UNPAID',
                    paymentType: document.getElementById('paymentType').value,
                    note: document.getElementById('note').value
                })
            });
            if (res.ok) { orderModal.hide(); loadData(); alert('更新成功！'); }
            else { alert('更新失敗：' + await res.text()); }
        } else {
            // 新增
            if (cart.length === 0) { alert('請至少加入一項商品！'); return; }
            const total = cart.reduce((sum, c) => sum + c.price * c.quantity, 0);

            // 1. 建立訂單
            const orderRes = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    member: { memberId: parseInt(memberId) },
                    totalAmount: total,
                    paymentType: document.getElementById('paymentType').value,
                    note: document.getElementById('note').value
                })
            });
            if (!orderRes.ok) { alert('建立訂單失敗：' + await orderRes.text()); return; }
            const newOrder = await orderRes.json();

            // 2. 逐筆新增明細
            for (const item of cart) {
                await fetch(`${API_URL}/${newOrder.orderId}/items`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        product: { productId: item.productId },
                        quantity: item.quantity,
                        unitPrice: item.price
                    })
                });
            }
            orderModal.hide();
            loadData();
            loadProducts(); // 更新庫存顯示
            alert('訂單建立成功！');
        }
    } catch (e) { alert('請求失敗：' + e.message); }
}

// ==================== 刪除 / 狀態 / 搜尋 ====================
async function deleteOrder(id) {
    if (!confirm(`確定要刪除訂單 #${id} 嗎？`)) return;
    try {
        const r = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (r.ok) { loadData(); alert('刪除成功！'); }
        else { alert('刪除失敗'); }
    } catch (e) { alert('請求失敗：' + e.message); }
}

async function changeStatus(id, newStatus) {
    try {
        const r = await fetch(`${API_URL}/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        if (r.ok) { loadData(); } else { alert('狀態更新失敗'); }
    } catch (e) { alert('請求失敗：' + e.message); }
}

function searchData() {
    loadData(document.getElementById('searchKeyword').value.trim() || null);
}

function clearSearch() {
    document.getElementById('searchKeyword').value = '';
    loadData();
}
