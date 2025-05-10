package dev.tunalime.SemesterProject.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Database initializer to ensure H2 database files can be created
 */
@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @PostConstruct
    public void init() {
        // Check if we're using a file-based H2 database
        if (databaseUrl.startsWith("jdbc:h2:file:")) {
            // Extract the path from the URL
            String path = databaseUrl.replace("jdbc:h2:file:", "");
            
            // Get the directory part
            int lastSlashIndex = path.lastIndexOf('/');
            if (lastSlashIndex == -1) {
                lastSlashIndex = path.lastIndexOf('\\');
            }
            
            if (lastSlashIndex != -1) {
                String directory = path.substring(0, lastSlashIndex);
                ensureDirectoryExists(directory);
            }
            
            logger.info("H2 database configured with path: {}", path);
        }
    }
    
    private void ensureDirectoryExists(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                logger.info("Created directory for H2 database: {}", directory);
            } else {
                logger.warn("Failed to create directory for H2 database: {}", directory);
            }
        } else {
            logger.info("Directory for H2 database already exists: {}", directory);
        }
    }
} 