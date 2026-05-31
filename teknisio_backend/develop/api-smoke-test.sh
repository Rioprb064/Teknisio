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
echo "TEKNISIO STRICT API SMOKE TEST V2"
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

CUSTOMER_PHONE="+62813${SUFFIX}"
OTHER_CUSTOMER_PHONE="+62815${SUFFIX}"
TECH_PHONE="+62814${SUFFIX}"

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
  "description": "Technician created by strict smoke test v2"
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
# 11. CLEANUP SKILL
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
green "ALL STRICT API SMOKE TESTS V2 PASSED"
echo "Passed: $PASS_COUNT"
echo "Failed: $FAIL_COUNT"
echo "Created customer email       : $CUSTOMER_EMAIL"
echo "Created other customer email : $OTHER_CUSTOMER_EMAIL"
echo "Created technician email     : $TECH_EMAIL"
echo "Active serviceRequestId      : $ACTIVE_REQUEST_ID"
echo "Cancelled serviceRequestId   : $CANCEL_REQUEST_ID"
echo "Cancelled serviceRequestCode : $CANCEL_REQUEST_CODE"
echo "============================================================"
