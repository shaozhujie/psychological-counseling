package com.project.counseling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//SET GLOBAL sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

/**
 * @author **
 */
@SpringBootApplication(scanBasePackages = "com.project.*")
@ComponentScan("com.project")
@MapperScan("com.**.mapper")
public class PsychologicalCounselingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsychologicalCounselingApplication.class, args);
    }

}
