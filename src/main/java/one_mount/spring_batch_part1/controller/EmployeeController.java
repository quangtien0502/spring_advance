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
    private final Job job;
    private final EmployeeService service;

    public EmployeeController(JobLauncher jobLauncher, Job job, EmployeeService service) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.service = service;
    }

    @PostMapping("/import")
    public String importEmployees(@RequestParam String filePath) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job, params);
        return "Batch job started with file: " + filePath;
    }

    @GetMapping
    public List<Employee> getAll() {
        return service.getAllEmployees();
    }
}


