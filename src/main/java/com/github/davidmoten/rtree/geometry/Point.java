package com.github.davidmoten.rtree.geometry;

public final class Point implements Rectangle {

    private final double x;
    private final double y;

    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    static Point create(float x, float y) {
        return new Point((double) x, (double) y);
    }

    static Point create(double x, double y) {
        return new Point(x, y);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return RectangleImpl.distance(x, y, x, y, r.x1(), r.y1(), r.x2(), r.y2());
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(Point p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return dx * dx + dy * dy;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return r.x1() <= x && x <= r.x2() && r.y1() <= y && y <= r.y2();
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(x);
        result = prime * result + hashCode(y);
        return result;
    }

    /**
     * Implement the hashCode function for double because it is not available for java6.
     *
     * Copied from java.lang.Double.hashCode in java8
     */
    private int hashCode(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point [x=" + x() + ", y=" + y() + "]";
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public double x1() {
        return x;
    }

    @Override
    public double y1() {
        return y;
    }

    @Override
    public double x2() {
        return x;
    }

    @Override
    public double y2() {
        return y;
    }

    @Override
    public double area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
        return RectangleImpl.create(Math.min(x, r.x1()), Math.min(y, r.y1()), Math.max(x, r.x2()),
                Math.max(y, r.y2()));
    }

    @Override
    public boolean contains(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public double intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public double perimeter() {
        return 0;
    }

}