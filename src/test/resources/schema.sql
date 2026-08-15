CREATE TABLE fund (
  id TEXT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE investor (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT uq_investor_email UNIQUE (email)
);

CREATE TABLE fund_transaction (
  id TEXT PRIMARY KEY,

  fund_id TEXT NOT NULL,
  investor_id TEXT NOT NULL,

  transaction_type TEXT NOT NULL,
  transaction_effect TEXT NOT NULL,
  amount NUMERIC NOT NULL CHECK (amount > 0),
  transaction_date TEXT NOT NULL,

  description TEXT,

  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_transaction_fund
      FOREIGN KEY (fund_id)
          REFERENCES fund(id)
          ON DELETE RESTRICT,

  CONSTRAINT fk_transaction_investor
      FOREIGN KEY (investor_id)
          REFERENCES investor(id)
          ON DELETE RESTRICT
);
