-- Create Accounts Table
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    -- For optimistic locking
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_balance_positive CHECK (balance >= 0)
);
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
-- Create Transactions Table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    reference_id VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    -- TRANSFER, DEPOSIT, WITHDRAWAL
    status VARCHAR(20) NOT NULL,
    -- PENDING, POSTED, FAILED
    metadata TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
-- Create Ledger Entries Table (Double Entry Bookkeeping)
CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    -- Positive for Credit, Negative for Debit
    direction VARCHAR(10) NOT NULL,
    -- CREDIT, DEBIT
    balance_after NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    CONSTRAINT fk_ledger_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);
CREATE INDEX idx_ledger_transaction_id ON ledger_entries(transaction_id);
CREATE INDEX idx_ledger_account_id ON ledger_entries(account_id);