import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.undo.*;

public class DrawingModel extends Observable {

    private List<ShapeModel> shapes = new ArrayList<>();

    UndoManager undoManager = new UndoManager();


    ShapeModel.ShapeType shapeType = ShapeModel.ShapeType.Rectangle;

    public ShapeModel.ShapeType getShape() {
        return shapeType;
    }


    ShapeUndoable shapeUndoable;
    ShapeAddDelete shapeAddDelete;

    public boolean isDuplicatable() {
        for(ShapeModel shape : shapes) {
            if (shape.selected) {
                return true;
            }
        }
        return false;
    }

    public void deselectAll() {
        for(ShapeModel shape : shapes) {
            shape.selected = false;
        }
    }

    public void endEdit(ShapeModel shape){
        shapeUndoable = new ShapeUndoable(shape);
        undoManager.addEdit(shapeUndoable);
        shape.beforeTranslateX = shape.translateX;
        shape.beforeTranslateY = shape.translateY;
        shape.beforeEndPoint = new Point(shape.endPoint.x, shape.endPoint.y);
        shape.beforeRotate = shape.rotate;
        modified();
    }

    public void setShape(ShapeModel.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public DrawingModel() { }

    public List<ShapeModel> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    public void addShape(ShapeModel shape) {
        this.shapes.add(shape);
        shapeAddDelete = new ShapeAddDelete(shape);
        undoManager.addEdit(shapeAddDelete);
        modified();
    }

    public void undo(){
        if(undoManager.canUndo()){
            try {
                undoManager.undo();
            } catch (CannotRedoException ex) {
            }
        }
    }

    public void redo(){
        if(undoManager.canRedo()){
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
            }
        }
    }

    public void modified() {
        this.setChanged();
        this.notifyObservers();
    }


    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public class ShapeUndoable extends AbstractUndoableEdit{

        ShapeModel shape;

        // position for undo
        public int p_translateX;
        public int p_translateY;
        public int p_rotate;
        public Point p_endPoint;

        // position for redo
        public int n_translateX;
        public int n_translateY;
        public int n_rotate;
        public Point n_endPoint;



        public ShapeUndoable(ShapeModel newShape){
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
            deselectAll();
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
            deselectAll();
            shape.selected = true;
            modified();
        }

    }

    public class ShapeAddDelete extends AbstractUndoableEdit{
        ShapeModel shape;
        public ShapeAddDelete(ShapeModel newShape){
            shape = newShape;
        }


        public void undo() throws CannotRedoException {
            super.undo();
            shape.invisible = true;
            deselectAll();
            modified();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.invisible = false;
            deselectAll();
            shape.selected = true;
            modified();
        }

    }

}
