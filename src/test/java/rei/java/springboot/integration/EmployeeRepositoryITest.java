package rei.java.springboot.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rei.java.springboot.model.Employee;
import rei.java.springboot.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// used to test repository components
// **@DataJpaTest** autoconfigure in-memory database
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryITest extends AbstractContainerBaseTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;

    // this method will be executed before each test
    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .firstName("Rei")
                .lastName("Dallo")
                .email("rd@domain.com")
                .build();
    }

    // JUnit test for save employee operation
    @Test
    @DisplayName("JUnit test for save employee operation")
    public void givenEmployee_whenSave_thenReturnSavedEmployee() {

        // given - precondition or setup
//        Employee employee = Employee.builder()
//                .firstName("Rei")
//                .lastName("Dallo")
//                .email("rd@domain.com")
//                .build();

        // when - action or behavior that we are going to test
        Employee savedEmployee = employeeRepository.save(employee);

        // then - verify the output
        // assertj.core.api is used for the assertions
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("JUnit test for find all employees operation")
    public void givenEmployeeList_whenFindAll_thenReturnEmployeeList() {

        Employee another_employee = Employee.builder()
                .firstName("Oni")
                .lastName("Dallo")
                .email("od@domain.com")
                .build();
        employeeRepository.save(employee);
        employeeRepository.save(another_employee);

        List<Employee> employeeList = employeeRepository.findAll();

        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("JUnit test for find employee by id operation")
    public void givenEmployee_whenFindById_thenReturnEmployee() {

        employeeRepository.save(employee);
        Optional<Employee> foundEmployee = employeeRepository.findById(employee.getId());

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get()).isNotNull();
    }

    @Test
    @DisplayName("JUnit test for find employee by email operation")
    public void givenEmployee_whenFindByEmail_thenReturnEmployee() {

        employeeRepository.save(employee);
        Optional<Employee> foundEmployee = employeeRepository.findByEmail(employee.getEmail());

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get()).isNotNull();
        assertThat(foundEmployee.get().getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    @DisplayName("JUnit test for update employee operation")
    public void givenEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() {

        employeeRepository.save(employee);

        Optional<Employee> savedEmployee = employeeRepository.findById(employee.getId());
        assertThat(savedEmployee).isPresent();

        savedEmployee.get().setEmail("rei@domain.com");
        savedEmployee.get().setFirstName("Reinildo");
        savedEmployee.get().setLastName("Dahallo");

        Employee updatedEmployee = employeeRepository.save(savedEmployee.get());

        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getEmail()).isEqualTo("rei@domain.com");
        assertThat(updatedEmployee.getFirstName()).isEqualTo("Reinildo");
    }

    @Test
    @DisplayName("JUnit test for delete employee operation")
    public void givenEmployee_whenDeleteEmployee_thenRemoveEmployee() {

        employeeRepository.save(employee);
        employeeRepository.delete(employee);

        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());
        assertThat(employeeOptional).isEmpty();
    }

    @Test
    @DisplayName("JUnit test for find employee by JPQL query (index param) operation")
    public void givenEmployee_whenFindByCustomQuery_thenReturnEmployee() {

        employeeRepository.save(employee);
        assertThat(employee.getFirstName()).isNotNull();
        assertThat(employee.getLastName()).isNotNull();

        Employee foundEmployee = employeeRepository.findByJPQL(employee.getFirstName(), employee.getLastName());
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(foundEmployee.getLastName()).isEqualTo(employee.getLastName());
    }

    @Test
    @DisplayName("JUnit test for find employee by JPQL query (named param) operation")
    public void givenEmployee_whenFindByCustomQueryNamedParam_thenReturnEmployee() {

        employeeRepository.save(employee);
        assertThat(employee.getFirstName()).isNotNull();
        assertThat(employee.getLastName()).isNotNull();

        Employee foundEmployee = employeeRepository.findByJPQLNamedParameters(employee.getFirstName(), employee.getLastName());
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(foundEmployee.getLastName()).isEqualTo(employee.getLastName());
    }

    @Test
    @DisplayName("JUnit test for find employee by native (index param) query operation")
    public void givenEmployee_whenFindByNativeQuery_thenReturnEmployee() {

        employeeRepository.save(employee);
        assertThat(employee.getFirstName()).isNotNull();
        assertThat(employee.getLastName()).isNotNull();

        Employee foundEmployee = employeeRepository.findByNative(employee.getFirstName(), employee.getLastName());
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(foundEmployee.getLastName()).isEqualTo(employee.getLastName());
    }

    @Test
    @DisplayName("JUnit test for find employee by native (index param) query operation")
    public void givenEmployee_whenFindByNativeQueryNamedParameters_thenReturnEmployee() {

        employeeRepository.save(employee);
        assertThat(employee.getFirstName()).isNotNull();
        assertThat(employee.getLastName()).isNotNull();

        Employee foundEmployee = employeeRepository.findByNativeNamedParameters(employee.getFirstName(), employee.getLastName());
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(foundEmployee.getLastName()).isEqualTo(employee.getLastName());
    }
}
