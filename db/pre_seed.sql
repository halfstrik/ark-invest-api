-- Pre-seed data for local development and manual API testing

INSERT INTO fund (id, name, description, is_deleted)
VALUES ('11111111-1111-1111-1111-111111111111'::uuid, 'To Delete Fund', 'To test deletion', false);

INSERT INTO fund (id, name, description, is_deleted)
VALUES ('22222222-2222-2222-2222-222222222222'::uuid, 'Growth Fund', 'A fund focused on growth stocks', false);

INSERT INTO investor (id, name, email)
VALUES ('33333333-3333-3333-3333-333333333333'::uuid, 'Alice Smith', 'alice@example.com');

INSERT INTO investor (id, name, email)
VALUES ('44444444-4444-4444-4444-444444444444'::uuid, 'Bob Jones', 'bob@example.com');

INSERT INTO fund_transaction (
    id, fund_id, investor_id, transaction_type, transaction_effect,
    amount, transaction_date, description
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid,
    '33333333-3333-3333-3333-333333333333'::uuid,
    'CONTRIBUTION', 'CREDIT', 1000.00, CURRENT_DATE + INTERVAL '12 hours', 'Initial contribution'
);

INSERT INTO fund_transaction (
    id, fund_id, investor_id, transaction_type, transaction_effect,
    amount, transaction_date, description
) VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid,
    '33333333-3333-3333-3333-333333333333'::uuid,
    'DISTRIBUTION', 'DEBIT', 100.00, CURRENT_DATE + INTERVAL '12 hours 30 minutes', 'Quarterly distribution'
);

INSERT INTO fund_transaction (
    id, fund_id, investor_id, transaction_type, transaction_effect,
    amount, transaction_date, description
) VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid,
    '44444444-4444-4444-4444-444444444444'::uuid,
    'INTEREST_INCOME', 'CREDIT', 50.00, CURRENT_DATE + INTERVAL '13 hours', 'Interest income'
);
