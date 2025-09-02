package one_mount.spring_batch_part1.batch;

import org.springframework.stereotype.Component;

@Component
public class EmployeeCodeGenerator {

    private int counter = 0;

    public synchronized String generateCode(int maxIdFromDb) {
        if (counter == 0) {
            // Khởi tạo counter dựa trên maxId trong DB
            counter = maxIdFromDb;
        }
        counter++;
        return String.format("EMP%05d", counter);
    }
}
