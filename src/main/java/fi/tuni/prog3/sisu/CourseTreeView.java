package fi.tuni.prog3.sisu;

import fi.tuni.prog3.sisu.ModuleReader.*;
import fi.tuni.prog3.sisu.ModuleReader.Module;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A class for creating a tree view for the degree structure. This tree view exists
 * in the second tab, on the left of the GUI.
 */
public class CourseTreeView {

    /**
     * TreeView element of JavaFX
     */
    public TreeView<TreeModule> treeView = new TreeView<>();
    /**
     * A TickBoxes object to handle the courses' check boxes
     */
    private TickBoxes tickBoxesController;
    /**
     * The root of the TreeView
     */
    public TreeModule treeViewRoot;
    /**
     * Label to display the progress
     */
    public Label progressLabel = new Label();
    /**
     * Progress bar to display the progress
     */
    ProgressBar progressBar = new ProgressBar(0);

    /**
     * Construct the CourseTreeView object
     * @param pane the pane that contains this tree view
     * @param user the current user
     * @param tickboxes the object containing the CheckBoxes of different courses
     * @param layoutX the horizontal layout value
     * @param layoutY the vertical layout value
     * @param prefWidth the preferred width of the tree view
     * @param prefHeight the preferred height of the tree view
     */
    public CourseTreeView(Pane pane, User user, TickBoxes tickboxes, int layoutX, int layoutY, int prefWidth, int prefHeight) {
        
        //
        //  constructor to set the TreeView position and dimenstion 
        //  
        //  dimenstion still not correct !!

        VBox vbox = new VBox(this.treeView);
        vbox.setMinSize(prefWidth, prefHeight);
        vbox.setLayoutX(layoutX);
        vbox.setLayoutY(layoutY);

        progressLabel.setLayoutX(100);
        progressLabel.setLayoutY(600);
        progressBar.setLayoutX(100);
        progressBar.setLayoutY(650);
        progressBar.setPrefWidth(750);

        pane.getChildren().add(vbox);
        pane.getChildren().add(progressLabel);
        pane.getChildren().add(progressBar);
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleMouseClicked(event);
        };
        
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle); 

        this.tickBoxesController = tickboxes;
    }

    /**
     * Clear the tree view
     */
    public void clearTreeView() {
        this.treeView.setRoot(null);
    }

    /**
     * Get the name of a module
     * @param a the module that we are interested about
     * @return the name of the module
     */
    public String getName(Module a) {

        //
        //  seperate getName method to built language changing option.
        //
        //  this now just get the available name, english is prefered

        if (a.name.en == null) {
            return a.name.fi;
        }
        return a.name.en;
    }

    /**
     * Build the tree view recursively for the GUI using the root of TreeModule
     * @param root the root of TreeModule
     * @return the root of the treeView
     */
    public TreeItem<TreeModule> getTreeViewRoot(TreeModule root) {

        //
        //  method to built the treeView recursively for the GUI using the root of TreeModule
        //  return the root of the treeView
        //  
        TreeItem<TreeModule> treeViewRoot = new TreeItem<>(root);
        
        for (TreeModule sub : root.subModule) {
            if (!sub.value.isCourse) {
                treeViewRoot.getChildren().add(getTreeViewRoot(sub));
            }
            else {
                if (sub.value.isChoosen) {
                    TreeItem<TreeModule> courseNode = new TreeItem<>(sub);
                    ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/icons/courseCompleted.png")));
                    img.setFitHeight(20);
                    img.setFitWidth(20);
                    courseNode.setGraphic(img);
                    treeViewRoot.getChildren().add(courseNode);
                }
            }
        }

        return expandTreeView(treeViewRoot);
    }

    /**
     * Call the readTreeModule method from ModuleReader (which construct the TreeMethod)
     * then take the returning TreeModule (the root of the whole tree) to put to the TreeView
     * @param root_id the id of the root
     * @throws IOException when reading the API's URL failed
     */
    public void populateData(String root_id) throws IOException {

        //
        //  this method call the readTreeModule method from ModuleReader (which construct the TreeMethod) 
        //  then take the returning TreeModule (the root of the whole tree) to put to the TreeView
        //


        this.treeViewRoot = ModuleReader.readTreeModule(root_id);
        
        treeView.setRoot(getTreeViewRoot(this.treeViewRoot));
        
        System.out.println("Done");
    }

    /**
     * Add the data to the tree view
     * @param track_id the id of the track
     * @param degree_id the id of the degree
     * @throws IOException when reading the API's URL failed
     */
    public void populateData(String track_id, String degree_id) throws IOException {

        this.treeViewRoot = ModuleReader.readTreeModule(track_id);
        List<TreeModule> temp = new ArrayList<>();
        temp.add(this.treeViewRoot);
        this.treeViewRoot = new TreeModule(ModuleReader.readModule(degree_id), temp);

        treeView.setRoot(getTreeViewRoot(this.treeViewRoot));

        System.out.println("Done");
    }
    
    /**
     * Load the information of the user, and select the already chosen courses
     * @param user the current user
     * @throws IOException when reading the API's URL failed
     */
    public void loadUser(User user) throws IOException {
        User.StudentInformation student = user.getStudentInformation();
        if (student.trackId == null) {
            populateData(student.degreeId);
        } else {
            populateData(student.trackId, student.degreeId);
        }
        setChoosenCourseFromUser(this.treeViewRoot, new HashSet<>(student.chosenCourses));
        update();
    }

    /**
     * Select the courses that has been chosen by the user
     * @param root the rood of TreeModule
     * @param choosenCourse the courses that the student has chosen
     */
    public void setChoosenCourseFromUser(TreeModule root, HashSet<String> choosenCourse) {
        if (choosenCourse.contains(root.getGroupId())) {
            choosenCourse.remove(root.getGroupId());
            root.value.isChoosen = true;
            System.out.println(root.value.name.en);
        }

        if (root.subModule == null) {
            return;
        }

        for (TreeModule subModule: root.subModule) {
            setChoosenCourseFromUser(subModule, choosenCourse);
        }
    }

    /**
     * Update the tree view
     */
    public void update() {
        progressLabel.setText(String.format("%d / %d", this.treeViewRoot.value.cre , this.treeViewRoot.value.targetCredits.min));
        progressBar.setProgress((float)this.treeViewRoot.value.cre / this.treeViewRoot.value.targetCredits.min);
        treeView.setRoot(getTreeViewRoot(this.treeViewRoot));
    }

    /**
     * empty
     * @param item empty
     * @return empty
     */
    private TreeItem<TreeModule> expandTreeView(TreeItem<TreeModule> item){
        if(item != null && !item.isLeaf()){
            item.setExpanded(true);
            for(TreeItem<TreeModule> child:item.getChildren()){
                expandTreeView(child);
            }
        }

        return item;
    }

    /**
     * empty
     * @return empty
     */
    public TreeModule getSelectedTreeModule() {
        return this.treeView.getSelectionModel().getSelectedItem().getValue();
    }

    /**
     * Handle the mouse clicks on the tree view
     * @param event event of clicking the mouse
     */
    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            tickBoxesController.setSelectedTreeModule(getSelectedTreeModule()); 
        }
    }
}
