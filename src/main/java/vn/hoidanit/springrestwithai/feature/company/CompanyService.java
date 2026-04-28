package vn.hoidanit.springrestwithai.feature.company;

import org.springframework.data.domain.Pageable;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.feature.company.dto.CompanyResponse;
import vn.hoidanit.springrestwithai.feature.company.dto.CreateCompanyRequest;
import vn.hoidanit.springrestwithai.feature.company.dto.UpdateCompanyRequest;

public interface CompanyService {
    PaginationDTO<CompanyResponse> getAllCompanies(Pageable pageable);
    CompanyResponse getCompanyById(long id);
    CompanyResponse createCompany(CreateCompanyRequest request);
    CompanyResponse updateCompany(UpdateCompanyRequest request);
    void deleteCompany(long id);
}
