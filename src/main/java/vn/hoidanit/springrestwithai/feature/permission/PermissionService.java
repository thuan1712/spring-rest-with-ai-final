package vn.hoidanit.springrestwithai.feature.permission;

import org.springframework.data.domain.Pageable;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.feature.permission.dto.CreatePermissionRequest;
import vn.hoidanit.springrestwithai.feature.permission.dto.PermissionResponse;
import vn.hoidanit.springrestwithai.feature.permission.dto.UpdatePermissionRequest;

public interface PermissionService {
    PaginationDTO<PermissionResponse> getAllPermissions(Pageable pageable);
    PermissionResponse getPermissionById(long id);
    PermissionResponse createPermission(CreatePermissionRequest request);
    PermissionResponse updatePermission(UpdatePermissionRequest request);
    void deletePermission(long id);
}
