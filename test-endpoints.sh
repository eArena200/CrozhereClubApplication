#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Testing Crozhere Club Application Endpoints"
echo "=========================================="

# Function to make API calls and check response
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    local description=$5

    echo -e "\nTesting: ${description}"
    echo "Endpoint: ${endpoint}"
    
    if [ -n "$token" ]; then
        response=$(curl -s -w "\n%{http_code}" -X ${method} "http://localhost:8080${endpoint}" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${token}" \
            ${data:+-d "$data"})
    else
        response=$(curl -s -w "\n%{http_code}" -X ${method} "http://localhost:8080${endpoint}" \
            -H "Content-Type: application/json" \
            ${data:+-d "$data"})
    fi

    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [[ $status_code -ge 200 && $status_code -lt 300 ]]; then
        echo -e "${GREEN}✓ Success (${status_code})${NC}"
        echo "Response: $body"
    else
        echo -e "${RED}✗ Failed (${status_code})${NC}"
        echo "Response: $body"
    fi
}

# 1. Authentication Tests
echo -e "\n${GREEN}Testing Authentication Endpoints${NC}"
echo "----------------------------------------"

# Initialize auth
test_endpoint "POST" "/auth/init" '{"phone": "1234567890"}' "" "Initialize Authentication"

# Wait for OTP (in real scenario, this would be received via SMS)
echo "Please enter the OTP received (for testing, use any 6 digit number):"
read otp

# Verify auth and get token
test_endpoint "POST" "/auth/verify" "{\"phone\": \"1234567890\", \"otp\": \"${otp}\", \"role\": \"PLAYER\"}" "" "Verify Authentication"

# Extract token from response (you'll need to manually copy this from the response)
echo "Please enter the JWT token from the verify response:"
read token

# 2. Player Management Tests
echo -e "\n${GREEN}Testing Player Management Endpoints${NC}"
echo "----------------------------------------"

test_endpoint "GET" "/players/1" "" "$token" "Get Player by ID"
test_endpoint "PUT" "/players/1" '{"username": "testuser", "name": "Test User", "email": "test@example.com"}' "$token" "Update Player"
test_endpoint "DELETE" "/players/1" "" "$token" "Delete Player"

# 3. Club Management Tests
echo -e "\n${GREEN}Testing Club Management Endpoints${NC}"
echo "----------------------------------------"

test_endpoint "POST" "/clubs" '{"name": "Test Club", "clubAdminId": 1}' "$token" "Create Club"
test_endpoint "GET" "/clubs" "" "$token" "Get All Clubs"
test_endpoint "GET" "/clubs/1" "" "$token" "Get Club by ID"
test_endpoint "PUT" "/clubs/1" '{"name": "Updated Club"}' "$token" "Update Club"
test_endpoint "DELETE" "/clubs/1" "" "$token" "Delete Club"

# 4. Club Admin Management Tests
echo -e "\n${GREEN}Testing Club Admin Management Endpoints${NC}"
echo "----------------------------------------"

test_endpoint "GET" "/club-admins/1" "" "$token" "Get Club Admin by ID"
test_endpoint "PUT" "/club-admins/1" '{"name": "Admin User", "email": "admin@example.com"}' "$token" "Update Club Admin"
test_endpoint "DELETE" "/club-admins/1" "" "$token" "Delete Club Admin"

# 5. Booking Management Tests
echo -e "\n${GREEN}Testing Booking Management Endpoints${NC}"
echo "----------------------------------------"

test_endpoint "POST" "/booking" '{
    "playerId": 1,
    "clubId": 1,
    "stationIds": [1, 2],
    "stationType": "CRICKET",
    "startTime": "2024-05-29T10:00:00",
    "endTime": "2024-05-29T12:00:00",
    "players": 2
}' "$token" "Create Booking"

test_endpoint "GET" "/booking/1" "" "$token" "Get Booking by ID"
test_endpoint "PUT" "/booking/1/cancel" "" "$token" "Cancel Booking"
test_endpoint "GET" "/booking/player/1" "" "$token" "List Bookings by Player"
test_endpoint "GET" "/booking/club/1" "" "$token" "List Bookings by Club"

# 6. Club Layout Management Tests
echo -e "\n${GREEN}Testing Club Layout Management Endpoints${NC}"
echo "----------------------------------------"

# Club Layout
test_endpoint "GET" "/api/layouts/clubs/1" "" "$token" "Get Club Layout"

# Zone Layout
test_endpoint "POST" "/api/layouts/zones" '{"clubLayoutId": "1", "name": "Zone 1"}' "$token" "Add Zone Layout"
test_endpoint "GET" "/api/layouts/zones/1" "" "$token" "Get Zone Layout"
test_endpoint "PUT" "/api/layouts/zones/1" '{"name": "Updated Zone"}' "$token" "Update Zone Layout"
test_endpoint "DELETE" "/api/layouts/zones/1" "" "$token" "Delete Zone Layout"

# Station Group Layout
test_endpoint "POST" "/api/layouts/groups" '{
    "zoneLayoutId": "1",
    "name": "Group 1",
    "stationType": "CRICKET",
    "layoutType": "GRID"
}' "$token" "Add Station Group Layout"

test_endpoint "GET" "/api/layouts/groups/1" "" "$token" "Get Station Group Layout"
test_endpoint "PUT" "/api/layouts/groups/1" '{"name": "Updated Group"}' "$token" "Update Station Group Layout"
test_endpoint "DELETE" "/api/layouts/groups/1" "" "$token" "Delete Station Group Layout"

# Station Layout
test_endpoint "GET" "/api/layouts/stations/1" "" "$token" "Get Station Layout"
test_endpoint "PUT" "/api/layouts/stations/1" '{
    "offsetX": 100,
    "offsetY": 100,
    "width": 200,
    "height": 200
}' "$token" "Update Station Layout"

# 7. Availability Check Tests
echo -e "\n${GREEN}Testing Availability Check Endpoints${NC}"
echo "----------------------------------------"

test_endpoint "POST" "/booking/availability/time" '{
    "clubId": 1,
    "stationType": "CRICKET",
    "startTime": "2024-05-29T10:00:00",
    "endTime": "2024-05-29T12:00:00"
}' "$token" "Check Availability by Time"

test_endpoint "POST" "/booking/availability/station" '{
    "clubId": 1,
    "stationType": "CRICKET",
    "stationIds": [1, 2],
    "durationHrs": 2,
    "searchWindow": {
        "dateTime": "2024-05-29T10:00:00",
        "windowHrs": 24
    }
}' "$token" "Check Availability by Station"

echo -e "\n${GREEN}All endpoint tests completed${NC}" 