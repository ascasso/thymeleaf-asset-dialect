package io.github.thymeleaf.assetdialect.tad;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Thymeleaf Asset Dialect.
 * Configuration prefix is 'tad' (Thymeleaf Asset Dialect).
 */
@ConfigurationProperties(prefix = "tad")
public class AssetProperties {

    private boolean enabled = true;
    private String defaultCdn;
    private Map<String, String> cdns = new HashMap<>();
    private String localPath = "";
    private boolean useLocalInDev = true;
    private boolean versionAssets = true;
    private String versionStrategy = "hash"; // or "timestamp"

    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultCdn() {
        return defaultCdn;
    }

    public void setDefaultCdn(String defaultCdn) {
        this.defaultCdn = defaultCdn;
    }

    public Map<String, String> getCdns() {
        return cdns;
    }

    public void setCdns(Map<String, String> cdns) {
        this.cdns = cdns;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isUseLocalInDev() {
        return useLocalInDev;
    }

    public void setUseLocalInDev(boolean useLocalInDev) {
        this.useLocalInDev = useLocalInDev;
    }

    public boolean isVersionAssets() {
        return versionAssets;
    }

    public void setVersionAssets(boolean versionAssets) {
        this.versionAssets = versionAssets;
    }

    public String getVersionStrategy() {
        return versionStrategy;
    }

    public void setVersionStrategy(String versionStrategy) {
        this.versionStrategy = versionStrategy;
    }
}