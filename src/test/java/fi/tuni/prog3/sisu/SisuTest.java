package fi.tuni.prog3.sisu;

import javafx.scene.control.Button;

import javafx.stage.Stage;

import org.testfx.api.FxAssert;

import org.testfx.framework.junit5.ApplicationTest;

import org.testfx.matcher.base.WindowMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;



import java.io.IOException;


public class SisuTest extends ApplicationTest {
    
    // private Sisu sisu;

    @Override 
    public void start(Stage stage) throws IOException {
        new Sisu().start(stage);
    }

    @Test
    @DisplayName("Test loading user")
    public void testLoadingUserFail() throws InterruptedException {
        Button loadButton = fromAll().lookup("Load").query();
        
        clickOn(loadButton);
        sleep(300);
        FxAssert.verifyThat(window("Load student information"), WindowMatchers.isShowing());
        write("123");
        clickOn("OK");
        FxAssert.verifyThat(window("Load student information"), WindowMatchers.isShowing());
    }

    @Test
    @DisplayName("Test loading user")
    public void testLoadingUserSucess() throws InterruptedException {
        Button loadButton = fromAll().lookup("Load").query();
        clickOn(loadButton);
        sleep(300);
        FxAssert.verifyThat(window("Load student information"), WindowMatchers.isShowing());
        write("45678");
        clickOn("OK");
    }
}