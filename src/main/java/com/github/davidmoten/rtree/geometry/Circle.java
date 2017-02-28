package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public interface Circle extends Geometry {

    public float x();

    public float y();

    public float radius();

    public boolean intersects(Circle c);

    public boolean intersects(Point point);

    public boolean intersects(Line line);

    public static class Helper {

        public static Circle create(double x, double y, double radius) {
            return create((float) x, (float) y, (float) radius);
        }

        public static Circle create(float x, float y, float radius) {
            return new CircleImpl(x, y, radius);
        }

        public static Rectangle mbr(Circle c) {
            return Rectangle.Helper.create(c.x() - c.radius(), c.y() - c.radius(),
                    c.x() + c.radius(), c.y() + c.radius());
        }

        public static double distance(Circle c, Rectangle r) {
            return Math.max(0, point(c.x(), c.y()).distance(r) - c.radius());
        }

        public static boolean intersects(Circle c, Rectangle r) {
            return distance(c, r) == 0;
        }

        public static boolean intersects(Circle c0, Circle c) {
            double total = c0.radius() + c.radius();
            return point(c0.x(), c0.y()).distanceSquared(point(c.x(), c.y())) <= total * total;
        }

        public static String toString(Circle c) {
            return "Circle [x=" + c.x() + ", y=" + c.y() + ", radius=" + c.radius() + "]";
        }

        public static int hashCode(Circle c) {
            return Objects.hashCode(c.x(), c.y(), c.radius());
        }

        public static boolean equals(Circle c, Circle other) {
            return Objects.equal(c.x(), other.x()) && Objects.equal(c.y(), other.y())
                    && Objects.equal(c.radius(), other.radius());
        }

        public static boolean intersects(Circle c, Point point) {
            return Math.sqrt(sqr(c.x() - point.x()) + sqr(c.y() - point.y())) <= c.radius();
        }

        private static float sqr(float x) {
            return x * x;
        }

        public static boolean intersects(Circle c, Line line) {
            return Line.Helper.intersects(line, c);
        }


    }
}
