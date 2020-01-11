package mn.foreman.parker.mongodb.repo;

import mn.foreman.parker.mongodb.dao.Pool;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A {@link PoolRepository} provides a spring-data repository for storing,
 * updating, and retrieving {@link Pool Pools} from a mongodb database.
 */
public interface PoolRepository
        extends MongoRepository<Pool, String> {
    
}