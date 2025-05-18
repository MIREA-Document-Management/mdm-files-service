package ru.mdm.files.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import ru.mdm.files.client.feign.FilesServiceFeignClient;

/**
 * Автоконфигурация клиента сервиса.
 */
@Configuration
@ComponentScan(basePackages = "ru.mdm.files.client")
@EnableReactiveFeignClients(clients = {FilesServiceFeignClient.class})
public class FilesClientAutoConfiguration {

    @Value("${mdm.files.service.url}")
    private String serviceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(serviceUrl);
    }
}
