import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;

public class ShapeModel {
    Shape shape;
    Point startPoint;
    Point endPoint;
    boolean selected = false;

    public ShapeModel(Point sp, Point ep) {
        startPoint = sp;
        endPoint = ep;
    }

    public Point getMidPoint() {
        return new Point((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);
    }

    public Shape getShape() {
        return shape;
    }

    // You will need to change the hittest to account for transformations.
    public boolean hitTest(Point2D p) {
        return this.getShape().contains(p);
    }

    /**
     * Given a ShapeType and the start and end point of the shape, ShapeFactory constructs a new ShapeModel
     * using the class reference in the ShapeType enum and returns it.
     */
    public static class ShapeFactory {
        public ShapeModel getShape(ShapeType shapeType, Point startPoint, Point endPoint) {
            try {
                Class<? extends ShapeModel> clazz = shapeType.shape;
                Constructor<? extends ShapeModel> constructor = clazz.getConstructor(Point.class, Point.class);

                return constructor.newInstance(startPoint, endPoint);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public enum ShapeType {
        Ellipse(EllipseModel.class),
        Rectangle(RectangleModel.class),
        Line(LineModel.class);

        public final Class<? extends ShapeModel> shape;
        ShapeType(Class<? extends ShapeModel> shape) {
            this.shape = shape;
        }
    }
}
