package fi.tuni.prog3.sisu;

import fi.tuni.prog3.sisu.ModuleReader.*;
import fi.tuni.prog3.sisu.ModuleReader.Module;

import java.io.IOException;
import java.util.List;

import fi.tuni.prog3.sisu.Sisu.comboItem;
import javafx.scene.control.ComboBox;

/**
 * Class to store the GUI comboBox actions / funtionalities
 */
public class GUIController {

    // string for the API
    // should be move to the API specific class (ModuleReader) 
    private static final String DEGREE_API = "https://sis-tuni.funidata.fi/kori/api/module-search?universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";

    /**
     * Construct the GUIController object
     */    
    public GUIController(){
    }

    //  BUGS: the comboBox's prompt text dissapere sometime when switching between the two tabs.

    /**
     * Take the target comboBox to change its value, the selected curriculumYear and language to put through the readSearchesFromURL method
     * to get all the degrees fit the parameters then put the result to the targeted comboBox (the curriculumYear filter process happen during the API
     * fetching but the API don't have option to filter out language so we have to filter out the language ourselve in the method)
     * @param target the target comboBox
     * @param curriculumYear the academic year
     * @param lang the language
     * @throws IOException when reading the API's URL failed
     */
    public void setFilteredContentToDegreeCB(ComboBox<comboItem> target, String curriculumYear, String lang) throws IOException{
        target.getItems().clear();
        target.setPromptText("Please choose a degree.");
        
        List<ModuleSearch> temp = ModuleReader.readSearchesFromURL(String.format("%s&curriculumPeriodId=uta-lvv-%s", DEGREE_API, curriculumYear));

        for(final ModuleSearch module : temp) {
            if (module.lang.compareTo(lang) == 0) {
                target.getItems().add(new comboItem(module.name, module.id));
            }
        }
    }
    
    /**
     * Called after the user select thier degree programme using the degree id as parameter to find its track (or to find out there is no track)
     * The track of a degree always in the degree.rule.rules (as I observe, could broken down in some other cases) 
     * therefore degree.rule.rules == null  =>  no track
     * otherwise, loop through degree.rule.rules to find the id for the track's module then set its name to the targeted comboBox.
     * @param target the target combobox
     * @param module_id the id of the module
     * @throws IOException when reading the API's URL failed
     */
    public void setTrackCB(ComboBox<comboItem> target, String module_id) throws IOException {
        target.getItems().clear();
        target.setPromptText("Please choose a track.");

        Module degree = ModuleReader.readModule(module_id);

        if (degree.rule.rules == null) {
            target.setPromptText("There is no track to be selected");
            return;
        }

        for (Rule r : degree.rule.rules) {
            Module subModule = ModuleReader.readModule(r.moduleGroupId);
            if (subModule.name.en == null)
            {
                target.getItems().add(new comboItem(subModule.name.fi, subModule.id));
            } else {
                target.getItems().add(new comboItem(subModule.name.en, subModule.id));
            }
        }
    }
}
