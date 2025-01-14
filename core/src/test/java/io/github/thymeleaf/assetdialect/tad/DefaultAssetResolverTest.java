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

        String path = "static/test.css";
        String result = resolver.resolve(path, null, false);

        assertThat(result).isEqualTo(path);
    }

    @Test
    void shouldUseLocalPathWhenForced() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getLocalPath()).thenReturn("/local");

        String path = "static/test.css";
        String result = resolver.resolve(path, null, true);

        assertThat(result).isEqualTo("/local/test.css");
    }

    @Test
    void shouldUseCdnWhenAvailable() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getCdns()).thenReturn(Map.of("cdn1", "https://cdn.example.com"));
        when(properties.isVersionAssets()).thenReturn(false);

        String path = "static/test.css";
        String result = resolver.resolve(path, "cdn1", false);

        assertThat(result).isEqualTo("https://cdn.example.com/test.css");
    }

    @Test
    void shouldAddVersionHashWhenEnabled() throws Exception {
        // Mock the properties to simulate hash-based versioning
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isVersionAssets()).thenReturn(true);
        when(properties.getVersionStrategy()).thenReturn("hash");
        when(properties.getLocalPath()).thenReturn("src/test/resources/static");

        // Create a test file in the expected local path
        Path testFilePath = Path.of("src/test/resources/static/test.css");
        Files.createDirectories(testFilePath.getParent());
        Files.writeString(testFilePath, "body { background: red; }");

        // Resolve the asset
        String resolvedPath = resolver.resolve("static/test.css", null, true);

        // Validate that the resolved path includes a hash
        assertThat(resolvedPath).matches("test\\.[a-f0-9]{32}\\.css");

        // Cleanup the test file
        Files.delete(testFilePath);
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