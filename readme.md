# Charging Session Management

This application enables to manage charging sessions of an electric vehicle at a charging station.<br>

Summary about the total number of charging sessions,
no. of sessions created and no. of sessions stopped within the specified time limit can also be obtained.

## Charging Session API

### POST /chargingSessions

Creates a new charging session
##### Request
```curl
curl --location --request POST 'http://localhost:8000/chargingSessions' \
--header 'Content-Type: application/json' \
--data-raw '{
    "stationId": "station-101"
}'
```

#### Response
```json
{
  "id": "494ec09c-9501-4785-8fac-13d02c899de9",
  "stationId": "station-101",
  "startedAt": "2021-03-22T22:23:06.577",
  "status": "IN_PROGRESS"
}
```

### GET /chargingSessions
Fetch all charging sessions
#### Request
```curl
curl --location --request GET 'http://localhost:8000/chargingSessions'
```
#### Response
```json
[
    {
        "id": "2a4c1c18-a844-4cd9-a33c-744cd73e2298",
        "stationId": "station-123",
        "startedAt": "2021-03-22T22:26:48.372",
        "status": "IN_PROGRESS"
    },
    {
        "id": "23d927a2-57b6-4595-927d-135f06b3593a",
        "stationId": "asdasd",
        "startedAt": "2021-03-22T22:19:24.619",
        "stoppedAt": "2021-03-22T22:20:24.849",
        "status": "FINISHED"
    },
    {
        "id": "494ec09c-9501-4785-8fac-13d02c899de9",
        "stationId": "station-101",
        "startedAt": "2021-03-22T22:23:06.577",
        "stoppedAt": "2021-03-22T22:23:18.497",
        "status": "FINISHED"
    }
]
```

### PUT /chargingSessions/{id} 
Updates a charging session status as finished with stopped timestamp.

#### Request
```curl
curl --location --request PUT 'http://localhost:8000/chargingSessions/494ec09c-9501-4785-8fac-13d02c899de9'
```

#### Response
```json
{
    "id": "494ec09c-9501-4785-8fac-13d02c899de9",
    "stationId": "station-101",
    "startedAt": "2021-03-22T22:23:06.577",
    "stoppedAt": "2021-03-22T22:23:18.497",
    "status": "FINISHED"
}
```

### GET /chargingSessions/summary

Fetches summary of total sessions handled within given time limit

#### Request
```curl
curl --location --request GET 'http://localhost:8000/chargingSessions/summary'
```

#### Response
```json
{
    "totalCount": 2,
    "startedCount": 1,
    "stoppedCount": 1
}
```

## Build & Documentation

### Build

```build
./gradlew clean build jacocoTestReport
```
Above command creates build artifacts, test artifacts and code coverage report for the unit tests

All the report artifacts are generated and placed at the location specified below.

### Java Documentation

Javadoc for the application is available at "/docs/javadoc/index.html"

### Code Coverage

Code coverage report is available at the path "/docs/jacoco/test/html/index.html"

### Test Report

Test report for the application is available at the path "/docs/tests/test/index.html"








