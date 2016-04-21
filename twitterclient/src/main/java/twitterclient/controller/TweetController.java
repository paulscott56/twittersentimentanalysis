package twitterclient.controller;

/**
 * Created by paul on 2016/04/21.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twitterclient.model.Sentiment;
import twitterclient.repo.TweetRepo;

import java.util.List;

/**
 * Created by paul on 2016/04/21.
 */
@RestController
@RequestMapping("/tweets")
public class TweetController {

    @Autowired
    private TweetRepo repo;

    @RequestMapping(method = RequestMethod.GET, path="/getall", produces = "application/json")
    public List<Sentiment> getAll() {
        return repo.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path="/getbytype", produces = "application/json")
    public List<Sentiment> getByType(@RequestParam(name = "type") String type) {
        return repo.findByType(type);
    }


}