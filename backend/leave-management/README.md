# LeaveMate Backend

Spring Boot backend for the leave-management application.

## Requirements

- Java 17 or newer
- PostgreSQL/Neon database
- Maven wrapper included in this project

## Environment configuration

Create or update `.env` in this directory:

```env
NEON_DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
NEON_DB_USER=<database-user>
NEON_DB_PASSWORD=<database-password>
JWT_SECRET=<long-random-secret>
ACCESS_TOKEN_LIFE=90000
REFRESH_TOKEN_LIFE=2592000
```

Do not commit `.env` or share database passwords and JWT secrets.

## Run the backend

From this directory:

```bash
cd LeaveManagementSystem/backend/leave-management
./mvnw spring-boot:run
```

The API starts on:

```text
http://localhost:8080
```

Keep this terminal running while using the frontend.

## Build and test

```bash
./mvnw test
./mvnw package
```

To run the packaged application:

```bash
java -jar target/leave-management-0.0.1-SNAPSHOT.jar
```

## Run with Docker

Build the image from this directory:

```bash
docker build -t leavemate-backend .
```

Run it by passing the required configuration as environment variables:

```bash
docker run --rm --name leavemate-backend \
  -p 8080:8080 \
  -e NEON_DB_URL="jdbc:postgresql://<host>/<database>?sslmode=require" \
  -e NEON_DB_USER="<database-user>" \
  -e NEON_DB_PASSWORD="<database-password>" \
  -e JWT_SECRET="<long-random-secret>" \
  -e ACCESS_TOKEN_LIFE="90000" \
  -e REFRESH_TOKEN_LIFE="2592000" \
  leavemate-backend
```

Do not copy `.env` into the image or commit real credentials. For a deployment platform, configure these values as platform environment variables/secrets instead.

## Important API areas

- `/auth/login` - sign in
- `/auth/logout` - sign out
- `/employees/leave` - employee leave history and requests
- `/employees/leave/{id}/withdraw` - applicant-only withdrawal
- `/employees/{id}` - employee details
- `/employees/days/{id}` - paid leave quota
- `/manager/employees` - direct reports for managers
- `/manager/leave` - leave requests in the manager's reporting tree
- `/manager/leave/{id}` - manager leave review

## Troubleshooting

### Port 8080 is already in use

Stop the existing backend process before starting another copy. On Linux:

```bash
lsof -nP -iTCP:8080 -sTCP:LISTEN
kill <PID>
```

Use the numeric PID printed by `lsof`.

### Database connection failure

Check that all `NEON_DB_*` variables are present and correct in `.env`. The URL must use the JDBC format beginning with:

```text
jdbc:postgresql://
```

### Frontend receives `Failed to fetch`

Start the backend first, then confirm that `http://localhost:8080` is reachable. Also check that the frontend `.env` contains:

```env
VITE_BACKEND_URL=http://localhost:8080
```

### Database schema updates

The application currently uses Hibernate schema update mode. When the application starts, it can add newly introduced columns such as `used_paid_leaves` to the existing database.

