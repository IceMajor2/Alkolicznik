package com.demo.alkolicznik.utils.matchers;

import com.demo.alkolicznik.utils.FileUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.awt.image.BufferedImage;

public class BufferedImageAssert extends AbstractAssert<BufferedImageAssert, BufferedImage> {

	public BufferedImageAssert(BufferedImage actual) {
		super(actual, BufferedImageAssert.class);
	}

	public static BufferedImageAssert assertThat(BufferedImage actual) {
		return new BufferedImageAssert(actual);
	}

	public BufferedImageAssert hasSameDimensionsAs(BufferedImage expected) {
		isNotNull();
		Assertions.assertThat(FileUtils.dimensionsSame(actual, expected))
				.as("Comparing only dimensions of the image")
				.isTrue();
		return this;
	}

	public BufferedImageAssert hasDifferentDimensionsAs(BufferedImage compare) {
		isNotNull();
		Assertions.assertThat(FileUtils.dimensionsSame(actual, compare))
				.as("Comparing only dimensions of the image")
				.isFalse();
		return this;
	}

	public BufferedImageAssert isEqualTo(BufferedImage expected) {
		isNotNull();
		Assertions.assertThat(FileUtils.imageEquals(actual, expected))
				.as("expecting images to be equal")
				.isTrue();
		return this;
	}

	public BufferedImageAssert isNotEqualTo(BufferedImage compare) {
		isNotNull();
		Assertions.assertThat(FileUtils.imageEquals(actual, compare))
				.as("expecting images not to be equal")
				.isFalse();
		return this;
	}
}
