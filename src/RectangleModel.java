import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectangleModel extends ShapeModel {

    public RectangleModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);

        Rectangle2D rect = new Rectangle2D.Double(startPoint.x,startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);

        this.shape = rect;
    }

    @Override
    public void reset(int dx, int dy) {
        int height = endPoint.y - startPoint.y;
        int width = endPoint.x - startPoint.x;
        ((Rectangle2D)this.shape).setRect(startPoint.x, startPoint.y, width + dx, height + dy);
        endPoint.x += dx;
        endPoint.y += dy;
    }

}
