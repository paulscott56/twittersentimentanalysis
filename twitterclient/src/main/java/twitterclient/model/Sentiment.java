package twitterclient.model;

/**
 * Created by paul on 2016/04/21.
 */
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by paul on 2016/04/21.
 */
@Document
public class Sentiment {
    @Id
    private String id;

    private String tweet;

    private String type;

    private Double score;

    private String mixed;

    private Date date;


    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getMixed() {
        return mixed;
    }

    public void setMixed(String mixed) {
        this.mixed = mixed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}