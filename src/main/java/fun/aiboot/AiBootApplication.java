package fun.aiboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("fun.aiboot.mapper")
@SpringBootApplication
public class AiBootApplication {

    public static void main(String[] args) {
        System.out.println("Dashscope key = " + System.getenv("DASHSCOPE_API_KEY"));
        SpringApplication.run(AiBootApplication.class, args);
    }

}
