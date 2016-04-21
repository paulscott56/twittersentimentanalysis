package twitterclient.repo;

/**
 * Created by paul on 2016/04/21.
 */
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;
import twitterclient.model.Sentiment;

import java.util.List;

/**
 * Created by paul on 2016/04/21.
 */
@EnableMongoRepositories
@Repository
public interface TweetRepo extends MongoRepository<Sentiment, String> {
    public List<Sentiment> findAll();
    public List<Sentiment> findByType(String type);
}