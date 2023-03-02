package shotbot.boost;

import shotbot.math.Vec3;

public class BoostPad {

	public static final double SMALL_PAD_HEIGHT = 165;
	public static final double SMALL_PAD_RADIUS = 144;
	public static final double SMALL_PAD_AMOUNT = 12;
	public static final double SMALL_PAD_REFRESH = 4;
	
	public static final double BIG_PAD_HEIGHT = 168;
	public static final double BIG_PAD_RADIUS = 208;
	public static final double BIG_PAD_AMOUNT = 100;
	public static final double BIG_PAD_REFRESH = 10;
	
    private final Vec3 location;
    private final boolean isFullBoost;
    private boolean isActive;

    public BoostPad(Vec3 location, boolean isFullBoost) {
        this.location = location;
        this.isFullBoost = isFullBoost;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Vec3 getLocation() {
        return location;
    }

    public boolean isFullBoost() {
        return isFullBoost;
    }

    public boolean isActive() {
        return isActive;
    }
}
