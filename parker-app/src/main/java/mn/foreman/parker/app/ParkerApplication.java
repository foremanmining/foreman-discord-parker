package mn.foreman.parker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Discord Parker provides updates to the Foreman Discord #pools channel as
 * pools are added to the Foreman database.
 */
@SpringBootApplication
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