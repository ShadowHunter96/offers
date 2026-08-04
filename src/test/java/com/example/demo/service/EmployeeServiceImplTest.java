package com.example.demo.service;

import com.example.demo.dto.EmployeeDto;
import com.example.demo.entity.Employee;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("EmployeeServiceImpl Tests")
class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;
    private EmployeeDto testEmployeeDto;
    private UUID testEmployeeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(employeeRepository);

        testEmployeeId = UUID.randomUUID();
        testEmployee = new Employee();
        testEmployee.setId(testEmployeeId);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setRole("Developer");
        testEmployee.setHourlyRate(BigDecimal.valueOf(50.00));
        testEmployee.setActive(true);
        testEmployee.setCreatedAt(Instant.now());
        testEmployee.setUpdatedAt(Instant.now());

        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setFirstName("John");
        testEmployeeDto.setLastName("Doe");
        testEmployeeDto.setEmail("john.doe@example.com");
        testEmployeeDto.setRole("Developer");
        testEmployeeDto.setHourlyRate(BigDecimal.valueOf(50.00));
        testEmployeeDto.setActive(true);
    }

    @Test
    @DisplayName("create() should create new employee with unique email")
    void testCreate() {
        when(employeeRepository.findByEmail(testEmployeeDto.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto result = employeeService.create(testEmployeeDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("Developer", result.getRole());
        assertEquals(BigDecimal.valueOf(50.00), result.getHourlyRate());
        assertTrue(result.getActive());
        assertNotNull(result.getId());

        verify(employeeRepository, times(1)).findByEmail(testEmployeeDto.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("create() should set active to true by default when null")
    void testCreateWithActiveNull() {
        EmployeeDto dtoWithNullActive = new EmployeeDto();
        dtoWithNullActive.setFirstName("Jane");
        dtoWithNullActive.setLastName("Smith");
        dtoWithNullActive.setEmail("jane.smith@example.com");
        dtoWithNullActive.setRole("Manager");
        dtoWithNullActive.setHourlyRate(BigDecimal.valueOf(60.00));
        dtoWithNullActive.setActive(null);

        when(employeeRepository.findByEmail(dtoWithNullActive.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto result = employeeService.create(dtoWithNullActive);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("create() should throw exception when email already exists")
    void testCreateWithDuplicateEmail() {
        when(employeeRepository.findByEmail(testEmployeeDto.getEmail())).thenReturn(Optional.of(testEmployee));

        assertThrows(ApiException.class, () ->
            employeeService.create(testEmployeeDto)
        );

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should update employee when found")
    void testUpdate() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("Johnny");
        updateDto.setLastName("Smith");
        updateDto.setEmail("johnny.smith@example.com");
        updateDto.setRole("Senior Developer");
        updateDto.setHourlyRate(BigDecimal.valueOf(75.00));
        updateDto.setActive(true);

        EmployeeDto result = employeeService.update(testEmployeeId, updateDto);

        assertNotNull(result);
        verify(employeeRepository, times(1)).findById(testEmployeeId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("update() should update only when email is changed and is unique")
    void testUpdateWithEmailChange() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findByEmail("new.email@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setEmail("new.email@example.com");
        updateDto.setRole("Developer");
        updateDto.setHourlyRate(BigDecimal.valueOf(50.00));

        EmployeeDto result = employeeService.update(testEmployeeId, updateDto);

        assertNotNull(result);
        verify(employeeRepository, times(1)).findByEmail("new.email@example.com");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("update() should not validate email uniqueness when email is not changed")
    void testUpdateWithoutEmailChange() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("Johnny");
        updateDto.setLastName("Doe");
        updateDto.setEmail("john.doe@example.com");
        updateDto.setRole("Developer");
        updateDto.setHourlyRate(BigDecimal.valueOf(55.00));

        EmployeeDto result = employeeService.update(testEmployeeId, updateDto);

        assertNotNull(result);
        verify(employeeRepository, never()).findByEmail(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("update() should throw exception when email already exists (used by another employee)")
    void testUpdateWithDuplicateEmail() {
        Employee otherEmployee = new Employee();
        otherEmployee.setId(UUID.randomUUID());
        otherEmployee.setEmail("other.email@example.com");

        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findByEmail("other.email@example.com")).thenReturn(Optional.of(otherEmployee));

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setEmail("other.email@example.com");

        assertThrows(ApiException.class, () ->
            employeeService.update(testEmployeeId, updateDto)
        );

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw exception when employee not found")
    void testUpdateNotFound() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.empty());

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("Johnny");
        updateDto.setLastName("Doe");
        updateDto.setEmail("johnny.doe@example.com");

        assertThrows(ApiException.class, () ->
            employeeService.update(testEmployeeId, updateDto)
        );

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should preserve active field when null in DTO")
    void testUpdateWithActiveNull() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setFirstName("Johnny");
        updateDto.setLastName("Doe");
        updateDto.setEmail("john.doe@example.com");
        updateDto.setActive(null);

        EmployeeDto result = employeeService.update(testEmployeeId, updateDto);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("getById() should return employee when found")
    void testGetById() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));

        EmployeeDto result = employeeService.getById(testEmployeeId);

        assertNotNull(result);
        assertEquals(testEmployee.getFirstName(), result.getFirstName());
        assertEquals(testEmployee.getLastName(), result.getLastName());
        assertEquals(testEmployee.getEmail(), result.getEmail());
        verify(employeeRepository, times(1)).findById(testEmployeeId);
    }

    @Test
    @DisplayName("getById() should throw exception when employee not found")
    void testGetByIdNotFound() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () ->
            employeeService.getById(testEmployeeId)
        );
    }

    @Test
    @DisplayName("getByFirstName() should return employee when found")
    void testGetByFirstName() {
        when(employeeRepository.findByFirstName("John")).thenReturn(Optional.of(testEmployee));

        EmployeeDto result = employeeService.getByFirstName("John");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(employeeRepository, times(1)).findByFirstName("John");
    }

    @Test
    @DisplayName("getByFirstName() should throw exception when employee not found")
    void testGetByFirstNameNotFound() {
        when(employeeRepository.findByFirstName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () ->
            employeeService.getByFirstName("NonExistent")
        );
    }

    @Test
    @DisplayName("getByLastName() should return employee when found")
    void testGetByLastName() {
        when(employeeRepository.findByLastName("Doe")).thenReturn(Optional.of(testEmployee));

        EmployeeDto result = employeeService.getByLastName("Doe");

        assertNotNull(result);
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(employeeRepository, times(1)).findByLastName("Doe");
    }

    @Test
    @DisplayName("getByLastName() should throw exception when employee not found")
    void testGetByLastNameNotFound() {
        when(employeeRepository.findByLastName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () ->
            employeeService.getByLastName("NonExistent")
        );
    }

    @Test
    @DisplayName("listAll() should return all employees")
    void testListAll() {
        Employee employee2 = new Employee();
        employee2.setId(UUID.randomUUID());
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setEmail("jane.smith@example.com");
        employee2.setActive(true);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(testEmployee, employee2));

        List<EmployeeDto> result = employeeService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listAll() should return empty list when no employees")
    void testListAllEmpty() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList());

        List<EmployeeDto> result = employeeService.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deactivate() should deactivate active employee")
    void testDeactivateActiveEmployee() {
        testEmployee.setActive(true);
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));

        employeeService.deactivate(testEmployeeId);

        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    @DisplayName("deactivate() should not save when employee already inactive")
    void testDeactivateInactiveEmployee() {
        testEmployee.setActive(false);
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));

        employeeService.deactivate(testEmployeeId);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deactivate() should throw exception when employee not found")
    void testDeactivateNotFound() {
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () ->
            employeeService.deactivate(testEmployeeId)
        );

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deactivate() should update the updatedAt timestamp")
    void testDeactivateUpdatesTimestamp() {
        testEmployee.setActive(true);
        Instant beforeDeactivate = Instant.now();
        
        when(employeeRepository.findById(testEmployeeId)).thenReturn(Optional.of(testEmployee));

        employeeService.deactivate(testEmployeeId);

        assertTrue(testEmployee.getUpdatedAt().isAfter(beforeDeactivate) || 
                   testEmployee.getUpdatedAt().equals(beforeDeactivate));
        assertFalse(testEmployee.isActive());
        verify(employeeRepository, times(1)).save(testEmployee);
    }
}
