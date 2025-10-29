package fun.aiboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AiBootApplication {

    public static void main(String[] args) {
        log.info("Dashscope key = {}", System.getenv("DASHSCOPE_API_KEY"));
        log.info("MYSQL PWD = {}", System.getenv("mysql_pwd"));
        SpringApplication.run(AiBootApplication.class, args);
    }

}
