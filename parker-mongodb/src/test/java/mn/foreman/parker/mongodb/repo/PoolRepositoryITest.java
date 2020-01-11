package mn.foreman.parker.mongodb.repo;

import mn.foreman.parker.mongodb.dao.Pool;
import mn.foreman.parker.utils.TimeUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit tests for {@link PoolRepository}. */
@ExtendWith(SpringExtension.class)
@DataMongoTest
class PoolRepositoryITest {

    /** The object under test. */
    @Autowired
    private PoolRepository poolRepository;

    /** Test setup. */
    @BeforeEach
    void setup() {
        this.poolRepository.deleteAll();
    }

    /** Verifies that {@link Pool Pools} can be inserted and found. */
    @Test
    void testInsertAndFind() {
        final Pool pool =
                Pool.builder()
                        .id(UUID.randomUUID().toString())
                        .stratumUrl("stratum-url")
                        .announced(TimeUtils.now())
                        .build();
        this.poolRepository.insert(pool);
        assertEquals(
                Optional.of(pool),
                this.poolRepository.findById(pool.getId()));
    }

    /** Bootstrap application for testing. */
    @SpringBootApplication
    public static class Application {

    }
}