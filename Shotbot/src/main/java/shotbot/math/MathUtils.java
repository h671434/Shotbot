package shotbot.math;

public class MathUtils {

	public static final double DELTA_TIME = 1 / 120; // gameticks per second

	public static double sign(double v) {
		return v >= 0 ? 1 : -1;
	}

	public static double cap(double v, double min, double max) {
		return (Math.min(Math.max(v, min), max));
	}

}
