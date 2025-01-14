package io.github.thymeleaf.assetdialect.tad;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Processor for the asset:src attribute.
 * Transforms static resource URLs according to configuration and environment.
 * Supports CDN resolution, local development paths, and asset versioning.
 */
public class AssetProcessor extends AbstractAttributeTagProcessor {
    private final AssetResolver resolver;

    public AssetProcessor(String dialectPrefix, AssetResolver resolver) {
        super(TemplateMode.HTML, dialectPrefix, null, false, "src", true, 1000, true);
        this.resolver = resolver;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
                             AttributeName attributeName, String attributeValue,
                             IElementTagStructureHandler handler) {
        if (attributeValue == null || attributeValue.trim().isEmpty()) {
            return;
        }

        // Get CDN attribute if present
        String cdn = null;
        var cdnAttr = tag.getAttribute(getDialectPrefix() + ":cdn");
        if (cdnAttr != null) {
            cdn = cdnAttr.getValue();
        }

        // Get local attribute if present
        boolean forceLocal = false;
        var localAttr = tag.getAttribute(getDialectPrefix() + ":local");
        if (localAttr != null) {
            forceLocal = Boolean.parseBoolean(localAttr.getValue());
        }

        // Resolve the URL and set both the original src and our prefixed attribute
        String resolvedUrl = resolver.resolve(attributeValue, cdn, forceLocal);
        handler.setAttribute("src", resolvedUrl);
        handler.removeAttribute(attributeName);
    }
}
