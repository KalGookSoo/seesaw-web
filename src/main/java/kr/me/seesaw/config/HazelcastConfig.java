package kr.me.seesaw.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@EnableCaching
@Configuration
@Profile({"local", "prod"})
public class HazelcastConfig {

    private final String profiles;

    private final Map<String, List<String>> profilesMapper = new HashMap<>();

    private final List<String> localMembers = Arrays.asList(
            "127.0.0.1:5701",
            "127.0.0.1:5702",
            "127.0.0.1:5703",
            "127.0.0.1:5704"
    );

/*
    private final List<String> developmentMembers = Arrays.asList(
            "10.100.100.161:5701",
            "10.100.100.161:5702",
            "10.100.100.161:5703",
            "10.100.100.161:5704"
    );
*/

    private final List<String> productionMembers = List.of(
            "10.0.0.177:5701"
    );

    private void map() {
        profilesMapper.put("local", localMembers);
//        profilesMapper.put("dev", developmentMembers);
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

    @Bean
    public HazelcastInstance hazelcastInstance(@Qualifier("hazelcast") Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }


}
