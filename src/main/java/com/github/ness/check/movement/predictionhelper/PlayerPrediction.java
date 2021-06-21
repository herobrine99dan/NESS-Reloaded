package com.github.ness.check.movement.predictionhelper;

public class PlayerPrediction {

	/*public void tick(float forward, float strafe) {
		moveRelative(strafe, forward, this.onGround ? 0.1F : 0.02F);
		this.xd *= 0.91F;
		this.yd *= 0.98F;
		this.zd *= 0.91F;
		if (this.onGround) {
			this.xd *= 0.6F;
			this.zd *= 0.6F;
		}
	}

	public void turn(float xo, float yo) {
		this.yaw = (float) (this.yaw + xo * 0.15D);
		this.pitch = (float) (this.pitch - yo * 0.15D);
		if (this.pitch < -90.0F)
			this.pitch = -90.0F;
		if (this.pitch > 90.0F)
			this.pitch = 90.0F;
	}
	
	public void moveRelative(float xa, float za, float speed) {
		float dist = xa * xa + za * za;
		if (dist < 0.01F)
			return;
		dist = speed / (float) Math.sqrt(dist);
		xa *= dist;
		za *= dist;
		float sin = (float) Math.sin(yaw * Math.PI / 180.0D);
		float cos = (float) Math.cos(yaw * Math.PI / 180.0D);
		this.xd += xa * cos - za * sin;
		this.zd += za * cos + xa * sin;
	}*/
}
