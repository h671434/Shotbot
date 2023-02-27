package shotbot.math;

public class Vec3 extends rlbot.vector.Vector3 {

    public static final Vec3 UP = new Vec3(0, 0, 1);
    public static final Vec3 ZERO = new Vec3(0, 0, 0);

    public final double x, y, z;

    public Vec3(double x, double y, double z) {
        super((float) x, (float) y, (float) z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(rlbot.flat.Vector3 vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public Vec3(double[] xyz) {
		this(xyz[0], xyz[1], xyz[2]);
	}

	public Vec3 withX(double newX) {
        return new Vec3(newX, y, z);
    }

    public Vec3 withY(double newY) {
        return new Vec3(x, newY, z);
    }

    public Vec3 withZ(double newZ) {
        return new Vec3(x, y, newZ);
    }

    public Vec3 plus(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }
    
    public Vec3 plus(double other) {
        return new Vec3(x + other, y + other, z + other);
    }

    public Vec3 minus(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }  

    public Vec3 scaled(double scale) {
        return new Vec3(x * scale, y * scale, z * scale);
    }

    public Vec3 scaledToMag(double magnitude) {
        if (isZero()) {
            throw new IllegalStateException("Cannot scale up a vector with length zero!");
        }
        double scaleRequired = magnitude / mag();
        return scaled(scaleRequired);
    }

    public double dist(Vec3 other) {
        double xDiff = x - other.x;
        double yDiff = y - other.y;
        double zDiff = z - other.z;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public double mag() {
        return Math.sqrt(magSqr());
    }

    public double magSqr() {
        return x * x + y * y + z * z;
    }

    public Vec3 normalized() {
        if (isZero()) {
            throw new IllegalStateException("Cannot normalize a vector with length zero!");
        }
        return this.scaled(1 / mag());
    }

    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vec3 flatten() {
        return new Vec3(x, y, 0);
    }
    
	public Vec3 flatten(Vec3 up) {
		up = up.normalized();
		return minus(up.scaled(dot(up)));
	}

    public double angle(Vec3 other) {
        double mag2 = magSqr();
        double vmag2 = other.magSqr();
        double dot = dot(other);
        return Math.acos(dot / Math.sqrt(mag2 * vmag2));
    }
    
    public double correctionAngle(Vec3 ideal) {
        double currentRad = Math.atan2(y, x);
        double idealRad = Math.atan2(ideal.y, ideal.x);

        if (Math.abs(currentRad - idealRad) > Math.PI) {
            if (currentRad < 0) {
                currentRad += Math.PI * 2;
            }
            if (idealRad < 0) {
                idealRad += Math.PI * 2;
            }
        }

        return idealRad - currentRad;
    }

    public Vec3 cross(Vec3 other) {
        double tx = y * other.z - z * other.y;
        double ty = z * other.x - x * other.z;
        double tz = x * other.y - y * other.x;
        return new Vec3(tx, ty, tz);
    }
    
    public Vec3 offset(Vec3 direction, double offsetvalue) {
    	return minus(direction.scaled(offsetvalue));
    }
    
	public Vec3 clamp(Vec3 start, Vec3 end) {
		Vec3 direction = this;
		
		Vec3 down = new Vec3(0, 0, -1);
		boolean isRight = direction.dot(end.cross(down)) < 0;
		boolean isLeft = direction.dot(start.cross(down)) > 0;
		
		if((end.dot(start.cross(down)) < 0) ? (isRight && isLeft) : (isRight || isLeft)) 
			return direction;
		
		if(start.dot(direction) < end.dot(direction))
			return end;
		
		return start;
	}
    
    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", x, y, z);
    }
}
