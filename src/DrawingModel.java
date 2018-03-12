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
        System.out.println("Edited shape!");
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
        System.out.println("added new shape!");
    }

    public void undo(){
        if(undoManager.canUndo()){
            try {
                undoManager.undo();
            } catch (CannotRedoException ex) {
            }
            modified();
        }
    }

    public void redo(){
        if(undoManager.canRedo()){
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
            }
            modified();
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



        public ShapeUndoable(ShapeModel shapee){
            shape = shapee;
            // position for undo
            p_translateX = shapee.beforeTranslateX;
            p_translateY = shapee.beforeTranslateY;
            p_rotate = shapee.beforeRotate;
            p_endPoint = new Point(shapee.beforeEndPoint.x, shapee.beforeEndPoint.y);
            // position for redo
            n_translateX = shapee.translateX;
            n_translateY = shapee.translateY;
            n_rotate = shapee.rotate;
            n_endPoint = new Point(shapee.endPoint.x, shapee.endPoint.y);
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
            System.out.println("Model: undo location to " + shape.translateX + "," + shape.translateY + "rotate to" + shape.rotate + "endpoint to" + shape.endPoint);
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
            System.out.println("Model: undo location to " + shape.translateX + "," + shape.translateY + "rotate to" + shape.rotate + "endpoint to" + shape.endPoint);
            modified();
        }

    }

    public class ShapeAddDelete extends AbstractUndoableEdit{
        ShapeModel shape;
        public ShapeAddDelete(ShapeModel shapee){
            shape = shapee;
        }


        public void undo() throws CannotRedoException {
            super.undo();
            shape.invisible = true;
            deselectAll();
            System.out.println("Model: undo shape add" );
            modified();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.invisible = false;
            deselectAll();
            shape.selected = true;
            System.out.println("Model: redo shape add");
            modified();
        }

    }

}
