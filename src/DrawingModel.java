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

    public boolean duplicatable() {
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
        shape.beforeScaleX = shape.scaleX;
        shape.beforeScaleY = shape.scaleY;
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
        public int p_translateX = 0;
        public int p_translateY = 0;
        public double p_scaleX = 0;
        public double p_scaleY = 0;
        public int p_rotate = 0;

        // position for redo
        public int n_translateX = 0;
        public int n_translateY = 0;
        public double n_scaleX = 0;
        public double n_scaleY = 0;
        public int n_rotate = 0;


        public ShapeUndoable(ShapeModel shapee){
            shape = shapee;
            // position for undo
            p_translateX = shapee.beforeTranslateX;
            p_translateY = shapee.beforeTranslateY;
            p_scaleX = shapee.beforeScaleX;
            p_scaleY = shapee.beforeScaleY;
            p_rotate = shapee.beforeRotate;
            // position for redo
            n_translateX = shapee.translateX;
            n_translateY = shapee.translateY;
            n_scaleX = shapee.scaleX;
            n_scaleY = shapee.scaleY;
            n_rotate = shapee.rotate;
        }


        public void undo() throws CannotRedoException {
            super.undo();
            shape.translateX = p_translateX;
            shape.translateY = p_translateY;
            shape.scaleX = p_scaleX;
            shape.scaleY = p_scaleY;
            shape.rotate = p_rotate;
            deselectAll();
            shape.selected = true;
            System.out.println("Model: undo location to " + shape.translateX + "," + shape.translateY);
            notifyObservers();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.translateX = n_translateX;
            shape.translateY = n_translateY;
            shape.scaleX = n_scaleX;
            shape.scaleY = n_scaleY;
            shape.rotate = n_rotate;
            deselectAll();
            shape.selected = true;
            System.out.println("Model: redo location to " + shape.translateX + "," + shape.translateY);
            notifyObservers();
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
            notifyObservers();
        }

        public void redo() throws CannotRedoException {
            super.redo();
            shape.invisible = false;
            deselectAll();
            shape.selected = true;
            System.out.println("Model: redo shape add");
            notifyObservers();
        }

    }

}
