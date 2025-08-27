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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
//@EnableBatchProcessing
public class EmployeeBatchConfig {



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
                      ItemWriter<Employee> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Employee, Employee>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // ✅ CSV Reader
    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader(
            @Value("#{jobParameters['filePath']}") String filePath) {

        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1); // skip header
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("name", "email", "department", "salary");
                setDelimiter(",");
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
        return reader;
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor() {
        return employee -> {
            employee.setName(employee.getName().trim());
            employee.setEmail(employee.getEmail().toLowerCase());
            return employee;
        };
    }

    @Bean
    public ItemWriter<Employee> writer(EmployeeRepository repository) {
        return repository::saveAll;
    }
}


