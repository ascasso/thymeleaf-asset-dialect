package io.github.thymeleaf.assetdialect.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SampleApplicationTests {

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Test
	void contextLoads() {
	}

	@Test
	void rendersHrefAndSrcAssets() {
		String renderedTemplate = templateEngine.process("index", new Context());

		assertThat(renderedTemplate)
				.doesNotContain("tad:href=\"")
				.containsPattern("href=\"/static/css/styles\\.[a-f0-9]{32}\\.css\"")
				.containsPattern("src=\"/static/image\\.[a-f0-9]{32}\\.jpg\"")
				.containsPattern("src=\"/static/js/main\\.[a-f0-9]{32}\\.js\"");
	}
}
