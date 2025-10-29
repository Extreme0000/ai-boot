package fun.aiboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "fun.aiboot.repository")
public class ElasticsearchConfig {
    // Spring Boot 自动配置会处理 ElasticsearchClient 的创建
    // 基于 application.yml 中的配置
}
