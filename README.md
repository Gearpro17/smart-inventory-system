# Smart Inventory & Reorder System

Smart Inventory is a web application for managing products, stock movements, suppliers, and supplier restock orders. Business rules and authorization are enforced by a Spring Boot REST API; the Angular app is its user interface.

## Feature status

### Implemented

- Product directory and creation, current stock, minimum stock, and stock health (`HEALTHY`, `APPROACHING_MINIMUM`, `LOW`).
- Inbound and outbound movements. Movement validation prevents stock from dropping below zero.
- Low-stock and reorder suggestions. Products at or below minimum are low; suggestions target `ceil(1.5 × minimum)` units. A zero minimum produces no suggestion.
- Supplier directory, contact and delivery details, and product-to-supplier assignment.
- Supplier orders with `PENDING`, `CANCELLED`, and `COMPLETED` statuses. Placing an order does not change stock. The order is completed only when its matching inbound movement is recorded.
- JWT sign-in, user management, and persisted role permissions. Admin has all permissions and its permissions are fixed. The last admin cannot be deleted, and users cannot delete themselves.
- Dashboard, product, movement, supplier, reorder, and Access & roles views.

### In progress

- No application feature issue in the Smart Inventory project was marked In Progress at review time.

### Planned

- Future work is tracked in the Linear project board and is not described here as available. The list above describes implemented behavior only.

## Architecture

The backend is organized into domain rules, application use cases, and Spring infrastructure for HTTP, persistence, and security. It uses Spring Boot, Spring Data JPA, and MySQL in Docker; local development and automated backend tests use an in-memory H2 database. The frontend is Angular 21 with server-side rendering and communicates with the API at `http://localhost:8080` by default.

## Prerequisites

- Java 21 for local backend work. The Maven wrapper is included.
- Node.js 22 and npm for local frontend work (`npm ci` installs the locked dependencies).
- Docker Desktop with Docker Compose for the containerized setup.

## Run locally

Run the API and frontend in separate terminals. The `dev` profile uses an in-memory H2 database; its data is discarded when the API stops.

### Backend (PowerShell)

From the repository root:

```powershell
cd inventory
$keyBytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($keyBytes)
$env:INVENTORY_JWT_SECRET = [Convert]::ToBase64String($keyBytes)
$env:INVENTORY_BOOTSTRAP_USERNAME = "admin"
$env:INVENTORY_BOOTSTRAP_PASSWORD = "change-this-local-password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The password must have at least 12 characters. The first administrator is created only if the user table is empty and both bootstrap variables are set. Keep the generated signing key and any real passwords out of source control.

### Frontend

In a second terminal from the repository root:

```powershell
cd webapp
npm ci
npm start
```

Open <http://localhost:4200>. The API listens on <http://localhost:8080>; `GET /actuator/health` is a public health check.

## Run with Docker Compose

Create an untracked `.env` file in the repository root. Compose requires a JWT secret and initial admin credentials. The MySQL password defaults shown below are for local development only; set your own values if needed.

```dotenv
INVENTORY_JWT_SECRET=<base64-encoded-random-value-at-least-32-bytes>
INVENTORY_BOOTSTRAP_USERNAME=admin
INVENTORY_BOOTSTRAP_PASSWORD=<local-password-at-least-12-characters>
MYSQL_PASSWORD=<local-mysql-password>
MYSQL_ROOT_PASSWORD=<local-mysql-root-password>
```

Generate a signing secret in PowerShell with:

```powershell
$keyBytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($keyBytes)
[Convert]::ToBase64String($keyBytes)
```

Start the app from the repository root:

```bash
docker compose up --build
```

The Angular app is at <http://localhost:4200>, the API is at <http://localhost:8080>, and MySQL data is stored in the `mysql_data` volume. To stop the services, press `Ctrl+C` and run `docker compose down`. To also remove the local database volume and its data, run `docker compose down -v`.

To build only the frontend image, run `docker compose build webapp`.

## Configuration

| Variable | Purpose | Default / requirement |
| --- | --- | --- |
| `INVENTORY_JWT_SECRET` | Base64 HMAC signing key | Required; decoded value must contain at least 32 bytes |
| `INVENTORY_JWT_EXPIRATION_SECONDS` | Token lifetime | `3600`; minimum `60` |
| `INVENTORY_BOOTSTRAP_USERNAME` | Initial admin username | Optional for local API; required by Compose |
| `INVENTORY_BOOTSTRAP_PASSWORD` | Initial admin password | At least 12 characters; required by Compose |
| `INVENTORY_ALLOWED_ORIGINS` | Comma-separated browser origins | `http://localhost:4200,http://127.0.0.1:4200` |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Docker profile MySQL connection | Compose supplies these; local `dev` profile uses H2 |
| `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` | MySQL container credentials | Development defaults are in Compose; override for local deployments |

For an existing production MySQL database using `ddl-auto: validate`, apply the migrations in order before deploying the corresponding code: `supplier_management_mysql.sql`, `jwt_authentication_mysql.sql`, `role_permissions_mysql.sql`, and `restock_supplier_details_mysql.sql`. The Docker profile uses `ddl-auto: update`; the local `dev` profile uses `create-drop`.

## Roles and access

All three roles can read inventory, movements, suppliers, and reorder suggestions by default. `INVENTORY_MANAGER` can also record movements and manage suppliers. `ADMIN` has all permissions, including product creation and user management; admin permissions cannot be edited. Admins can change permissions for Inventory Manager and Viewer in Access & roles. API permission checks are enforced on the server.

Only authenticated requests may use protected endpoints. Send the token returned by login as `Authorization: Bearer <accessToken>`. Invalid or missing credentials return `401`; an authenticated user without the required permission receives `403`. Invalid input and domain rule violations return `400` Problem Details; missing products and suppliers return `404`.

## API workflows

Examples use `curl`; replace `<token>` with a token from the login response.

### Sign in and create a product

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"your-local-password"}'

curl -X POST http://localhost:8080/products/create-product \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"id":"widget-1","name":"Widget","currentStock":0,"minStock":5}'
```

### Add and assign a supplier

```bash
curl -X POST http://localhost:8080/suppliers \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"id":"supplier-1","name":"Acme Supply","contactName":"Alex","email":"alex@example.com","phone":"555-0100","address":"1 Main St","leadTimeDays":5,"taxNumber":"TAX-1"}'

curl -X PUT http://localhost:8080/products/widget-1/supplier \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"supplierId":"supplier-1"}'
```

Supplier fields other than `id` and `name` are optional. Update a supplier with `PUT /suppliers/{supplierId}` and the same fields (the ID remains stable). Supplier list and detail routes are `GET /suppliers` and `GET /suppliers/{supplierId}`.

### Place and receive a supplier order

Create an order for a product that has an assigned supplier. This records a `PENDING` supplier order and does not change inventory:

```bash
curl -X POST http://localhost:8080/supplier-orders \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"productId":"widget-1","quantity":10}'
```

List order history with `GET /supplier-orders`. Cancel a pending order with `POST /supplier-orders/{id}/cancel`. To receive it, record the corresponding inbound movement with the order ID returned at creation. The product and quantity must match the order; only then are stock and order status updated:

```bash
curl -X POST http://localhost:8080/products/widget-1/movements \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"type":"IN","quantity":10,"supplierOrderId":1}'
```

### Inventory and movement reads

- `GET /products` lists products and current stock health.
- `GET /products/low-stock` lists products at or below their minimum.
- `GET /products/reorder-suggestions` returns suggested order quantities.
- `GET /products/{productId}/movements` and `GET /movements` list movement history.
- `POST /products/{productId}/movements` records an `IN` or `OUT` movement; an outbound quantity greater than current stock is rejected.

### User and role management

`POST /auth/users` accepts `{ "username": "operator", "password": "at-least-12-characters", "role": "INVENTORY_MANAGER" }`. `GET /auth/users` lists accounts and `DELETE /auth/users/{username}` removes an account. Users cannot delete themselves, and the last admin account cannot be deleted. `GET /auth/roles` lists permissions; `PUT /auth/roles/{role}` updates Inventory Manager or Viewer permissions. Admin permissions are fixed.

## Build and tests

Backend commands from `inventory`:

```powershell
.\mvnw.cmd -DskipTests package
.\mvnw.cmd test
```

The backend tests use the H2 `dev` profile or in-memory test repositories; no external database is required. JWT tests supply a test-only signing key.

Frontend commands from `webapp`:

```powershell
npm ci
npm run build
npm test -- --watch=false
```

The frontend tests use Angular's unit-test builder with Vitest. `npm ci` requires network access on the first run if dependencies are not already cached.
