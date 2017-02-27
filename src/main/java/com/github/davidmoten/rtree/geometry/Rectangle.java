package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;

public abstract class Rectangle implements Geometry, HasGeometry {

    public abstract float x1();

    public abstract float y1();

    public abstract float x2();

    public abstract float y2();

    public static Rectangle create(double x1, double y1, double x2, double y2) {
        return create((float) x1, (float) y1, (float) x2, (float) y2);
    }

    public static Rectangle create(float x1, float y1, float x2, float y2) {
        return new RectangleImpl(x1, y1, x2, y2);
    }

    public float area() {
        return (x2() - x1()) * (y2() - y1());
    }

    public Rectangle add(Rectangle r) {
        return create(Math.min(x1(), r.x1()), Math.min(y1(), r.y1()),
                Math.max(x2(), r.x2()), Math.max(y2(), r.y2()));
    }

    public boolean contains(double x, double y) {
        return x >= x1() && x <= x2() && y >= y1() && y <= y2();
    }

    public boolean intersects(Rectangle r) {
        return intersects(x1(), y1(), x2(), y2(), r.x1(), r.y1(), r.x2(), r.y2());
    }

    public double distance(Rectangle r) {
        return distance(x1(), y1(), x2(), y2(), r.x1(), r.y1(), r.x2(), r.y2());
    }

    public static double distance(float x1, float y1, float x2, float y2, float a1, float b1,
                                  float a2, float b2) {
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

    private static boolean intersects(float x1, float y1, float x2, float y2, float a1, float b1,
                                      float a2, float b2) {
        return x1 <= a2 && a1 <= x2 && y1 <= b2 && b1 <= y2;
    }

    public Rectangle mbr() {
        return this;
    }

    public String toString() {
        return "Rectangle [x1=" + x1() + ", y1=" + y1() + ", x2=" + x2() + ", y2=" + y2() + "]";
    }

    public int hashCode() {
        return Objects.hashCode(x1(), y1(), x2(), y2());
    }

    public abstract boolean equals(Object obj);

    public float intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else
            return create(Math.max(x1(), r.x1()), Math.max(y1(), r.y1()),
                          Math.min(x2(), r.x2()), Math.min(y2(), r.y2()))
                    .area();
    }

    public float perimeter() {
        return 2 * (x2() - x1()) + 2 * (y2() - y1());
    }

    public Geometry geometry() {
        return this;
    }

}