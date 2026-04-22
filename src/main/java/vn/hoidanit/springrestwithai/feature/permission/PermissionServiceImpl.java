package vn.hoidanit.springrestwithai.feature.permission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.exception.DuplicateResourceException;
import vn.hoidanit.springrestwithai.exception.ResourceNotFoundException;
import vn.hoidanit.springrestwithai.feature.permission.dto.CreatePermissionRequest;
import vn.hoidanit.springrestwithai.feature.permission.dto.PermissionResponse;
import vn.hoidanit.springrestwithai.feature.permission.dto.UpdatePermissionRequest;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PaginationDTO<PermissionResponse> getAllPermissions(Pageable pageable) {
        Page<Permission> pageData = permissionRepository.findAll(pageable);
        List<PermissionResponse> result = pageData.getContent().stream()
                .map(PermissionResponse::fromEntity)
                .toList();

        PaginationDTO.Meta meta = new PaginationDTO.Meta(
                pageData.getNumber() + 1,
                pageData.getSize(),
                pageData.getTotalPages(),
                pageData.getTotalElements()
        );

        return new PaginationDTO<>(meta, result);
    }

    @Override
    public PermissionResponse getPermissionById(long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        return PermissionResponse.fromEntity(permission);
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByApiPathAndMethod(request.apiPath(), request.method())) {
            throw new DuplicateResourceException("Permission", "apiPath + method",
                    request.apiPath() + " + " + request.method());
        }

        Permission permission = new Permission();
        permission.setName(request.name());
        permission.setApiPath(request.apiPath());
        permission.setMethod(request.method());
        permission.setModule(request.module());

        return PermissionResponse.fromEntity(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", request.id()));

        // Check for duplicates only if changing apiPath or method
        if (!permission.getApiPath().equals(request.apiPath()) || !permission.getMethod().equals(request.method())) {
            if (permissionRepository.existsByApiPathAndMethod(request.apiPath(), request.method())) {
                throw new DuplicateResourceException("Permission", "apiPath + method",
                        request.apiPath() + " + " + request.method());
            }
        }

        permission.setName(request.name());
        permission.setApiPath(request.apiPath());
        permission.setMethod(request.method());
        permission.setModule(request.module());

        return PermissionResponse.fromEntity(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public void deletePermission(long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));

        // When relationships to Role exist, they should be cleared here. Right now Role entity doesn't exist.
        // It relies on @ManyToMany cascade settings later or clearing manually inside Role.
        permissionRepository.delete(permission);
    }
}
