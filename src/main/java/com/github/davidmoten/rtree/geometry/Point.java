package com.github.davidmoten.rtree.geometry;

public interface Point extends Rectangle {

    public abstract float x();

    public abstract float y();

    public double distance(Point p);

    public double distanceSquared(Point p);

    public static class Helper {

        public static Point create(float x, float y) {
            return new PointImpl(x, y);
        }

        public static Point create(double x, double y) {
            return create((float) x, (float) y);
        }

        public static double distance(Point p0, Rectangle r) {
            return Rectangle.Helper.distance(p0.x(), p0.y(), p0.x(), p0.y(), r.x1(), r.y1(), r.x2(), r.y2());
        }

        public static double distance(Point p0, Point p) {
            return Math.sqrt(p0.distanceSquared(p));
        }

        public static double distanceSquared(Point p0, Point p) {
            float dx = p0.x() - p.x();
            float dy = p0.y() - p.y();
            return dx * dx + dy * dy;
        }

        public static boolean intersects(Point p0, Rectangle r) {
            return r.x1() <= p0.x() && p0.x() <= r.x2() && r.y1() <= p0.y() && p0.y() <= r.y2();
        }

        public static int hashCode(Point p0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + Float.floatToIntBits(p0.x());
            result = prime * result + Float.floatToIntBits(p0.y());
            return result;
        }

        public static boolean equals(Point p0, Object obj) {
            if (p0 == obj)
                return true;
            if (obj == null)
                return false;
            if (p0.getClass() != obj.getClass())
                return false;
            Point other = (Point) obj;
            if (Float.floatToIntBits(p0.x()) != Float.floatToIntBits(other.x()))
                return false;
            if (Float.floatToIntBits(p0.y()) != Float.floatToIntBits(other.y()))
                return false;
            return true;
        }

        public static String toString(Point p0) {
            return "Point [x=" + p0.x() + ", y=" + p0.y() + "]";
        }

        public static Rectangle add(Point p0, Rectangle r) {
            return Rectangle.Helper.create(Math.min(p0.x(), r.x1()), Math.min(p0.y(), r.y1()),
                    Math.max(p0.x(), r.x2()), Math.max(p0.y(), r.y2()));
        }

        public static boolean contains(Point p0, double x, double y) {
            return p0.x() == x && p0.y() == y;
        }
    }
}