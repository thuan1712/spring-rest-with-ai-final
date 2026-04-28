# Company Module Context

## Responsibilities
- Manage companies.
- Provide CRUD operations for companies.

## Relationships
- A `Company` has many `User`s (OneToMany). Note: This relationship will be fully implemented when the `User` module is created.
- `Company` is independent of `Role` and `Permission`.

## Key Business Rules
- `name` is required and must be unique.
- Deleting a company should set `company_id = null` for all associated users (to be handled properly once users are integrated).

## Known Issues / Technical Debt
- User association is currently deferred. When `User` entity is added, we need to map the `@OneToMany` relationship.
