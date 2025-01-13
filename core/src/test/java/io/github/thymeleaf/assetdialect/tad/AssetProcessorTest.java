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
    void shouldResolveAssetPath() {
        // Mock the attribute behavior
        when(tag.getAttribute("src")).thenReturn(mock(IAttribute.class));
        when(tag.getAttributeValue("src")).thenReturn("test.css");
        when(resolver.resolve("test.css", null, false)).thenReturn("/resolved/test.css");

        // Process the tag
        processor.doProcess(context, tag, mock(AttributeName.class), "test.css", handler);

        // Verify the handler sets the resolved attribute
        verify(handler).setAttribute("src", "/resolved/test.css");
    }
}