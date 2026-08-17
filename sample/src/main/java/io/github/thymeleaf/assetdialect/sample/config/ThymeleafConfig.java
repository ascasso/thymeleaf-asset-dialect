package io.github.thymeleaf.assetdialect.sample.config;

import io.github.thymeleaf.assetdialect.tad.AssetDialect;
import io.github.thymeleaf.assetdialect.tad.AssetProperties;
import io.github.thymeleaf.assetdialect.tad.DefaultAssetResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableConfigurationProperties(AssetProperties.class)
public class ThymeleafConfig {

    @Bean
    public AssetDialect assetDialect(AssetProperties properties, Environment environment) {
        DefaultAssetResolver resolver = new DefaultAssetResolver(properties, environment);
        return new AssetDialect(properties, resolver);
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, AssetDialect assetDialect) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.addDialect(assetDialect);
        return engine;
    }
}

