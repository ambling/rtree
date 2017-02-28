package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

/**
 * Simple implementation of Circle
 */
public final class CircleImpl implements Circle {

    private final float x, y, radius;
    private final Rectangle mbr;

    protected CircleImpl(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = Circle.Helper.mbr(this);
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
    public float radius() {
        return radius;
    }

    @Override
    public boolean intersects(Circle c) {
        return Circle.Helper.intersects(this, c);
    }

    @Override
    public boolean intersects(Point point) {
        return Circle.Helper.intersects(this, point);
    }

    @Override
    public boolean intersects(Line line) {
        return Circle.Helper.intersects(this, line);
    }

    @Override
    public double distance(Rectangle r) {
        return Circle.Helper.distance(this, r);
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return Circle.Helper.intersects(this, r);
    }

    @Override
    public String toString() {
        return Circle.Helper.toString(this);
    }

    @Override
    public int hashCode() {
        return Circle.Helper.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<CircleImpl> other = ObjectsHelper.asClass(obj, CircleImpl.class);
        if (other.isPresent()) {
            return Circle.Helper.equals(this, other.get());
        } else
            return false;
    }
}
