package com.petify.petify.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HikariCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(HikariCPConfig.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minIdle;

    @Value("${spring.datasource.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:300000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1200000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.pool-name:PetifyHikariPool}")
    private String poolName;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setPoolName(poolName);
        config.setAutoCommit(true);
        config.setLeakDetectionThreshold(60000);

        logger.info("========== HIKARICP CONFIGURATION ==========");
        logger.info("✓ Pool Name: {}", poolName);
        logger.info("✓ JDBC URL: {}", jdbcUrl);
        logger.info("✓ Maximum Pool Size: {}", maxPoolSize);
        logger.info("✓ Minimum Idle Connections: {}", minIdle);
        logger.info("✓ Connection Timeout: {} ms", connectionTimeout);
        logger.info("✓ Idle Timeout: {} ms", idleTimeout);
        logger.info("✓ Max Lifetime: {} ms", maxLifetime);
        logger.info("✓ Leak Detection Threshold: 60000 ms");
        logger.info("==========================================");

        HikariDataSource dataSource = new HikariDataSource(config);

        // Log pool stats
        logger.info("✓ HikariCP DataSource initialized successfully");
        logger.info("✓ Pool Status: Active={}, Idle={}, Size={}",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                //dataSource.getHikariPoolMXBean().getPendingThreads(),
                dataSource.getHikariPoolMXBean().getTotalConnections());

        return dataSource;
    }
}
