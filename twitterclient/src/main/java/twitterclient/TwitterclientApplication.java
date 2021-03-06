package twitterclient;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import twitterclient.model.Sentiment;
import twitterclient.repo.TweetRepo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableAutoConfiguration
@ComponentScan
@EnableMongoRepositories(basePackages = "twitterclient.repo")
@SpringBootApplication
public class TwitterclientApplication {

	private String apikey = "5eac1e1b0886b7ea50a8ed1809054a61fbe9076b";

	@Autowired
	private TweetRepo repo;

	public static void main(String[] args) {
		SpringApplication.run(TwitterclientApplication.class, args);
	}

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter =
				new MqttPahoMessageDrivenChannelAdapter("tcp://197.80.203.63:1883", "testClient",
						"tweets/dstv");
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				AlchemyLanguage service = new AlchemyLanguage();
				service.setApiKey(apikey);

				Map<String, Object> params = new HashMap<String, Object>();
				params.put(AlchemyLanguage.TEXT, message.getPayload());
				DocumentSentiment sentiment = service.getSentiment(params);

				Sentiment s = new Sentiment();
				s.setMixed(sentiment.getSentiment().getMixed());
				s.setScore(sentiment.getSentiment().getScore());
				s.setType(sentiment.getSentiment().getType().toString());
				s.setDate(new Date());
				s.setTweet(message.getPayload().toString());

				repo.save(s);
			}
		};
	}
}
