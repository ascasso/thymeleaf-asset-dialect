package io.github.thymeleaf.assetdialect.tad;

import java.util.Map;

/**
 * Configuration interface for Thymeleaf Asset Dialect.
 */
public interface AssetDialectConfiguration {
    boolean isEnabled();
    String getDefaultCdn();
    Map<String, String> getCdns();
    String getLocalPath();
    boolean isUseLocalInDev();
    boolean isVersionAssets();
    String getVersionStrategy();
    String getAssetBasePath();
}
