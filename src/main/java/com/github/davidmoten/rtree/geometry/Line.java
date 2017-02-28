package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.predicate.RectangleIntersects;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;

/**
 * A line segment.
 */
public interface Line extends Geometry {

    public float x1();

    public float y1();

    public float x2();

    public float y2();

    public double distance(Rectangle r);

    public Rectangle mbr();

    public boolean intersects(Rectangle r);

    public boolean intersects(Line b);

    public boolean intersects(Point point);

    public boolean intersects(Circle circle);

    public static class Helper {
        public static Line create(float x1, float y1, float x2, float y2) {
            return new LineImpl(x1, y1, x2, y2);
        }

        public static Line create(double x1, double y1, double x2, double y2) {
            return create((float) x1, (float) y1, (float) x2, (float) y2);
        }

        public static double distance(Line l, Rectangle r) {
            if (r.contains(l.x1(), l.y1()) || r.contains(l.x2(), l.y2())) {
                return 0;
            } else {
                double d1 = distance(l, r.x1(), r.y1(), r.x1(), r.y2());
                if (d1 == 0)
                    return 0;
                double d2 = distance(l, r.x1(), r.y2(), r.x2(), r.y2());
                if (d2 == 0)
                    return 0;
                double d3 = distance(l, r.x2(), r.y2(), r.x2(), r.y1());
                double d4 = distance(l, r.x2(), r.y1(), r.x1(), r.y1());
                return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
            }
        }

        private static double distance(Line l, float x1, float y1, float x2, float y2) {
            Float line = new Line2D.Float(x1, y1, x2, y2);
            double d1 = line.ptSegDist(l.x1(), l.y1());
            double d2 = line.ptSegDist(l.x2(), l.y2());
            Float line2 = new Line2D.Float(l.x1(), l.y1(), l.x2(), l.y2());
            double d3 = line2.ptSegDist(x1, y1);
            if (d3 == 0)
                return 0;
            double d4 = line2.ptSegDist(x2, y2);
            if (d4 == 0)
                return 0;
            else
                return Math.min(d1, Math.min(d2, Math.min(d3, d4)));

        }

        public static Rectangle mbr(Line l) {
            return Geometries.rectangle(Math.min(l.x1(), l.x2()), Math.min(l.y1(), l.y2()),
                    Math.max(l.x1(), l.x2()), Math.max(l.y1(), l.y2()));
        }

        public static boolean intersects(Line l, Rectangle r) {
            GeometryFactory gf = new GeometryFactory();
            GeometricShapeFactory f = new GeometricShapeFactory(gf);
            f.setBase(new Coordinate(r.x1(), r.y1()));
            f.setWidth(r.x2() - r.x1());
            f.setHeight(r.y2() - r.y1());
            Polygon rect = f.createRectangle();
            LineSegment line = new LineSegment(l.x1(), l.y1(), l.x2(), l.y2());
            return RectangleIntersects.intersects(rect, line.toGeometry(gf));
        }

        public static boolean intersects(Line a, Line b) {
            Line2D line1 = new Line2D.Float(a.x1(), a.y1(), a.x2(), a.y2());
            Line2D line2 = new Line2D.Float(b.x1(), b.y1(), b.x2(), b.y2());
            return line2.intersectsLine(line1);
        }

        public static boolean intersects(Line l, Point point) {
            return intersects(l, point.mbr());
        }

        public static boolean intersects(Line l, Circle circle) {
            // using Vector Projection
            // https://en.wikipedia.org/wiki/Vector_projection
            Vector c = Vector.create(circle.x(), circle.y());
            Vector a = Vector.create(l.x1(), l.y1());
            Vector cMinusA = c.minus(a);
            float radiusSquared = circle.radius() * circle.radius();
            if (l.x1() == l.x2() && l.y1() == l.y2()) {
                return cMinusA.modulusSquared() <= radiusSquared;
            } else {
                Vector b = Vector.create(l.x2(), l.y2());
                Vector bMinusA = b.minus(a);
                float bMinusAModulus = bMinusA.modulus();
                float lambda = cMinusA.dot(bMinusA) / bMinusAModulus;
                // if projection is on the segment
                if (lambda >= 0 && lambda <= bMinusAModulus) {
                    Vector dMinusA = bMinusA.times(lambda / bMinusAModulus);
                    // calculate distance to line from c using pythagoras' theorem
                    return cMinusA.modulusSquared() - dMinusA.modulusSquared() <= radiusSquared;
                } else {
                    // return true if and only if an endpoint is within radius of
                    // centre
                    return cMinusA.modulusSquared() <= radiusSquared
                            || c.minus(b).modulusSquared() <= radiusSquared;
                }
            }
        }

        public static String toString(Line l) {
            return "Line [x1=" + l.x1() + ", y1=" + l.y1() + ", x2=" + l.x2() + ", y2=" + l.y2() + "]";
        }

        public static int hashCode(Line l) {
            return Objects.hashCode(l.x1(), l.y1(), l.x2(), l.y2());
        }

        public static boolean equals(Line l, Line other) {
            return Objects.equal(l.x1(), other.x1()) && Objects.equal(l.y1(), other.y1())
                    && Objects.equal(l.x2(), other.x2()) && Objects.equal(l.y2(), other.y2());

        }

        private static final class Vector {
            final float x;
            final float y;

            static Vector create(float x, float y) {
                return new Vector(x, y);
            }

            Vector(float x, float y) {
                this.x = x;
                this.y = y;
            }

            float dot(Vector v) {
                return x * v.x + y * v.y;
            }

            Vector times(float value) {
                return create(value * x, value * y);
            }

            Vector minus(Vector v) {
                return create(x - v.x, y - v.y);
            }

            float modulus() {
                return (float) Math.sqrt(x * x + y * y);
            }

            float modulusSquared() {
                return x * x + y * y;
            }
        }
    }



}
