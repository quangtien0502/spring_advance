package one_mount.spring_batch_part1.service;

import one_mount.spring_batch_part1.model.Employee;
import one_mount.spring_batch_part1.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }
}
