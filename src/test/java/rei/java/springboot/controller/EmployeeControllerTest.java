package rei.java.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rei.java.springboot.model.Employee;
import rei.java.springboot.service.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// It will load all the components that are required to Employee Controller
@WebMvcTest
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the dependency
    // Tells Spring to create a mock instance of EmployeeService and add it to the application context so that it is injected into EmployeeController
    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception{

        // given
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();

        // Stub the called method (employeeService.saveEmployee)
        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when
        // Make call to the Rest API
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

        // given
        given(employeeService.getAllEmployee()).willReturn(employeeList);

        // when
        ResultActions response = mockMvc.perform(get("/api/employee")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andDo(print());
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {

        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();

        // given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));

        // when
        ResultActions response = mockMvc.perform(get("/api/employee/{id}", employeeId)
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

        long employeeId = 1L;

        // given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/api/employee/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {

        long employeeId = 1L;
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

        // given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));
        given(employeeService.updateEmployee(any(Employee.class))).willAnswer((invocation) -> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(put("/api/employee/{id}", employeeId)
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

        long employeeId = 1L;

        Employee updatedEmployee = Employee.builder()
                .firstName("Oni")
                .lastName("Dado")
                .email("od@domain.com")
                .build();

        // given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
        given(employeeService.updateEmployee(any(Employee.class))).willAnswer((invocation) -> invocation.getArgument(0));

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

        long employeeId = 1L;

        // given
        willDoNothing().given(employeeService).deleteEmployee(employeeId);

        // when
        ResultActions response = mockMvc.perform(delete("/api/employee/{id}", employeeId));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}
