package mn.foreman.parker.discord.bot;

import mn.foreman.parker.mongodb.dao.Pool;
import mn.foreman.parker.mongodb.repo.PoolRepository;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit tests for {@link DiscordParker}. */
class DiscordParkerTest {

    /** A test channel name. */
    private static final String ANNOUNCEMENT_CHANNEL = "pools";

    /** The object under test. */
    private DiscordParker discordParker;

    /** A mock client. */
    private PoolsClient mockClient;

    /** A mock discord handle. */
    private JDA mockJda;

    /** A mock repository. */
    private PoolRepository mockRepository;

    /** Test setup. */
    @BeforeEach
    void setup() {
        this.mockJda =
                createMock(JDA.class);
        this.mockRepository =
                createMock(PoolRepository.class);
        this.mockClient =
                createMock(PoolsClient.class);
        this.discordParker =
                new DiscordParker(
                        this.mockJda,
                        this.mockRepository,
                        ANNOUNCEMENT_CHANNEL,
                        this.mockClient);
    }

    /** Tests {@link DiscordParker#checkForPools()}. */
    @SuppressWarnings("ConstantConditions")
    @Test
    void testNewPools() {
        final String oldStratum = "oldStratum";
        final String newStratum = "newStratum";

        final PoolsClient.Pool mockNewPool1 =
                createMock(PoolsClient.Pool.class);
        expect(mockNewPool1.getStratum()).andReturn(oldStratum);
        replay(mockNewPool1);

        final PoolsClient.Pool mockNewPool2 =
                createMock(PoolsClient.Pool.class);
        expect(mockNewPool2.getStratum()).andReturn(newStratum);
        replay(mockNewPool2);

        expect(this.mockClient.getAll())
                .andReturn(
                        List.of(
                                mockNewPool1,
                                mockNewPool2));
        replay(this.mockClient);

        final Pool mockStoredPool = createMock(Pool.class);
        expect(mockStoredPool.getStratumUrl()).andReturn(oldStratum);
        replay(mockStoredPool);

        expect(this.mockRepository.findAll())
                .andReturn(Collections.singletonList(mockStoredPool));
        expect(this.mockRepository.insert(anyObject(Pool.class)))
                .andAnswer(() -> {
                    final Pool pool = (Pool) getCurrentArguments()[0];
                    assertEquals(
                            newStratum,
                            pool.getStratumUrl());
                    return pool;
                });
        replay(this.mockRepository);

        final MessageAction mockAction = createMock(MessageAction.class);
        mockAction.queue();
        expectLastCall();
        replay(mockAction);

        final TextChannel mockChannel =
                createMock(TextChannel.class);
        expect(mockChannel.sendMessage(anyObject(Message.class)))
                .andAnswer(() -> {
                    final Message message = (Message) getCurrentArguments()[0];
                    assertEquals(
                            "**POOLS ADDED**\n\n* " + newStratum,
                            message.getContentRaw());
                    return mockAction;
                });
        replay(mockChannel);

        expect(this.mockJda.getTextChannelsByName(ANNOUNCEMENT_CHANNEL, true))
                .andReturn(Collections.singletonList(mockChannel));
        replay(this.mockJda);

        this.discordParker.checkForPools();

        verify(this.mockJda);
        verify(mockChannel);
        verify(mockAction);
        verify(this.mockRepository);
        verify(this.mockClient);
        verify(mockNewPool2);
        verify(mockNewPool1);
    }

    /** Tests {@link DiscordParker#checkForPools()}. */
    @SuppressWarnings("ConstantConditions")
    @Test
    void testNewPoolsInitialLoad() {
        final String stratum = "stratum";

        final PoolsClient.Pool mockNewPool = createMock(PoolsClient.Pool.class);
        expect(mockNewPool.getStratum()).andReturn(stratum);
        replay(mockNewPool);

        expect(this.mockClient.getAll())
                .andReturn(Collections.singletonList(mockNewPool));
        replay(this.mockClient);

        expect(this.mockRepository.findAll())
                .andReturn(Collections.emptyList());
        expect(this.mockRepository.insert(anyObject(Pool.class)))
                .andAnswer(() -> {
                    final Pool pool = (Pool) getCurrentArguments()[0];
                    assertEquals(
                            stratum,
                            pool.getStratumUrl());
                    return pool;
                });
        replay(this.mockRepository);

        replay(this.mockJda);

        this.discordParker.checkForPools();

        verify(this.mockJda);
        verify(this.mockRepository);
        verify(this.mockClient);
        verify(mockNewPool);
    }

    /** Tests {@link DiscordParker#checkForPools()}. */
    @Test
    void testNoNewPools() {
        final String stratum = "stratum";

        final PoolsClient.Pool mockNewPool = createMock(PoolsClient.Pool.class);
        expect(mockNewPool.getStratum()).andReturn(stratum);
        replay(mockNewPool);

        expect(this.mockClient.getAll())
                .andReturn(Collections.singletonList(mockNewPool));
        replay(this.mockClient);

        final Pool mockStoredPool = createMock(Pool.class);
        expect(mockStoredPool.getStratumUrl()).andReturn(stratum);
        replay(mockStoredPool);

        expect(this.mockRepository.findAll())
                .andReturn(Collections.singletonList(mockStoredPool));
        replay(this.mockRepository);

        replay(this.mockJda);

        this.discordParker.checkForPools();

        verify(this.mockJda);
        verify(this.mockRepository);
        verify(mockStoredPool);
        verify(this.mockClient);
        verify(mockNewPool);
    }

    /** Tests {@link DiscordParker#checkForPools()}. */
    @Test
    void testNoPools() {
        expect(this.mockClient.getAll()).andReturn(Collections.emptyList());
        replay(this.mockClient);

        replay(this.mockRepository);
        replay(this.mockJda);

        this.discordParker.checkForPools();

        verify(this.mockJda);
        verify(this.mockRepository);
        verify(this.mockClient);
    }
}