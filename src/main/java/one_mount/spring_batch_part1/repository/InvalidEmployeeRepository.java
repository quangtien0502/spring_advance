package one_mount.spring_batch_part1.repository;

import one_mount.spring_batch_part1.model.InvalidEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidEmployeeRepository extends JpaRepository<InvalidEmployee, Long> {
}
