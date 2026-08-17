package io.github.thymeleaf.assetdialect.tad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import static org.mockito.Mockito.*;

class AssetProcessorTest {

    @Mock
    private AssetResolver resolver;
    @Mock
    private ITemplateContext context;
    @Mock
    private IProcessableElementTag tag;
    @Mock
    private IElementTagStructureHandler handler;

    private AssetProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new AssetProcessor("asset", resolver);
    }

    @Test
    void shouldResolveSrcUsingTheExistingPublicConstructor() {
        when(resolver.resolve("test.css", null, false)).thenReturn("/resolved/test.css");
        AttributeName attributeName = mock(AttributeName.class);

        processor.doProcess(context, tag, attributeName, "test.css", handler);

        verify(resolver).resolve("test.css", null, false);
        verify(handler).setAttribute("src", "/resolved/test.css");
        verify(handler).removeAttribute(attributeName);
    }

    @Test
    void shouldResolveHrefAndPreserveCdnAndLocalOptions() {
        AssetProcessor hrefProcessor = new AssetProcessor("asset", "href", resolver);
        IAttribute cdnAttribute = mock(IAttribute.class);
        IAttribute localAttribute = mock(IAttribute.class);
        AttributeName attributeName = mock(AttributeName.class);
        when(tag.getAttribute("asset:cdn")).thenReturn(cdnAttribute);
        when(cdnAttribute.getValue()).thenReturn("styles");
        when(tag.getAttribute("asset:local")).thenReturn(localAttribute);
        when(localAttribute.getValue()).thenReturn("true");
        when(resolver.resolve("test.css", "styles", true)).thenReturn("/resolved/test.css");

        hrefProcessor.doProcess(context, tag, attributeName, "test.css", handler);

        verify(resolver).resolve("test.css", "styles", true);
        verify(handler).setAttribute("href", "/resolved/test.css");
        verify(handler).removeAttribute(attributeName);
    }
}
