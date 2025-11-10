import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);

// ------------------------------------------------------------------
// UNUPDATED Mock API implementation
// ------------------------------------------------------------------



function startMockApiServer() {
    console.log('Mock Fast & Reckless Bank API running...');

    // --- In-memory data ---
    let nextAccountId = 1;
    let accounts = [];
    let transactions = [];

    // Helper delay (simulate latency)
    const delay = (ms) => new Promise((res) => setTimeout(res, ms));

    // --- Utility functions ---
    const findAccount = (id) => accounts.find((a) => a.id === id);
    const json = (data) =>
        new Response(JSON.stringify(data), {
            status: 200,
            headers: { 'Content-Type': 'application/json' },
        });
    const error = (msg, status = 400) =>
        new Response(JSON.stringify({ message: msg }), {
            status,
            headers: { 'Content-Type': 'application/json' },
        });

    // ------------------------------------------------------------------
    // DashboardController endpoints
    // ------------------------------------------------------------------
    async function handleDashboard(url, method) {
        if (url === '/dashboard' && method === 'GET') {
            return json({
                message: 'Welcome to the Fast & Reckless Bank — mock API is live!',
            });
        }
        return null;
    }

    // ------------------------------------------------------------------
    // AccountController endpoints
    // ------------------------------------------------------------------
    async function handleAccounts(url, method, body) {
        // List all accounts
        if (url === '/accounts/allAccounts' && method === 'GET') {
            return json(accounts);
        }

        // Get balances of all accounts
        if (url === '/api/accounts/balance' && method === 'GET') {
            const balances = accounts.map((a) => ({
                id: a.id,
                owner: a.owner,
                balance: a.balance,
            }));
            return json(balances);
        }

        // Create new account
        if (url === '/api/accounts/createNewAccount' && method === 'POST') {
            const { holder, contact, type } = body;
            if (!holder) return error('Account holder is required');
            const newAcc = {
                id: String(nextAccountId++),
                owner: holder,
                contact,
                type,
                balance: 0,
                outgoingTransfers: [],
            };
            accounts.push(newAcc);
            return json(newAcc);
        }


        // Update account (e.g. owner name)
        if (url === '/api/accounts/updateAccount' && method === 'POST') {
            const { id, owner } = body;
            const acc = findAccount(id);
            if (!acc) return error('Account not found');
            acc.owner = owner || acc.owner;
            return json(acc);
        }

        // Close (delete) account
        if (url === '/api/accounts/closeAccount' && method === 'POST') {
            const { id } = body;
            accounts = accounts.filter((a) => a.id !== id);
            return json({ message: 'Account closed successfully' });
        }

        return null;
    }

    // ------------------------------------------------------------------
    // TransactionController endpoints
    // ------------------------------------------------------------------
    async function handleTransactions(url, method, body) {
        if (url === '/transactions' && method === 'GET') {
            return json(transactions);
        }

        if (url === '/api/transactions/transactionHistory' && method === 'GET') {
            return json(transactions);
        }

        // Deposit
        if (url === '/api/transactions/deposit' && method === 'POST') {
            const { accountId, amount } = body;
            const acc = findAccount(accountId);
            if (!acc) return error('Account not found');
            acc.balance += Number(amount);
            const txn = {
                type: 'DEPOSIT',
                accountId,
                amount: Number(amount),
                timestamp: new Date().toISOString(),
            };
            transactions.unshift(txn);
            return json(txn);
        }

        // Withdraw
        if (url === '/api/transactions/withdraw' && method === 'POST') {
            const { accountId, amount } = body;
            const acc = findAccount(accountId);
            if (!acc) return error('Account not found');
            if (acc.balance < amount) return error('Insufficient funds');
            acc.balance -= Number(amount);
            const txn = {
                type: 'WITHDRAW',
                accountId,
                amount: Number(amount),
                timestamp: new Date().toISOString(),
            };
            transactions.unshift(txn);
            return json(txn);
        }

        // Transfer
        if (url === '/api/transactions/transfer' && method === 'POST') {
            const { fromAccountId, toAccountId, amount } = body;
            const from = findAccount(fromAccountId);
            const to = findAccount(toAccountId);
            if (!from || !to) return error('Invalid account(s)');
            if (from.balance < amount) return error('Insufficient funds');

            from.balance -= Number(amount);
            to.balance += Number(amount);

            const txn = {
                type: 'TRANSFER',
                fromAccountId,
                toAccountId,
                amount: Number(amount),
                timestamp: new Date().toISOString(),
            };

            transactions.unshift(txn);
            from.outgoingTransfers.unshift({
                toAccountId,
                amount: Number(amount),
                timestamp: txn.timestamp,
            });
            from.outgoingTransfers = from.outgoingTransfers.slice(0, 50);

            return json(txn);
        }

        return null;
    }

    // ------------------------------------------------------------------
    // Intercept fetch()
    // ------------------------------------------------------------------
    const originalFetch = window.fetch;
    window.fetch = async (url, options = {}) => {
        if (typeof url !== 'string' || !url.startsWith('/api/')) {
            return originalFetch(url, options);
        }

        await delay(100); // simulate small network delay

        const method = options.method || 'GET';
        let body = {};
        try {
            body = options.body ? JSON.parse(options.body) : {};
        } catch {}

        // Try controllers in order
        const handlers = [handleDashboard, handleAccounts, handleTransactions];
        for (const handler of handlers) {
            const res = await handler(url, method, body);
            if (res) return res;
        }

        return error('Endpoint not found', 404);
    };
}
