# Smart Inventory & Reorder System

## Overview

The Smart Inventory & Reorder System is a web-based backend-driven application designed to manage product inventory, track stock movements, and proactively identify reorder needs based on configurable business rules.\
  
The project’s primary objective is not to build a feature-heavy UI, but to demonstrate correct backend architecture, strong separation of concerns, and centralized business logic, following Clean Architecture and SOLID principles.\
  
All business decisions—such as stock validation, movement rules, and reorder calculations—are enforced exclusively in the backend, ensuring consistency, correctness, and testability regardless of the frontend consumer.\
  
## Project Goals
  
### This project seeks to achieve the following:

1. Centralize all business logic in the backend  
- The frontend acts purely as a client
- No business rules exist in Angular or any UI layer
2. Demonstrate Clean Architecture in a real domain
- Framework-independent domain logic
- Explicit use cases
- Clear dependency direction
3. Model a realistic business problem
- Inventory levels
- Stock inflow/outflow
- Reorder thresholds
- Supplier association
4. Produce a portfolio-grade codebase
- Readable structure
- Testable core
- Architecture that can be defended in technical interviews
  
### What the System Does
  
At a high level, the system:\
  
- Manages products and their associated suppliers
- Tracks stock movements (IN / OUT)
- Ensures stock levels never become invalid
- Identifies low-stock products
- Generates reorder suggestions based on defined policies
- Exposes all functionality via a REST API
  
The system is designed so that inventory state can only change through explicit business operations, not direct database manipulation or UI-side logic.\
  
### How the System Works
  
Inventory is not updated directly.\
  
Instead:\
- Every stock change is represented by a Stock Movement
- Stock levels are recalculated and validated in the domain layer
- Invalid operations (e.g. negative stock) are rejected by business rules
  
This approach provides:\
- Full traceability
- Auditability
- Strong invariants
   
### Reorder Logic
  
Each product defines a minimum stock threshold.\
  
The system:\
- Continuously evaluates current stock against this threshold
- Flags products that fall below the minimum
- Generates reorder suggestions using a defined policy (e.g. target stock level)
- Reorder logic is:
- Deterministic
- Centralized
- Easily testable
- Replaceable without changing infrastructure code
  
### Authentication & Access
  
The system supports authenticated access using JWT-based authentication.\
  
Roles determine access to:\
- Product creation
- Stock movement registration
- Dashboard views
- Authorization rules are enforced server-side and are independent of the client.
  
### Architecture & Design Rationale
**Clean Architecture**\
  
This project follows Clean Architecture, enforcing a strict separation of concerns and a clear dependency direction:\
  
*Infrastructure* `->` *Application* `->` *Domain*\
  
Dependencies always point inward, ensuring that core business logic is isolated from frameworks and delivery mechanisms.\

**Layer Responsibilities**\
1. Domain Layer
- Contains core business rules and entities
- Enforces invariants such as non-negative stock
- Has no dependency on frameworks or infrastructure
The domain defines what the business is, independent of technical concerns.\
  
2. Application Layer
- Defines use cases representing system behavior
- Orchestrates domain logic and workflows 
- Depends only on domain abstractions
This layer defines what the system does.\
  
3. Infrastructure Layer
- Handles REST APIs, persistence, security, and configuration
- Maps external requests to application use cases
- Can be replaced without affecting business logic
This layer defines how the system is delivered.\
  
4. Design Principles Applied
- Single Responsibility through clearly scoped classes
- Dependency Inversion by depending on abstractions
- Separation of concerns across architectural layers
  
5. Why This Design Was Chosen
- Centralizes all business logic in the backend
- Prevents logic leakage into controllers or frontend code
- Improves testability and maintainability
- Reflects production-grade backend architecture

### Use Cases
- Create Product
  `->` Register products with required attributes and validations
- Register Stock Movement
  `->` Record stock inflow and outflow through controlled operations
  `->` Reject invalid inventory changes
- View Inventory Status
  `->` Retrieve current stock levels and low-stock indicators
- Generate Reorder Suggestions
  `->` Calculate reorder quantities based on defined policies
- Dashboard Overview
  `->` Provide aggregated, read-only inventory summaries
- Authenticate User
  `->` Authenticate users and enforce role-based access
    
