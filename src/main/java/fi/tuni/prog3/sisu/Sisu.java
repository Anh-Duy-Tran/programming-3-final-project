package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.util.Objects;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class for handling the main application
 */

public class Sisu extends Application {

    /**
     * Custom class for the comboBox to both store a displacing name and its usable String value
     */

    public static class comboItem {
        public String key, value;
        
        /**
         * Constructs a new comboItem
         * @param DisplaceValue the key of the comboItem
         * @param Value the value of the conmboItem
         */
        public comboItem(String DisplaceValue, String Value) {
            this.key = DisplaceValue;
            this.value = Value;
        }
        @Override
        public String toString() {
            return this.key;
        }
    }

    private String root_id;

    private static final int WINDOW_HEIGHT = 720;
    private static final int WINDOW_WIDTH = 1080;
    private static final int LEFT_MARGIN = 400;
    private static final int LEFT_MARGIN_2 = 600;

    private GUIController GUIcontroller;
    private User user = new User();    

    @Override
    public void start(Stage stage) throws IOException{

        // init classes
        GUIcontroller = new GUIController();

        stage.setTitle("SISU");

        /////////////////////////////////
        // tab1 : Student information. //
        /////////////////////////////////

        Tab tab_1 = new Tab("Student Information");

        TextField StudentNameInput = new TextField();
        StudentNameInput.setPromptText("Enter student name."); 
        StudentNameInput.setPrefWidth(150);
        StudentNameInput.setLayoutX(LEFT_MARGIN);
        StudentNameInput.setLayoutY(300);

        TextField StudentNumberInput = new TextField();
        StudentNumberInput.setPromptText("Enter student number."); 
        StudentNumberInput.setPrefWidth(150);
        StudentNumberInput.setLayoutX(LEFT_MARGIN);
        StudentNumberInput.setLayoutY(350);

        Button exitButton1 = new Button("Exit");
        exitButton1.setLayoutX(WINDOW_WIDTH-48);
        exitButton1.setLayoutY(WINDOW_HEIGHT-70);
        
        exitButton1.setOnAction((event) -> {stage.close();});

        Button beginButton = new Button("Begin");
        beginButton.setLayoutX(WINDOW_WIDTH/2);
        beginButton.setLayoutY(500);

        Button loadButton = new Button("Load");
        loadButton.setLayoutX(WINDOW_WIDTH/2+50);
        loadButton.setLayoutY(500);
        
        ComboBox<comboItem> langComboBox = new ComboBox<>();
        langComboBox.getItems().add(new comboItem("English", "en"));
        langComboBox.getItems().add(new comboItem("Finnish", "fi"));

        langComboBox.setLayoutX(LEFT_MARGIN_2);
        langComboBox.setLayoutY(300);
        langComboBox.setPromptText("Degree language");

        ComboBox<String> yearComboBox = new ComboBox<>();
        for (int i = 2019; i <= 2030; i++) {
            yearComboBox.getItems().add(String.format("%d", i));
        }
        yearComboBox.setLayoutX(LEFT_MARGIN_2);
        yearComboBox.setLayoutY(350);
        yearComboBox.setPromptText("Academic year");
        
        ImageView TUNI_logo = new ImageView(new Image(getClass().getResourceAsStream("/icons/Tampere_University_logo.png")));
        TUNI_logo.setLayoutX(WINDOW_WIDTH - 260);
        TUNI_logo.setLayoutY(-10);
        TUNI_logo.setFitHeight(100);
        TUNI_logo.setPreserveRatio(true);
        
        ImageView SISU_logo = new ImageView(new Image(getClass().getResourceAsStream("/icons/Sisu_logo.png")));
        SISU_logo.setLayoutX(WINDOW_WIDTH/2 - 185);
        SISU_logo.setLayoutY(50);
        
        ImageView BANNER = new ImageView(new Image(getClass().getResourceAsStream("/icons/Banner.png")));
        BANNER.setLayoutX(0);
        BANNER.setLayoutY(80);
        BANNER.setFitWidth(WINDOW_WIDTH);
        BANNER.setPreserveRatio(true);
        
        Pane tab1 = new Pane();
        tab1.getChildren().add(BANNER);
        tab1.getChildren().add(SISU_logo);
        tab1.getChildren().add(TUNI_logo);
        tab1.getChildren().add(beginButton);
        tab1.getChildren().add(StudentNameInput);
        tab1.getChildren().add(StudentNumberInput);
        tab1.getChildren().add(exitButton1);
        tab1.getChildren().add(loadButton);
        tab1.getChildren().add(langComboBox);
        tab1.getChildren().add(yearComboBox);
        
        tab_1.setContent(tab1);

        /////////////////////////////////
        // tab2 : Structure of studies //
        /////////////////////////////////
        
        Tab tab_2 = new Tab("Structure of studies");
        Pane tab2 = new Pane();

        TickBoxes tickBoxesController = new TickBoxes(tab2, user, 600, 300);
        
        ComboBox<comboItem> degreeComboBox = new ComboBox<>();
        degreeComboBox.setPromptText("Please choose a degree.");
        degreeComboBox.setPrefWidth(WINDOW_WIDTH/2);
        degreeComboBox.setLayoutX(0);
        degreeComboBox.setLayoutY(0);
        
        ComboBox<comboItem> trackComboBox = new ComboBox<>();
        trackComboBox.setPromptText("Please choose a track.");
        trackComboBox.setPrefWidth(WINDOW_WIDTH/2);
        trackComboBox.setLayoutX(WINDOW_WIDTH/2);
        trackComboBox.setLayoutY(0);
        
        CourseTreeView courseTreeView = new CourseTreeView(tab2, user, tickBoxesController, 0, 27, WINDOW_WIDTH/2, 500);

        tickBoxesController.setCourseTreeView(courseTreeView);

        Button exitButton2 = new Button("Save and exit");
        exitButton2.setLayoutX(WINDOW_WIDTH-100);
        exitButton2.setLayoutY(WINDOW_HEIGHT-70);

        Button switchBack = new Button("Back");
        switchBack.setLayoutX(10);
        switchBack.setLayoutY(WINDOW_HEIGHT-70);
        
        tab2.getChildren().add(exitButton2);
        tab2.getChildren().add(switchBack);
        tab2.getChildren().add(degreeComboBox);
        tab2.getChildren().add(trackComboBox);
            
            
        // Quit button on tab 2 save the information of the student into a JSON file
        exitButton2.setOnAction((event) -> {
            user.printJsonToFile();

            // Close the program
            stage.close();
        });
        
        stage.setOnCloseRequest((event) -> {Platform.exit();});
            
            
        tab_2.setContent(tab2); 
        
        TabPane tabpane = new TabPane(tab_1, tab_2);
        Scene scene1 = new Scene(tabpane, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene1);

        degreeComboBox.setOnAction(e -> {
            try {
                this.root_id = degreeComboBox.getSelectionModel().getSelectedItem().value;
                GUIcontroller.setTrackCB(trackComboBox, this.root_id);
                
                if (trackComboBox.getItems().size() == 0) {
                    courseTreeView.populateData(root_id);
                }

                user.setSelectedDegree(degreeComboBox.getSelectionModel().getSelectedItem().value);
                
                
            } catch (Exception e1) {}
        });
        
        trackComboBox.setOnAction( e -> {
            
            if (trackComboBox.getSelectionModel().getSelectedItem() != null) 
            this.root_id = trackComboBox.getSelectionModel().getSelectedItem().value;
            
            if (trackComboBox.getItems().size() != 0) {
                try {
                    courseTreeView.populateData(this.root_id, degreeComboBox.getSelectionModel().getSelectedItem().value);
                    user.setSelectedTrack(trackComboBox.getSelectionModel().getSelectedItem().value);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        // Back button help to switch back to the student information tab
        switchBack.setOnAction((event) -> {
            tabpane.getSelectionModel().select(0);
        });

        // Begin button's action trigers the API reader and put the data to the comboBox.
        beginButton.setOnAction((event) -> {
            try {
                GUIcontroller.setFilteredContentToDegreeCB(degreeComboBox, yearComboBox.getValue(), langComboBox.getValue().value);
                courseTreeView.clearTreeView();
            } catch (Exception e) {

                System.out.println("Cannot find Degree");
            }

            tabpane.getSelectionModel().select(1);

            degreeComboBox.setPromptText("Please choose a degree.");
            trackComboBox.setPromptText("Please choose a track.");

            
            user.setUserInformation( new User.StudentInformation(StudentNumberInput.getText(), 
                                                                 StudentNameInput.getText() ) );

        });


        // Subwindow for searching for the student number
        final Stage numberSearchStage = new Stage();

        Label prompt = new Label("Please enter your student number");
        TextField studentNumberInput = new TextField();
        studentNumberInput.setAlignment(Pos.CENTER);
        Label infoBox = new Label("Message");
        Button okButton = new Button("OK");

        VBox numberSearchLayout = new VBox();
        numberSearchLayout.setAlignment(Pos.CENTER);
        numberSearchLayout.getChildren().addAll(prompt, studentNumberInput, infoBox, okButton);
        numberSearchStage.setScene(new Scene(numberSearchLayout, 300, 150));
        numberSearchStage.setTitle("Load student information");

        // OK button triggers the searching and loading process
        okButton.setOnAction((event) -> {
            try {
                String studentNumber = studentNumberInput.getText().toString();

                // If no student number is entered, print the error message
                if (studentNumber.equals("")) {
                    infoBox.setText("Please enter a valid student number!");
                } else {
                    // Find the student
                    User.StudentInformation student = user.searchJson(studentNumber);

                    // If the student is not found, print the error message
                    // Else, populate the data and switch the tab
                    if (Objects.isNull(student)) {
                        infoBox.setText("Student not found!");
                    } else {
                        
                        user.setUserInformation(student);

                        StudentNumberInput.setText(studentNumber);
                        StudentNameInput.setText(student.getStudentName());

  
                        courseTreeView.loadUser(user);

                        numberSearchStage.close();
                        tabpane.getSelectionModel().select(1);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Load button triggers a new subwindow for entering student number
        loadButton.setOnAction((event) -> {
            numberSearchStage.show();
        });

        stage.show();
    }

        /**
     * Run the program
     * @param args arguments
     */
    public static void main(String[] args) {
        launch( args );
    }
}
