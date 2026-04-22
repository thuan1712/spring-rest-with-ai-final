# Permission Module

## Domain Concept

A `Permission` represents a single action allowed by the RBAC system, uniquely identified by a combination of `apiPath` and `method` (e.g., `POST` to `/api/v1/users`).
Permissions do not map to individual users directly. Instead:
- A `Role` has many `Permissions`.
- A `User` has many `Roles`.

## Technical Implementation

- **REST Actions**: Supports standard CRUD operations under `/api/v1/permissions`. Fetching is paginated and standardizes the response body using `PaginationDTO` inside `ApiResponse`.
- **Validation**: Enforces unique constraints on `apiPath` + `method` mapped using a custom repository query `boolean existsByApiPathAndMethod(String, String)`. Duplicate additions will throw `DuplicateResourceException` mapping to 409 Conflict.
- **DTOs**: Usage of Java Records (`CreatePermissionRequest`, `UpdatePermissionRequest`, `PermissionResponse`). `NotBlank` validations ensure structural integrity.
- **Security Context**: Currently configured for Phase 0 where JWT is not enforced rigidly on endpoints. `PermissionControllerIntegrationTest` tests utilize `@WithMockUser` to bypass contextual security interceptors to validate core logic functionally.

## Testing Strategy
- Unit Tests: `PermissionServiceImplTest` comprehensively tests the `PermissionService` by utilizing Mockito.
- Integration Tests: `PermissionControllerIntegrationTest` validates full endpoint workflows. These tests load the real `application-test.yml` context connecting to the real configured local Database.

## Relationships
Wait for Phase 2 implementation of `Role` to add `@ManyToMany` bridging from internal entities.
