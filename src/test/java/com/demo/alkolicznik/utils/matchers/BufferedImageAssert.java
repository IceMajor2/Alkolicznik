package com.demo.alkolicznik.utils.matchers;

import java.awt.image.BufferedImage;

import com.demo.alkolicznik.utils.TestUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class BufferedImageAssert extends AbstractAssert<BufferedImageAssert, BufferedImage> {

	public BufferedImageAssert(BufferedImage actual) {
		super(actual, BufferedImageAssert.class);
	}

	public static BufferedImageAssert assertThat(BufferedImage actual) {
		return new BufferedImageAssert(actual);
	}

	public BufferedImageAssert isEqualTo(BufferedImage expected) {
		isNotNull();
		Assertions.assertThat(TestUtils.imageEquals(actual, expected))
				.as("equals")
				.isTrue();
		return this;
	}

	public BufferedImageAssert isNotEqualTo(BufferedImage compare) {
		isNotNull();
		Assertions.assertThat(TestUtils.imageEquals(actual, compare))
				.as("not equal")
				.isFalse();
		return this;
	}
}
