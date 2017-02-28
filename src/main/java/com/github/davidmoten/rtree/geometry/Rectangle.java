package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;

public interface Rectangle extends Geometry, HasGeometry {

    public float x1();

    public float y1();

    public float x2();

    public float y2();

    public float area();

    public Rectangle add(Rectangle r);

    public boolean contains(double x, double y);

    public float intersectionArea(Rectangle r);

    public float perimeter();

    public static class Helper {
        public static Rectangle create(double x1, double y1, double x2, double y2) {
            return create((float) x1, (float) y1, (float) x2, (float) y2);
        }

        public static Rectangle create(float x1, float y1, float x2, float y2) {
            return new RectangleImpl(x1, y1, x2, y2);
        }

        public static float area(Rectangle r) {
            return (r.x2() - r.x1()) * (r.y2() - r.y1());
        }

        public static Rectangle add(Rectangle a, Rectangle r) {
            return create(Math.min(a.x1(), r.x1()), Math.min(a.y1(), r.y1()),
                    Math.max(a.x2(), r.x2()), Math.max(a.y2(), r.y2()));
        }

        public static boolean contains(Rectangle r, double x, double y) {
            return x >= r.x1() && x <= r.x2() && y >= r.y1() && y <= r.y2();
        }

        public static boolean intersects(Rectangle a, Rectangle r) {
            return intersects(a.x1(), a.y1(), a.x2(), a.y2(), r.x1(), r.y1(), r.x2(), r.y2());
        }

        public static double distance(Rectangle a, Rectangle r) {
            return distance(a.x1(), a.y1(), a.x2(), a.y2(), r.x1(), r.y1(), r.x2(), r.y2());
        }

        public static double distance(float x1, float y1, float x2, float y2,
                                      float a1, float b1, float a2, float b2) {
            if (intersects(x1, y1, x2, y2, a1, b1, a2, b2)) {
                return 0;
            }
            boolean xyMostLeft = x1 < a1;
            float mostLeftX1 = xyMostLeft ? x1 : a1;
            float mostRightX1 = xyMostLeft ? a1 : x1;
            float mostLeftX2 = xyMostLeft ? x2 : a2;
            double xDifference = Math.max(0, mostLeftX1 == mostRightX1 ? 0 : mostRightX1 - mostLeftX2);

            boolean xyMostDown = y1 < b1;
            float mostDownY1 = xyMostDown ? y1 : b1;
            float mostUpY1 = xyMostDown ? b1 : y1;
            float mostDownY2 = xyMostDown ? y2 : b2;

            double yDifference = Math.max(0, mostDownY1 == mostUpY1 ? 0 : mostUpY1 - mostDownY2);

            return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        }

        public static boolean intersects(float x1, float y1, float x2, float y2,
                                         float a1, float b1, float a2, float b2) {
            return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2;
        }

        public static float intersectionArea(Rectangle a, Rectangle r) {
            if (!intersects(a, r))
                return 0;
            else
                return create(Math.max(a.x1(), r.x1()), Math.max(a.y1(), r.y1()),
                        Math.min(a.x2(), r.x2()), Math.min(a.y2(), r.y2()))
                        .area();
        }

        public static float perimeter(Rectangle r) {
            return 2 * (r.x2() - r.x1()) + 2 * (r.y2() - r.y1());
        }

        public static String toString(Rectangle r) {
            return "Rectangle [x1=" + r.x1() + ", y1=" + r.y1() + ", x2=" + r.x2() + ", y2=" + r.y2() + "]";
        }

        public static int hashCode(Rectangle r) {
            return Objects.hashCode(r.x1(), r.y1(), r.x2(), r.y2());
        }

        public static boolean equals(Rectangle r, Rectangle other)  {
            return Objects.equal(r.x1(), other.x1()) && Objects.equal(r.x2(), other.x2())
                    && Objects.equal(r.y1(), other.y1()) && Objects.equal(r.y2(), other.y2());
        }
    }

}