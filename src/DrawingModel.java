import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.undo.*;

public class DrawingModel extends Observable {

    private List<ShapeModel> shapes = new ArrayList<>();

    private ShapeModel.ShapeType shapeType = ShapeModel.ShapeType.Rectangle;

    private UndoManager undoManager = new UndoManager();

    public ShapeModel.ShapeType getShape() {
        return shapeType;
    }

    DrawingModel() { }

    public List<ShapeModel> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    public void setShape(ShapeModel.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public void deselectAllShapes() {
        for(ShapeModel shape : shapes) {
            shape.selected = false;
        }
    }

    public void selectShape(ShapeModel s) {
        s.selected = true;
        modified();
    }

    public boolean isDuplicatable() {
        for(ShapeModel shape : shapes) {
            if (shape.selected) {
                return true;
            }
        }
        return false;
    }

    public void addShape(ShapeModel shape) {
        this.shapes.add(shape);
        ShapeAddDelete shapeAddDelete = new ShapeAddDelete(shape);
        undoManager.addEdit(shapeAddDelete);
        modified();
    }

    public void endEdit(ShapeModel shape){
        ShapeUndoable shapeUndoable = new ShapeUndoable(shape);
        undoManager.addEdit(shapeUndoable);
        shape.beforeTranslateX = shape.translateX;
        shape.beforeTranslateY = shape.translateY;
        shape.beforeEndPoint = new Point(shape.endPoint.x, shape.endPoint.y);
        shape.beforeRotate = shape.rotate;
        modified();
    }

    public void undo(){
        if(undoManager.canUndo()){
            try {
                undoManager.undo();
            } catch (CannotRedoException ex) {
                System.out.println("Exception: " + ex);
                ex.printStackTrace();
            }
        }
    }

    public void redo(){
        if(undoManager.canRedo()){
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Exception: " + ex);
                ex.printStackTrace();
            }
        }
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    private void modified() {
        this.setChanged();
        this.notifyObservers();
    }

    public class ShapeUndoable extends AbstractUndoableEdit{

        ShapeModel shape;

        // position for undo
        double p_translateX;
        double p_translateY;
        double p_rotate;
        Point p_endPoint;

        // position for redo
        double n_translateX;
        double n_translateY;
        double n_rotate;
        Point n_endPoint;

        ShapeUndoable(ShapeModel newShape){
            shape = newShape;
            // position for undo
            p_translateX = newShape.beforeTranslateX;
            p_translateY = newShape.beforeTranslateY;
            p_rotate = newShape.beforeRotate;
            p_endPoint = new Point(newShape.beforeEndPoint.x, newShape.beforeEndPoint.y);
            // position for redo
            n_translateX = newShape.translateX;
            n_translateY = newShape.translateY;
            n_rotate = newShape.rotate;
            n_endPoint = new Point(newShape.endPoint.x, newShape.endPoint.y);
        }

        public void undo() throws CannotRedoException {
            super.undo();
            shape.translateX = p_translateX;
            shape.translateY = p_translateY;
            shape.rotate = p_rotate;
            shape.endPoint = new Point(p_endPoint.x, p_endPoint.y);
            shape.reset(0, 0);
            deselectAllShapes();
            shape.selected = true;
            modified();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.translateX = n_translateX;
            shape.translateY = n_translateY;
            shape.rotate = n_rotate;
            shape.endPoint = new Point(n_endPoint.x, n_endPoint.y);
            shape.reset(0, 0);
            deselectAllShapes();
            shape.selected = true;
            modified();
        }
    }

    public class ShapeAddDelete extends AbstractUndoableEdit{
        ShapeModel shape;

        ShapeAddDelete(ShapeModel newShape){
            shape = newShape;
        }

        public void undo() throws CannotRedoException {
            super.undo();
            shape.invisible = true;
            deselectAllShapes();
            modified();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.invisible = false;
            deselectAllShapes();
            shape.selected = true;
            modified();
        }
    }
}