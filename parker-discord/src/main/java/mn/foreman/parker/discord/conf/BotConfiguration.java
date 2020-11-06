package mn.foreman.parker.discord.conf;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.LoginException;

/**
 * A {@link Configuration} that creates the beans needed for the Discord bot.
 */
@Configuration
public class BotConfiguration {

    /** The activity message. */
    @Value("${discord.playing}")
    private String playing;

    /** The discord bot token. */
    @Value("${discord.token}")
    private String token;

    /**
     * Creates a Discord {@link JDA}.
     *
     * @return The new {@link JDA}.
     *
     * @throws LoginException       on failure to authenticate.
     * @throws InterruptedException on failure to {@link JDA#awaitReady()}.
     */
    @Bean
    public JDA jda()
            throws LoginException, InterruptedException {
        return JDABuilder.createDefault(this.token)
                .setActivity(Activity.playing(this.playing))
                .build()
                .awaitReady();
    }

    /**
     * Creates a {@link RestTemplate}.
     *
     * @return The new {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
