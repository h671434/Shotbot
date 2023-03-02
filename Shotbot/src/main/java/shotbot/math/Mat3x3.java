package shotbot.math;

public class Mat3x3 {
	
	public double[][] mat;
	
	public Vec3 forward;
	public Vec3 left;
	public Vec3 up; // 0, 0, 1 means car is upright
	
	public Mat3x3(double[][] mat) {
		this.mat = mat;
		this.forward = new Vec3(mat[0][0], mat[1][0], mat[2][0]);
		this.left = new Vec3(mat[2][1], mat[1][1], mat[0][1]);
		this.up = new Vec3(mat[0][2], mat[1][2], mat[2][2]);
	}
	
	public Mat3x3(Vec3 forward, Vec3 left, Vec3 up) {
		this.mat = new double[][]{
			{forward.x, left.x, up.x},
			{forward.y, left.y, up.y},
			{forward.z, left.z, up.z}
		};
		
		this.forward = forward;
		this.left = left;
		this.up = up;
	}
	
	public double[][] getMatrix() {
		return mat;
	}
	
	public Mat3x3 transpose() {
		return new Mat3x3(
				new Vec3(forward.x, left.x, up.x),
				new Vec3(forward.y, left.y, up.y),
				new Vec3(forward.z, left.z, up.z)
			);
	}
	
	/* Returns inner product of vector and matrix */
	public Vec3 dot(Vec3 other) {
		return new Vec3(forward.dot(other), left.dot(other), up.dot(other));
	}
	
	public Mat3x3 dot(Mat3x3 otherMat) {	
		double[][] dotProduct = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				dotProduct[i][j] = 0;
				for(int k = 0; k < 3; k++) {
					dotProduct[i][j] += mat[i][k] * otherMat.getMatrix()[k][j];
				}
			}
		}
		
		return new Mat3x3(dotProduct);
	}
	
	public Mat3x3 plus(Mat3x3 otherMat) {
		double[][] sum = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				sum[i][j] = mat[i][j] + otherMat.getMatrix()[i][j];
			}
		}
		
		return new Mat3x3(sum);
	}
	
	public Mat3x3 minus(Mat3x3 otherMat) {
		double[][] sum = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				sum[i][j] = mat[i][j] - otherMat.getMatrix()[i][j];
			}
		}
		
		return new Mat3x3(sum);
	}
	
	public Mat3x3 mult(Mat3x3 otherMat) {
		double[][] product = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				product[i][j] = mat[i][j] * otherMat.getMatrix()[i][j];
			}
		}
		
		return new Mat3x3(product);
	}
	
	public Mat3x3 scaled(double scale) {
		double[][] product = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				product[i][j] = mat[i][j] * scale;
			}
		}
		
		return new Mat3x3(product);
	}
	
	public Vec3 local(Vec3 carToTarget) {
		return dot(carToTarget);
	}
	

    public Vec3 local(Vec3 target, Vec3 from) {
        Vec3 carToTarget = target.minus(from);
        return new Vec3(
                carToTarget.dot(forward),
                carToTarget.dot(left),
                carToTarget.dot(up)
        );
    }
	
	public static Mat3x3 eye() {
		return new Mat3x3(
			new Vec3(1, 0, 0),
			new Vec3(0, 1, 0),
			new Vec3(0, 0, 1));
	}
	
	public static Mat3x3 lookingInDir(Vec3 direction) {
		Vec3 forward = direction.normalized();
		Vec3 safeUp = Math.abs(forward.z) == 1 && Math.abs(Vec3.UP.z) == 1 ? new Vec3 (direction.x, direction.y, 1) : Vec3.UP;
		Vec3 left = forward.cross(safeUp).normalized();
		Vec3 up = forward.cross(left).normalized();
		
		return new Mat3x3(forward, left, up);
	}
	
    public static Mat3x3 eulerToRotation(double pitch, double yaw, double roll) {
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);

        Vec3 forward = new Vec3	(cp*cy, 			cp*sy, 				sp);
        Vec3 left = new Vec3	(sy*sp*sr-cr*sy, 	sy*sp*sr+cr*cy, 	-cp*cr);
        Vec3 up = new Vec3		(-cr*cy*sp-sr*sy,	-cr*sy*sp+sr*cy,	cp*cr);

       return new Mat3x3(forward, left, up);
   }
	
}
