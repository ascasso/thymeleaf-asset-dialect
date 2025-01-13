package io.github.thymeleaf.assetdialect.tad;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of AssetDialectConfiguration.
 */
public class DefaultAssetDialectConfiguration implements AssetDialectConfiguration {
    private boolean enabled = true;
    private String defaultCdn;
    private Map<String, String> cdns = new HashMap<>();
    private String localPath = "";
    private boolean useLocalInDev = true;
    private boolean versionAssets = true;
    private String versionStrategy = "hash";

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getDefaultCdn() {
        return defaultCdn;
    }

    public void setDefaultCdn(String defaultCdn) {
        this.defaultCdn = defaultCdn;
    }

    @Override
    public Map<String, String> getCdns() {
        return cdns;
    }

    public void setCdns(Map<String, String> cdns) {
        this.cdns = cdns;
    }

    @Override
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public boolean isUseLocalInDev() {
        return useLocalInDev;
    }

    public void setUseLocalInDev(boolean useLocalInDev) {
        this.useLocalInDev = useLocalInDev;
    }

    @Override
    public boolean isVersionAssets() {
        return versionAssets;
    }

    public void setVersionAssets(boolean versionAssets) {
        this.versionAssets = versionAssets;
    }

    @Override
    public String getVersionStrategy() {
        return versionStrategy;
    }

    public void setVersionStrategy(String versionStrategy) {
        this.versionStrategy = versionStrategy;
    }
}
