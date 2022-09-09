package fi.tuni.prog3.sisu;

import fi.tuni.prog3.sisu.ModuleReader.TreeModule;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * A class to handle the check boxes for users to select completed
 */
public class TickBoxes {
    
    private TreeModule selectedTreeModule;
    private User user;
    private VBox Vbox_checkboxGroup = new VBox();
    private CourseTreeView courseTreeView;
    
    /**
     * Constructs a new TickBoxes object
     * @param pane the pane that contains the TickBoxes
     * @param user the current user that is using the program
     * @param layoutX the horizontal layout of the TickBoxes
     * @param layoutY the vertical layout of the TickBoxes
     */
    public TickBoxes(Pane pane, User user, int layoutX, int layoutY) {
        pane.getChildren().add(Vbox_checkboxGroup);
        Vbox_checkboxGroup.setLayoutX(layoutX);
        Vbox_checkboxGroup.setLayoutY(layoutY);

        this.user = user;
    }

    /**
     * Set the course tree view that TickBoxes based on for creating the check boxes
     * @param tv the course tree view
     */
    public void setCourseTreeView(CourseTreeView tv) {
        this.courseTreeView = tv;
    }
    
    /**
     * Create a separate check box for each course
     * @param tm the TreeModule representing each course
     */
    public void setSelectedTreeModule(TreeModule tm) {
        this.selectedTreeModule = tm;

        Vbox_checkboxGroup.getChildren().clear();

        if (tm.subModule == null)
            return;

        for (TreeModule course : tm.subModule) {
            if (course.value.isCourse)
            {
                CheckBox cb;
                if (course.value.name.en == null) {
                    cb = new CheckBox(course.value.name.fi);
                } else {
                    cb = new CheckBox(course.value.name.en);
                }
                if (course.value.isChoosen) {
                    cb.setSelected(true);
                }
                cb.setOnMouseClicked(e -> {
                    course.value.isChoosen = !course.value.isChoosen;
                    user.addChoosenCourse(course.getGroupId());
                    updateCre(this.courseTreeView.treeViewRoot); 

                    this.courseTreeView.update();
                });

                // Subwindow to show the information of the courses
                final Stage courseInfoStage = new Stage();
                courseInfoStage.setX(400);
                courseInfoStage.setY(350);
                Label infoName = new Label("Name: " + course.getName());
                Label infoCode = new Label("Code: " + course.getCode());
                Label infoCredits = new Label("Credits: " + course.getCredits());
                Label infoGradeScale = new Label("Grade scale: " + course.getGradeScale());
                // Label infoOutcomes = new Label("Outcomes: " + course.getOutcomes());
                // Label infoPrerequisites = new Label("Prerequisites: " + course.getPrerequisites());
                // Label infoContent = new Label("Content: " + course.getContent());

                VBox courseInfoLayout = new VBox();
                courseInfoLayout.setAlignment(Pos.CENTER);
                courseInfoLayout.getChildren().addAll(infoName, infoCode, infoCredits, infoGradeScale);
                courseInfoStage.setScene(new Scene(courseInfoLayout, 350, 150));
                courseInfoStage.setTitle(course.getName());

                cb.setOnMouseEntered(e -> {
                    courseInfoStage.show();
                });

                cb.setOnMouseExited(e -> {
                    courseInfoStage.close();
                });

                Vbox_checkboxGroup.getChildren().add(cb);
            }
        }
    }

    /**
     * Update the total credit of the user
     * @param root the root TreeModule
     * @return the credit of the root TreeModule
     */
    public Integer updateCre(TreeModule root) {
        if (root.subModule == null) {
            if (root.value.isChoosen) {
                if (root.value.credits.max == null)
                    return 0;
                    
                return root.value.credits.max;
            }
            else return 0;
        }
        else {
            root.value.cre = 0;
            for (TreeModule sub : root.subModule) {
                root.value.cre += updateCre(sub);
            }
        }
        return root.value.cre;
    }
}

