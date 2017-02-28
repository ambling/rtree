package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

final class RectangleImpl implements Rectangle {
    private final float x1, y1, x2, y2;

    protected RectangleImpl(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x1()
     */
    @Override
    public float x1() {
        return x1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y1()
     */
    @Override
    public float y1() {
        return y1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x2()
     */
    @Override
    public float x2() {
        return x2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y2()
     */
    @Override
    public float y2() {
        return y2;
    }

    @Override
    public float area() {
        return Rectangle.Helper.area(this);
    }

    @Override
    public Rectangle add(Rectangle r) {
        return Rectangle.Helper.add(this, r);
    }

    @Override
    public boolean contains(double x, double y) {
        return Rectangle.Helper.contains(this, x, y);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return Rectangle.Helper.intersects(this, r);
    }

    @Override
    public double distance(Rectangle r) {
        return Rectangle.Helper.distance(this, r);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public float intersectionArea(Rectangle r) {
        return Rectangle.Helper.intersectionArea(this, r);
    }

    @Override
    public float perimeter() {
        return Rectangle.Helper.perimeter(this);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public String toString() {
        return Rectangle.Helper.toString(this);
    }

    @Override
    public int hashCode() {
        return Rectangle.Helper.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<RectangleImpl> other = ObjectsHelper.asClass(obj, RectangleImpl.class);
        if (other.isPresent()) {
            return Rectangle.Helper.equals(this, other.get());
        } else
            return false;
    }
}