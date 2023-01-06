package rei.java.springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rei.java.springboot.model.Employee;
import rei.java.springboot.repository.EmployeeRepository;
import rei.java.springboot.service.implementation.EmployeeServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    // EmployeeService depends on EmployeeRepository
    // Mock using ** @Mock ** annotation
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private Employee employee;

    // private EmployeeService employeeService;

    // Mock the dependency using mock() method
    @BeforeEach
    public void setup() {
        // Mock EmployeeRepository
            // employeeRepository = Mockito.mock(EmployeeRepository.class);
        // Inject EmployeeRepository into EmployeeService
            // employeeService = new EmployeeServiceImpl(employeeRepository);
        employee = Employee.builder()
                // employeeRepository.save(e) would return the employee with the id generated
                .id(1L)
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
    }

    @Test
    @DisplayName("JUnit test for saveEmployee method")
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployee() {

        // stubbing the methods that are called into saveEmployee method

        // given
        given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.empty());
        given(employeeRepository.save(employee)).willReturn(employee);

        // when
        Employee savedEmployee = employeeService.saveEmployee(employee);

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("JUnit test for saveEmployee method which throws exception")
    public void givenExistingEmail_whenSaveEmployee_thenThrowsException() {

        given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.of(employee));

        // junit.jupiter.api is used for the assertion
        assertThrows(IllegalStateException.class, () -> employeeService.saveEmployee(employee));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("JUnit test for getAllEmployee method")
    public void givenEmployeeList_whenGetAllEmployee_thenReturnEmployeeList() {

        Employee anotherEmployee = Employee.builder()
                .id(2L)
                .firstName("Oni")
                .lastName("Dallo")
                .email("od@domain.com")
                .build();

        // given
        given(employeeRepository.findAll()).willReturn(List.of(employee, anotherEmployee));
        // when
        List<Employee> employeeList = employeeService.getAllEmployee();
        // then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("JUnit test for getAllEmployee method, negative scenario")
    public void givenEmptyList_whenGetAllEmployee_thenReturnEmptyList() {

        // given
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());
        // when
        List<Employee> employeeList = employeeService.getAllEmployee();
        // then
        assertThat(employeeList).isEmpty();
    }

    @Test
    @DisplayName("JUnit test for getEmployeeById method")
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() {

        // given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        // when
        Optional<Employee> employeeOptional = employeeService.getEmployeeById(employee.getId());
        // then
        assertThat(employeeOptional).isNotNull();
    }

    @Test
    @DisplayName("JUnit test for getEmployeeById method, negative scenario")
    public void givenEmployeeId_whenGetEmployeeById_thenReturnNull() {

        // given
        given(employeeRepository.findById(1L)).willReturn(Optional.empty());
        // when
        Optional<Employee> employeeOptional = employeeService.getEmployeeById(employee.getId());
        // then
        assertThat(employeeOptional).isEmpty();
    }

    @Test
    @DisplayName("JUnit test for updateEmployee method")
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {

        // given
        given(employeeRepository.save(employee)).willReturn(employee);
        employee.setEmail("rei@domain.com");
        // when
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        // then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getEmail()).isEqualTo("rei@domain.com");
    }

    @Test
    @DisplayName("JUnit test for deleteEmployee method")
    public void givenEmployeeId_whenDeleteEmployee_thenDoNothing() {

        Long employeeId = 1L;
        // stubbing a void method
        // given
        willDoNothing().given(employeeRepository).deleteById(employeeId);
        // when
        employeeService.deleteEmployee(employeeId);
        // verifying that the method is called only once
        // then
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }
}
