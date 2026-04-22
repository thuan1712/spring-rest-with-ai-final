package vn.hoidanit.springrestwithai.feature.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class PermissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        permissionRepository.deleteAll();
    }

    @Test
    @DisplayName("Should fetch paginated permissions successfully")
    void getAllPermissions_returnsPaginatedData() throws Exception {
        Permission p = new Permission();
        p.setName("VIEW_COMPANIES");
        p.setApiPath("/api/v1/companies");
        p.setMethod("GET");
        p.setModule("COMPANY");
        permissionRepository.save(p);

        mockMvc.perform(get("/api/v1/permissions?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.meta.total").value(1))
                .andExpect(jsonPath("$.data.result[0].name").value("VIEW_COMPANIES"));
    }

    @Test
    @DisplayName("Should create permission successfully")
    void createPermission_validPayload_createsSuccessfully() throws Exception {
        String payload = """
                {
                    "name": "CREATE_USER",
                    "apiPath": "/api/v1/users",
                    "method": "POST",
                    "module": "USER"
                }
                """;

        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.name").value("CREATE_USER"));
    }

    @Test
    @DisplayName("Should return 400 when creating with blank name")
    void createPermission_blankName_returnsBadRequest() throws Exception {
        String payload = """
                {
                    "name": "",
                    "apiPath": "/api/v1/users",
                    "method": "POST",
                    "module": "USER"
                }
                """;

        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("Should return 409 when creating duplicate permission")
    void createPermission_duplicate_returnsConflict() throws Exception {
        Permission p = new Permission();
        p.setName("CREATE_USER_OLD");
        p.setApiPath("/api/v1/users");
        p.setMethod("POST");
        p.setModule("USER");
        permissionRepository.save(p);

        String payload = """
                {
                    "name": "CREATE_USER",
                    "apiPath": "/api/v1/users",
                    "method": "POST",
                    "module": "USER"
                }
                """;

        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent permission")
    void updatePermission_notFound_returns404() throws Exception {
        String payload = """
                {
                    "id": 999999,
                    "name": "UPDATE",
                    "apiPath": "/api/v1/test",
                    "method": "PUT",
                    "module": "TEST"
                }
                """;

        mockMvc.perform(put("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update permission successfully")
    void updatePermission_validPayload_updatesSuccessfully() throws Exception {
        Permission p = new Permission();
        p.setName("OLD_OBJ");
        p.setApiPath("/api/v1/old");
        p.setMethod("GET");
        p.setModule("OLD_MOD");
        p = permissionRepository.save(p);

        String payload = """
                {
                    "id": %d,
                    "name": "NEW_OBJ",
                    "apiPath": "/api/v1/new",
                    "method": "POST",
                    "module": "NEW_MOD"
                }
                """.formatted(p.getId());

        mockMvc.perform(put("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("NEW_OBJ"));
    }

    @Test
    @DisplayName("Should delete permission successfully")
    void deletePermission_existingId_returnsOk() throws Exception {
        Permission p = new Permission();
        p.setName("TO_DELETE");
        p.setApiPath("/api/v1/delete");
        p.setMethod("DELETE");
        p.setModule("TEST");
        p = permissionRepository.save(p);

        mockMvc.perform(delete("/api/v1/permissions/" + p.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/permissions/" + p.getId()))
                .andExpect(status().isNotFound());
    }
}
