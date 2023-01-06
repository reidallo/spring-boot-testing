package rei.java.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rei.java.springboot.model.Employee;
import rei.java.springboot.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
// integrate test containers
//@Testcontainers
public class EmployeeControllerITest extends AbstractContainerBaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        employeeRepository.deleteAll();
    }

    @AfterEach
    void finish() {
        employeeRepository.deleteAll();
    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception{

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();

        // there is no need to mock

        // when
        ResultActions response = mockMvc.perform(post("/api/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())))
                .andDo(print());
    }

    @Test
    public void givenListOfEmployees_whenGetAllEmployees_returnEmployeesList() throws Exception {

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
        Employee anotherEmployee = Employee.builder()
                .firstName("Oni")
                .lastName("Dallo")
                .email("od@domain.com")
                .build();
        List<Employee> employeeList = new ArrayList<>(List.of(employee, anotherEmployee));

        employeeRepository.saveAll(employeeList);

        // when
        ResultActions response = mockMvc.perform(get("/api/employee")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(employeeList.size())))
                .andDo(print());
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
        employeeRepository.save(employee);

        // when
        ResultActions response = mockMvc.perform(get("/api/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())))
                .andDo(print());
    }

    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {

        // given
        long employeeId = 1L;

        // when
        ResultActions response = mockMvc.perform(get("/api/employee/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
        Employee updatedEmployee = Employee.builder()
                .firstName("Oni")
                .lastName("Dado")
                .email("od@domain.com")
                .build();
        employeeRepository.save(employee);


        // when
        ResultActions response = mockMvc.perform(put("/api/employee/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())))
                .andDo(print());
    }

    @Test
    public void givenInvalidEmployeeId_whenUpdateEmployee_thenReturnEmpty() throws Exception {

        // given
        long employeeId = 1L;
        Employee updatedEmployee = Employee.builder()
                .firstName("Oni")
                .lastName("Dado")
                .email("od@domain.com")
                .build();

        // when
        ResultActions response = mockMvc.perform(put("/api/employee/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void givenEmployeeId_whenDeleteEmployee_return200() throws Exception {

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
        employeeRepository.save(employee);

        // when
        ResultActions response = mockMvc.perform(delete("/api/employee/{id}", employee.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}
