package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssetPropertiesTest {

    @Test
    void shouldAllowSettingProperties() {
        AssetProperties properties = new AssetProperties();
        properties.setEnabled(false);
        properties.setDefaultCdn("https://cdn.example.com");
        properties.setCdns(Map.of("cdn1", "https://cdn1.example.com"));
        properties.setLocalPath("/local");
        properties.setUseLocalInDev(false);
        properties.setVersionAssets(true);
        properties.setVersionStrategy("timestamp");

        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getDefaultCdn()).isEqualTo("https://cdn.example.com");
        assertThat(properties.getCdns()).containsEntry("cdn1", "https://cdn1.example.com");
        assertThat(properties.getLocalPath()).isEqualTo("/local");
        assertThat(properties.isUseLocalInDev()).isFalse();
        assertThat(properties.isVersionAssets()).isTrue();
        assertThat(properties.getVersionStrategy()).isEqualTo("timestamp");
    }
}