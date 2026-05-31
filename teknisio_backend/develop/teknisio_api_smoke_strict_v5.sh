#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TMP_DIR="$(mktemp -d)"
LAST_BODY="$TMP_DIR/last_response.json"

PASS_COUNT=0
FAIL_COUNT=0

cleanup() {
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT

green() { printf "\033[32m%s\033[0m\n" "$1"; }
red() { printf "\033[31m%s\033[0m\n" "$1"; }
yellow() { printf "\033[33m%s\033[0m\n" "$1"; }

fail() {
  local message="$1"
  red "FAIL $message"
  echo
  echo "Last response body:"
  if [[ -f "$LAST_BODY" ]]; then
    cat "$LAST_BODY"
    echo
  else
    echo "(no response body)"
  fi
  FAIL_COUNT=$((FAIL_COUNT + 1))
  exit 1
}

pass() {
  local message="$1"
  green "PASS $message"
  PASS_COUNT=$((PASS_COUNT + 1))
}

need_cmd() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    fail "Command '$cmd' belum terinstall."
  fi
}

request() {
  local method="$1"
  local path="$2"
  local body="${3:-}"
  local token="${4:-}"

  local args=(-sS -o "$LAST_BODY" -w "%{http_code}" -X "$method" "$BASE_URL$path" -H "Accept: application/json")

  if [[ -n "$body" ]]; then
    args+=(-H "Content-Type: application/json" -d "$body")
  fi

  if [[ -n "$token" ]]; then
    args+=(-H "Authorization: Bearer $token")
  fi

  curl "${args[@]}"
}

assert_status() {
  local name="$1"
  local expected="$2"
  local actual="$3"

  if [[ "$actual" != "$expected" ]]; then
    fail "[$actual != $expected] $name"
  fi

  pass "[$actual] $name"
}

assert_json() {
  local name="$1"
  jq empty "$LAST_BODY" >/dev/null 2>&1 || fail "$name response bukan JSON valid"
  pass "$name JSON valid"
}

assert_jq_equals() {
  local name="$1"
  local jq_expr="$2"
  local expected="$3"

  local actual
  actual="$(jq -r "$jq_expr" "$LAST_BODY")"

  if [[ "$actual" != "$expected" ]]; then
    fail "$name expected '$expected' but got '$actual'"
  fi

  pass "$name = $expected"
}

assert_jq_not_empty() {
  local name="$1"
  local jq_expr="$2"

  local actual
  actual="$(jq -r "$jq_expr" "$LAST_BODY")"

  if [[ -z "$actual" || "$actual" == "null" ]]; then
    fail "$name kosong/null"
  fi

  pass "$name exists"
}

assert_jq_condition() {
  local name="$1"
  shift

  if ! jq -e "$@" "$LAST_BODY" >/dev/null 2>&1; then
    fail "$name condition failed: $*"
  fi

  pass "$name"
}

extract() {
  local jq_expr="$1"
  jq -r "$jq_expr" "$LAST_BODY"
}

need_cmd curl
need_cmd jq

echo "============================================================"
echo "TEKNISIO STRICT API SMOKE TEST V5"
echo "Base URL: $BASE_URL"
echo "============================================================"

# ============================================================
# 0. HEALTH
# ============================================================

CODE="$(request GET "/actuator/health")"
assert_status "GET /actuator/health" "200" "$CODE"
assert_json "GET /actuator/health"
assert_jq_equals "health.status" ".status" "UP"

# ============================================================
# 1. PUBLIC DEVICE CATEGORIES
# ============================================================

CODE="$(request GET "/api/device-categories")"
assert_status "GET /api/device-categories public" "200" "$CODE"
assert_json "GET /api/device-categories"
assert_jq_equals "device categories success" ".success" "true"
assert_jq_equals "device categories message" ".message" "Device categories retrieved successfully"
assert_jq_condition "device categories data is array" ".data | type == \"array\""
assert_jq_condition "device categories not empty" ".data | length >= 1"
assert_jq_condition "Air Conditioner exists" '.data | any(.name == "Air Conditioner")'
assert_jq_condition "Refrigerator exists" '.data | any(.name == "Refrigerator")'
assert_jq_condition "Washing Machine exists" '.data | any(.name == "Washing Machine")'

AC_ID="$(jq -r '.data[] | select(.name == "Air Conditioner") | .deviceCategoryId' "$LAST_BODY" | head -n 1)"
REF_ID="$(jq -r '.data[] | select(.name == "Refrigerator") | .deviceCategoryId' "$LAST_BODY" | head -n 1)"

[[ -n "$AC_ID" && "$AC_ID" != "null" ]] || fail "Air Conditioner category id tidak ditemukan"
[[ -n "$REF_ID" && "$REF_ID" != "null" ]] || fail "Refrigerator category id tidak ditemukan"

CODE="$(request GET "/api/device-categories/$AC_ID")"
assert_status "GET /api/device-categories/{deviceCategoryId}" "200" "$CODE"
assert_json "GET /api/device-categories/{deviceCategoryId}"
assert_jq_equals "device category detail success" ".success" "true"
assert_jq_equals "device category detail id" ".data.deviceCategoryId" "$AC_ID"
assert_jq_equals "device category detail name" ".data.name" "Air Conditioner"
assert_jq_not_empty "device category detail icon" ".data.icon"

CODE="$(request GET "/api/device-categories/salah-id")"
assert_status "GET /api/device-categories invalid UUID" "400" "$CODE"
assert_json "GET /api/device-categories invalid UUID"
assert_jq_equals "device category invalid success" ".success" "false"
assert_jq_equals "device category invalid data" ".data" "null"

CODE="$(request GET "/api/device-categories/00000000-0000-0000-0000-000000000000")"
assert_status "GET /api/device-categories fake UUID" "404" "$CODE"
assert_json "GET /api/device-categories fake UUID"
assert_jq_equals "device category not found success" ".success" "false"

# ============================================================
# 2. REGISTER UNIQUE CUSTOMER, SECOND CUSTOMER, & TECHNICIAN
# ============================================================

TS="$(date +%H%M%S)"
RAND="$((RANDOM % 900 + 100))"
SUFFIX="${TS}${RAND}"
PASSWORD="password123"

CUSTOMER_EMAIL="smoke.customer.${SUFFIX}@mail.com"
OTHER_CUSTOMER_EMAIL="smoke.other.customer.${SUFFIX}@mail.com"
TECH_EMAIL="smoke.technician.${SUFFIX}@mail.com"
OTHER_TECH_EMAIL="smoke.other.technician.${SUFFIX}@mail.com"

CUSTOMER_PHONE="+62813${SUFFIX}"
OTHER_CUSTOMER_PHONE="+62815${SUFFIX}"
TECH_PHONE="+62814${SUFFIX}"
OTHER_TECH_PHONE="+62816${SUFFIX}"

CUSTOMER_REGISTER_BODY="$(cat <<JSON
{
  "name": "Smoke Customer ${SUFFIX}",
  "email": "${CUSTOMER_EMAIL}",
  "phoneNumber": "${CUSTOMER_PHONE}",
  "password": "${PASSWORD}",
  "address": "Jl. Smoke Test Customer"
}
JSON
)"

OTHER_CUSTOMER_REGISTER_BODY="$(cat <<JSON
{
  "name": "Smoke Other Customer ${SUFFIX}",
  "email": "${OTHER_CUSTOMER_EMAIL}",
  "phoneNumber": "${OTHER_CUSTOMER_PHONE}",
  "password": "${PASSWORD}",
  "address": "Jl. Smoke Test Other Customer"
}
JSON
)"

TECH_REGISTER_BODY="$(cat <<JSON
{
  "name": "Smoke Technician ${SUFFIX}",
  "email": "${TECH_EMAIL}",
  "phoneNumber": "${TECH_PHONE}",
  "password": "${PASSWORD}",
  "address": "Jl. Smoke Test Technician",
  "description": "Technician created by strict smoke test v3"
}
JSON
)"

OTHER_TECH_REGISTER_BODY="$(cat <<JSON
{
  "name": "Smoke Other Technician ${SUFFIX}",
  "email": "${OTHER_TECH_EMAIL}",
  "phoneNumber": "${OTHER_TECH_PHONE}",
  "password": "${PASSWORD}",
  "address": "Jl. Smoke Test Other Technician",
  "description": "Other technician created by strict smoke test v3"
}
JSON
)"

CODE="$(request POST "/api/auth/register/customer" "$CUSTOMER_REGISTER_BODY")"
assert_status "POST /api/auth/register/customer" "201" "$CODE"
assert_json "POST /api/auth/register/customer"
assert_jq_equals "register customer success" ".success" "true"
assert_jq_equals "register customer email" ".data.user.email" "$CUSTOMER_EMAIL"
assert_jq_equals "register customer role" ".data.user.role" "CUSTOMER"
assert_jq_not_empty "register customer accessToken" ".data.accessToken"
assert_jq_not_empty "register customer userId" ".data.user.userId"

CUSTOMER_TOKEN="$(extract '.data.accessToken')"
CUSTOMER_ID="$(extract '.data.user.userId')"

CODE="$(request POST "/api/auth/register/customer" "$CUSTOMER_REGISTER_BODY")"
assert_status "POST /api/auth/register/customer duplicate" "409" "$CODE"
assert_json "POST /api/auth/register/customer duplicate"
assert_jq_equals "register customer duplicate success" ".success" "false"

CODE="$(request POST "/api/auth/register/customer" "$OTHER_CUSTOMER_REGISTER_BODY")"
assert_status "POST /api/auth/register/customer second customer" "201" "$CODE"
assert_json "POST /api/auth/register/customer second customer"
assert_jq_equals "register second customer success" ".success" "true"
assert_jq_equals "register second customer email" ".data.user.email" "$OTHER_CUSTOMER_EMAIL"
assert_jq_equals "register second customer role" ".data.user.role" "CUSTOMER"
assert_jq_not_empty "register second customer accessToken" ".data.accessToken"

OTHER_CUSTOMER_TOKEN="$(extract '.data.accessToken')"
OTHER_CUSTOMER_ID="$(extract '.data.user.userId')"

CODE="$(request POST "/api/auth/register/technician" "$TECH_REGISTER_BODY")"
assert_status "POST /api/auth/register/technician" "201" "$CODE"
assert_json "POST /api/auth/register/technician"
assert_jq_equals "register technician success" ".success" "true"
assert_jq_equals "register technician email" ".data.user.email" "$TECH_EMAIL"
assert_jq_equals "register technician role" ".data.user.role" "TECHNICIAN"
assert_jq_not_empty "register technician accessToken" ".data.accessToken"
assert_jq_not_empty "register technician userId" ".data.user.userId"
assert_jq_not_empty "register technician technicianProfileId" ".data.user.technicianProfileId"

TECH_TOKEN="$(extract '.data.accessToken')"
TECHNICIAN_USER_ID="$(extract '.data.user.userId')"
TECHNICIAN_PROFILE_ID="$(extract '.data.user.technicianProfileId')"

CODE="$(request POST "/api/auth/register/technician" "$TECH_REGISTER_BODY")"
assert_status "POST /api/auth/register/technician duplicate" "409" "$CODE"
assert_json "POST /api/auth/register/technician duplicate"
assert_jq_equals "register technician duplicate success" ".success" "false"

CODE="$(request POST "/api/auth/register/technician" "$OTHER_TECH_REGISTER_BODY")"
assert_status "POST /api/auth/register/technician second technician" "201" "$CODE"
assert_json "POST /api/auth/register/technician second technician"
assert_jq_equals "register second technician success" ".success" "true"
assert_jq_equals "register second technician email" ".data.user.email" "$OTHER_TECH_EMAIL"
assert_jq_equals "register second technician role" ".data.user.role" "TECHNICIAN"
assert_jq_not_empty "register second technician accessToken" ".data.accessToken"
assert_jq_not_empty "register second technician userId" ".data.user.userId"
assert_jq_not_empty "register second technician technicianProfileId" ".data.user.technicianProfileId"

OTHER_TECH_TOKEN="$(extract '.data.accessToken')"
OTHER_TECHNICIAN_USER_ID="$(extract '.data.user.userId')"
OTHER_TECHNICIAN_PROFILE_ID="$(extract '.data.user.technicianProfileId')"

CODE="$(request POST "/api/auth/register/customer" '{"name":"","email":"bad-email","phoneNumber":"123","password":"1","address":""}')"
assert_status "POST /api/auth/register/customer invalid validation" "400" "$CODE"
assert_json "POST /api/auth/register/customer invalid validation"
assert_jq_equals "register customer invalid success" ".success" "false"
assert_jq_equals "register customer invalid message" ".message" "Validation failed"

# ============================================================
# 3. LOGIN
# ============================================================

CUSTOMER_LOGIN_BODY="$(cat <<JSON
{
  "email": "${CUSTOMER_EMAIL}",
  "password": "${PASSWORD}"
}
JSON
)"

OTHER_CUSTOMER_LOGIN_BODY="$(cat <<JSON
{
  "email": "${OTHER_CUSTOMER_EMAIL}",
  "password": "${PASSWORD}"
}
JSON
)"

TECH_LOGIN_BODY="$(cat <<JSON
{
  "email": "${TECH_EMAIL}",
  "password": "${PASSWORD}"
}
JSON
)"

OTHER_TECH_LOGIN_BODY="$(cat <<JSON
{
  "email": "${OTHER_TECH_EMAIL}",
  "password": "${PASSWORD}"
}
JSON
)"

CODE="$(request POST "/api/auth/login" "$CUSTOMER_LOGIN_BODY")"
assert_status "POST /api/auth/login customer" "200" "$CODE"
assert_json "POST /api/auth/login customer"
assert_jq_equals "login customer success" ".success" "true"
assert_jq_equals "login customer email" ".data.user.email" "$CUSTOMER_EMAIL"
assert_jq_equals "login customer role" ".data.user.role" "CUSTOMER"
assert_jq_not_empty "login customer accessToken" ".data.accessToken"
CUSTOMER_TOKEN="$(extract '.data.accessToken')"

CODE="$(request POST "/api/auth/login" "$OTHER_CUSTOMER_LOGIN_BODY")"
assert_status "POST /api/auth/login second customer" "200" "$CODE"
assert_json "POST /api/auth/login second customer"
assert_jq_equals "login second customer success" ".success" "true"
assert_jq_equals "login second customer email" ".data.user.email" "$OTHER_CUSTOMER_EMAIL"
assert_jq_equals "login second customer role" ".data.user.role" "CUSTOMER"
assert_jq_not_empty "login second customer accessToken" ".data.accessToken"
OTHER_CUSTOMER_TOKEN="$(extract '.data.accessToken')"

CODE="$(request POST "/api/auth/login" "$TECH_LOGIN_BODY")"
assert_status "POST /api/auth/login technician" "200" "$CODE"
assert_json "POST /api/auth/login technician"
assert_jq_equals "login technician success" ".success" "true"
assert_jq_equals "login technician email" ".data.user.email" "$TECH_EMAIL"
assert_jq_equals "login technician role" ".data.user.role" "TECHNICIAN"
assert_jq_not_empty "login technician accessToken" ".data.accessToken"
TECH_TOKEN="$(extract '.data.accessToken')"

CODE="$(request POST "/api/auth/login" "$OTHER_TECH_LOGIN_BODY")"
assert_status "POST /api/auth/login second technician" "200" "$CODE"
assert_json "POST /api/auth/login second technician"
assert_jq_equals "login second technician success" ".success" "true"
assert_jq_equals "login second technician email" ".data.user.email" "$OTHER_TECH_EMAIL"
assert_jq_equals "login second technician role" ".data.user.role" "TECHNICIAN"
assert_jq_not_empty "login second technician accessToken" ".data.accessToken"
OTHER_TECH_TOKEN="$(extract '.data.accessToken')"

CODE="$(request POST "/api/auth/login" '{"email":"bad-email","password":""}')"
assert_status "POST /api/auth/login invalid validation" "400" "$CODE"
assert_json "POST /api/auth/login invalid validation"
assert_jq_equals "login invalid success" ".success" "false"
assert_jq_equals "login invalid message" ".message" "Validation failed"

CODE="$(request POST "/api/auth/login" '{"email":"notfound@mail.com","password":"password123"}')"
assert_status "POST /api/auth/login wrong credentials" "401" "$CODE"
assert_json "POST /api/auth/login wrong credentials"
assert_jq_equals "login wrong credentials success" ".success" "false"

# ============================================================
# 4. PROFILE & SECURITY
# ============================================================

CODE="$(request GET "/api/auth/profile" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/auth/profile customer" "200" "$CODE"
assert_json "GET /api/auth/profile customer"
assert_jq_equals "profile customer success" ".success" "true"
assert_jq_equals "profile customer userId" ".data.userId" "$CUSTOMER_ID"
assert_jq_equals "profile customer email" ".data.email" "$CUSTOMER_EMAIL"
assert_jq_equals "profile customer role" ".data.role" "CUSTOMER"

CODE="$(request GET "/api/auth/profile" "" "$OTHER_CUSTOMER_TOKEN")"
assert_status "GET /api/auth/profile second customer" "200" "$CODE"
assert_json "GET /api/auth/profile second customer"
assert_jq_equals "profile second customer success" ".success" "true"
assert_jq_equals "profile second customer userId" ".data.userId" "$OTHER_CUSTOMER_ID"
assert_jq_equals "profile second customer email" ".data.email" "$OTHER_CUSTOMER_EMAIL"
assert_jq_equals "profile second customer role" ".data.role" "CUSTOMER"

CODE="$(request GET "/api/auth/profile" "" "$TECH_TOKEN")"
assert_status "GET /api/auth/profile technician" "200" "$CODE"
assert_json "GET /api/auth/profile technician"
assert_jq_equals "profile technician success" ".success" "true"
assert_jq_equals "profile technician userId" ".data.userId" "$TECHNICIAN_USER_ID"
assert_jq_equals "profile technician email" ".data.email" "$TECH_EMAIL"
assert_jq_equals "profile technician role" ".data.role" "TECHNICIAN"

CODE="$(request GET "/api/auth/profile")"
assert_status "GET /api/auth/profile no token" "401" "$CODE"
assert_json "GET /api/auth/profile no token"
assert_jq_equals "profile no token success" ".success" "false"
assert_jq_equals "profile no token message" ".message" "Unauthorized"

CODE="$(request GET "/api/auth/profile" "" "invalid.token.value")"
assert_status "GET /api/auth/profile invalid token" "401" "$CODE"
assert_json "GET /api/auth/profile invalid token"
assert_jq_equals "profile invalid token success" ".success" "false"

# ============================================================
# 5. TECHNICIAN DEVICE CATEGORIES
# ============================================================

CODE="$(request GET "/api/technicians/device-categories" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/device-categories technician" "200" "$CODE"
assert_json "GET /api/technicians/device-categories technician"
assert_jq_equals "technician categories initial success" ".success" "true"
assert_jq_condition "technician categories initial data array" ".data | type == \"array\""

ADD_SKILL_BODY="$(cat <<JSON
{
  "deviceCategoryId": "${AC_ID}"
}
JSON
)"

CODE="$(request POST "/api/technicians/device-categories" "$ADD_SKILL_BODY" "$TECH_TOKEN")"
assert_status "POST /api/technicians/device-categories add AC" "201" "$CODE"
assert_json "POST /api/technicians/device-categories add AC"
assert_jq_equals "add AC success" ".success" "true"
assert_jq_equals "add AC category id" ".data.deviceCategoryId" "$AC_ID"
assert_jq_equals "add AC category name" ".data.name" "Air Conditioner"
assert_jq_equals "add AC active" ".data.active" "true"

CODE="$(request GET "/api/technicians/device-categories" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/device-categories after add" "200" "$CODE"
assert_json "GET /api/technicians/device-categories after add"
assert_jq_condition "AC skill exists after add" --arg ac "$AC_ID" '.data | any(.deviceCategoryId == $ac and .active == true)'

CODE="$(request POST "/api/technicians/device-categories" "$ADD_SKILL_BODY" "$TECH_TOKEN")"
assert_status "POST /api/technicians/device-categories duplicate" "409" "$CODE"
assert_json "POST /api/technicians/device-categories duplicate"
assert_jq_equals "duplicate skill success" ".success" "false"

CODE="$(request POST "/api/technicians/device-categories" '{"deviceCategoryId":"salah-id"}' "$TECH_TOKEN")"
assert_status "POST /api/technicians/device-categories invalid UUID" "400" "$CODE"
assert_json "POST /api/technicians/device-categories invalid UUID"
assert_jq_equals "add skill invalid success" ".success" "false"

CODE="$(request GET "/api/technicians/device-categories" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/technicians/device-categories with customer token" "403" "$CODE"
assert_json "GET /api/technicians/device-categories with customer token"
assert_jq_equals "technician categories customer forbidden success" ".success" "false"
assert_jq_equals "technician categories customer forbidden message" ".message" "Forbidden"

CODE="$(request POST "/api/technicians/device-categories" "$ADD_SKILL_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/technicians/device-categories with customer token" "403" "$CODE"
assert_json "POST /api/technicians/device-categories with customer token"
assert_jq_equals "add skill customer forbidden success" ".success" "false"

CODE="$(request GET "/api/technicians/device-categories")"
assert_status "GET /api/technicians/device-categories no token" "401" "$CODE"
assert_json "GET /api/technicians/device-categories no token"
assert_jq_equals "technician categories no token success" ".success" "false"

# ============================================================
# 6. CUSTOMER SEARCH TECHNICIAN
# ============================================================

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians by category" "200" "$CODE"
assert_json "GET /api/customers/technicians by category"
assert_jq_equals "search technicians success" ".success" "true"
assert_jq_condition "search technicians data array" ".data | type == \"array\""
assert_jq_condition "new technician appears in search" --arg id "$TECHNICIAN_PROFILE_ID" '.data | any(.technicianProfileId == $id)'
assert_jq_condition "new technician has AC in supported categories" --arg id "$TECHNICIAN_PROFILE_ID" --arg ac "$AC_ID" '.data[] | select(.technicianProfileId == $id) | .supportedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&availabilityStatus=OFFLINE" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians filter availability" "200" "$CODE"
assert_json "GET /api/customers/technicians filter availability"
assert_jq_equals "search availability success" ".success" "true"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&sort=rating" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians sort rating" "200" "$CODE"
assert_json "GET /api/customers/technicians sort rating"
assert_jq_equals "search sort rating success" ".success" "true"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&sort=totalJobs" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians sort totalJobs" "200" "$CODE"
assert_json "GET /api/customers/technicians sort totalJobs"
assert_jq_equals "search sort totalJobs success" ".success" "true"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&sort=name" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians sort name" "200" "$CODE"
assert_json "GET /api/customers/technicians sort name"
assert_jq_equals "search sort name success" ".success" "true"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&availabilityStatus=SALAH" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians invalid availabilityStatus" "400" "$CODE"
assert_json "GET /api/customers/technicians invalid availabilityStatus"
assert_jq_equals "search invalid availability success" ".success" "false"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}&sort=SALAH" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians invalid sort" "400" "$CODE"
assert_json "GET /api/customers/technicians invalid sort"
assert_jq_equals "search invalid sort success" ".success" "false"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=salah-id" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians invalid deviceCategoryId" "400" "$CODE"
assert_json "GET /api/customers/technicians invalid deviceCategoryId"
assert_jq_equals "search invalid category success" ".success" "false"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=00000000-0000-0000-0000-000000000000" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians fake category" "404" "$CODE"
assert_json "GET /api/customers/technicians fake category"
assert_jq_equals "search fake category success" ".success" "false"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}" "" "$TECH_TOKEN")"
assert_status "GET /api/customers/technicians with technician token" "403" "$CODE"
assert_json "GET /api/customers/technicians with technician token"
assert_jq_equals "search technician token success" ".success" "false"

CODE="$(request GET "/api/customers/technicians?deviceCategoryId=${AC_ID}")"
assert_status "GET /api/customers/technicians no token" "401" "$CODE"
assert_json "GET /api/customers/technicians no token"
assert_jq_equals "search no token success" ".success" "false"

# ============================================================
# 7. CUSTOMER TECHNICIAN DETAIL
# ============================================================

CODE="$(request GET "/api/customers/technicians/${TECHNICIAN_PROFILE_ID}" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians/{technicianProfileId}" "200" "$CODE"
assert_json "GET /api/customers/technicians/{technicianProfileId}"
assert_jq_equals "technician detail success" ".success" "true"
assert_jq_equals "technician detail id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_condition "technician detail has AC" --arg ac "$AC_ID" '.data.supportedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/customers/technicians/salah-id" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians invalid UUID" "400" "$CODE"
assert_json "GET /api/customers/technicians invalid UUID"
assert_jq_equals "technician detail invalid success" ".success" "false"

CODE="$(request GET "/api/customers/technicians/00000000-0000-0000-0000-000000000000" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/technicians fake UUID" "404" "$CODE"
assert_json "GET /api/customers/technicians fake UUID"
assert_jq_equals "technician detail fake success" ".success" "false"

CODE="$(request GET "/api/customers/technicians/${TECHNICIAN_PROFILE_ID}" "" "$TECH_TOKEN")"
assert_status "GET /api/customers/technicians/{id} with technician token" "403" "$CODE"
assert_json "GET /api/customers/technicians/{id} with technician token"
assert_jq_equals "technician detail technician token success" ".success" "false"

# ============================================================
# 8. CUSTOMER CREATE SERVICE REQUEST
# ============================================================

CREATE_ACTIVE_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v2: active AC request",
  "address": "Jl. Strict Smoke Test Active Request",
  "addressDetail": "Rumah pagar hitam"
}
JSON
)"

CREATE_CANCEL_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v2: cancel AC request",
  "address": "Jl. Strict Smoke Test Cancel Request",
  "addressDetail": "Rumah pagar putih"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_ACTIVE_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests active valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests active valid"
assert_jq_equals "create active request success" ".success" "true"
assert_jq_equals "create active request customerId" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "create active request technicianProfileId" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "create active request status" ".data.status" "WAITING"
assert_jq_equals "create active request issueDescription" ".data.issueDescription" "Strict smoke test v2: active AC request"
assert_jq_equals "create active request address" ".data.address" "Jl. Strict Smoke Test Active Request"
assert_jq_equals "create active request addressDetail" ".data.addressDetail" "Rumah pagar hitam"
assert_jq_equals "create active request cancelReason null" ".data.cancelReason" "null"
assert_jq_equals "create active request cancelledAt null" ".data.cancelledAt" "null"
assert_jq_condition "create active request selected categories length" ".data.selectedDeviceCategories | length == 1"
assert_jq_equals "create active request selected category id" ".data.selectedDeviceCategories[0].deviceCategoryId" "$AC_ID"
assert_jq_equals "create active request selected category name" ".data.selectedDeviceCategories[0].name" "Air Conditioner"
assert_jq_not_empty "create active request serviceRequestId" ".data.serviceRequestId"
assert_jq_not_empty "create active request serviceRequestCode" ".data.serviceRequestCode"
assert_jq_not_empty "create active request requestTime" ".data.requestTime"

ACTIVE_REQUEST_ID="$(extract '.data.serviceRequestId')"
ACTIVE_REQUEST_CODE="$(extract '.data.serviceRequestCode')"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_CANCEL_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests cancel target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests cancel target valid"
assert_jq_equals "create cancel target success" ".success" "true"
assert_jq_equals "create cancel target customerId" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "create cancel target technicianProfileId" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "create cancel target status" ".data.status" "WAITING"
assert_jq_not_empty "create cancel target serviceRequestId" ".data.serviceRequestId"
assert_jq_not_empty "create cancel target serviceRequestCode" ".data.serviceRequestCode"

CANCEL_REQUEST_ID="$(extract '.data.serviceRequestId')"
CANCEL_REQUEST_CODE="$(extract '.data.serviceRequestCode')"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_ACTIVE_REQUEST_BODY")"
assert_status "POST /api/customers/service-requests no token" "401" "$CODE"
assert_json "POST /api/customers/service-requests no token"
assert_jq_equals "create service request no token success" ".success" "false"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_ACTIVE_REQUEST_BODY" "$TECH_TOKEN")"
assert_status "POST /api/customers/service-requests with technician token" "403" "$CODE"
assert_json "POST /api/customers/service-requests with technician token"
assert_jq_equals "create service request technician token success" ".success" "false"

DUPLICATE_CATEGORY_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}",
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test duplicate category",
  "address": "Jl. Strict Smoke Test Duplicate",
  "addressDetail": "Rumah pagar hitam"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$DUPLICATE_CATEGORY_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests duplicate category" "400" "$CODE"
assert_json "POST /api/customers/service-requests duplicate category"
assert_jq_equals "duplicate service category success" ".success" "false"

UNSUPPORTED_CATEGORY_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${REF_ID}"
  ],
  "issueDescription": "Strict smoke test unsupported category",
  "address": "Jl. Strict Smoke Test Unsupported",
  "addressDetail": "Rumah pagar hitam"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$UNSUPPORTED_CATEGORY_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests unsupported category" "400" "$CODE"
assert_json "POST /api/customers/service-requests unsupported category"
assert_jq_equals "unsupported service category success" ".success" "false"

CODE="$(request POST "/api/customers/service-requests" '{"technicianProfileId":"salah-id","deviceCategoryIds":["'"${AC_ID}"'"],"issueDescription":"x","address":"x"}' "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests invalid technician id" "400" "$CODE"
assert_json "POST /api/customers/service-requests invalid technician id"
assert_jq_equals "invalid technician id success" ".success" "false"

CODE="$(request POST "/api/customers/service-requests" '{"technicianProfileId":"00000000-0000-0000-0000-000000000000","deviceCategoryIds":["'"${AC_ID}"'"],"issueDescription":"x","address":"x"}' "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests technician not found" "404" "$CODE"
assert_json "POST /api/customers/service-requests technician not found"
assert_jq_equals "technician not found success" ".success" "false"

CODE="$(request POST "/api/customers/service-requests" '{"technicianProfileId":"'"${TECHNICIAN_PROFILE_ID}"'","deviceCategoryIds":["salah-id"],"issueDescription":"x","address":"x"}' "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests invalid category id" "400" "$CODE"
assert_json "POST /api/customers/service-requests invalid category id"
assert_jq_equals "invalid category id success" ".success" "false"

CODE="$(request POST "/api/customers/service-requests" '{"technicianProfileId":"'"${TECHNICIAN_PROFILE_ID}"'","deviceCategoryIds":[],"issueDescription":"","address":""}' "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests validation failed" "400" "$CODE"
assert_json "POST /api/customers/service-requests validation failed"
assert_jq_equals "service request validation success" ".success" "false"
assert_jq_equals "service request validation message" ".message" "Validation failed"

# ============================================================
# 9. CUSTOMER LIST & DETAIL SERVICE REQUESTS
# ============================================================

CODE="$(request GET "/api/customers/service-requests" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests" "200" "$CODE"
assert_json "GET /api/customers/service-requests"
assert_jq_equals "list service requests success" ".success" "true"
assert_jq_equals "list service requests message" ".message" "Service requests retrieved successfully"
assert_jq_condition "list service requests data array" ".data | type == \"array\""
assert_jq_condition "active request appears in list" --arg id "$ACTIVE_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "cancel target appears in list before cancel" --arg id "$CANCEL_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "active request has selected category in list" --arg id "$ACTIVE_REQUEST_ID" --arg ac "$AC_ID" '.data[] | select(.serviceRequestId == $id) | .selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/customers/service-requests?status=WAITING" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests status WAITING" "200" "$CODE"
assert_json "GET /api/customers/service-requests status WAITING"
assert_jq_equals "list service requests WAITING success" ".success" "true"
assert_jq_condition "all listed requests are WAITING" '.data | all(.status == "WAITING")'
assert_jq_condition "active request appears in WAITING list" --arg id "$ACTIVE_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "cancel target appears in WAITING list before cancel" --arg id "$CANCEL_REQUEST_ID" '.data | any(.serviceRequestId == $id)'

CODE="$(request GET "/api/customers/service-requests?status=waiting" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests lowercase status" "200" "$CODE"
assert_json "GET /api/customers/service-requests lowercase status"
assert_jq_equals "lowercase status success" ".success" "true"

CODE="$(request GET "/api/customers/service-requests?status=SALAH" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests invalid status" "400" "$CODE"
assert_json "GET /api/customers/service-requests invalid status"
assert_jq_equals "invalid status success" ".success" "false"
assert_jq_equals "invalid status message" ".message" "Invalid status. Allowed values: WAITING, ACCEPTED, ON_PROGRESS, COMPLETED, CANCELLED, REJECTED"

CODE="$(request GET "/api/customers/service-requests/${ACTIVE_REQUEST_ID}" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests/{serviceRequestId}" "200" "$CODE"
assert_json "GET /api/customers/service-requests/{serviceRequestId}"
assert_jq_equals "detail service request success" ".success" "true"
assert_jq_equals "detail service request message" ".message" "Service request retrieved successfully"
assert_jq_equals "detail service request id" ".data.serviceRequestId" "$ACTIVE_REQUEST_ID"
assert_jq_equals "detail service request code" ".data.serviceRequestCode" "$ACTIVE_REQUEST_CODE"
assert_jq_equals "detail service request customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "detail service request technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "detail service request status" ".data.status" "WAITING"
assert_jq_equals "detail service request cancelReason null" ".data.cancelReason" "null"
assert_jq_equals "detail service request cancelledAt null" ".data.cancelledAt" "null"
assert_jq_condition "detail service request has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/customers/service-requests/${ACTIVE_REQUEST_ID}" "" "$OTHER_CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests/{id} by non-owner customer" "404" "$CODE"
assert_json "GET /api/customers/service-requests/{id} by non-owner customer"
assert_jq_equals "detail non-owner success" ".success" "false"
assert_jq_equals "detail non-owner message" ".message" "Service request not found"

CODE="$(request GET "/api/customers/service-requests/salah-id" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests invalid UUID" "400" "$CODE"
assert_json "GET /api/customers/service-requests invalid UUID"
assert_jq_equals "detail invalid UUID success" ".success" "false"
assert_jq_equals "detail invalid UUID message" ".message" "Invalid service request id"

CODE="$(request GET "/api/customers/service-requests/00000000-0000-0000-0000-000000000000" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests fake UUID" "404" "$CODE"
assert_json "GET /api/customers/service-requests fake UUID"
assert_jq_equals "detail fake UUID success" ".success" "false"
assert_jq_equals "detail fake UUID message" ".message" "Service request not found"

CODE="$(request GET "/api/customers/service-requests")"
assert_status "GET /api/customers/service-requests no token" "401" "$CODE"
assert_json "GET /api/customers/service-requests no token"
assert_jq_equals "list no token success" ".success" "false"
assert_jq_equals "list no token message" ".message" "Unauthorized"

CODE="$(request GET "/api/customers/service-requests" "" "$TECH_TOKEN")"
assert_status "GET /api/customers/service-requests with technician token" "403" "$CODE"
assert_json "GET /api/customers/service-requests with technician token"
assert_jq_equals "list technician token success" ".success" "false"
assert_jq_equals "list technician token message" ".message" "Forbidden"

CODE="$(request GET "/api/customers/service-requests/${ACTIVE_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET /api/customers/service-requests/{id} with technician token" "403" "$CODE"
assert_json "GET /api/customers/service-requests/{id} with technician token"
assert_jq_equals "detail technician token success" ".success" "false"

# ============================================================
# 10. CUSTOMER CANCEL SERVICE REQUEST
# ============================================================

CANCEL_REASON="Customer membatalkan dari strict smoke test v2"

CANCEL_BODY="$(cat <<JSON
{
  "cancelReason": "${CANCEL_REASON}"
}
JSON
)"

CODE="$(request PATCH "/api/customers/service-requests/${CANCEL_REQUEST_ID}/cancel" "$CANCEL_BODY" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel valid" "200" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel valid"
assert_jq_equals "cancel success" ".success" "true"
assert_jq_equals "cancel message" ".message" "Service request cancelled successfully"
assert_jq_equals "cancel service request id" ".data.serviceRequestId" "$CANCEL_REQUEST_ID"
assert_jq_equals "cancel service request code" ".data.serviceRequestCode" "$CANCEL_REQUEST_CODE"
assert_jq_equals "cancel customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "cancel technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "cancel status" ".data.status" "CANCELLED"
assert_jq_equals "cancel reason" ".data.cancelReason" "$CANCEL_REASON"
assert_jq_not_empty "cancelledAt" ".data.cancelledAt"
assert_jq_condition "cancel response still has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/customers/service-requests/${CANCEL_REQUEST_ID}" "" "$CUSTOMER_TOKEN")"
assert_status "GET cancelled service request detail" "200" "$CODE"
assert_json "GET cancelled service request detail"
assert_jq_equals "cancelled detail success" ".success" "true"
assert_jq_equals "cancelled detail status" ".data.status" "CANCELLED"
assert_jq_equals "cancelled detail cancelReason" ".data.cancelReason" "$CANCEL_REASON"
assert_jq_not_empty "cancelled detail cancelledAt" ".data.cancelledAt"

CODE="$(request GET "/api/customers/service-requests?status=CANCELLED" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests status CANCELLED" "200" "$CODE"
assert_json "GET /api/customers/service-requests status CANCELLED"
assert_jq_equals "list CANCELLED success" ".success" "true"
assert_jq_condition "all listed cancelled requests are CANCELLED" '.data | all(.status == "CANCELLED")'
assert_jq_condition "cancelled request appears in CANCELLED list" --arg id "$CANCEL_REQUEST_ID" '.data | any(.serviceRequestId == $id)'

CODE="$(request GET "/api/customers/service-requests?status=WAITING" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/customers/service-requests status WAITING after cancel" "200" "$CODE"
assert_json "GET /api/customers/service-requests status WAITING after cancel"
assert_jq_condition "cancelled request not in WAITING list" --arg id "$CANCEL_REQUEST_ID" '.data | all(.serviceRequestId != $id)'
assert_jq_condition "active request still in WAITING list" --arg id "$ACTIVE_REQUEST_ID" '.data | any(.serviceRequestId == $id)'

CODE="$(request PATCH "/api/customers/service-requests/${CANCEL_REQUEST_ID}/cancel" '{"cancelReason":"Cancel ulang"}' "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel already CANCELLED" "409" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel already CANCELLED"
assert_jq_equals "cancel already cancelled success" ".success" "false"
assert_jq_equals "cancel already cancelled message" ".message" "Service request cannot be cancelled from status CANCELLED"

CODE="$(request PATCH "/api/customers/service-requests/${ACTIVE_REQUEST_ID}/cancel" '{"cancelReason":"Non-owner mencoba cancel"}' "$OTHER_CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel non-owner customer" "404" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel non-owner customer"
assert_jq_equals "cancel non-owner success" ".success" "false"
assert_jq_equals "cancel non-owner message" ".message" "Service request not found"

CODE="$(request PATCH "/api/customers/service-requests/${ACTIVE_REQUEST_ID}/cancel" '{"cancelReason":"Technician coba cancel"}' "$TECH_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel technician token" "403" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel technician token"
assert_jq_equals "cancel technician token success" ".success" "false"
assert_jq_equals "cancel technician token message" ".message" "Forbidden"

CODE="$(request PATCH "/api/customers/service-requests/${ACTIVE_REQUEST_ID}/cancel" '{"cancelReason":"No token"}')"
assert_status "PATCH /api/customers/service-requests/{id}/cancel no token" "401" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel no token"
assert_jq_equals "cancel no token success" ".success" "false"
assert_jq_equals "cancel no token message" ".message" "Unauthorized"

CODE="$(request PATCH "/api/customers/service-requests/salah-id/cancel" '{"cancelReason":"Invalid UUID"}' "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/salah-id/cancel" "400" "$CODE"
assert_json "PATCH /api/customers/service-requests/salah-id/cancel"
assert_jq_equals "cancel invalid UUID success" ".success" "false"
assert_jq_equals "cancel invalid UUID message" ".message" "Invalid service request id"

CODE="$(request PATCH "/api/customers/service-requests/00000000-0000-0000-0000-000000000000/cancel" '{"cancelReason":"Fake UUID"}' "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests fake UUID cancel" "404" "$CODE"
assert_json "PATCH /api/customers/service-requests fake UUID cancel"
assert_jq_equals "cancel fake UUID success" ".success" "false"
assert_jq_equals "cancel fake UUID message" ".message" "Service request not found"

CODE="$(request PATCH "/api/customers/service-requests/${ACTIVE_REQUEST_ID}/cancel" '{"cancelReason":""}' "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel validation failed" "400" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel validation failed"
assert_jq_equals "cancel validation success" ".success" "false"
assert_jq_equals "cancel validation message" ".message" "Validation failed"
assert_jq_condition "cancel validation has cancelReason error" '.errors.cancelReason != null'

LONG_REASON="$(python3 - <<'PY'
print("x" * 1001)
PY
)"

LONG_REASON_BODY="$(jq -n --arg cancelReason "$LONG_REASON" '{cancelReason:$cancelReason}')"

CODE="$(request PATCH "/api/customers/service-requests/${ACTIVE_REQUEST_ID}/cancel" "$LONG_REASON_BODY" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/customers/service-requests/{id}/cancel reason too long" "400" "$CODE"
assert_json "PATCH /api/customers/service-requests/{id}/cancel reason too long"
assert_jq_equals "cancel too long success" ".success" "false"
assert_jq_equals "cancel too long message" ".message" "Validation failed"
assert_jq_condition "cancel too long has cancelReason error" '.errors.cancelReason != null'


# ============================================================
# 11. TECHNICIAN LIST & DETAIL SERVICE REQUESTS
# ============================================================

CODE="$(request GET "/api/technicians/service-requests" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests" "200" "$CODE"
assert_json "GET /api/technicians/service-requests"
assert_jq_equals "technician list service requests success" ".success" "true"
assert_jq_equals "technician list service requests message" ".message" "Service requests retrieved successfully"
assert_jq_condition "technician list data array" ".data | type == \"array\""
assert_jq_condition "technician active request appears in list" --arg id "$ACTIVE_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "technician cancelled request appears in list" --arg id "$CANCEL_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "technician list active request has customer summary" --arg id "$ACTIVE_REQUEST_ID" --arg customer "$CUSTOMER_ID" '.data[] | select(.serviceRequestId == $id) | .customerId == $customer and (.customerName | length > 0) and (.customerPhoneNumber | length > 0)'
assert_jq_condition "technician list active request has selected AC" --arg id "$ACTIVE_REQUEST_ID" --arg ac "$AC_ID" '.data[] | select(.serviceRequestId == $id) | .selectedDeviceCategories | any(.deviceCategoryId == $ac)'
assert_jq_condition "technician list cancelled request has cancellation data" --arg id "$CANCEL_REQUEST_ID" --arg reason "$CANCEL_REASON" '.data[] | select(.serviceRequestId == $id) | .status == "CANCELLED" and .cancelReason == $reason and .cancelledAt != null'

CODE="$(request GET "/api/technicians/service-requests?status=WAITING" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests status WAITING" "200" "$CODE"
assert_json "GET /api/technicians/service-requests status WAITING"
assert_jq_equals "technician WAITING list success" ".success" "true"
assert_jq_condition "technician WAITING list all WAITING" '.data | all(.status == "WAITING")'
assert_jq_condition "technician active request appears in WAITING list" --arg id "$ACTIVE_REQUEST_ID" '.data | any(.serviceRequestId == $id)'
assert_jq_condition "technician cancelled request not in WAITING list" --arg id "$CANCEL_REQUEST_ID" '.data | all(.serviceRequestId != $id)'

CODE="$(request GET "/api/technicians/service-requests?status=CANCELLED" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests status CANCELLED" "200" "$CODE"
assert_json "GET /api/technicians/service-requests status CANCELLED"
assert_jq_equals "technician CANCELLED list success" ".success" "true"
assert_jq_condition "technician CANCELLED list all CANCELLED" '.data | all(.status == "CANCELLED")'
assert_jq_condition "technician cancelled request appears in CANCELLED list" --arg id "$CANCEL_REQUEST_ID" '.data | any(.serviceRequestId == $id)'

CODE="$(request GET "/api/technicians/service-requests?status=waiting" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests lowercase status" "200" "$CODE"
assert_json "GET /api/technicians/service-requests lowercase status"
assert_jq_equals "technician lowercase status success" ".success" "true"

CODE="$(request GET "/api/technicians/service-requests?status=SALAH" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests invalid status" "400" "$CODE"
assert_json "GET /api/technicians/service-requests invalid status"
assert_jq_equals "technician invalid status success" ".success" "false"
assert_jq_equals "technician invalid status message" ".message" "Invalid status. Allowed values: WAITING, ACCEPTED, ON_PROGRESS, COMPLETED, CANCELLED, REJECTED"

CODE="$(request GET "/api/technicians/service-requests?sort=latest" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests sort latest" "200" "$CODE"
assert_json "GET /api/technicians/service-requests sort latest"
assert_jq_equals "technician sort latest success" ".success" "true"
assert_jq_equals "technician sort latest newest first" ".data[0].serviceRequestId" "$CANCEL_REQUEST_ID"

CODE="$(request GET "/api/technicians/service-requests?sort=oldest" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests sort oldest" "200" "$CODE"
assert_json "GET /api/technicians/service-requests sort oldest"
assert_jq_equals "technician sort oldest success" ".success" "true"
assert_jq_equals "technician sort oldest oldest first" ".data[0].serviceRequestId" "$ACTIVE_REQUEST_ID"

CODE="$(request GET "/api/technicians/service-requests?sort=random" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests invalid sort" "400" "$CODE"
assert_json "GET /api/technicians/service-requests invalid sort"
assert_jq_equals "technician invalid sort success" ".success" "false"
assert_jq_equals "technician invalid sort message" ".message" "Invalid sort. Allowed values: latest, oldest"

CODE="$(request GET "/api/technicians/service-requests")"
assert_status "GET /api/technicians/service-requests no token" "401" "$CODE"
assert_json "GET /api/technicians/service-requests no token"
assert_jq_equals "technician list no token success" ".success" "false"
assert_jq_equals "technician list no token message" ".message" "Unauthorized"

CODE="$(request GET "/api/technicians/service-requests" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/technicians/service-requests with customer token" "403" "$CODE"
assert_json "GET /api/technicians/service-requests with customer token"
assert_jq_equals "technician list customer token success" ".success" "false"
assert_jq_equals "technician list customer token message" ".message" "Forbidden"

CODE="$(request GET "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests/{serviceRequestId}" "200" "$CODE"
assert_json "GET /api/technicians/service-requests/{serviceRequestId}"
assert_jq_equals "technician detail success" ".success" "true"
assert_jq_equals "technician detail message" ".message" "Service request retrieved successfully"
assert_jq_equals "technician detail service request id" ".data.serviceRequestId" "$ACTIVE_REQUEST_ID"
assert_jq_equals "technician detail service request code" ".data.serviceRequestCode" "$ACTIVE_REQUEST_CODE"
assert_jq_equals "technician detail customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_not_empty "technician detail customer name" ".data.customerName"
assert_jq_not_empty "technician detail customer phone number" ".data.customerPhoneNumber"
assert_jq_equals "technician detail technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "technician detail status" ".data.status" "WAITING"
assert_jq_equals "technician detail cancelReason null" ".data.cancelReason" "null"
assert_jq_equals "technician detail cancelledAt null" ".data.cancelledAt" "null"
assert_jq_condition "technician detail has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/technicians/service-requests/${CANCEL_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests/{cancelledServiceRequestId}" "200" "$CODE"
assert_json "GET /api/technicians/service-requests/{cancelledServiceRequestId}"
assert_jq_equals "technician cancelled detail success" ".success" "true"
assert_jq_equals "technician cancelled detail status" ".data.status" "CANCELLED"
assert_jq_equals "technician cancelled detail cancelReason" ".data.cancelReason" "$CANCEL_REASON"
assert_jq_not_empty "technician cancelled detail cancelledAt" ".data.cancelledAt"

CODE="$(request GET "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}" "" "$OTHER_TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests/{id} by non-owner technician" "404" "$CODE"
assert_json "GET /api/technicians/service-requests/{id} by non-owner technician"
assert_jq_equals "technician detail non-owner success" ".success" "false"
assert_jq_equals "technician detail non-owner message" ".message" "Service request not found"

CODE="$(request GET "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}" "" "$CUSTOMER_TOKEN")"
assert_status "GET /api/technicians/service-requests/{id} with customer token" "403" "$CODE"
assert_json "GET /api/technicians/service-requests/{id} with customer token"
assert_jq_equals "technician detail customer token success" ".success" "false"
assert_jq_equals "technician detail customer token message" ".message" "Forbidden"

CODE="$(request GET "/api/technicians/service-requests/salah-id" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests invalid UUID" "400" "$CODE"
assert_json "GET /api/technicians/service-requests invalid UUID"
assert_jq_equals "technician detail invalid UUID success" ".success" "false"
assert_jq_equals "technician detail invalid UUID message" ".message" "Invalid service request id"

CODE="$(request GET "/api/technicians/service-requests/00000000-0000-0000-0000-000000000000" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests fake UUID" "404" "$CODE"
assert_json "GET /api/technicians/service-requests fake UUID"
assert_jq_equals "technician detail fake UUID success" ".success" "false"
assert_jq_equals "technician detail fake UUID message" ".message" "Service request not found"


# ============================================================
# 12. TECHNICIAN ACCEPT & REJECT SERVICE REQUESTS
# ============================================================

CREATE_ACCEPT_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v4: accept AC request",
  "address": "Jl. Strict Smoke Test Accept Request",
  "addressDetail": "Rumah pagar biru"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_ACCEPT_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests accept target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests accept target valid"
assert_jq_equals "create accept target success" ".success" "true"
assert_jq_equals "create accept target customerId" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "create accept target technicianProfileId" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "create accept target status" ".data.status" "WAITING"
assert_jq_equals "create accept target issueDescription" ".data.issueDescription" "Strict smoke test v4: accept AC request"
assert_jq_not_empty "create accept target serviceRequestId" ".data.serviceRequestId"
assert_jq_not_empty "create accept target serviceRequestCode" ".data.serviceRequestCode"

ACCEPT_REQUEST_ID="$(extract '.data.serviceRequestId')"
ACCEPT_REQUEST_CODE="$(extract '.data.serviceRequestCode')"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPT_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept valid" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept valid"
assert_jq_equals "accept success" ".success" "true"
assert_jq_equals "accept message" ".message" "Service request accepted successfully"
assert_jq_equals "accept service request id" ".data.serviceRequestId" "$ACCEPT_REQUEST_ID"
assert_jq_equals "accept service request code" ".data.serviceRequestCode" "$ACCEPT_REQUEST_CODE"
assert_jq_equals "accept customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "accept technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "accept status" ".data.status" "ACCEPTED"
assert_jq_not_empty "accept acceptedAt exists" ".data.acceptedAt"
assert_jq_equals "accept rejectedAt null" ".data.rejectedAt" "null"
assert_jq_equals "accept cancelReason null" ".data.cancelReason" "null"
assert_jq_equals "accept rejectReason null" ".data.rejectReason" "null"
assert_jq_condition "accept response still has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/technicians/service-requests/${ACCEPT_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET accepted service request detail" "200" "$CODE"
assert_json "GET accepted service request detail"
assert_jq_equals "accepted detail success" ".success" "true"
assert_jq_equals "accepted detail status" ".data.status" "ACCEPTED"
assert_jq_not_empty "accepted detail acceptedAt exists" ".data.acceptedAt"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPT_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept already ACCEPTED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept already ACCEPTED"
assert_jq_equals "accept already accepted success" ".success" "false"
assert_jq_equals "accept already accepted message" ".message" "Service request cannot be accepted from status ACCEPTED"

REJECT_ACCEPTED_BODY="$(cat <<JSON
{
  "rejectReason": "Tidak bisa reject request yang sudah accepted"
}
JSON
)"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPT_REQUEST_ID}/reject" "$REJECT_ACCEPTED_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject already ACCEPTED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject already ACCEPTED"
assert_jq_equals "reject already accepted success" ".success" "false"
assert_jq_equals "reject already accepted message" ".message" "Service request cannot be rejected from status ACCEPTED"

CREATE_REJECT_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v4: reject AC request",
  "address": "Jl. Strict Smoke Test Reject Request",
  "addressDetail": "Rumah pagar merah"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_REJECT_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests reject target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests reject target valid"
assert_jq_equals "create reject target success" ".success" "true"
assert_jq_equals "create reject target customerId" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "create reject target technicianProfileId" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "create reject target status" ".data.status" "WAITING"
assert_jq_equals "create reject target issueDescription" ".data.issueDescription" "Strict smoke test v4: reject AC request"
assert_jq_not_empty "create reject target serviceRequestId" ".data.serviceRequestId"
assert_jq_not_empty "create reject target serviceRequestCode" ".data.serviceRequestCode"

REJECT_REQUEST_ID="$(extract '.data.serviceRequestId')"
REJECT_REQUEST_CODE="$(extract '.data.serviceRequestCode')"
REJECT_REASON="Jadwal teknisi penuh"

REJECT_BODY="$(cat <<JSON
{
  "rejectReason": "${REJECT_REASON}"
}
JSON
)"

CODE="$(request PATCH "/api/technicians/service-requests/${REJECT_REQUEST_ID}/reject" "$REJECT_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject valid" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject valid"
assert_jq_equals "reject success" ".success" "true"
assert_jq_equals "reject message" ".message" "Service request rejected successfully"
assert_jq_equals "reject service request id" ".data.serviceRequestId" "$REJECT_REQUEST_ID"
assert_jq_equals "reject service request code" ".data.serviceRequestCode" "$REJECT_REQUEST_CODE"
assert_jq_equals "reject customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "reject technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "reject status" ".data.status" "REJECTED"
assert_jq_equals "reject reason" ".data.rejectReason" "$REJECT_REASON"
assert_jq_not_empty "reject rejectedAt exists" ".data.rejectedAt"
assert_jq_equals "reject acceptedAt null" ".data.acceptedAt" "null"
assert_jq_equals "reject cancelReason null" ".data.cancelReason" "null"
assert_jq_condition "reject response still has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/technicians/service-requests/${REJECT_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET rejected service request detail" "200" "$CODE"
assert_json "GET rejected service request detail"
assert_jq_equals "rejected detail success" ".success" "true"
assert_jq_equals "rejected detail status" ".data.status" "REJECTED"
assert_jq_equals "rejected detail rejectReason" ".data.rejectReason" "$REJECT_REASON"
assert_jq_not_empty "rejected detail rejectedAt exists" ".data.rejectedAt"

CODE="$(request PATCH "/api/technicians/service-requests/${REJECT_REQUEST_ID}/reject" "$REJECT_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject already REJECTED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject already REJECTED"
assert_jq_equals "reject already rejected success" ".success" "false"
assert_jq_equals "reject already rejected message" ".message" "Service request cannot be rejected from status REJECTED"

CODE="$(request PATCH "/api/technicians/service-requests/${REJECT_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept already REJECTED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept already REJECTED"
assert_jq_equals "accept already rejected success" ".success" "false"
assert_jq_equals "accept already rejected message" ".message" "Service request cannot be accepted from status REJECTED"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/accept" "" "$OTHER_TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept non-owner technician" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept non-owner technician"
assert_jq_equals "accept non-owner technician success" ".success" "false"
assert_jq_equals "accept non-owner technician message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/reject" "$REJECT_BODY" "$OTHER_TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject non-owner technician" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject non-owner technician"
assert_jq_equals "reject non-owner technician success" ".success" "false"
assert_jq_equals "reject non-owner technician message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/accept" "" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept customer token" "403" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept customer token"
assert_jq_equals "accept customer token success" ".success" "false"
assert_jq_equals "accept customer token message" ".message" "Forbidden"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/reject" "$REJECT_BODY" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject customer token" "403" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject customer token"
assert_jq_equals "reject customer token success" ".success" "false"
assert_jq_equals "reject customer token message" ".message" "Forbidden"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/accept")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept no token" "401" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept no token"
assert_jq_equals "accept no token success" ".success" "false"
assert_jq_equals "accept no token message" ".message" "Unauthorized"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/reject" "$REJECT_BODY")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject no token" "401" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject no token"
assert_jq_equals "reject no token success" ".success" "false"
assert_jq_equals "reject no token message" ".message" "Unauthorized"

CODE="$(request PATCH "/api/technicians/service-requests/salah-id/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests invalid UUID accept" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests invalid UUID accept"
assert_jq_equals "accept invalid UUID success" ".success" "false"
assert_jq_equals "accept invalid UUID message" ".message" "Invalid service request id"

CODE="$(request PATCH "/api/technicians/service-requests/salah-id/reject" "$REJECT_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests invalid UUID reject" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests invalid UUID reject"
assert_jq_equals "reject invalid UUID success" ".success" "false"
assert_jq_equals "reject invalid UUID message" ".message" "Invalid service request id"

CODE="$(request PATCH "/api/technicians/service-requests/00000000-0000-0000-0000-000000000000/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests fake UUID accept" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests fake UUID accept"
assert_jq_equals "accept fake UUID success" ".success" "false"
assert_jq_equals "accept fake UUID message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/00000000-0000-0000-0000-000000000000/reject" "$REJECT_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests fake UUID reject" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests fake UUID reject"
assert_jq_equals "reject fake UUID success" ".success" "false"
assert_jq_equals "reject fake UUID message" ".message" "Service request not found"

LONG_REJECT_REASON="$(printf 'a%.0s' {1..1001})"
LONG_REJECT_BODY="$(jq -n --arg rejectReason "$LONG_REJECT_REASON" '{rejectReason: $rejectReason}')"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/reject" "$LONG_REJECT_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/reject reason too long" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/reject reason too long"
assert_jq_equals "reject too long success" ".success" "false"
assert_jq_equals "reject too long message" ".message" "Validation failed"
assert_jq_condition "reject too long has rejectReason error" '.errors.rejectReason != null'

CREATE_SKILL_VALIDATION_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v4: accept without active skill",
  "address": "Jl. Strict Smoke Test Skill Validation",
  "addressDetail": "Rumah pagar kuning"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_SKILL_VALIDATION_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests skill validation target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests skill validation target valid"
assert_jq_equals "create skill validation target success" ".success" "true"
assert_jq_equals "create skill validation target status" ".data.status" "WAITING"
assert_jq_not_empty "create skill validation target serviceRequestId" ".data.serviceRequestId"

SKILL_VALIDATION_REQUEST_ID="$(extract '.data.serviceRequestId')"

CODE="$(request DELETE "/api/technicians/device-categories/${AC_ID}" "" "$TECH_TOKEN")"
assert_status "DELETE /api/technicians/device-categories/{deviceCategoryId} before skill validation" "200" "$CODE"
assert_json "DELETE /api/technicians/device-categories/{deviceCategoryId} before skill validation"
assert_jq_equals "delete AC before skill validation success" ".success" "true"

CODE="$(request PATCH "/api/technicians/service-requests/${SKILL_VALIDATION_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept without active skill" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept without active skill"
assert_jq_equals "accept without active skill success" ".success" "false"
assert_jq_equals "accept without active skill message" ".message" "Technician does not support selected device category: Air Conditioner"

CODE="$(request POST "/api/technicians/device-categories" '{"deviceCategoryId":"'"${AC_ID}"'"}' "$TECH_TOKEN")"
assert_status "POST /api/technicians/device-categories restore AC after skill validation" "201" "$CODE"
assert_json "POST /api/technicians/device-categories restore AC after skill validation"
assert_jq_equals "restore AC after skill validation success" ".success" "true"
assert_jq_equals "restore AC after skill validation category id" ".data.deviceCategoryId" "$AC_ID"


# ============================================================
# 13. TECHNICIAN START & COMPLETE SERVICE REQUESTS
# ============================================================

CREATE_WORK_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v5: start complete AC request",
  "address": "Jl. Strict Smoke Test Work Request",
  "addressDetail": "Rumah pagar hijau"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_WORK_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests work target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests work target valid"
assert_jq_equals "create work target success" ".success" "true"
assert_jq_equals "create work target customerId" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "create work target technicianProfileId" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "create work target status" ".data.status" "WAITING"
assert_jq_equals "create work target issueDescription" ".data.issueDescription" "Strict smoke test v5: start complete AC request"
assert_jq_not_empty "create work target serviceRequestId" ".data.serviceRequestId"
assert_jq_not_empty "create work target serviceRequestCode" ".data.serviceRequestCode"

WORK_REQUEST_ID="$(extract '.data.serviceRequestId')"
WORK_REQUEST_CODE="$(extract '.data.serviceRequestCode')"

CODE="$(request PATCH "/api/technicians/service-requests/${WORK_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept work target" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept work target"
assert_jq_equals "accept work target success" ".success" "true"
assert_jq_equals "accept work target status" ".data.status" "ACCEPTED"
assert_jq_not_empty "accept work target acceptedAt exists" ".data.acceptedAt"

CODE="$(request PATCH "/api/technicians/service-requests/${WORK_REQUEST_ID}/start" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/start valid" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start valid"
assert_jq_equals "start success" ".success" "true"
assert_jq_equals "start message" ".message" "Service request started successfully"
assert_jq_equals "start service request id" ".data.serviceRequestId" "$WORK_REQUEST_ID"
assert_jq_equals "start service request code" ".data.serviceRequestCode" "$WORK_REQUEST_CODE"
assert_jq_equals "start customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "start technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "start status" ".data.status" "ON_PROGRESS"
assert_jq_not_empty "start startedAt exists" ".data.startedAt"
assert_jq_not_empty "start acceptedAt still exists" ".data.acceptedAt"
assert_jq_equals "start completedAt null" ".data.completedAt" "null"
assert_jq_equals "start finalCost null" ".data.finalCost" "null"
assert_jq_equals "start technicianNote null" ".data.technicianNote" "null"
assert_jq_condition "start response still has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

COMPLETE_BODY="$(cat <<JSON
{
  "finalCost": 250000,
  "technicianNote": "AC sudah dibersihkan dan freon dicek."
}
JSON
)"

CODE="$(request PATCH "/api/technicians/service-requests/${WORK_REQUEST_ID}/complete" "$COMPLETE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete valid" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete valid"
assert_jq_equals "complete success" ".success" "true"
assert_jq_equals "complete message" ".message" "Service request completed successfully"
assert_jq_equals "complete service request id" ".data.serviceRequestId" "$WORK_REQUEST_ID"
assert_jq_equals "complete service request code" ".data.serviceRequestCode" "$WORK_REQUEST_CODE"
assert_jq_equals "complete customer id" ".data.customerId" "$CUSTOMER_ID"
assert_jq_equals "complete technician id" ".data.technicianProfileId" "$TECHNICIAN_PROFILE_ID"
assert_jq_equals "complete status" ".data.status" "COMPLETED"
assert_jq_condition "complete finalCost saved" '.data.finalCost == 250000'
assert_jq_equals "complete technicianNote saved" ".data.technicianNote" "AC sudah dibersihkan dan freon dicek."
assert_jq_not_empty "complete completedAt exists" ".data.completedAt"
assert_jq_not_empty "complete startedAt still exists" ".data.startedAt"
assert_jq_equals "complete rejectedAt null" ".data.rejectedAt" "null"
assert_jq_equals "complete cancelledAt null" ".data.cancelledAt" "null"
assert_jq_condition "complete response still has selected AC" --arg ac "$AC_ID" '.data.selectedDeviceCategories | any(.deviceCategoryId == $ac)'

CODE="$(request GET "/api/technicians/service-requests/${WORK_REQUEST_ID}" "" "$TECH_TOKEN")"
assert_status "GET completed service request detail" "200" "$CODE"
assert_json "GET completed service request detail"
assert_jq_equals "completed detail success" ".success" "true"
assert_jq_equals "completed detail status" ".data.status" "COMPLETED"
assert_jq_condition "completed detail finalCost saved" '.data.finalCost == 250000'
assert_jq_equals "completed detail technicianNote saved" ".data.technicianNote" "AC sudah dibersihkan dan freon dicek."
assert_jq_not_empty "completed detail completedAt exists" ".data.completedAt"

CODE="$(request GET "/api/technicians/service-requests?status=COMPLETED" "" "$TECH_TOKEN")"
assert_status "GET /api/technicians/service-requests status COMPLETED" "200" "$CODE"
assert_json "GET /api/technicians/service-requests status COMPLETED"
assert_jq_equals "technician COMPLETED list success" ".success" "true"
assert_jq_condition "technician COMPLETED list all COMPLETED" '.data | all(.status == "COMPLETED")'
assert_jq_condition "technician completed request appears in COMPLETED list" --arg id "$WORK_REQUEST_ID" '.data | any(.serviceRequestId == $id)'

CODE="$(request PATCH "/api/technicians/service-requests/${WORK_REQUEST_ID}/start" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/start already COMPLETED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start already COMPLETED"
assert_jq_equals "start already completed success" ".success" "false"
assert_jq_equals "start already completed message" ".message" "Service request cannot be started from status COMPLETED"

CODE="$(request PATCH "/api/technicians/service-requests/${WORK_REQUEST_ID}/complete" "$COMPLETE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete already COMPLETED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete already COMPLETED"
assert_jq_equals "complete already completed success" ".success" "false"
assert_jq_equals "complete already completed message" ".message" "Service request cannot be completed from status COMPLETED"

CREATE_WAITING_START_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v5: start from waiting request",
  "address": "Jl. Strict Smoke Test Start From Waiting",
  "addressDetail": "Rumah pagar abu"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_WAITING_START_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests start from WAITING target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests start from WAITING target valid"
assert_jq_equals "create start from WAITING target success" ".success" "true"
assert_jq_equals "create start from WAITING target status" ".data.status" "WAITING"
assert_jq_not_empty "create start from WAITING target serviceRequestId" ".data.serviceRequestId"

WAITING_START_REQUEST_ID="$(extract '.data.serviceRequestId')"

CODE="$(request PATCH "/api/technicians/service-requests/${WAITING_START_REQUEST_ID}/start" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/start from WAITING" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start from WAITING"
assert_jq_equals "start from WAITING success" ".success" "false"
assert_jq_equals "start from WAITING message" ".message" "Service request cannot be started from status WAITING"

CREATE_ACCEPTED_COMPLETE_REQUEST_BODY="$(cat <<JSON
{
  "technicianProfileId": "${TECHNICIAN_PROFILE_ID}",
  "deviceCategoryIds": [
    "${AC_ID}"
  ],
  "issueDescription": "Strict smoke test v5: complete from accepted request",
  "address": "Jl. Strict Smoke Test Complete From Accepted",
  "addressDetail": "Rumah pagar coklat"
}
JSON
)"

CODE="$(request POST "/api/customers/service-requests" "$CREATE_ACCEPTED_COMPLETE_REQUEST_BODY" "$CUSTOMER_TOKEN")"
assert_status "POST /api/customers/service-requests complete from ACCEPTED target valid" "201" "$CODE"
assert_json "POST /api/customers/service-requests complete from ACCEPTED target valid"
assert_jq_equals "create complete from ACCEPTED target success" ".success" "true"
assert_jq_equals "create complete from ACCEPTED target status" ".data.status" "WAITING"
assert_jq_not_empty "create complete from ACCEPTED target serviceRequestId" ".data.serviceRequestId"

ACCEPTED_COMPLETE_REQUEST_ID="$(extract '.data.serviceRequestId')"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/accept" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/accept complete from ACCEPTED target" "200" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/accept complete from ACCEPTED target"
assert_jq_equals "accept complete from ACCEPTED target success" ".success" "true"
assert_jq_equals "accept complete from ACCEPTED target status" ".data.status" "ACCEPTED"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/complete" "$COMPLETE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete from ACCEPTED" "409" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete from ACCEPTED"
assert_jq_equals "complete from ACCEPTED success" ".success" "false"
assert_jq_equals "complete from ACCEPTED message" ".message" "Service request cannot be completed from status ACCEPTED"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/start" "" "$OTHER_TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/start non-owner technician" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start non-owner technician"
assert_jq_equals "start non-owner technician success" ".success" "false"
assert_jq_equals "start non-owner technician message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/complete" "$COMPLETE_BODY" "$OTHER_TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete non-owner technician" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete non-owner technician"
assert_jq_equals "complete non-owner technician success" ".success" "false"
assert_jq_equals "complete non-owner technician message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/start" "" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/start customer token" "403" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start customer token"
assert_jq_equals "start customer token success" ".success" "false"
assert_jq_equals "start customer token message" ".message" "Forbidden"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/complete" "$COMPLETE_BODY" "$CUSTOMER_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete customer token" "403" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete customer token"
assert_jq_equals "complete customer token success" ".success" "false"
assert_jq_equals "complete customer token message" ".message" "Forbidden"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/start")"
assert_status "PATCH /api/technicians/service-requests/{id}/start no token" "401" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/start no token"
assert_jq_equals "start no token success" ".success" "false"
assert_jq_equals "start no token message" ".message" "Unauthorized"

CODE="$(request PATCH "/api/technicians/service-requests/${ACTIVE_REQUEST_ID}/complete" "$COMPLETE_BODY")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete no token" "401" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete no token"
assert_jq_equals "complete no token success" ".success" "false"
assert_jq_equals "complete no token message" ".message" "Unauthorized"

CODE="$(request PATCH "/api/technicians/service-requests/salah-id/start" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests invalid UUID start" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests invalid UUID start"
assert_jq_equals "start invalid UUID success" ".success" "false"
assert_jq_equals "start invalid UUID message" ".message" "Invalid service request id"

CODE="$(request PATCH "/api/technicians/service-requests/salah-id/complete" "$COMPLETE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests invalid UUID complete" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests invalid UUID complete"
assert_jq_equals "complete invalid UUID success" ".success" "false"
assert_jq_equals "complete invalid UUID message" ".message" "Invalid service request id"

CODE="$(request PATCH "/api/technicians/service-requests/00000000-0000-0000-0000-000000000000/start" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests fake UUID start" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests fake UUID start"
assert_jq_equals "start fake UUID success" ".success" "false"
assert_jq_equals "start fake UUID message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/00000000-0000-0000-0000-000000000000/complete" "$COMPLETE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests fake UUID complete" "404" "$CODE"
assert_json "PATCH /api/technicians/service-requests fake UUID complete"
assert_jq_equals "complete fake UUID success" ".success" "false"
assert_jq_equals "complete fake UUID message" ".message" "Service request not found"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/complete" "" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete missing body" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete missing body"
assert_jq_equals "complete missing body success" ".success" "false"
assert_jq_equals "complete missing body message" ".message" "Invalid request body"

MISSING_FINAL_COST_BODY="$(cat <<JSON
{
  "technicianNote": "Catatan tanpa biaya akhir"
}
JSON
)"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/complete" "$MISSING_FINAL_COST_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete missing finalCost" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete missing finalCost"
assert_jq_equals "complete missing finalCost success" ".success" "false"
assert_jq_equals "complete missing finalCost message" ".message" "Validation failed"
assert_jq_condition "complete missing finalCost has finalCost error" '.errors.finalCost != null'

NEGATIVE_FINAL_COST_BODY="$(cat <<JSON
{
  "finalCost": -1,
  "technicianNote": "Biaya negatif"
}
JSON
)"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/complete" "$NEGATIVE_FINAL_COST_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete negative finalCost" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete negative finalCost"
assert_jq_equals "complete negative finalCost success" ".success" "false"
assert_jq_equals "complete negative finalCost message" ".message" "Validation failed"
assert_jq_condition "complete negative finalCost has finalCost error" '.errors.finalCost != null'

LONG_TECHNICIAN_NOTE="$(printf 'a%.0s' {1..1001})"
LONG_TECHNICIAN_NOTE_BODY="$(jq -n --arg note "$LONG_TECHNICIAN_NOTE" '{finalCost: 250000, technicianNote: $note}')"

CODE="$(request PATCH "/api/technicians/service-requests/${ACCEPTED_COMPLETE_REQUEST_ID}/complete" "$LONG_TECHNICIAN_NOTE_BODY" "$TECH_TOKEN")"
assert_status "PATCH /api/technicians/service-requests/{id}/complete technicianNote too long" "400" "$CODE"
assert_json "PATCH /api/technicians/service-requests/{id}/complete technicianNote too long"
assert_jq_equals "complete technicianNote too long success" ".success" "false"
assert_jq_equals "complete technicianNote too long message" ".message" "Validation failed"
assert_jq_condition "complete technicianNote too long has technicianNote error" '.errors.technicianNote != null'

# ============================================================
# 14. CLEANUP SKILL
# ============================================================

CODE="$(request DELETE "/api/technicians/device-categories/${AC_ID}" "" "$TECH_TOKEN")"
assert_status "DELETE /api/technicians/device-categories/{deviceCategoryId}" "200" "$CODE"
assert_json "DELETE /api/technicians/device-categories/{deviceCategoryId}"
assert_jq_equals "delete skill success" ".success" "true"

CODE="$(request DELETE "/api/technicians/device-categories/${AC_ID}" "" "$TECH_TOKEN")"
assert_status "DELETE /api/technicians/device-categories/{deviceCategoryId} already inactive/not found" "404" "$CODE"
assert_json "DELETE /api/technicians/device-categories/{deviceCategoryId} already inactive/not found"
assert_jq_equals "delete skill second success" ".success" "false"

echo
echo "============================================================"
green "ALL STRICT API SMOKE TESTS V5 PASSED"
echo "Passed: $PASS_COUNT"
echo "Failed: $FAIL_COUNT"
echo "Created customer email       : $CUSTOMER_EMAIL"
echo "Created other customer email : $OTHER_CUSTOMER_EMAIL"
echo "Created technician email     : $TECH_EMAIL"
echo "Created other technician email: $OTHER_TECH_EMAIL"
echo "Active serviceRequestId      : $ACTIVE_REQUEST_ID"
echo "Cancelled serviceRequestId   : $CANCEL_REQUEST_ID"
echo "Cancelled serviceRequestCode : $CANCEL_REQUEST_CODE"
echo "Accepted serviceRequestId   : ${ACCEPT_REQUEST_ID:-}"
echo "Rejected serviceRequestId   : ${REJECT_REQUEST_ID:-}"
echo "Completed serviceRequestId  : ${WORK_REQUEST_ID:-}"
echo "============================================================"
