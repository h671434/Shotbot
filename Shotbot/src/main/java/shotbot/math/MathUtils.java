package shotbot.math;

import shotbot.data.CarData;

public class MathUtils {

	public static double sign(double v) {
		return v >= 0 ? 1 : -1;
	}

	public static double cap(double v, double min, double max) {
		return (Math.min(Math.max(v, min), max));
	}
	
	public static Vec3 clamp(Vec3 direction, Vec3 start, Vec3 end) {
		Vec3 down = new Vec3(0, 0, -1);
		boolean isRight = direction.dot(end.cross(down)) < 0;
		boolean isLeft = direction.dot(start.cross(down)) > 0;
		
		if((end.dot(start.cross(down)) < 0) ? (isRight && isLeft) : (isRight || isLeft)) 
			return direction;
		
		if(start.dot(direction) < end.dot(direction))
			return end;
		
		return start;
	}

}
