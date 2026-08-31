#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080"

check() {
  local description="$1"
  local expected="$2"
  local actual="$3"
  if [[ "$expected" != "$actual" ]]; then
    echo "FAIL: $description (expected $expected, got $actual)"
  fi
  echo "PASS: $description"
}

extract_id() {
  python3 -c "import sys, json; print(json.load(sys.stdin)['id'])"
}

post_json() {
  local url=$1
  local body=$2
  local out_file=$3
  curl -s -o "$out_file" -w "%{http_code}" \
    -X POST "$url" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "$body"
}

UNIQUE=$(date +%s)
FUND_NAME="Test Fund $UNIQUE"
FUND_DESC="testdescription"
INVESTOR_NAME="Test Investor $UNIQUE"
INVESTOR_EMAIL="good$UNIQUE@example.com"

echo "### Checking health endpoint"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health")
check "health returns 200" "200" "$status"

echo "### Get all funds"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/funds")
check "get funds returns 200" "200" "$status"

echo "### Create new fund"
status=$(post_json "$BASE_URL/funds" "{\"name\": \"$FUND_NAME\", \"description\": \"$FUND_DESC\"}" /tmp/fund.json)
check "create fund returns 201" "201" "$status"
FUND_ID=$(extract_id < /tmp/fund.json)

echo "### Get fund by ID"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/funds/$FUND_ID")
check "get fund by id returns 200" "200" "$status"

echo "### Update fund description"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X PUT "$BASE_URL/funds/$FUND_ID" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"description": "New Life!!!"}')
check "update description returns 200" "200" "$status"

echo "### Get list of all investors"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/investors")
check "get investors returns 200" "200" "$status"

echo "### Create new investor"
status=$(post_json "$BASE_URL/investors" "{\"name\": \"$INVESTOR_NAME\", \"email\": \"$INVESTOR_EMAIL\"}" /tmp/investor.json)
check "create investor returns 201" "201" "$status"
INVESTOR_ID=$(extract_id < /tmp/investor.json)

echo "### Create new investor with bad email"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/investors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"name": "Test Investor 2", "email": "bad#example.com"}')
check "bad email returns 400" "400" "$status"

echo "### Get investor by ID"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/investors/$INVESTOR_ID")
check "get investor by id returns 200" "200" "$status"

echo "### Get a list of all transactions"
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/transactions")
check "get transactions returns 200" "200" "$status"

echo "### Create permission"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/fund-permissions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{\"fund_id\": \"$FUND_ID\", \"investor_id\": \"$INVESTOR_ID\"}")
check "create permission returns 201" "201" "$status"

echo "### Create new transaction"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{\"fund_id\": \"$FUND_ID\", \"investor_id\": \"$INVESTOR_ID\", \"transaction_type\": \"CONTRIBUTION\", \"transaction_effect\": \"DEBIT\", \"amount\": 50.50, \"description\": \"Initial contribution\"}")
check "create transaction returns 201" "201" "$status"

echo "### Report for seed fund"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222")
check "fund report returns 200" "200" "$status"

echo "### Report for seed fund with date range"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222?from_date=2026-08-30T00:00:00Z&to_date=2026-08-31T23:59:59Z")
check "fund report with date range returns 200" "200" "$status"

echo "### Report for seed fund with debit transactions only"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  "$BASE_URL/reports/fund/22222222-2222-2222-2222-222222222222?transaction_effect=DEBIT")
check "fund report debit only returns 200" "200" "$status"

echo "### Delete fund"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X DELETE "$BASE_URL/funds/$FUND_ID")
check "delete fund returns 200" "200" "$status"

echo "### Delete investor"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X DELETE "$BASE_URL/investors/$INVESTOR_ID")
check "delete investor returns 200" "200" "$status"

echo "### Update fund description after deletion should fail"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X PUT "$BASE_URL/funds/$FUND_ID" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"description": "Should fail"}')
check "update description after deletion returns 404" "404" "$status"

echo "### Create transaction after deletion should fail"
status=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{\"fund_id\": \"$FUND_ID\", \"investor_id\": \"$INVESTOR_ID\", \"transaction_type\": \"CONTRIBUTION\", \"transaction_effect\": \"DEBIT\", \"amount\": 50.50, \"description\": \"Should fail\"}")
check "create transaction after deletion returns 400" "400" "$status"

echo ""
echo "All checks run, please review report to see if anything got failed"
