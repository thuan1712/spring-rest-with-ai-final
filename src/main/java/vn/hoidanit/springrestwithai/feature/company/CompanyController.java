package vn.hoidanit.springrestwithai.feature.company;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hoidanit.springrestwithai.dto.ApiResponse;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.feature.company.dto.CompanyResponse;
import vn.hoidanit.springrestwithai.feature.company.dto.CreateCompanyRequest;
import vn.hoidanit.springrestwithai.feature.company.dto.UpdateCompanyRequest;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationDTO<CompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        int pageNo = page > 0 ? page : 1;
        int pageSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(direction, sortBy));
        PaginationDTO<CompanyResponse> response = companyService.getAllCompanies(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Fetch all companies", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable long id) {
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetch company", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Company created", response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(request);
        return ResponseEntity.ok(ApiResponse.success("Company updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted", null));
    }
}
