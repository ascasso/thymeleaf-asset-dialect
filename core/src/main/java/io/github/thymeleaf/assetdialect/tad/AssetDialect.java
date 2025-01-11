package io.github.thymeleaf.assetdialect.tad;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * A Thymeleaf dialect that provides asset URL resolution with CDN support.
 * This dialect adds the 'asset' namespace with attributes for managing static resources.
 */
public class AssetDialect extends AbstractProcessorDialect {
    private final AssetProperties properties;
    private final AssetResolver resolver;

    public AssetDialect(AssetProperties properties, AssetResolver resolver) {
        super("Asset Manager", "asset", 1000);
        this.properties = properties;
        this.resolver = resolver;
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new AssetProcessor(dialectPrefix, resolver));
        return processors;
    }
}