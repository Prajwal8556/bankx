package com.suntech.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suntech.domain.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Integer>{

}
