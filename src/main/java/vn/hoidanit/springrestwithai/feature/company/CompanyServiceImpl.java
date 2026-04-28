package vn.hoidanit.springrestwithai.feature.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hoidanit.springrestwithai.dto.PaginationDTO;
import vn.hoidanit.springrestwithai.exception.DuplicateResourceException;
import vn.hoidanit.springrestwithai.exception.ResourceNotFoundException;
import vn.hoidanit.springrestwithai.feature.company.dto.CompanyResponse;
import vn.hoidanit.springrestwithai.feature.company.dto.CreateCompanyRequest;
import vn.hoidanit.springrestwithai.feature.company.dto.UpdateCompanyRequest;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public PaginationDTO<CompanyResponse> getAllCompanies(Pageable pageable) {
        Page<Company> pageData = companyRepository.findAll(pageable);
        List<CompanyResponse> result = pageData.getContent().stream()
                .map(CompanyResponse::fromEntity)
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
    public CompanyResponse getCompanyById(long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return CompanyResponse.fromEntity(company);
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Company", "name", request.name());
        }

        Company company = new Company();
        company.setName(request.name());
        company.setDescription(request.description());
        company.setAddress(request.address());
        company.setLogo(request.logo());

        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(UpdateCompanyRequest request) {
        Company company = companyRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.id()));

        if (!company.getName().equals(request.name()) && companyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Company", "name", request.name());
        }

        company.setName(request.name());
        company.setDescription(request.description());
        company.setAddress(request.address());
        company.setLogo(request.logo());

        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void deleteCompany(long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        // TODO: when users exist, setting company_id = null on users might be needed
        // For now, just delete the company
        companyRepository.delete(company);
    }
}
