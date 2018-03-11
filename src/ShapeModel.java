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
    ShapeType type = null;

    int beforeTranslateX = 0, beforeTranslateY = 0, beforeRotate = 0;
    Point beforeEndPoint;

    boolean selected = false;

    boolean invisible = false;

    public ShapeModel(Point sp, Point ep) {
        startPoint = sp;
        endPoint = ep;
        beforeEndPoint = ep;
    }

    public ShapeModel duplicate() {
        Point sp = this.startPoint;
        Point ep = this.endPoint;
        ShapeModel s = new ShapeModel.ShapeFactory().getShape(
                type, new Point(sp.x + 10, sp.y + 10), new Point(ep.x + 10, ep.y + 10));
        s.translateX = this.translateX;
        s.translateY = this.translateY;
        s.rotate = this.rotate;
        s.selected = true;
        s.type = this.type;
        return s;
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

    public void reset(int dx, int dy) {
    }

    public Shape rotateHandle() {
        int yMin = Math.min(this.startPoint.y, this.endPoint.y);
        Point midpoint = this.getMidPoint();
        Shape s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Ellipse,
                new Point(midpoint.x - 5, yMin - 20),
                new Point(midpoint.x + 5, yMin - 10)
        ).getShape();
        return s;
    }

    public void rotateShape(int rotatee) {
        rotate += rotatee;
    }

    public Shape scaleHandle() {
        int yMax = Math.max(this.startPoint.y, this.endPoint.y);
        int xMax = Math.max(this.startPoint.x, this.endPoint.x);
        Shape s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Rectangle,
                new Point(xMax -  5, yMax - 5),
                new Point(xMax +  5, yMax + 5)
        ).getShape();
        return s;
    }

    public Shape XScaleHandle() {
        Point midpoint = this.getMidPoint();
        int xMax = Math.max(this.startPoint.x, this.endPoint.x);
        Shape s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Rectangle,
                new Point(xMax -  5, midpoint.y - 5),
                new Point(xMax +  5, midpoint.y + 5)
        ).getShape();
        return s;
    }

    public Shape YScaleHandle() {
        Point midpoint = this.getMidPoint();
        int yMax = Math.max(this.startPoint.y, this.endPoint.y);
        Shape s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Rectangle,
                new Point(midpoint.x -  5, yMax - 5),
                new Point(midpoint.x +  5, yMax + 5)
        ).getShape();
        return s;
    }

    public boolean rotateHitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return rotateHandle().contains(mouseTransformed);
    }

    public boolean scaleHitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return scaleHandle().contains(mouseTransformed);
    }

    public boolean XScaleHitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return XScaleHandle().contains(mouseTransformed);
    }

    public boolean YScaleHitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return YScaleHandle().contains(mouseTransformed);
    }

    public boolean hitTest(Point2D p) {
        Point mouseTransformed = transformMouse(p);
        return this.getShape().contains(mouseTransformed);
    }

    public Point transformMouse(Point2D p) {
        Point mouseTransformed = new Point();
        try {
            AffineTransform newAffine = this.generateAffine(null);
            AffineTransform IAT = newAffine.createInverse();
            IAT.transform(p, mouseTransformed);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return mouseTransformed;
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
