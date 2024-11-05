package ru.mdm.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MdmFilesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdmFilesServiceApplication.class, args);
	}

}
