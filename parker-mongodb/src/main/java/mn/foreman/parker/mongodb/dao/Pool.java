package mn.foreman.parker.mongodb.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

/**
 * A {@link Pool} represents a pool that is known to parker and has successfully
 * been announced on Discord.
 */
@Data
@Builder
public class Pool {

    /** The ID. */
    @Id
    private String id;

    /** The pool's stratum URL. */
    private String stratumUrl;

    /** When the pool was announced. */
    private Instant announced;
}
