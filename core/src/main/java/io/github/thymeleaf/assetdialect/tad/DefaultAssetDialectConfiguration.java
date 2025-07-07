package io.github.thymeleaf.assetdialect.tad;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of AssetDialectConfiguration.
 * Can be configured using setter methods or the builder pattern.
 */
public class DefaultAssetDialectConfiguration implements AssetDialectConfiguration {
    private boolean enabled = true;
    private String defaultCdn;
    private Map<String, String> cdns = new HashMap<>();
    private String localPath = "";
    private boolean useLocalInDev = true;
    private boolean versionAssets = true;
    private String versionStrategy = "hash";
    private String assetBasePath = "src/main/resources/static";

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

    @Override
    public String getAssetBasePath() {
        return assetBasePath;
    }

    public void setAssetBasePath(String assetBasePath) {
        this.assetBasePath = assetBasePath;
    }

    /**
     * Creates a new builder for DefaultAssetDialectConfiguration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for DefaultAssetDialectConfiguration.
     */
    public static class Builder {
        private final DefaultAssetDialectConfiguration instance = new DefaultAssetDialectConfiguration();

        public Builder enabled(boolean enabled) {
            instance.setEnabled(enabled);
            return this;
        }

        public Builder defaultCdn(String defaultCdn) {
            instance.setDefaultCdn(defaultCdn);
            return this;
        }

        public Builder cdns(Map<String, String> cdns) {
            instance.setCdns(cdns);
            return this;
        }

        public Builder addCdn(String name, String url) {
            instance.getCdns().put(name, url);
            return this;
        }

        public Builder localPath(String localPath) {
            instance.setLocalPath(localPath);
            return this;
        }

        public Builder useLocalInDev(boolean useLocalInDev) {
            instance.setUseLocalInDev(useLocalInDev);
            return this;
        }

        public Builder versionAssets(boolean versionAssets) {
            instance.setVersionAssets(versionAssets);
            return this;
        }

        public Builder versionStrategy(String versionStrategy) {
            instance.setVersionStrategy(versionStrategy);
            return this;
        }

        public Builder assetBasePath(String assetBasePath) {
            instance.setAssetBasePath(assetBasePath);
            return this;
        }

        public DefaultAssetDialectConfiguration build() {
            return instance;
        }
    }
}