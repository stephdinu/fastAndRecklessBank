import React, {useEffect, useState} from 'react';

const API_BASE = process.env.REACT_APP_API_BASE || '/api';

// --- Helper function for API requests ---
async function api(path, options) {
    const res = await fetch(`${API_BASE}${path}`, {
        headers: {'Content-Type': 'application/json'},
        ...options,
    });
    if (!res.ok) throw new Error(await res.text());
    try {
        return await res.json();
    } catch {
        return null;
    }
}

export default function App() {
    const [welcome, setWelcome] = useState('');
    const [accounts, setAccounts] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [error, setError] = useState(null);
    const [newAccount, setNewAccount] = useState({
        holder: '',
        contact: '',
        type: 'SAVINGS',
    });
    const [lastCreatedAccount, setLastCreatedAccount] = useState(null);

    // --- DASHBOARD CONTROLLER ---
    const loadDashboard = async () => {
        try {
            const data = await api('/dashboard');
            setWelcome(data?.message || 'Welcome to the Fast & Reckless Bank!');
        } catch (err) {
            setError(err.message);
        }
    };

    // --- ACCOUNT CONTROLLER ---
    const loadAccounts = async () => {
        try {
            const data = await api('/accounts/allAccounts');
            setAccounts(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message);
        }
    };

    const createAccount = async (e) => {
        e.preventDefault();
        try {
            const acc = await api('/accounts/createNewAccount', {
                method: 'POST',
                body: JSON.stringify({
                    holder: newAccount.holder,
                    contact: newAccount.contact,
                    type: newAccount.type,
                }),
            });
            setLastCreatedAccount(acc);
            setAccounts((prev) => [acc, ...prev]);
            setNewAccount({holder: '', contact: '', type: 'SAVINGS'});
        } catch (err) {
            setError(err.message);
        }
    };

    const closeAccount = async (id) => {
        try {
            await api('/accounts/closeAccount', {
                method: 'DELETE',
                body: JSON.stringify(id),
            });
            setAccounts((prev) => prev.filter((a) => a.id !== id));
        } catch (err) {
            setError(err.message);
        }
    };

    // --- TRANSACTION CONTROLLER ---
    const loadTransactionHistory = async (accountNumber) => {
        if (!accountNumber) {
            console.warn("No account number provided for transaction history.");
            return;
        }

        try {
            const data = await api(`/transactions/transactionHistory?accountNumber=${encodeURIComponent(accountNumber)}`);
            setTransactions(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Error loading transaction history:", err);
            setError(err.message);
        }
    };


    const handleTransaction = async (endpoint, payload, successMsg) => {
        try {
            await api(endpoint, {
                method: 'POST',
                body: JSON.stringify(payload),
            });
            alert(successMsg);
            await loadAccounts(); // refresh balances
            await loadTransactionHistory(payload.accountNumber); // refresh transactions
        } catch (err) {
            setError(err.message);
        }
    };

    const depositMoney = async () => {
        const amount = prompt('Enter amount to deposit:');
        if (!amount || isNaN(amount) || parseFloat(amount) <= 0)
            return alert('Please enter a valid positive amount.');

        const accountNumber = prompt("Enter Account Number (e.g., FRB-{Your Account ID}):");
        await handleTransaction(
            '/transactions/deposit',
            {accountNumber, amount: parseFloat(amount)},
            `Deposited €${amount} into Account ${accountNumber}.`
        );
    };

    const withdrawMoney = async () => {
        const amount = prompt('Enter amount to withdraw:');
        if (!amount || isNaN(amount) || parseFloat(amount) <= 0)
            return alert('Please enter a valid positive amount.');

        const accountNumber = prompt("Enter Account Number (e.g., FRB-{Your Account ID}):");
        await handleTransaction(
            '/transactions/withdraw',
            {accountNumber, amount: parseFloat(amount)},
            `Withdrew €${amount} from Account ${accountNumber}.`
        );
    };

    const transferMoney = async () => {
        const toAccountNumber = prompt('Enter destination Account Number (e.g., FRB-{Your Account ID}):');
        const amount = prompt('Enter amount to transfer:');
        if (!amount || isNaN(amount) || parseFloat(amount) <= 0)
            return alert('Please enter a valid amount.');

        const fromAccountNumber = prompt('Enter initiating Account Number (e.g., FRB-{Your Account ID}):');
        await handleTransaction(
            '/transactions/transfer',
            {
                fromAccountId: fromAccountNumber,
                toAccountId: toAccountNumber,
                amount: parseFloat(amount),
            },
            `Transferred €${amount} from ${fromAccountNumber} to ${toAccountNumber}.`
        );
    };

    // --- INITIAL LOAD ---
    useEffect(() => {
        loadDashboard();
        loadAccounts();
    }, []);

    return (
        <div className="p-6 max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold mb-2">Fast & Reckless Bank</h1>
            <p className="mb-6 text-gray-600">{welcome}</p>
            {error && <p className="text-red-500 mb-4">{error}</p>}

            {/* Create Account Form */}
            <section className="mb-8 border p-4 rounded bg-white">
                <h2 className="font-semibold text-lg mb-3">Create Account</h2>
                <form onSubmit={createAccount} className="flex flex-wrap gap-3">
                    <input
                        placeholder="Account Holder Name"
                        value={newAccount.holder}
                        onChange={(e) =>
                            setNewAccount({...newAccount, holder: e.target.value})
                        }
                        className="border p-2 rounded flex-1 min-w-[150px]"
                    />
                    <input
                        placeholder="Contact"
                        value={newAccount.contact}
                        onChange={(e) =>
                            setNewAccount({...newAccount, contact: e.target.value})
                        }
                        className="border p-2 rounded flex-1 min-w-[150px]"
                    />
                    <select
                        value={newAccount.type}
                        onChange={(e) =>
                            setNewAccount({...newAccount, type: e.target.value})
                        }
                        className="border p-2 rounded w-40"
                    >
                        <option value="SAVINGS">Savings</option>
                        <option value="CHECKING">Checking</option>
                    </select>
                    <button
                        type="submit"
                        className="bg-blue-600 text-white px-4 py-2 rounded"
                    >
                        Create
                    </button>
                </form>
                {lastCreatedAccount && (
                    <p className="mt-2 text-green-600">
                        Account created successfully! Your account number is:{' '}
                        <strong>
                            {lastCreatedAccount.accountNumber || lastCreatedAccount.id}
                        </strong>
                    </p>
                )}
            </section>

            {/* Accounts */}
            <section className="mb-8">
                <h2 className="font-semibold text-lg mb-2">Accounts</h2>
                {accounts.length === 0 ? (
                    <p>No accounts yet.</p>
                ) : (
                    accounts.map((acc) => (
                        <AccountCard
                            key={acc.id}
                            account={acc}
                            onDeposit={depositMoney}
                            onWithdraw={withdrawMoney}
                            onTransfer={transferMoney}
                            onDelete={closeAccount}
                            onTransactionHistory={loadTransactionHistory}
                        />
                    ))
                )}
            </section>
        </div>
    );
}

// --- AccountCard Component ---
function AccountCard({account, onDeposit, onWithdraw, onTransfer, onDelete, onTransactionHistory}) {
    const name = account.holder || account.accountHolderName || 'Unknown';
    const type = account.type || account.accountType || 'Unknown';
    const number = account.number || account.accountNumber;

    return (
        <div className="border rounded p-4 mb-3 bg-white shadow-sm">
            <div className="flex justify-between items-center mb-2">
                <h3 className="font-semibold">
                    {name} ({type}) : {number}
                </h3>
                {/*<button
                    onClick={() => onDelete(id)}
                    className="text-sm text-red-500 hover:underline"
                >
                    Close Account
                </button>*/}
            </div>
            <p className="font-mono text-lg mb-3">
                Balance: €{account.balance?.toFixed(2) || 0}
            </p>
            <div className="flex gap-3 mt-3">
                <button
                    onClick={() => onDeposit()}
                    className="bg-green-600 text-white px-4 py-2 rounded"
                >
                    Deposit
                </button>
                <button
                    onClick={() => onWithdraw()}
                    className="bg-yellow-600 text-white px-4 py-2 rounded"
                >
                    Withdraw
                </button>
                <button
                    onClick={() => onTransfer()}
                    className="bg-blue-600 text-white px-4 py-2 rounded"
                >
                    Transfer
                </button>
                <button
                    onClick={() => onTransactionHistory(number)}
                    className="text-sm text-blue-600"
                >
                    View Transaction History
                </button>

            </div>
        </div>
    );
}