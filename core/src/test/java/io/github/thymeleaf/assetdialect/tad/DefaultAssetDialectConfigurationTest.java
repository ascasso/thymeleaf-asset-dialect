package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAssetDialectConfigurationTest {

    @Test
    void shouldBuildConfigurationCorrectly() {
        DefaultAssetDialectConfiguration config = DefaultAssetDialectConfiguration.builder()
                .enabled(true)
                .defaultCdn("https://cdn.example.com")
                .addCdn("cdn1", "https://cdn1.example.com")
                .localPath("/local")
                .useLocalInDev(false)
                .versionAssets(true)
                .versionStrategy("hash")
                .build();

        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultCdn()).isEqualTo("https://cdn.example.com");
        assertThat(config.getCdns()).containsEntry("cdn1", "https://cdn1.example.com");
        assertThat(config.getLocalPath()).isEqualTo("/local");
        assertThat(config.isUseLocalInDev()).isFalse();
        assertThat(config.isVersionAssets()).isTrue();
        assertThat(config.getVersionStrategy()).isEqualTo("hash");
    }
}
