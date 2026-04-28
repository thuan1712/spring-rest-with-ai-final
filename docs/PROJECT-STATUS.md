# Project Status

> Last updated: 2026-04-22 | By: @hoidanit | Session: #1
>
> AI: update this file at the end of every session when asked.
> Follow this exact format. Keep it concise — under 80 lines.

---

## Completed
- ✅ Project skeleton (Spring Boot 4, Maven, application.yml)
- ✅ Documentation setup (CLAUDE.md, PROJECT-RULES, ARCHITECTURE, DATABASE, API_SPEC)
- ✅ ADR-001: Refresh token strategy decided (Cookie + Body)
- ✅ ADR-002: File upload strategy decided (Local Storage + Static Resource Serving)
- ✅ AI workflow setup (.claude/commands/)
- ✅ Phase 0: Foundation (Base Exceptions, ExceptionHandler, ApiResponse, SecurityConfig, JwtConfig)
- ✅ Phase 1: Permission module implemented (CRUD, tests, Context)
- ✅ Phase 2: Role module implemented (CRUD, tests, Context)
- ✅ Phase 3: Company module implemented (CRUD, tests, Context)

## In Progress
_None._

## Deferred Issues
_None._

## Warnings
_None._

## Next Tasks
1. **[P0]** User CRUD + unit test + integration test + CONTEXT.md

## Milestones

### Phase 0 — Foundation
- [x] Base exception classes (AppException, ResourceNotFoundException, DuplicateResourceException)
- [x] GlobalExceptionHandler
- [x] ApiResponse wrapper
- [x] SecurityConfig (permitAll tạm thời)
- [x] JwtConfig (JwtEncoder, JwtDecoder — chuẩn bị sẵn)

### Phase 1 — Independent Entities
- [x] Permission CRUD + unit test + integration test + CONTEXT.md
- [x] Company CRUD + unit test + integration test + CONTEXT.md

### Phase 2 — Role (depends on Permission)
- [x] Role CRUD + ManyToMany Permission + test + CONTEXT.md

### Phase 3 — User (depends on Role + Company)
- [ ] User CRUD + ManyToOne Company + ManyToMany Role + test + CONTEXT.md

### Phase 4 — Authentication
- [ ] CustomUserDetailsService
- [ ] POST /auth/login + POST /auth/register + test
- [ ] Enable JWT enforce in SecurityConfig
- [ ] GET /auth/me + test

### Phase 5 — Refresh Token (ADR-001)
- [ ] RefreshToken entity + repository
- [ ] POST /auth/refresh (cookie SPA + body mobile)
- [ ] POST /auth/logout (revoke + clear cookie)
- [ ] Full auth flow test

### Phase 6 — File Upload (ADR-002)
- [ ] StorageService (upload, delete, getUrl)
- [ ] POST /api/v1/files/upload (multipart/form-data)
- [ ] File validation (size, MIME type whitelist)
- [ ] Integration test

### Phase 7 — RBAC (Permission-based Authorization)
- [ ] Middleware: match request (path + method) → Permission → Role
- [ ] Integrate into SecurityFilterChain
- [ ] Test: 200 (authorized) + 403 (forbidden)
- [ ] Add 401/403 test cases to Phase 1-3 endpoints

### Phase 8 — Polish
- [ ] Pagination + sorting for all list endpoints
- [ ] Search / filter (if needed)
- [ ] Scheduled job: cleanup expired refresh tokens
- [ ] Full review (/review-pr) + final docs update