package one_mount.spring_batch_part1.batch;

import lombok.AllArgsConstructor;
import one_mount.spring_batch_part1.model.Employee;
import one_mount.spring_batch_part1.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@Configuration
@AllArgsConstructor
//@EnableBatchProcessing
public class EmployeeBatchConfig {

    private EmployeeCodeGenerator employeeCodeGenerator;

    @Bean
    public Job importEmployeeJob(JobRepository jobRepository,
                                 Step step1) {
        return new JobBuilder("importEmployeeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ItemReader<Employee> reader,
                      ItemProcessor<Employee, Employee> processor,
                      ItemWriter<Employee> writer,
                      EmployeeSkipListener skipListener) {
        return new StepBuilder("step1", jobRepository)
                .<Employee, Employee>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(skipListener)
                .taskExecutor(new SimpleAsyncTaskExecutor()) // Parallel running
                .build();
    }

    // ✅ CSV Reader
    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader(
            @Value("#{jobParameters['filePath']}") String filePath) {

        FlatFileItemReader<Employee> tokenizer = new FlatFileItemReader<>();
        tokenizer.setResource(new FileSystemResource(filePath));
        tokenizer.setLinesToSkip(1); // skip header
        tokenizer.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("name", "email", "department", "salary");
                setDelimiter(",");
                setIncludedFields(1,2,3,4);
            }});
            setFieldSetMapper(fieldSet -> {
                Employee emp = new Employee();
                emp.setName(fieldSet.readString("name"));
                emp.setEmail(fieldSet.readString("email"));
                emp.setDepartment(fieldSet.readString("department"));
                emp.setSalary(fieldSet.readBigDecimal("salary"));
                return emp;
            });
        }});
        return tokenizer;
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor(EmployeeRepository repository) {
        return employee -> {
            if (employee.getSalary().compareTo(new BigDecimal("70000")) < 0) {
                throw new ValidationException("Salary too low: " + employee.getSalary());
            }
            if (employee.getSalary().compareTo(new BigDecimal("90000")) > 0) {
                throw new ValidationException("Salary too high: " + employee.getSalary());
            }
            employee.setName(employee.getName().trim());
            employee.setEmail(employee.getEmail().toLowerCase());
            Integer maxId = repository.findMaxEmployeeId(); // SELECT MAX(id) FROM employees
            String newCode = employeeCodeGenerator.generateCode(maxId == null ? 0 : maxId);
            employee.setEmployeeCode(newCode);
            return employee;
        };
    }

    @Bean
    public ItemWriter<Employee> writer(EmployeeRepository repository) {
        return repository::saveAll;
    }
}


