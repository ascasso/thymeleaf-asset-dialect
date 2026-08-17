package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAssetResolverTest {

    private DefaultAssetResolver resolver;

    @Mock
    private AssetProperties properties;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
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

        String resolvedPath = resolver.resolve("test.css", null, true);

        assertThat(resolvedPath).isEqualTo("/assets/test.9e1d43085fa164a02a4d8526e278d5d9.css");
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
