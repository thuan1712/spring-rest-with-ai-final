package vn.hoidanit.springrestwithai.feature.permission;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.exception.DuplicateResourceException;
import vn.hoidanit.springrestwithai.exception.ResourceNotFoundException;
import vn.hoidanit.springrestwithai.feature.permission.dto.CreatePermissionRequest;
import vn.hoidanit.springrestwithai.feature.permission.dto.PermissionResponse;
import vn.hoidanit.springrestwithai.feature.permission.dto.UpdatePermissionRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    @DisplayName("Should return paginated permissions")
    void getAllPermissions_returnsPaginatedData() {
        Permission p1 = new Permission();
        p1.setId(1L);
        p1.setName("VIEW_USER");

        Page<Permission> pageData = new PageImpl<>(List.of(p1), PageRequest.of(0, 10), 1);
        when(permissionRepository.findAll(any(PageRequest.class))).thenReturn(pageData);

        PaginationDTO<PermissionResponse> result = permissionService.getAllPermissions(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.meta().total());
        assertEquals(1, result.result().size());
        assertEquals("VIEW_USER", result.result().get(0).name());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when permission not found")
    void getPermissionById_notFound_throwsException() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.getPermissionById(1L));
    }

    @Test
    @DisplayName("Should return permission response when found")
    void getPermissionById_found_returnsResponse() {
        Permission p = new Permission();
        p.setId(1L);
        p.setName("VIEW_USER");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(p));

        PermissionResponse response = permissionService.getPermissionById(1L);

        assertEquals(1L, response.id());
        assertEquals("VIEW_USER", response.name());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when apiPath and method exist on creation")
    void createPermission_duplicatePathMethod_throwsException() {
        CreatePermissionRequest request = new CreatePermissionRequest("CREATE_USER", "/api/v1/users", "POST", "USER");
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/users", "POST")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> permissionService.createPermission(request));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("Should create and return permission successfully")
    void createPermission_validRequest_createsSuccessfully() {
        CreatePermissionRequest request = new CreatePermissionRequest("CREATE_USER", "/api/v1/users", "POST", "USER");
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/users", "POST")).thenReturn(false);

        Permission savedPermission = new Permission();
        savedPermission.setId(1L);
        savedPermission.setName("CREATE_USER");
        when(permissionRepository.save(any(Permission.class))).thenReturn(savedPermission);

        PermissionResponse response = permissionService.createPermission(request);

        assertEquals(1L, response.id());
        assertEquals("CREATE_USER", response.name());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on update if changing to existing apiPath and method")
    void updatePermission_duplicatePathMethod_throwsException() {
        UpdatePermissionRequest request = new UpdatePermissionRequest(1L, "CREATE_PRODUCT", "/api/v1/products", "POST", "PRODUCT");
        
        Permission existing = new Permission();
        existing.setId(1L);
        existing.setApiPath("/api/v1/users");
        existing.setMethod("POST");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/products", "POST")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> permissionService.updatePermission(request));
    }

    @Test
    @DisplayName("Should update permission successfully")
    void updatePermission_validRequest_updatesSuccessfully() {
        UpdatePermissionRequest request = new UpdatePermissionRequest(1L, "UPDATE_USER", "/api/v1/users", "PUT", "USER");
        
        Permission existing = new Permission();
        existing.setId(1L);
        existing.setApiPath("/api/v1/users");
        existing.setMethod("POST");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/users", "PUT")).thenReturn(false);
        
        Permission saved = new Permission();
        saved.setId(1L);
        saved.setName("UPDATE_USER");
        when(permissionRepository.save(any(Permission.class))).thenReturn(saved);

        PermissionResponse response = permissionService.updatePermission(request);
        assertEquals(1L, response.id());
        assertEquals("UPDATE_USER", response.name());
    }

    @Test
    @DisplayName("Should delete permission successfully")
    void deletePermission_existingId_deletesSuccessfully() {
        Permission p = new Permission();
        p.setId(1L);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(p));

        permissionService.deletePermission(1L);

        verify(permissionRepository, times(1)).delete(p);
    }
}
