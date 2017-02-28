package com.github.davidmoten.rtree.geometry;

/**
 * Simple implementation of Point
 */
public class PointImpl implements Point {

    private final float x;
    private final float y;

    protected PointImpl(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public double distance(Point p) {
        return Point.Helper.distance(this, p);
    }

    @Override
    public double distanceSquared(Point p) {
        return Point.Helper.distanceSquared(this, p);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public float x1() {
        return x;
    }

    @Override
    public float y1() {
        return y;
    }

    @Override
    public float x2() {
        return x;
    }

    @Override
    public float y2() {
        return y;
    }

    @Override
    public float area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
        return Point.Helper.add(this, r);
    }

    @Override
    public boolean contains(double x, double y) {
        return Point.Helper.contains(this, x, y);
    }

    @Override
    public float intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public float perimeter() {
        return 0;
    }

    @Override
    public double distance(Rectangle r) {
        return Point.Helper.distance(this, r);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return Point.Helper.intersects(this, r);
    }

    @Override
    public String toString() {
        return Point.Helper.toString(this);
    }

    @Override
    public int hashCode() {
        return Point.Helper.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Point.Helper.equals(this, obj);
    }
}
