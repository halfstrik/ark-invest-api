CREATE TABLE fund (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE investor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Since Sqlite does not support enums, we're storing them as VARCHAR(25)
-- CREATE TYPE transaction_effect AS ENUM ('CREDIT', 'DEBIT');
--
-- CREATE TYPE transaction_type AS ENUM (
--     'CONTRIBUTION',
--     'INTEREST_INCOME',
--     'DISTRIBUTION',
--     'GENERAL_EXPENSE',
--     'MANAGEMENT_FEE'
-- );

CREATE TABLE fund_transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fund_id UUID NOT NULL REFERENCES fund(id),
    investor_id UUID NOT NULL REFERENCES investor(id),
    transaction_type VARCHAR(25) NOT NULL,
    transaction_effect VARCHAR(25) NOT NULL,
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    transaction_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
