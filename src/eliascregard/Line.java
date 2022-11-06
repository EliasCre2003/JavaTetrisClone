package eliascregard;

public class Line {
    Vector2D point1;
    Vector2D point2;

    public Line(Vector2D point1, Vector2D point2) {
        this.point1 = point1;
        this.point2 = point2;
    }
    public Line(double x1, double y1, double x2, double y2) {
        this.point1 = new Vector2D(x1, y1);
        this.point2 = new Vector2D(x2, y2);
    }
    public double length() {
        return this.point1.distance(this.point2);
    }

    public static Vector2D lineLineIntersection(Line line1, Line line2) {
        double x1 = line1.point1.x;
        double y1 = line1.point1.y;
        double x2 = line1.point2.x;
        double y2 = line1.point2.y;
        double x3 = line2.point1.x;
        double y3 = line2.point1.y;
        double x4 = line2.point2.x;
        double y4 = line2.point2.y;

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denominator == 0) {
            return null;
        }
        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
        double u = ((x1 - x2) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / denominator;
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            return new Vector2D(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
        }
        return null;
    }
    public static Vector2D lineLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return Line.lineLineIntersection(new Line(x1, y1, x2, y2), new Line(x3, y3, x4, y4));
    }
    public static Vector2D lineLineIntersection(Vector2D point1, Vector2D point2, Vector2D point3, Vector2D point4) {
        return Line.lineLineIntersection(new Line(point1, point2), new Line(point3, point4));
    }
    public Vector2D lineLineIntersection(Line otherLine) {
        return Line.lineLineIntersection(this, otherLine);
    }
}
