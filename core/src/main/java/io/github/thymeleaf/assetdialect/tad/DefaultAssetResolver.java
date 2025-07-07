package io.github.thymeleaf.assetdialect.tad;

import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default implementation of AssetResolver.
 * Provides CDN resolution, local development paths, and asset versioning
 * based on Spring environment and configuration properties.
 */
public class DefaultAssetResolver implements AssetResolver {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAssetResolver.class);
    
    private final AssetProperties properties;
    private final Environment environment;

    public DefaultAssetResolver(AssetProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Override
    public String resolve(String path, String cdn, boolean forceLocal) {
        if (!properties.isEnabled()) {
            return path;
        }

        // Validate input path for security
        if (!isValidAssetPath(path)) {
            logger.warn("Security violation: Invalid asset path detected - {}", path);
            throw new IllegalArgumentException("Invalid asset path: " + path);
        }

        // Check if we should use local path
        if (shouldUseLocal(forceLocal)) {
            return resolveLocal(path);
        }

        // Resolve CDN URL
        String cdnUrl = resolveCdnUrl(cdn);
        if (!StringUtils.hasText(cdnUrl)) {
            return path;
        }

        // Add version if enabled
        String versionedPath = addVersionIfNeeded(path);

        // Combine CDN URL with path
        return combinePaths(cdnUrl, versionedPath);
    }

    private boolean shouldUseLocal(boolean forceLocal) {
        if (forceLocal) {
            return true;
        }

        return properties.isUseLocalInDev() && isDevelopmentEnvironment();
    }

    private boolean isDevelopmentEnvironment() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("dev") || profile.equalsIgnoreCase("development")) {
                return true;
            }
        }
        return environment.getActiveProfiles().length == 0; // Consider default as dev
    }

    private String resolveLocal(String path) {
        String localPath = properties.getLocalPath();
        String resolvedPath = StringUtils.hasText(localPath) ? combinePaths(localPath, path) : path;
        return addVersionIfNeeded(resolvedPath);
    }

    private String resolveCdnUrl(String cdnName) {
        if (StringUtils.hasText(cdnName)) {
            return properties.getCdns().get(cdnName);
        }
        return properties.getDefaultCdn();
    }

    private String addVersionIfNeeded(String path) {
        if (!properties.isVersionAssets()) {
            return path;
        }

        try {
            String version;
            if ("hash".equals(properties.getVersionStrategy())) {
                version = calculateFileHash(path);
            } else {
                version = String.valueOf(System.currentTimeMillis());
            }

            if (version != null) {
                String extension = StringUtils.getFilenameExtension(path);
                String basePath = StringUtils.stripFilenameExtension(path);
                return basePath + "." + version + (extension != null ? "." + extension : "");
            }
        } catch (Exception e) {
            // If versioning fails, return original path
        }

        return path;
    }

    private String calculateFileHash(String path) {
        try {
            // Validate and normalize the path securely
            Path normalizedPath = securePathResolution(path);
            if (normalizedPath == null) {
                return null;
            }
            
            // Use the configurable base path instead of hardcoded path
            String configuredBasePath = properties.getAssetBasePath();
            Path basePath = Paths.get(configuredBasePath).normalize();
            Path filePath = basePath.resolve(normalizedPath).normalize();
            
            // Ensure the resolved path stays within the base directory
            if (!isPathContainedWithin(filePath, basePath)) {
                logger.error("Security violation: Path traversal attempt detected - {} resolved to {}", 
                           path, filePath.toString());
                throw new SecurityException("Path traversal attempt detected: " + path);
            }
            
            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                return DigestUtils.md5DigestAsHex(content);
            }
        } catch (SecurityException e) {
            // Re-throw security exceptions to prevent silent failures
            throw e;
        } catch (Exception e) {
            // Log other exceptions for debugging but don't expose details
            logger.debug("Failed to calculate hash for asset path: {}", path, e);
        }
        return null;
    }
    
    /**
     * Securely resolves a path by normalizing it and validating it doesn't escape boundaries.
     */
    private Path securePathResolution(String inputPath) {
        try {
            // Remove leading slash and normalize
            String cleanPath = inputPath.startsWith("/") ? inputPath.substring(1) : inputPath;
            Path path = Paths.get(cleanPath).normalize();
            
            // Check if the normalized path contains any parent directory references
            if (path.toString().contains("..")) {
                return null;
            }
            
            // Ensure the path is not absolute after normalization
            if (path.isAbsolute()) {
                return null;
            }
            
            return path;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Verifies that a resolved path stays within the allowed base directory.
     */
    private boolean isPathContainedWithin(Path resolvedPath, Path basePath) {
        try {
            // Get the canonical paths to handle symlinks and normalize
            Path canonicalResolved = resolvedPath.toRealPath();
            Path canonicalBase = basePath.toRealPath();
            
            // Check if the resolved path starts with the base path
            return canonicalResolved.startsWith(canonicalBase);
        } catch (Exception e) {
            // If we can't resolve the paths, assume it's not contained for safety
            return false;
        }
    }

    /**
     * Validates that an asset path is safe and doesn't contain path traversal sequences.
     * 
     * @param path The asset path to validate
     * @return true if the path is valid, false otherwise
     */
    private boolean isValidAssetPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // Check for path traversal sequences
        if (containsPathTraversalSequences(path)) {
            logger.warn("Security violation: Path traversal sequence detected in path - {}", path);
            return false;
        }
        
        // Check for invalid characters
        if (containsInvalidCharacters(path)) {
            logger.warn("Security violation: Invalid characters detected in path - {}", path);
            return false;
        }
        
        // Validate file extension against whitelist
        if (!hasValidFileExtension(path)) {
            logger.warn("Security violation: Invalid file extension detected in path - {}", path);
            return false;
        }
        
        // Path should not be absolute (except for web root relative paths starting with /)
        if (path.contains(":\\") || path.startsWith("\\\\")) {
            logger.warn("Security violation: Absolute path detected - {}", path);
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if the path contains path traversal sequences.
     */
    private boolean containsPathTraversalSequences(String path) {
        // Normalize the path to handle different encodings
        String normalizedPath = path.toLowerCase();
        
        // Check for various path traversal patterns
        String[] traversalPatterns = {
            "../", "..\\", "..",
            "%2e%2e/", "%2e%2e\\", "%2e%2e",
            "..%2f", "..%5c",
            "%2e%2e%2f", "%2e%2e%5c",
            "....//", "....\\\\",
            "..%252f", "..%255c"
        };
        
        for (String pattern : traversalPatterns) {
            if (normalizedPath.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if the path contains invalid characters.
     */
    private boolean containsInvalidCharacters(String path) {
        // Check for null bytes and other control characters
        if (path.contains("\0") || path.contains("\r") || path.contains("\n")) {
            return true;
        }
        
        // Check for other potentially dangerous characters
        char[] invalidChars = {'<', '>', '"', '|', '?', '*'};
        for (char c : invalidChars) {
            if (path.indexOf(c) >= 0) {
                return true;
            }
        }
        
        // Check for valid characters only (alphanumeric, dash, underscore, dot, slash)
        for (char c : path.toCharArray()) {
            if (!isValidPathCharacter(c)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a character is valid for asset paths.
     */
    private boolean isValidPathCharacter(char c) {
        return Character.isLetterOrDigit(c) || 
               c == '/' || c == '-' || c == '_' || c == '.' || c == ' ';
    }
    
    /**
     * Validates that the file has an approved extension for web assets.
     */
    private boolean hasValidFileExtension(String path) {
        String extension = StringUtils.getFilenameExtension(path);
        if (extension == null) {
            // Allow files without extensions (e.g., favicon)
            return true;
        }
        
        // Whitelist of approved file extensions for web assets
        String[] allowedExtensions = {
            // Images
            "jpg", "jpeg", "png", "gif", "svg", "webp", "ico", "bmp", "tiff",
            // Stylesheets
            "css", "scss", "sass", "less",
            // Scripts
            "js", "ts", "jsx", "tsx", "mjs",
            // Fonts
            "woff", "woff2", "ttf", "otf", "eot",
            // Documents
            "pdf", "txt", "md", "json", "xml",
            // Audio/Video
            "mp3", "mp4", "wav", "ogg", "webm", "avi", "mov",
            // Archives (for bundled assets)
            "zip", "gz", "tar"
        };
        
        String lowerExtension = extension.toLowerCase();
        for (String allowed : allowedExtensions) {
            if (lowerExtension.equals(allowed)) {
                return true;
            }
        }
        
        return false;
    }

    private String combinePaths(String base, String path) {
        String cleanBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String cleanPath = path.startsWith("/") ? path : "/" + path;
        return cleanBase + cleanPath;
    }
}
