//package one_mount.spring_batch_part1.batch;
//
//import io.micrometer.core.instrument.MultiGauge;
//import one_mount.spring_batch_part1.model.Employee;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.ss.usermodel.Row;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.stereotype.Component;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.util.Iterator;
//
//@Component
//public class ExcelEmployeeReader implements ItemReader<Employee> {
//
//    private Iterator<Row> rowIterator;
//
//    public ExcelEmployeeReader() throws IOException {
//        InputStream is = new FileInputStream("src/main/resources/employees-sample.xlsx");
//        Workbook workbook = new XSSFWorkbook(is);
//        Sheet sheet = workbook.getSheetAt(0);
//        this.rowIterator = sheet.iterator();
//        rowIterator.next(); // skip header
//    }
//
//    @Override
//    public Employee read() {
//        if (!rowIterator.hasNext()) {
//            return null;
//        }
//
//        Row row = rowIterator.next();
//        Employee emp = new Employee();
//        emp.setName(row.getCell(0).getStringCellValue());
//        emp.setEmail(row.getCell(1).getStringCellValue());
//        emp.setDepartment(row.getCell(2).getStringCellValue());
//        emp.setSalary(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
//        return emp;
//    }
//}
//
