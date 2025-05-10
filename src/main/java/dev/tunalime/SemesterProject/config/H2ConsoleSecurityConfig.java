package dev.tunalime.SemesterProject.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for H2 Console Security
 * Note: In a production environment, you would want to add Spring Security
 * to protect the H2 console with authentication
 */
@Configuration
public class H2ConsoleSecurityConfig implements WebMvcConfigurer {

    private final Environment env;

    public H2ConsoleSecurityConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void h2ConsoleConfig() {
        // Enable H2 console only in development
        if (isDevelopment()) {
            System.setProperty("h2.console.settings.web-allow-others", "true");
        }
    }

    private boolean isDevelopment() {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length == 0) {
            // If no profiles are active, assume it's development
            return true;
        }
        
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "development".equals(profile)) {
                return true;
            }
        }
        
        return false;
    }
} 