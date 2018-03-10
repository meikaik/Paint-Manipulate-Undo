import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

public class CanvasView extends JPanel implements Observer {

    private DrawingModel model;
    private Point2D lastMouse;
    private Point2D startMouse;
    private boolean selectMode = false;


    public CanvasView(DrawingModel model) {
        super();
        this.model = model;


        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                lastMouse = e.getPoint();
                startMouse = e.getPoint();

                for(ShapeModel shape : model.getShapes()) {
                        shape.selected = false;
                }

                for(ShapeModel shape : model.getShapes()) {
                    if (startMouse != null && shape.hitTest(startMouse)) {
                        shape.selected = true;
                        repaint();
                        System.out.println("Hit" + shape);
                        selectMode = true;
                    }
                }

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                for(ShapeModel shape : model.getShapes()) {
                    if (startMouse != null && shape.selected) {
                        shape.translate((int)(e.getX() - lastMouse.getX()), (int)(e.getY() - lastMouse.getY()));
                    }
                }
                lastMouse = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if (!selectMode) {
                    ShapeModel shape = new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse);
                    model.addShape(shape);
                }

                selectMode = false;

                startMouse = null;
                lastMouse = null;
            }
        };

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        setBackground(Color.WHITE);

        drawAllShapes(g2);
        drawCurrentShape(g2);
    }

    private void drawAllShapes(Graphics2D g2) {
        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for(ShapeModel shape : model.getShapes()) {
            AffineTransform oldAffine = g2.getTransform();
            AffineTransform newAffine = shape.generateAffine(oldAffine);
            g2.setTransform(newAffine);
            if (shape.selected) {
                drawSelect(g2, shape);
            }
            g2.setColor(new Color(66,66,66));
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(shape.getShape());
            g2.setTransform(oldAffine);
        }
    }

    private void drawCurrentShape(Graphics2D g2) {
        if (selectMode || startMouse == null) {
            return;
        }

        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse).getShape());
    }

    private void drawSelect(Graphics2D g2, ShapeModel shape) {
        int yMin = Math.min(shape.startPoint.y, shape.endPoint.y);
        int yMax = Math.max(shape.startPoint.y, shape.endPoint.y);
        int xMax = Math.max(shape.startPoint.x, shape.endPoint.x);
        Point midpoint = shape.getMidPoint();
        g2.setColor(Color.BLUE);
        Shape s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Ellipse,
                new Point(midpoint.x - 3, yMin - 15),
                new Point(midpoint.x + 2, yMin - 10)
        ).getShape();
        g2.draw(s);
        g2.fill(s);
        s = new ShapeModel.ShapeFactory().getShape(
                ShapeModel.ShapeType.Rectangle,
                new Point(xMax -  3, yMax - 2),
                new Point(xMax +  2, yMax + 3)
        ).getShape();
        g2.draw(s);
        g2.fill(s);
        g2.setColor(new Color(66,66,66));
    }
}
