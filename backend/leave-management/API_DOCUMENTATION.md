# Leave Management System API Documentation

## Table of Contents
- [AuthController](#authcontroller)
- [EmployeeController](#employeecontroller)
- [ManagerController](#managercontroller)
- [Request & Response Structures](#request--response-structures)

---

## AuthController
**Base Path:** `/auth`

| Endpoint   | Method | Request Body | Response Type     | Description         |
|------------|--------|--------------|-------------------|---------------------|
| /login     | POST   | Login        | ResponseEntity<?> | User login          |
| /logout    | POST   | None         | ResponseEntity<?> | User logout         |
| /refresh   | POST   | None         | ResponseEntity<?> | Refresh token       |

### Example: Login
**Request:**
```json
{
  "mail": "user@example.com",
  "password": "string"
}
```
**Response:**
- JWT token or user info (see AuthService)

---

## EmployeeController
**Base Path:** `/employees`

| Endpoint              | Method | Request Body   | Response Type                | Description                      |
|-----------------------|--------|---------------|------------------------------|----------------------------------|
| /days/{empId}         | GET    | None          | LeaveQuota                   | Get leave quota for employee     |
| /{empId}              | GET    | None          | Info                         | Get employee info                |
| /{empId}              | PUT    | UpdateName    | Info                         | Update employee name             |
| /leave                | POST   | Create        | Integer                      | Apply for leave                  |
| /leave                | GET    | None          | List<AllLeave>               | Get all leaves for employee      |
| /leave/{leave_id}     | GET    | None          | IndividualLeaveDetails        | Get details of a specific leave  |
| /leave/{leave_id}     | DELETE | None          | String                       | Delete a leave                   |
| /leave/{leave_id}     | PUT    | Update        | Integer                      | Update a leave                   |

---

## ManagerController
**Base Path:** `/manager`

| Endpoint              | Method | Request Body   | Response Type                | Description                      |
|-----------------------|--------|---------------|------------------------------|----------------------------------|
| /employees            | GET    | None          | List<IndividualEmployeeDetails>| Get all employees               |
| /create               | POST   | CreateEmployee| Info                         | Create a new employee            |
| /days/{empId}         | PUT    | UpdateQuota   | LeaveQuota                   | Edit employee leave quota        |
| /leave/{leave_id}     | GET    | None          | IndividualLeaveDetails        | Get leave details                |
| /leave                | GET    | LeaveFilterReq (query params) | List<LeaveFilter> | Filter leaves         |

---

## Request & Response Structures

### AuthController
- **Login**
  - `mail`: string
  - `password`: string

### EmployeeController
- **UpdateName**
  - `newName`: string
- **Create**
  - `reason`: string
  - `dates`: List<Date>
- **Update**
  - `reason`: string
  - `dates`: List<Date>

### ManagerController
- **CreateEmployee**
  - `name`: string
  - `mail`: string
  - `password`: string
  - `phone_number`: string
  - `manager_id`: int
  - `allowed_paid_days`: int
- **UpdateQuota**
  - `paidDays`: int
- **LeaveFilterReq**
  - (Fields depend on implementation)

### Response Types
- **Info**: Employee information (see response_bodies/EmployeeDetails/EmployeeView/Info.java)
- **LeaveQuota**: Leave quota details (see response_bodies/EmployeeDetails/EmployeeView/LeaveQuota.java)
- **AllLeave**: List of all leaves (see response_bodies/LeaveDetails/EmployeeView/AllLeave.java)
- **IndividualLeaveDetails**: Details of a specific leave (see response_bodies/LeaveDetails/EmployeeView/IndividualLeaveDetails.java or ManagerView)
- **IndividualEmployeeDetails**: Employee details for manager (see response_bodies/EmployeeDetails/ManagerView/IndividualEmployeeDetails.java)
- **LeaveFilter**: Filtered leave details (see response_bodies/LeaveDetails/ManagerView/LeaveFilter.java)

> For exact field definitions, refer to the respective Java classes in the `request_bodies` and `response_bodies` packages.
