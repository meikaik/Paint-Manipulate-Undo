import java.awt.*;
import java.awt.geom.Ellipse2D;

public class EllipseModel extends ShapeModel {

    public EllipseModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);
        Rectangle rect = new java.awt.Rectangle(startPoint);
        rect.add(endPoint);
        this.shape = new Ellipse2D.Double(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void reset(double dx, double dy) {
        int height = endPoint.y - startPoint.y;
        int width = endPoint.x - startPoint.x;
        ((Ellipse2D)this.shape).setFrame(startPoint.x, startPoint.y, width + dx, height + dy);
        endPoint.x += dx;
        endPoint.y += dy;
    }

}
