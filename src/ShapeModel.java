import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;

import java.awt.geom.AffineTransform;

public class ShapeModel {
    Shape shape;
    Point startPoint;
    Point endPoint;
    int translateX = 0, translateY = 0, rotate = 0;
    double scaleX = 1, scaleY = 1;

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

    public void translate(int dx, int dy) {
        translateX += dx;
        translateY += dy;
    }

    // You will need to change the hittest to account for transformations.
    public boolean hitTest(Point2D p) {
        Point mouseTransformed = new Point();

        try {
            AffineTransform newAffine = this.generateAffine(null);
            AffineTransform IAT = newAffine.createInverse();
            IAT.transform(p, mouseTransformed);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return this.getShape().contains(mouseTransformed);
    }

    public AffineTransform generateAffine(AffineTransform affine) {
        Point midpoint = getMidPoint();
        AffineTransform affineNew;
        if (affine == null) {
            affineNew = new AffineTransform();
        } else {
            affineNew = new AffineTransform(affine);
        }
        affineNew.translate(translateX, translateY);
        affineNew.translate(midpoint.x, midpoint.y);
        affineNew.rotate(Math.toRadians(rotate));
        affineNew.scale(scaleX, scaleY);
        affineNew.translate(-(midpoint.x), -(midpoint.y));
        return affineNew;
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

                ShapeModel newShape = constructor.newInstance(startPoint, endPoint);
                return newShape;
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
