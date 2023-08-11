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

	public BufferedImageAssert hasSameDimensionsAs(BufferedImage expected) {
		isNotNull();
		Assertions.assertThat(TestUtils.dimensionsSame(actual, expected))
				.as("Comparing only dimensions of the image")
				.isTrue();
		return this;
	}

	public BufferedImageAssert isEqualTo(BufferedImage expected) {
		isNotNull();
		Assertions.assertThat(TestUtils.imageEquals(actual, expected))
				.as("expecting images to be equal")
				.isTrue();
		return this;
	}

	public BufferedImageAssert isNotEqualTo(BufferedImage compare) {
		isNotNull();
		Assertions.assertThat(TestUtils.imageEquals(actual, compare))
				.as("expecting images not to be equal")
				.isFalse();
		return this;
	}
}
