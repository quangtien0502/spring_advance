package one_mount.spring_batch_part1.repository;

import one_mount.spring_batch_part1.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

//    @Lock(LockModeType.PESSIMISTIC_READ)
//    @Query(value = "SELECT MAX(e.id) FROM Employee e")
    @Query(value = "SELECT id FROM employees ORDER BY id DESC LIMIT 1 FOR UPDATE;", nativeQuery = true)
    Integer findMaxEmployeeId();
}
