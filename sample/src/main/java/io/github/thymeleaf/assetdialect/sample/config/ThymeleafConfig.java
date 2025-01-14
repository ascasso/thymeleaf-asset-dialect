package io.github.thymeleaf.assetdialect.sample.config;

import io.github.thymeleaf.assetdialect.tad.AssetDialect;
import io.github.thymeleaf.assetdialect.tad.AssetProperties;
import io.github.thymeleaf.assetdialect.tad.DefaultAssetResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(AssetProperties.class)
public class ThymeleafConfig {

    @Bean
    public AssetDialect assetDialect(AssetProperties properties, Environment environment) {
        DefaultAssetResolver resolver = new DefaultAssetResolver(properties, environment);
        return new AssetDialect(properties, resolver);
    }
}
