package agrawal93.kafka.test.config;

import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:kafka.properties")
public class KafkaAdminConfiguration {

    @Value("${zookeeper.server}")
    private String zookeeperServer;

    @Bean
    public ZkUtils getZKClientUtils() {
        int sessionTimeoutMs = 10000;
        int connectionTimeoutMs = 10000;
        boolean isSecureKafkaCluster = false;
        return new ZkUtils(
                new ZkClient(zookeeperServer, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$),
                new ZkConnection(zookeeperServer), isSecureKafkaCluster);
    }

    @Bean
    public KafkaConfiguration getKafkaConfiguration() {
        return new KafkaConfiguration();
    }
}
