package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

public final class Circle implements Geometry {

    private final double x, y, radius;
    private final Rectangle mbr;

    protected Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = RectangleImpl.create(x - radius, y - radius, x + radius, y + radius);
    }

    static Circle create(float x, float y, float radius) {
        return new Circle((double) x, (double) y, (double) radius);
    }

    static Circle create(double x, double y, double radius) {
        return new Circle(x, y, radius);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double radius() {
        return radius;
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, point(x, y).distance(r) - radius);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return distance(r) == 0;
    }

    public boolean intersects(Circle c) {
        double total = radius + c.radius;
        return point(x, y).distanceSquared(point(c.x, c.y)) <= total * total;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, radius);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Circle> other = ObjectsHelper.asClass(obj, Circle.class);
        if (other.isPresent()) {
            return Objects.equal(x, other.get().x) && Objects.equal(y, other.get().y)
                    && Objects.equal(radius, other.get().radius);
        } else
            return false;
    }

    public boolean intersects(Point point) {
        return Math.sqrt(sqr(x - point.x()) + sqr(y - point.y())) <= radius;
    }

    private double sqr(double x) {
        return x * x;
    }

    public boolean intersects(Line line) {
        return line.intersects(this);
    }
}
