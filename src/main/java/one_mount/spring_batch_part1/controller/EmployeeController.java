package one_mount.spring_batch_part1.controller;

import one_mount.spring_batch_part1.model.Employee;
import one_mount.spring_batch_part1.service.EmployeeService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final JobLauncher jobLauncher;
    private final Job importEmployeeJob;
    private final EmployeeService service;

    public EmployeeController(JobLauncher jobLauncher, Job importEmployeeJob, EmployeeService service) {
        this.jobLauncher = jobLauncher;
        this.importEmployeeJob = importEmployeeJob;
        this.service = service;
    }

    @PostMapping("/import")
    public String importEmployees(@RequestParam List<String> fileNames) throws Exception {
        String basePath = "src/main/resources/"; // folder hold csv

        for (String fileName : fileNames) {
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", basePath + fileName)
                    .addLong("time", System.currentTimeMillis()) // unique mỗi lần run
                    .toJobParameters();

            jobLauncher.run(importEmployeeJob, params);
        }

        return "Batch jobs started for files: " + String.join(", ", fileNames);
    }

    @GetMapping
    public List<Employee> getAll() {
        return service.getAllEmployees();
    }
}


