package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

/**
 * Simple implementation of Circle
 */
public final class CircleImpl extends Circle {

    private final float x, y, radius;
    private final Rectangle mbr;

    protected CircleImpl(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = RectangleImpl.create(x - radius, y - radius, x + radius, y + radius);
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
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public boolean equals(Object obj) {
        Optional<CircleImpl> other = ObjectsHelper.asClass(obj, CircleImpl.class);
        if (other.isPresent()) {
            return Objects.equal(x, other.get().x) && Objects.equal(y, other.get().y)
                    && Objects.equal(radius, other.get().radius);
        } else
            return false;
    }
}
