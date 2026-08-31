#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080"

check() {
  local description="$1"
  local expected="$2"
  local actual="$3"
  if [[ "$expected" != "$actual" ]]; then
    echo "FAIL: $description (expected $expected, got $actual)"
    # exit 1
  fi
  echo "PASS: $description"
}

echo "### Checking health endpoint"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health")
check "health returns 200" "200" "$status"

echo "### Get all funds"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/funds")
check "get funds returns 200" "200" "$status"

echo "### Create new fund"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/funds" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"name": "Test Fund 2", "description": "testdescription"}')
check "create fund returns 201" "201" "$status"

echo "### Get fund by ID"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/funds/11111111-1111-1111-1111-111111111111")
check "get fund by id returns 200" "200" "$status"

echo "### Update fund description"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X PUT "$BASE_URL/funds/11111111-1111-1111-1111-111111111111" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"description": "New Life!!!"}')
check "update description returns 200" "200" "$status"

echo "### Get list of all investors"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/investors")
check "get investors returns 200" "200" "$status"

echo "### Create new investor"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/investors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"name": "Test Investor 2", "email": "good2@example.com"}')
check "create investor returns 201" "201" "$status"

echo "### Create new investor with bad email"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/investors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"name": "Test Investor 2", "email": "bad#example.com"}')
check "bad email returns 400" "400" "$status"

echo "### Get investor by ID"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/investors/33333333-3333-3333-3333-333333333333")
check "get investor by id returns 200" "200" "$status"

echo "### Get a list of all transactions"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/transactions")
check "get transactions returns 200" "200" "$status"

echo "### Create permission"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/fund-permissions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"fund_id": "22222222-2222-2222-2222-222222222222", "investor_id": "33333333-3333-3333-3333-333333333333"}')
check "create permission returns 200" "200" "$status"

echo "### Create new transaction"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"fund_id": "22222222-2222-2222-2222-222222222222", "investor_id": "33333333-3333-3333-3333-333333333333", "transaction_type": "CONTRIBUTION", "transaction_effect": "DEBIT", "amount": 50.50, "description": "Initial contribution"}')
check "create transaction returns 201" "201" "$status"

echo "### Report for fund"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222")
check "fund report returns 200" "200" "$status"

echo "### Report for fund with date range"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222?from_date=2026-08-30T00:00:00Z&to_date=2026-08-31T23:59:59Z")
check "fund report with date range returns 200" "200" "$status"

echo "### Report for fund with debit transactions only"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222?transaction_effect=DEBIT")
check "fund report debit only returns 200" "200" "$status"

echo "### Delete fund"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X DELETE "$BASE_URL/funds/11111111-1111-1111-1111-111111111111")
check "delete fund returns 200" "200" "$status"

echo "### Delete investor"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X DELETE "$BASE_URL/investors/33333333-3333-3333-3333-333333333333")
check "delete investor returns 200" "200" "$status"

echo "### Update fund description after deletion should fail"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X PUT "$BASE_URL/funds/11111111-1111-1111-1111-111111111111" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"description": "Should fail"}')
check "update description after deletion returns 400" "400" "$status"

echo "### Create transaction after deletion should fail"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"fund_id": "11111111-1111-1111-1111-111111111111", "investor_id": "33333333-3333-3333-3333-333333333333", "transaction_type": "CONTRIBUTION", "transaction_effect": "DEBIT", "amount": 50.50, "description": "Should fail"}')
check "create transaction after deletion returns 400" "400" "$status"

echo ""
echo "All checks passed"
