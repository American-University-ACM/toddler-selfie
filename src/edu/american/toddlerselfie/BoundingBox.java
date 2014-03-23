package edu.american.toddlerselfie;

public class BoundingBox {

	public double xLeft = 0;
	public double yLeft = 0;
	public double xRight = 0;
	public double yRight = 0;

	public BoundingBox(double xLeft, double yLeft, double xRight, double yRight) {
		this.xLeft = xLeft;
		this.yLeft = yLeft;
		this.xRight = xRight;
		this.yRight = yRight;
	}

	public double getXLeft() {
		return xLeft;
	}

	public double getYLeft() {
		return yLeft;
	}

	public double getYRight() {
		return yRight;
	}

	public double getXRight() {
		return xRight;
	}

	public boolean contains(double x, double y) {
		return x >= xLeft && x <= xRight && y >= yLeft && y <= yRight;
	}
}
