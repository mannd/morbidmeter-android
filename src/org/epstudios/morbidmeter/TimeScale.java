package org.epstudios.morbidmeter;

public class TimeScale {
	public TimeScale(String name, int minimum, int maximum, String formatString) {
		this.name = name;
		this.minimum = minimum;
		this.maximum = maximum;
		this.formatString = formatString;
	}

	public int duration() {
		return maximum - minimum;
	}

	public double proportionalTime(double percent) {
		return minimum + percent * duration();
	}

	public double reverseProportionaltime(double percent) {
		return maximum - percent * duration();
	}

	private String name;
	private int maximum;
	private int minimum;
	private String formatString;
}
