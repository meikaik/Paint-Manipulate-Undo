import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectangleModel extends ShapeModel {

    public RectangleModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);

        this.shape = new Rectangle2D.Double(
                startPoint.x,startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }

    @Override
    public void reset(double dx, double dy) {
        int height = endPoint.y - startPoint.y;
        int width = endPoint.x - startPoint.x;
        ((Rectangle2D)this.shape).setRect(startPoint.x, startPoint.y, width + dx, height + dy);
        endPoint.x += dx;
        endPoint.y += dy;
    }

}
