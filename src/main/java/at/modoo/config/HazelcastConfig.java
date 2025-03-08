package at.modoo.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@Configuration
@Profile("prod")
public class HazelcastConfig {

    private final String profiles;

    private final Map<String, List<String>> profilesMapper = new HashMap<>();

    private final List<String> localMembers = Arrays.asList(
            "127.0.0.1:5701",
            "127.0.0.1:5702",
            "127.0.0.1:5703",
            "127.0.0.1:5704"
    );

    private final List<String> developmentMembers = Arrays.asList(
            "10.100.100.161:5701",
            "10.100.100.161:5702",
            "10.100.100.161:5703",
            "10.100.100.161:5704"
    );

    private final List<String> productionMembers = Arrays.asList(
            "10.100.100.164:5701",
            "10.100.100.164:5702",
            "10.100.100.164:5703",
            "10.100.100.164:5704",

            "10.100.100.165:5701",
            "10.100.100.165:5702",
            "10.100.100.165:5703",
            "10.100.100.165:5704"
    );

    private void map() {
        profilesMapper.put("local", localMembers);
        profilesMapper.put("dev", developmentMembers);
        profilesMapper.put("prod", productionMembers);
    }

    private List<String> getMembers() {
        return profilesMapper.getOrDefault(profiles, Collections.emptyList());
    }

    public HazelcastConfig(@Value("${spring.profiles.active:local}") String profiles) {
        this.profiles = profiles;
        map();
    }

    @Bean
    public Config hazelcast() {
        Config config = new Config();

        // Optional: Set the logging type
        config.setProperty("hazelcast.logging.type", "slf4j");

        // Optional: Set the cluster name
        config.setClusterName("session-clustering");

        // Optional: Set the instance name
        config.setInstanceName("session-clustering-instance");

        // Group Configuration
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(5701).setPortCount(100).setPortAutoIncrement(false);

        // TCP-IP Configuration
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);

        TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);

        // Set members based on the active profile
        tcpIpConfig.setMembers(getMembers());

        // Optional, but recommended: Set the connection timeout to a low value
        tcpIpConfig.setConnectionTimeoutSeconds(5);

        return config;
    }

}
