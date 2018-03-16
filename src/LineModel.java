import java.awt.*;
import java.awt.geom.*;

public class LineModel extends ShapeModel {

    private Point a;
    private Point b;
    public LineModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);
        this.a = startPoint;
        this.b = endPoint;

        Path2D path = new Path2D.Double();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        this.shape = path;
    }

    @Override
    public boolean hitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return pointToLineDistance(a,b, mouseTransformed) < 10;
    }

    @Override
    public void reset(double dx, double dy) {
        endPoint.x += dx;
        endPoint.y += dy;
        Path2D path = new Path2D.Double();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        this.shape = path;
    }

    private double pointToLineDistance(Point A, Point B, Point P) {
        double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
        return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
    }
}