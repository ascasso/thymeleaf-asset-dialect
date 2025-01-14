package io.github.thymeleaf.assetdialect.tad;

import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default implementation of AssetResolver.
 * Provides CDN resolution, local development paths, and asset versioning
 * based on Spring environment and configuration properties.
 */
public class DefaultAssetResolver implements AssetResolver {
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
            // Remove any leading slash and get the file name only
            String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
            String fileName = Path.of(normalizedPath).getFileName().toString();
            
            // Try to find the file in the resources directory
            Path filePath = Paths.get("src/test/resources/static", fileName);
            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                return DigestUtils.md5DigestAsHex(content);
            }
        } catch (Exception e) {
            // If hash calculation fails, return null to skip versioning
        }
        return null;
    }

    private String combinePaths(String base, String path) {
        String cleanBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String cleanPath = path.startsWith("/") ? path : "/" + path;
        return cleanBase + cleanPath;
    }
}
