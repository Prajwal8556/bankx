package com.suntech;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.suntech.dao.EmployeeDao;
import com.suntech.domain.Employee;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class EmployeeTest {

	@Autowired
	private EmployeeDao employeeDao;
	
	@Test
	public void testEmployee() {
		
		Employee createEmployee = createEmployee();
		try {
			Employee insertedEmployee = employeeDao.save(createEmployee);
			System.out.println("Data saved");
			// validaing created atm inserted atm.
			validateEquals(createEmployee, insertedEmployee);
			
			Employee changeEmployee = changeEmployee(insertedEmployee);
			// validating after updting the data
			Employee updatedEmployee= employeeDao.save(createEmployee);
			System.out.println("Data updated");
			validateEquals(changeEmployee, updatedEmployee);
		} catch (Exception e) {
			System.out.println("error during executing test case for CRUD Employee");
		} finally {
			if (null != createEmployee.getId()) {
				Optional<Employee> deleteEmployee = employeeDao.findById(createEmployee.getId());
				employeeDao.delete(deleteEmployee.get());
				System.out.println("data deleated");
			}

		}

	}

	public Employee createEmployee() {
		Employee employee = new Employee();
		employee.setName("abc");
		employee.setType("sd");
		employee.setSalary(25000.00);
		return employee;
	}

	public Employee changeEmployee (Employee employee) {
		employee.setName("bcd");
		employee.setType("test");
		employee.setSalary(25000.00);
		
		return employee;
	}

	public void validateEquals(Employee employee, Employee updatedEmployee ){
		assertEquals(employee.getName(), updatedEmployee.getName());
		assertEquals(employee.getType(), updatedEmployee.getType());
		assertEquals(employee.getSalary(), updatedEmployee.getSalary());
		
	}



	}
