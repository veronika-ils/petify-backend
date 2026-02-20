package com.petify.petify.service;

import com.petify.petify.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Get top 10 most active users from the database
     */
    public List<Long> getTop10ActiveUserIds() {
        logger.info("Fetching top 10 active users");
        try {
            List<Long> topUserIds = userRepository.findTop10ActiveUserIds();
            logger.info("Found {} top active users", topUserIds.size());
            return topUserIds;
        } catch (Exception e) {
            logger.error("Error fetching top 10 active users: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Check if a specific user is in the top 10 most active users
     */
    public boolean isUserVerified(Long userId) {
        try {
            List<Long> topUsers = getTop10ActiveUserIds();
            boolean isVerified = topUsers.contains(userId);
            logger.debug("User {} verification check: {}", userId, isVerified);
            return isVerified;
        } catch (Exception e) {
            logger.error("Error checking verification status for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
