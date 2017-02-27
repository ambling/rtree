package com.github.davidmoten.rtree.geometry;

/**
 * Simple implementation of Point
 */
public class PointImpl extends Point {

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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointImpl other = (PointImpl) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }
}
