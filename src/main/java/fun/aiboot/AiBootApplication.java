package fun.aiboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiBootApplication {

    public static void main(String[] args) {
        System.out.println("Dashscope key = " + System.getenv("DASHSCOPE_API_KEY"));
        SpringApplication.run(AiBootApplication.class, args);
    }

}
