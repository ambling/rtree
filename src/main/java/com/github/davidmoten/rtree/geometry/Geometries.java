package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class Geometries {

    private Geometries() {
        // prevent instantiation
    }

    public static Point point(double x, double y) {
        return Point.create(x, y);
    }

    public static Point point(float x, float y) {
        return Point.create(x, y);
    }

    public static Rectangle rectangle(double x1, double y1, double x2, double y2) {
        return RectangleImpl.create(x1, y1, x2, y2);
    }

    public static Rectangle rectangle(float x1, float y1, float x2, float y2) {
        return RectangleImpl.create(x1, y1, x2, y2);
    }

    public static Circle circle(double x, double y, double radius) {
        return Circle.create(x, y, radius);
    }

    public static Circle circle(float x, float y, float radius) {
        return Circle.create(x, y, radius);
    }

    public static Line line(double x1, double y1, double x2, double y2) {
        return Line.create(x1, y1, x2, y2);
    }

    public static Line line(float x1, float y1, float x2, float y2) {
        return Line.create(x1, y1, x2, y2);
    }

    public static Rectangle rectangleGeographic(float lon1, float lat1, float lon2, float lat2) {
        return rectangleGeographic((double) lon1, (double) lat1, (double) lon2, (double) lat2);
    }

    public static Rectangle rectangleGeographic(double lon1, double lat1, double lon2, double lat2) {
        double x1 = normalizeLongitude(lon1);
        double x2 = normalizeLongitude(lon2);
        if (x2 < x1) {
            x2 += 360;
        }
        return rectangle(x1, lat1, x2, lat2);
    }

    public static Point pointGeographic(double lon, double lat) {
        return point(normalizeLongitude(lon), lat);
    }

    @VisibleForTesting
    static double normalizeLongitude(float d) {
        return normalizeLongitude((double) d);
    }

    private static double normalizeLongitude(double d) {
        if (d == -180.0f)
            return -180.0f;
        else {
            double sign = Math.signum(d);
            double x = Math.abs(d) / 360;
            double x2 = (x - (double) Math.floor(x)) * 360;
            if (x2 >= 180)
                x2 -= 360;
            return x2 * sign;
        }
    }

}
