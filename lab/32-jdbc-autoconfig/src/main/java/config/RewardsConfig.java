package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
public class RewardsConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public DataSource dataSource() {
        logger.debug("Creating the datasource bean explicitly");

        return
                (new EmbeddedDatabaseBuilder())
                        .addScript("classpath:schema.sql")
                        .addScript("classpath:data.sql")
                        .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public RewardNetwork rewardNetwork() {
        return new RewardNetworkImpl(
                accountRepository(),
                restaurantRepository(),
                rewardRepository());
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource());
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return new JdbcRestaurantRepository(dataSource());
    }

    @Bean
    public RewardRepository rewardRepository() {
        return new JdbcRewardRepository(dataSource());
    }
}
