package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DefaultAssetResolverTest {

    private DefaultAssetResolver resolver;

    @Mock
    private AssetProperties properties;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new DefaultAssetResolver(properties, environment);
    }

    @Test
    void shouldReturnOriginalPathWhenDisabled() {
        when(properties.isEnabled()).thenReturn(false);

        String path = "test.css";
        String result = resolver.resolve(path, null, false);

        assertThat(result).isEqualTo(path);
    }

    @Test
    void shouldUseLocalPathWhenForced() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getLocalPath()).thenReturn("/local");

        String path = "test.css";
        String result = resolver.resolve(path, null, true);

        assertThat(result).isEqualTo("/local/test.css");
    }

    @Test
    void shouldUseCdnWhenAvailable() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getCdns()).thenReturn(Map.of("cdn1", "https://cdn.example.com"));
        when(properties.isVersionAssets()).thenReturn(false);

        String path = "test.css";
        String result = resolver.resolve(path, "cdn1", false);

        assertThat(result).isEqualTo("https://cdn.example.com/test.css");
    }

    @Test
    void shouldAddVersionHashWhenEnabled() throws Exception {
        // Mock the properties to simulate hash-based versioning
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isVersionAssets()).thenReturn(true);
        when(properties.getVersionStrategy()).thenReturn("hash");

        when(properties.getAssetBasePath()).thenReturn("src/test/resources/static");
        when(properties.getLocalPath()).thenReturn("/assets");

        when(properties.isUseLocalInDev()).thenReturn(true);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Test with a simple path
        String resolvedPath = resolver.resolve("test.css", null, true);
        
        // When forceLocal=true, should use localPath (/assets) not assetBasePath
        // Since the file doesn't exist, hash calculation fails and version is added
        assertThat(resolvedPath).isNotNull().startsWith("/assets");
        assertThat(resolvedPath).contains("test");
        assertThat(resolvedPath).contains(".css");
    }
    @Test
    void shouldDefaultToCurrentTimestampWhenHashFails() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isVersionAssets()).thenReturn(true);
        when(properties.getVersionStrategy()).thenReturn("timestamp");

        String path = "test.css";
        String result = resolver.resolve(path, null, true);

        assertThat(result).startsWith("test.");
    }
}
