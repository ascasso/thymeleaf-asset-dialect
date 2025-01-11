package io.github.thymeleaf.assetdialect.tad;


/**
 * Interface for resolving asset URLs based on configuration and environment.
 * Implementations should handle CDN resolution, local paths, and asset versioning.
 */
public interface AssetResolver {

    /**
     * Resolves an asset path to its final URL based on current configuration and environment
     *
     * @param path The original asset path
     * @param cdn Optional CDN name from configuration (can be null)
     * @param forceLocal Whether to force local resolution regardless of environment
     * @return The resolved URL
     */
    String resolve(String path, String cdn, boolean forceLocal);

    /**
     * Simple resolution without specific CDN or local override
     */
    default String resolve(String path) {
        return resolve(path, null, false);
    }

}
