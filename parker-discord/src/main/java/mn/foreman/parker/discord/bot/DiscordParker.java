package mn.foreman.parker.discord.bot;

import mn.foreman.parker.mongodb.dao.Pool;
import mn.foreman.parker.mongodb.repo.PoolRepository;
import mn.foreman.parker.utils.TimeUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link DiscordParker} provides the primary Discord Parker bot
 * implementation.
 *
 * <p>Via spring scheduling, it will query the Foreman API and announce
 * pools as new ones are encountered.</p>
 */
@Component
public class DiscordParker {

    /** The banner of the announcement message. */
    private static final String BANNER =
            "**POOLS ADDED**\n";

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(DiscordParker.class);

    /** Where to send announcements. */
    private final String announcementChannel;

    /** The discord handler. */
    private final JDA jda;

    /** The repository containing known {@link Pool Pools}. */
    private final PoolRepository poolRepository;

    /** The client to use for retrieving pools from Foreman. */
    private final PoolsClient poolsClient;

    /**
     * Constructor.
     *
     * @param jda                 The discord handler.
     * @param poolRepository      The repository containing known {@link Pool
     *                            Pools}.
     * @param announcementChannel The channel to use for announcements.
     * @param poolsClient         The {@link PoolsClient}.
     */
    @Autowired
    public DiscordParker(
            final JDA jda,
            final PoolRepository poolRepository,
            @Value("${discord.announcementChannel}") final String announcementChannel,
            final PoolsClient poolsClient) {
        this.jda = jda;
        this.poolRepository = poolRepository;
        this.announcementChannel = announcementChannel;
        this.poolsClient = poolsClient;
    }

    /**
     * Queries the Foreman API to get all of the known pools and identifies
     * which ones are new.
     *
     * <p>The new ones are inserted and announced in the
     * {@link #announcementChannel}.</p>
     */
    @Scheduled(fixedRateString = "${bot.checkRate}")
    public void checkForPools() {
        // Get all of the pools from the Foreman API
        final List<String> pools =
                this.poolsClient.getAll()
                        .stream()
                        .map(PoolsClient.Pool::getStratum)
                        .collect(Collectors.toList());

        LOG.debug("Obtained {} pools from the Foreman API", pools.size());

        if (!pools.isEmpty()) {
            // Get all of the known pools from our db
            final List<String> knownPools =
                    this.poolRepository.findAll()
                            .stream()
                            .map(Pool::getStratumUrl)
                            .collect(Collectors.toList());

            // See what we're missing
            pools.removeAll(knownPools);

            if (!pools.isEmpty()) {
                addNewPools(
                        pools,
                        knownPools.isEmpty());
            } else {
                LOG.info("No new pools have been added");
            }
        } else {
            LOG.warn("No pools returned from the Foreman API");
        }
    }

    /**
     * Converts the provided stratum to a {@link Pool}.
     *
     * @param stratum The stratum.
     *
     * @return The new {@link Pool}.
     */
    private static Pool toPool(final String stratum) {
        return Pool.builder()
                .stratumUrl(stratum)
                .announced(TimeUtils.now())
                .build();
    }

    /**
     * Adds all of the new pools if this isn't the initial load of the database
     * (don't announce all of the old ones).
     *
     * @param pools         The pools to add.
     * @param isInitialLoad Whether or not this is the initial load.
     */
    private void addNewPools(
            final List<String> pools,
            final boolean isInitialLoad) {
        // Save the new ones and announce them as added
        final MessageBuilder messageBuilder =
                new MessageBuilder()
                        .append(BANNER);
        pools
                .stream()
                .map(DiscordParker::toPool)
                .forEach(pool -> {
                    this.poolRepository.insert(pool);
                    messageBuilder
                            .append("\n* ")
                            .append(pool.getStratumUrl());
                });

        // Only announce if this isn't the initial load of everything
        if (!isInitialLoad) {
            final TextChannel textChannel =
                    this.jda.getTextChannelsByName(
                            this.announcementChannel,
                            true)
                            .stream()
                            .findFirst()
                            .orElseThrow();
            textChannel.sendMessage(messageBuilder.build()).queue();
        } else {
            LOG.info("Loaded {} pools", pools.size());
        }
    }
}
