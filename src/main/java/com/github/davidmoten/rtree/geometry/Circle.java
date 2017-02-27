package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

public abstract class Circle implements Geometry {

    public static Circle create(double x, double y, double radius) {
        return create((float) x, (float) y, (float) radius);
    }

    public static Circle create(float x, float y, float radius) {
        return new CircleImpl(x, y, radius);
    }

    public abstract float x();

    public abstract float y();

    public abstract float radius();

    @Override
    public abstract Rectangle mbr();

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, point(x(), y()).distance(r) - radius());
    }

    @Override
    public boolean intersects(Rectangle r) {
        return distance(r) == 0;
    }

    public boolean intersects(Circle c) {
        double total = radius() + c.radius();
        return point(x(), y()).distanceSquared(point(c.x(), c.y())) <= total * total;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x(), y(), radius());
    }

    @Override
    public abstract boolean equals(Object obj);

    public boolean intersects(Point point) {
        return Math.sqrt(sqr(x() - point.x()) + sqr(y() - point.y())) <= radius();
    }

    private float sqr(float x) {
        return x * x;
    }

    public boolean intersects(Line line) {
        return line.intersects(this);
    }
}
