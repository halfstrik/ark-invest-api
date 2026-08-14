CREATE TABLE fund (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE investor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_investor_email UNIQUE (email)
);

CREATE TYPE transaction_effect AS ENUM ('CREDIT', 'DEBIT');

CREATE TYPE transaction_type AS ENUM (
    'CONTRIBUTION',
    'INTEREST_INCOME',
    'DISTRIBUTION',
    'GENERAL_EXPENSE',
    'MANAGEMENT_FEE'
);

CREATE TABLE fund_transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    fund_id UUID NOT NULL,
    investor_id UUID NOT NULL,

    transaction_type transaction_type NOT NULL,
    transaction_effect transaction_effect NOT NULL,
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    transaction_date DATE NOT NULL,

    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_transaction_fund
        FOREIGN KEY (fund_id)
            REFERENCES fund(id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_transaction_investor
        FOREIGN KEY (investor_id)
            REFERENCES investor(id)
            ON DELETE RESTRICT
);
