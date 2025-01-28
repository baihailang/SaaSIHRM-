package org.baihialang.activtiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringActivitiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringActivitiApplication.class, args);
    }

}
