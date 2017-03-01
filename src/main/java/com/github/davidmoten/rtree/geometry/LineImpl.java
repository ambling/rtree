package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

/**
 * Simple implementation of Line
 */
public class LineImpl implements Line {


    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;

    protected LineImpl(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public float x1() {
        return x1;
    }

    @Override
    public float y1() {
        return y1;
    }

    @Override
    public float x2() {
        return x2;
    }

    @Override
    public float y2() {
        return y2;
    }

    @Override
    public double distance(Rectangle r) {
        return Line.Helper.distance(this, r);
    }

    @Override
    public Rectangle mbr() {
        return Line.Helper.mbr(this);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return Line.Helper.intersects(this, r);
    }

    @Override
    public boolean intersects(Line b) {
        return Line.Helper.intersects(this, b);
    }

    @Override
    public boolean intersects(Point point) {
        return Line.Helper.intersects(this, point);
    }

    @Override
    public boolean intersects(Circle circle) {
        return Line.Helper.intersects(this, circle);
    }

    @Override
    public String toString() {
        return Line.Helper.toString(this);
    }

    @Override
    public int hashCode() {
        return Line.Helper.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Line.Helper.equals(this, obj);
    }
}
