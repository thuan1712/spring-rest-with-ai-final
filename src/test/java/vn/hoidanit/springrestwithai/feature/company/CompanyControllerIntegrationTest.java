package vn.hoidanit.springrestwithai.feature.company;

import tools.jackson.databind.ObjectMapper;
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
import vn.hoidanit.springrestwithai.feature.company.dto.CreateCompanyRequest;
import vn.hoidanit.springrestwithai.feature.company.dto.UpdateCompanyRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 201 and create company when request is valid")
    void createCompany_validRequest_returnsCreated() throws Exception {
        CreateCompanyRequest request = new CreateCompanyRequest("HoiDanIT", "Education", "HCM", "logo.png");

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.name").value("HoiDanIT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when name is blank during creation")
    void createCompany_blankName_returnsBadRequest() throws Exception {
        CreateCompanyRequest request = new CreateCompanyRequest("", "Education", "HCM", "logo.png");

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 409 when company name already exists")
    void createCompany_duplicateName_returnsConflict() throws Exception {
        Company company = new Company();
        company.setName("HoiDanIT");
        companyRepository.save(company);

        CreateCompanyRequest request = new CreateCompanyRequest("HoiDanIT", "Education", "HCM", "logo.png");

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 and paginated companies")
    void getAllCompanies_returnsPaginatedList() throws Exception {
        Company c1 = new Company(); c1.setName("C1"); companyRepository.save(c1);
        Company c2 = new Company(); c2.setName("C2"); companyRepository.save(c2);

        mockMvc.perform(get("/api/v1/companies?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.result.length()").value(2))
                .andExpect(jsonPath("$.data.meta.total").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 and company by id")
    void getCompanyById_found_returnsCompany() throws Exception {
        Company c1 = new Company(); c1.setName("C1"); 
        Company saved = companyRepository.save(c1);

        mockMvc.perform(get("/api/v1/companies/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.name").value("C1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when getting non-existent company")
    void getCompanyById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/companies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update and return 200 when request is valid")
    void updateCompany_validRequest_returnsOk() throws Exception {
        Company company = new Company(); company.setName("Old");
        Company saved = companyRepository.save(company);

        UpdateCompanyRequest request = new UpdateCompanyRequest(saved.getId(), "New", "Desc", "Addr", "Logo");

        mockMvc.perform(put("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.name").value("New"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 when deleting company")
    void deleteCompany_found_returnsOk() throws Exception {
        Company company = new Company(); company.setName("DeleteMe");
        Company saved = companyRepository.save(company);

        mockMvc.perform(delete("/api/v1/companies/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        mockMvc.perform(get("/api/v1/companies/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
