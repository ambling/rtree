package com.github.davidmoten.rtree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

    double x1();

    double y1();

    double x2();

    double y2();

    double area();

    Rectangle add(Rectangle r);

    boolean contains(double x, double y);

    double intersectionArea(Rectangle r);

    double perimeter();

}