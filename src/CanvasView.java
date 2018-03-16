import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;


public class CanvasView extends JPanel implements Observer {

    private DrawingModel model;
    private Point2D lastMouse;
    private Point2D startMouse;
    private boolean selectMode = false;
    private boolean hitRotate = false;
    private boolean hitScale = false;
    private boolean hitXScale = false;
    private boolean hitYScale = false;
    private boolean needStore = false;


    public CanvasView(DrawingModel model) {
        super();
        this.model = model;

        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                lastMouse = e.getPoint();
                startMouse = e.getPoint();

                List<ShapeModel> shapesArray = model.getShapes();
                ListIterator<ShapeModel> it = shapesArray.listIterator(shapesArray.size());
                while(it.hasPrevious()) {
                    ShapeModel shape = it.previous();
                    if (!shape.invisible && shape.selected) {
                        if (startMouse != null && shape.rotateHitTest(startMouse)) {
                            selectMode = true;
                            hitRotate = true;
                        } else if (startMouse != null && shape.scaleHitTest(startMouse)) {
                            selectMode = true;
                            hitScale = true;
                        } else if (startMouse != null && shape.XScaleHitTest(startMouse)) {
                            selectMode = true;
                            hitXScale = true;
                        } else if (startMouse != null && shape.YScaleHitTest(startMouse)) {
                            selectMode = true;
                            hitYScale = true;
                        }
                        break;
                    }
                }

                if (!hitRotate && !hitScale && !hitXScale && !hitYScale) {
                    model.deselectAll();
                } else {
                    return;
                }

                ListIterator<ShapeModel> itNew = shapesArray.listIterator(shapesArray.size());
                while(itNew.hasPrevious()) {
                    ShapeModel shape = itNew.previous();
                    if (!shape.invisible && startMouse != null && shape.hitTest(startMouse)) {
                        shape.selected = true;
                        model.modified();
                        repaint();
                        selectMode = true;
                        break;
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                for(ShapeModel shape : model.getShapes()) {
                    if (startMouse != null && shape.selected && hitScale) {
                        needStore = true;
                        shape.reset((int)(e.getX() - lastMouse.getX()), (int)(e.getY() - lastMouse.getY()));
                    } else if (startMouse != null && shape.selected && hitRotate) {
                        needStore = true;
                        shape.rotateShape((int) (e.getX() - lastMouse.getX()));
                    } else if (startMouse != null && shape.selected && hitXScale) {
                        needStore = true;
                        shape.reset((int)(e.getX() - lastMouse.getX()), 0);
                    } else if (startMouse != null && shape.selected && hitYScale) {
                        needStore = true;
                        shape.reset(0, (int)(e.getY() - lastMouse.getY()));
                    }
                    else if (startMouse != null && shape.selected) {
                        needStore = true;
                        shape.translate((int)(e.getX() - lastMouse.getX()), (int)(e.getY() - lastMouse.getY()));
                    }

                }
                lastMouse = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if (!selectMode && !isTooSmall((Point)startMouse, (Point) lastMouse)) {
                    ShapeModel shape = new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse);
                    shape.type = model.getShape();
                    shape.selected = true;
                    model.addShape(shape);
                }

                if (needStore) {
                    for(ShapeModel shape : model.getShapes()) {
                        if (shape.selected){
                            model.endEdit(shape);
                        }
                    }
                    needStore = false;
                }

                selectMode = false;
                hitRotate = false;
                hitScale = false;
                hitXScale = false;
                hitYScale = false;
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
            if (!shape.invisible) {
                AffineTransform oldAffine = g2.getTransform();
                AffineTransform newAffine = shape.generateAffine(oldAffine);
                g2.setTransform(newAffine);
                if (shape.selected) {
                    drawSelect(g2, shape);
                }
                g2.setColor(new Color(66, 66, 66));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(shape.getShape());
                g2.setTransform(oldAffine);
            }
        }
    }

    private void drawCurrentShape(Graphics2D g2) {
        if (selectMode || startMouse == null || isTooSmall((Point) startMouse, (Point) lastMouse)) {
            return;
        }

        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse).getShape());
    }

    private void drawSelect(Graphics2D g2, ShapeModel shape) {
        g2.setColor(Color.BLUE);
        Shape s = shape.rotateHandle();
        g2.draw(s);
        g2.fill(s);
        s = shape.scaleHandle();
        g2.draw(s);
        g2.fill(s);
        s = shape.XScaleHandle();
        g2.draw(s);
        g2.fill(s);
        s = shape.YScaleHandle();
        g2.draw(s);
        g2.fill(s);
        g2.setColor(new Color(66,66,66));
    }

    public boolean isTooSmall(Point startMouse, Point lastMouse) {
        return Math.hypot(startMouse.x - lastMouse.x,  startMouse.y - lastMouse.y) < 15;
    }
}
