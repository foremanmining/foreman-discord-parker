package mn.foreman.parker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Discord Parker provides updates to the Foreman Discord #pools channel as
 * pools are added to the Foreman database.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "mn.foreman")
public class ParkerApplication {

    /**
     * Application entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(ParkerApplication.class, args);
    }
}