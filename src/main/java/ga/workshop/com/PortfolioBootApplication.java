package ga.workshop.com;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import ga.workshop.com.util.DataStorageSettings;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DataStorageSettings.class)
public class PortfolioBootApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
//		SpringApplication.run(PortfolioBootApplication.class, args);
		try {
			SpringApplication application = new SpringApplication(PortfolioBootApplication.class);
			application.setAddCommandLineProperties(false);
			application.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}
