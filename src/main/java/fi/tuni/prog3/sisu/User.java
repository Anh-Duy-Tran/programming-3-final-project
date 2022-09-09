package fi.tuni.prog3.sisu;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A class to handle user information and facilitates user-related methods
 */

public class User {
    private Gson gson = new Gson();

    /**
     * A class to store student information
     */
    public static class StudentInformation {
        /**
         * The student number
         */
        public String studentNumber;
        /**
         * The student name
         */
        public String studentName;
        /**
         * The degree ID
         */
        public String degreeId = null;
        /**
         * The track ID
         */
        public String trackId = null;
        /**
         * The student's chosen courses
         */
        public HashSet<String> chosenCourses = new HashSet<>();

        /**
         * Construct the class
         * @param studentNumber the student number
         * @param studentName the student name
         */
        public StudentInformation(String studentNumber, String studentName) {
            this.studentNumber = studentNumber;
            this.studentName = studentName;
        }

        /**
         * Get the student number
         * @return the student number
         */
        public String getStudentNumber() {
            return this.studentNumber;
        }

        /**
         * Get the student name
         * @return the student name
         */
        public String getStudentName() {
            return this.studentName;
        }

        /**
         * Get the student's chosen courses
         * @return the chosen courses
         */
        public ArrayList<String> getChosenCourses() {
            return new ArrayList<>(this.chosenCourses);
        }
    }

    private StudentInformation userInformation;

    /**
     * Set the student who this object belongs to
     * @param user the current student
     */
    public void setUserInformation(StudentInformation user) {
        this.userInformation = user;
    }

    /**
     * Get the student who this object belongs to
     * @return the current student
     */
    public StudentInformation getStudentInformation() {
        return this.userInformation;
    }

    /**
     * Add a completed course for the student
     * @param course_id the course ID
     */
    public void addChoosenCourse(String course_id) {
        this.userInformation.chosenCourses.add(course_id);
    }
     /**
      * Set the student's selected degree
      * @param id the degree ID
      */
    public void setSelectedDegree(String id) {
        this.userInformation.degreeId = id;
    }

    /**
     * Set the student's selected track
     * @param id the track ID
     */
    public void setSelectedTrack(String id) {
        this.userInformation.trackId = id;
    }

    /**
     * Add the student information to the JSON file
     */
    public void printJsonToFile() {
        try {
            Type jsonType = new TypeToken<HashMap<String, StudentInformation>>(){}.getType();

            FileReader jsonReader = new FileReader("src/main/resources/students.json");
            HashMap<String, StudentInformation> students = gson.fromJson(
                jsonReader, jsonType);
            students.put(this.userInformation.studentNumber, this.userInformation);

            FileWriter jsonWriter = new FileWriter("src/main/resources/students.json");
            gson.toJson(students, jsonWriter);
            jsonWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Search the JSON file for the student using student number
     * @param studentNumber the student number
     * @return a StudentInformation object of the student
     * @throws IOException when opening the JSON file failed
     */
    public StudentInformation searchJson(String studentNumber) throws IOException {
        Type jsonType = new TypeToken<HashMap<String, StudentInformation>>(){}.getType();

        FileReader jsonReader = new FileReader("src/main/resources/students.json");
        HashMap<String, StudentInformation> students = gson.fromJson(
            jsonReader, jsonType);

        // Find the student according to the student number
        StudentInformation student = students.get(studentNumber);
        return student;
    }
}
