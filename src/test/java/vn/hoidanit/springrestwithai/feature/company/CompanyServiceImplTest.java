package vn.hoidanit.springrestwithai.feature.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.hoidanit.springrestwithai.exception.DuplicateResourceException;
import vn.hoidanit.springrestwithai.exception.ResourceNotFoundException;
import vn.hoidanit.springrestwithai.feature.company.dto.CompanyResponse;
import vn.hoidanit.springrestwithai.feature.company.dto.CreateCompanyRequest;
import vn.hoidanit.springrestwithai.feature.company.dto.UpdateCompanyRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    @DisplayName("Should throw ResourceNotFoundException when company not found by id")
    void getCompanyById_notFound_throwsException() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById(1L));
    }

    @Test
    @DisplayName("Should return company response when found by id")
    void getCompanyById_found_returnsResponse() {
        Company company = new Company();
        company.setId(1L);
        company.setName("HoiDanIT");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        CompanyResponse response = companyService.getCompanyById(1L);

        assertEquals(1L, response.id());
        assertEquals("HoiDanIT", response.name());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating company with existing name")
    void createCompany_duplicateName_throwsException() {
        CreateCompanyRequest request = new CreateCompanyRequest("HoiDanIT", "Desc", "Address", "Logo");
        when(companyRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> companyService.createCompany(request));
    }

    @Test
    @DisplayName("Should create and return company response when request is valid")
    void createCompany_validRequest_returnsResponse() {
        CreateCompanyRequest request = new CreateCompanyRequest("HoiDanIT", "Desc", "Address", "Logo");
        when(companyRepository.existsByName(anyString())).thenReturn(false);

        Company savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setName("HoiDanIT");
        savedCompany.setDescription("Desc");
        
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        CompanyResponse response = companyService.createCompany(request);

        assertNotNull(response);
        assertEquals("HoiDanIT", response.name());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent company")
    void updateCompany_notFound_throwsException() {
        UpdateCompanyRequest request = new UpdateCompanyRequest(1L, "New Name", "Desc", "Address", "Logo");
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.updateCompany(request));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating name to an existing one")
    void updateCompany_duplicateName_throwsException() {
        UpdateCompanyRequest request = new UpdateCompanyRequest(1L, "Existing Name", "Desc", "Address", "Logo");
        
        Company existingCompany = new Company();
        existingCompany.setId(1L);
        existingCompany.setName("Old Name");
        
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByName("Existing Name")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> companyService.updateCompany(request));
    }

    @Test
    @DisplayName("Should update and return company response when request is valid")
    void updateCompany_validRequest_returnsResponse() {
        UpdateCompanyRequest request = new UpdateCompanyRequest(1L, "New Name", "Desc", "Address", "Logo");
        
        Company existingCompany = new Company();
        existingCompany.setId(1L);
        existingCompany.setName("Old Name");
        
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByName("New Name")).thenReturn(false);
        
        Company updatedCompany = new Company();
        updatedCompany.setId(1L);
        updatedCompany.setName("New Name");
        updatedCompany.setDescription("Desc");
        
        when(companyRepository.save(existingCompany)).thenReturn(updatedCompany);

        CompanyResponse response = companyService.updateCompany(request);

        assertNotNull(response);
        assertEquals("New Name", response.name());
        assertEquals("Desc", existingCompany.getDescription());
    }

    @Test
    @DisplayName("Should delete company when found by id")
    void deleteCompany_found_deletesCompany() {
        Company company = new Company();
        company.setId(1L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        companyService.deleteCompany(1L);

        verify(companyRepository).delete(company);
    }
}
