package mqtttweets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.stream.CharacterStreamReadingMessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by paul on 2016/05/02.
 */
@Component
@Configuration
@ComponentScan
@ConfigurationProperties
public class AppConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setLocation(new ClassPathResource("application.yml"));
        return c;
    }

    private String outbound;

    private String consumerkey;

    private String consumersecret;

    private String brokerURL;

    private String inbound;

    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    public String getConsumerkey() {
        return consumerkey;
    }

    public void setConsumerkey(String consumerkey) {
        this.consumerkey = consumerkey;
    }

    public String getConsumersecret() {
        return consumersecret;
    }

    public void setConsumersecret(String consumersecret) {
        this.consumersecret = consumersecret;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURl(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    @Bean
    public String newline() {
        return System.getProperty("line.separator");
    }

    @Bean
    public String tstamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    @ConfigurationProperties(prefix = "twitter")
    @Bean
    public Twitter twitterTemplate() {
        return new TwitterTemplate(consumerkey, consumersecret);
    }

    @Bean
    public DefaultMqttPahoClientFactory clientFactory() {
        // could define username and password for broker connection here
        return new DefaultMqttPahoClientFactory();
    }



    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs("tcp://197.80.203.63:1883");
        return factory;
    }

    // publisher

    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    public IntegrationFlow mqttOutFlow() {
        return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin(),
                e -> e.poller(Pollers.fixedDelay(1000)))
                .transform(p -> p)
                .handle(mqttOutbound())
                .get();
    }

    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("Publisher", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(outbound);
        return messageHandler;
    }

    // consumer
    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://197.80.203.63:1883", "SpringClient",
                        "/tweets/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @ConfigurationProperties(prefix = "mqtt")
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("--------------------------------------------------------------->>>> " + message.getPayload());

            }

        };
    }
}
