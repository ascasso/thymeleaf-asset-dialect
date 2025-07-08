package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Security-focused tests for DefaultAssetResolver to ensure path traversal
 * and other security vulnerabilities are properly handled.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultAssetResolverSecurityTest {

    @Mock
    private Environment environment;

    @Mock
    private AssetProperties properties;
    private DefaultAssetResolver resolver;

    @BeforeEach
    void setUp() {
        // Only stub essential properties that are always accessed
        when(properties.isEnabled()).thenReturn(true);
        
        resolver = new DefaultAssetResolver(properties, environment);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "../../../etc/passwd",
        "..\\..\\windows\\system32\\config\\sam",
        "../../../../root/.ssh/id_rsa",
        "..%2f..%2f..%2fetc%2fpasswd",
        "%2e%2e/%2e%2e/%2e%2e/etc/passwd",
        "....//....//etc/passwd",
        "..%252f..%252f..%252fetc%252fpasswd",
        "../\u0000etc/passwd",
        "..\\..\\..\\windows\\system32\\drivers\\etc\\hosts"
    })
    void shouldRejectPathTraversalAttempts(String maliciousPath) {
        assertThatThrownBy(() -> resolver.resolve(maliciousPath))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image<script>.png",
        "style>alert().css",
        "file|pipe.js",
        "name?query.png",
        "wild*card.jpg",
        "null\u0000byte.png",
        "newline\nchar.css",
        "carriage\rreturn.js"
    })
    void shouldRejectInvalidCharacters(String invalidPath) {
        assertThatThrownBy(() -> resolver.resolve(invalidPath))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "C:\\windows\\system32\\config\\sam",
        "\\\\server\\share\\file.txt",
        "D:\\sensitive\\data.txt",
        "/etc/passwd",
        "\\windows\\system32\\drivers\\etc\\hosts"
    })
    void shouldRejectAbsolutePaths(String absolutePath) {
        assertThatThrownBy(() -> resolver.resolve(absolutePath))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "malware.exe",
        "script.bat", 
        "virus.com",
        "trojan.scr",
        "backdoor.cmd",
        "shell.sh",
        "config.ini",
        "database.db",
        "backup.sql"
    })
    void shouldRejectDangerousFileExtensions(String dangerousFile) {
        assertThatThrownBy(() -> resolver.resolve(dangerousFile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image.jpg",
        "style.css",
        "script.js",
        "font.woff2",
        "document.pdf",
        "data.json",
        "favicon.ico",
        "logo.svg",
        "animation.gif",
        "music.mp3"
    })
    void shouldAllowValidAssetPaths(String validPath) {
        // Should not throw exception for valid paths
        String result = resolver.resolve(validPath, null, false);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldHandleNullPath() {
        assertThatThrownBy(() -> resolver.resolve(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @Test
    void shouldHandleEmptyPath() {
        assertThatThrownBy(() -> resolver.resolve(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @Test
    void shouldHandleWhitespaceOnlyPath() {
        assertThatThrownBy(() -> resolver.resolve("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid asset path");
    }

    @Test
    void shouldAllowValidPathsWithSpaces() {
        String validPath = "my image.jpg";
        String result = resolver.resolve(validPath, null, false);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldAllowPathsWithoutExtensions() {
        String validPath = "favicon";
        String result = resolver.resolve(validPath, null, false);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldWorkWithCdnConfiguration() {
        when(properties.getDefaultCdn()).thenReturn("https://cdn.example.com");
        when(properties.isVersionAssets()).thenReturn(false);
        
        String result = resolver.resolve("image.jpg");
        
        assertThat(result).startsWith("https://cdn.example.com");
    }

    @Test
    void shouldWorkWithLocalPathConfiguration() {
        when(properties.getLocalPath()).thenReturn("/assets");
        when(properties.isUseLocalInDev()).thenReturn(true);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"}); // Ensure dev profile is active
        
        String result = resolver.resolve("image.jpg");
        
        assertThat(result).startsWith("/assets");
    }

    @Test
    void shouldDisableSecurityWhenDialectDisabled() {
        when(properties.isEnabled()).thenReturn(false);
        
        // Even malicious paths should pass through when dialect is disabled
        String maliciousPath = "../../../etc/passwd";
        String result = resolver.resolve(maliciousPath, null, false);
        assertThat(result).isEqualTo(maliciousPath);
    }
}