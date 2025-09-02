package one_mount.spring_batch_part1.batch;

import lombok.RequiredArgsConstructor;
import one_mount.spring_batch_part1.model.Employee;
import one_mount.spring_batch_part1.model.InvalidEmployee;
import one_mount.spring_batch_part1.repository.InvalidEmployeeRepository;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeSkipListener implements SkipListener<Employee, Employee> {

    private final InvalidEmployeeRepository invalidRepo;

    @Override
    public void onSkipInRead(Throwable t) {
        // Error when reading file -> could print to log file
        System.err.println("Skipped in READ: " + t.getMessage());
    }

    @Override
    public void onSkipInWrite(Employee employee, Throwable t) {
        // Error when error at writer in DB
        System.err.println("Skipped in WRITE: " + employee + " cause: " + t.getMessage());
    }

    @Override
    public void onSkipInProcess(Employee employee, Throwable t) {
        // Error when validate in processor -> Save in table invalid_employees
        InvalidEmployee invalid = new InvalidEmployee();
        invalid.setName(employee.getName());
        invalid.setEmail(employee.getEmail());
        invalid.setDepartment(employee.getDepartment());
        invalid.setSalary(employee.getSalary());
        invalid.setErrorMessage(t.getMessage());

        invalidRepo.save(invalid);

        System.err.println("Skipped in PROCESS: " + employee + " cause: " + t.getMessage());
    }
}
