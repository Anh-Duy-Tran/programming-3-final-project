package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 *  Class for all the API realated method
 */

public class ModuleReader {

    private static final String MODULE_API = "https://sis-tuni.funidata.fi/kori/api/modules/%s";
    private static final String COURSE_API = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=%s&universityId=tuni-university-root-id";

    //////////////////////////////
    // SUB CLASS FOR SEARCH API //
    //////////////////////////////
    
    // reference: the module-search API
    // https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000

    /**
     * A very similar subClass to Module, needed because the way the API store "name" is different when fetching
     * use solely for the readSearchesFromAPI to read the degree.
     */
    public static class ModuleSearch {
        public String id;
        public String code;
        public String lang;
        public String groupId;
        public String name;
        public List<String> curriculumPeriodIds;
        public Credit credits;
        public Rule rule;
    }

    /**
     * Main class for the module-search API
     */
    public static class Page {
        public Integer start;
        public Integer limit;
        public Integer total;
        public List<ModuleSearch> searchResults;
    }

    /////////////////////
    // EVERYTHING ELSE //
    /////////////////////

    /**
     * The class to represent literally everything
     */
    public static class Module {
        public String id;
        public String code;
        public String groupId;
        public Name name;
        public List<String> curriculumPeriodIds;
        public Credit credits;
        public Rule rule = null; 
        public Credit targetCredits;
        public String gradeScaleId;
        public EnFi outcomes;
        public EnFi prerequisites;
        public EnFi content;

        public boolean isCourse = false;
        public boolean isChoosen = false;
        public Integer cre = 0;
    }

    //  BUT always contains its subModule except when the Module is a courseUnitModule
    // Still a mistery

    /**
     * A subclass for Module, store some additional/optional (optional in term of some Module have it some don't) information for the Module 
     * .i.e Module's requirement, ..
     */
    public static class Rule {
        public String type;
        public String localId;
        public Rule rule;
        public List<Rule> rules;
        
        public String moduleGroupId;
        public String courseUnitGroupId;
        
        public Credit credits;
    }
    
    /**
     * Helper class Credit
     */
    public static class Credit {
        public Integer min;
        public Integer max;
    }

    /**
     * Helper class Name
     */
    public static class Name {
        public String en;
        public String fi;
        public String sv;
    }

    /**
     * Helper class EnFi
     */
    public static class EnFi {
        public String en;
        public String fi;
    }

    ////////////////////////////////////////
    // Comprehensive Tree of Module class //
    ////////////////////////////////////////

    /**
     * The class that represents a Module and its children Modules
     */
    public static class TreeModule {
        public Module value;
        public List<TreeModule> subModule = null;
        
        /**
         * Construct a new TreeModule, that contains the module and its children modules
         * @param value the current module
         * @param subModule the children modules
         */
        public TreeModule(Module value, List<TreeModule> subModule) {
            this.value = value;
            this.subModule = subModule;
        }
        
        /**
         * Print TreeModule as strings
         */
        @Override
        public String toString() {
            String name;
            if (this.value.name.en == null)
                name = this.value.name.fi;
            else
                name = this.value.name.en;
            
            if (this.value.isCourse) {
                return String.format("%s %dop", name, this.value.credits.min);
            }

            if (this.value.credits == null) {
                if (this.value.targetCredits == null) {
                    return name;
                }
                return String.format("%s %dop / %dop", name, this.value.cre, this.value.targetCredits.min);
            }
            return String.format("%s %dop / %dop", name, this.value.cre, this.value.credits.min);

        }

        /**
         * Get the name of the TreeModule
         * @return name of the TreeModule
         */
        public String getName() {
            String name;
            if (this.value.name.en == null)
                name = this.value.name.fi;
            else
                name = this.value.name.en;
            return name;
        }
        
        /**
         * Get the group ID of the TreeModule
         * @return group ID of the TreeModule
         */
        public String getGroupId() {
            return this.value.groupId;
        }
        
        /**
         * Get the course code of the TreeModule
         * @return course code of the TreeModule
         */
        public String getCode() {
            return this.value.code;
        }
        
        /**
         * Get the credits of the TreeModule
         * @return credits of the TreeModule
         */
        public String getCredits() {
            if (this.value.credits.min == this.value.credits.max) {
                return String.format("%dop", this.value.credits.min);
            }
            return String.format("Min: %dop - Max: %dop", this.value.credits.min, this.value.credits.max);
        }
        
        /**
         * Get the grade scale of the TreeModule
         * @return grade scale of the TreeModule
         */
        public String getGradeScale() {
            if (this.value.gradeScaleId.equals("sis-0-5")) {
                return "0-5";
            } else {
                return "Pass-Fail";
            }
        }
        
        /**
         * Get the outcomes of the TreeModule
         * @return outcomes of the TreeModule
         */
        public String getOutcomes() {
            if (this.value.outcomes == null) {
                return "";
            }

            String outcomes;
            if (this.value.outcomes.en == null)
                outcomes = this.value.outcomes.fi;
            else
                outcomes = this.value.outcomes.en;
            return outcomes;
        }

        /**
         * Get the prerequisites of the TreeModule
         * @return prerequisites of the TreeModule
         */
        public String getPrerequisites() {
            if (this.value.prerequisites == null) {
                return "";
            }

            String prerequisites;
            if (this.value.prerequisites.en == null)
                prerequisites = this.value.prerequisites.fi;
            else
                prerequisites = this.value.prerequisites.en;
            return prerequisites;
        }

        /**
         * Get the content of the TreeModule
         * @return content of the TreeModule
         */
        public String getContent() {
            if (this.value.content == null) {
                return "";
            }

            String content;
            if (this.value.content.en == null)
                content = this.value.content.fi;
            else
                content = this.value.content.en;
            return content;
        }
    }

    /**
     * Construct a new ModuleReader
     */
    public ModuleReader() {}

    /**
     * Function needed for API reading
     * @param urlString URL address of the API
     * @return the API response
     * @throws IOException when reading the URL failed
     */
    private static String readUrl(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 
    
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Read all the Module from the module-search API
     * Read to ModuleSearch class cause it store "name" atribute differently
     * @param URL URL address of the API
     * @return all the found degree Module
     * @throws IOException when reading the URL failed
     */
    public static List<ModuleSearch> readSearchesFromURL(String URL) throws IOException {

        String json = readUrl(URL);
        Gson gson = new Gson();        
        Page page = gson.fromJson(json, Page.class);

        return page.searchResults;

    }

    /**
     * Return the Module from the given ID
     * @param module_id ID of the module
     * @return the found module
     * @throws IOException when reading the API's URL failed
     */
    public static Module readModule(String module_id) throws IOException {
        String combinedURL = String.format(MODULE_API, module_id);
        String json = readUrl(combinedURL);
        Gson gson = new Gson(); 
        
        return gson.fromJson(json, Module.class);
    }
    
    /**
     * Similar to readModule. This method exists because looking for courses
     * requires a different API address
     * @param course_id the course ID
     * @return the course Module
     * @throws IOException when reading the API's URL failed
     */
    public static Module readCourseUnit(String course_id) throws IOException {
        String combinedURL = String.format(COURSE_API, course_id);
        System.out.println(combinedURL);
        String json = readUrl(combinedURL);
        Gson gson = new Gson(); 

        Module course = gson.fromJson(json, Module[].class)[0];
        course.isCourse = true;

        return course;
    }

    /**
     * Read the course units
     * This exists because course nodes do not have subNodes
     * @param root_id the ID of the node
     * @return the Module
     * @throws IOException when reading the API's URL failed
     */
    public static TreeModule readTreeCourseUnit(String root_id) throws IOException {
        return new TreeModule(readCourseUnit(root_id), null);
    }

    // BUT not always, there are some "CompositeRule" that contains another "CompositeRule" :) , thus, the sketchy if statement. 
    // (the "CompositeRule" that contains the subModules is the one that have its "rules" to contains rule of type either: CourseUnitRule, ModuleRule, 
    //                                                                                                                     AnyCourseUnitRule, AnyModuleRule) 
    /**
     * Read all the Module from the given Module's root_id recursively and return the root of the Tree of Module.
     * The method mostly interest to Rule that have the type "CompositeRule" because it contains the Module's submodule.
     * @param root_id The ID of the root
     * @return the root of the TreeModule
     * @throws IOException when reading the API's URL failed
     */
    public static TreeModule readTreeModule(String root_id) throws IOException {
        Module root = readModule(root_id); 
        List<TreeModule> subRoot = new ArrayList<>();

        Rule rule = root.rule;
        List<Rule> rules = null;

        if (rule == null) {
            return new TreeModule(root, null);
        }

        // find the correct rule class within the Module (the correct rule class is which contains the subModules)
        while (true) {

            if (rule.type.compareTo("CreditsRule") == 0) {
                root.credits = rule.credits;
                rule = rule.rule;
                continue;
            }

            if (rule.type.compareTo("CompositeRule") == 0) {
                if (rule.rules.get(0).type.compareTo("CourseUnitRule") == 0 || rule.rules.get(0).type.compareTo("ModuleRule") == 0 
                    || rule.rules.get(0).type.compareTo("AnyCourseUnitRule") == 0 || rule.rules.get(0).type.compareTo("AnyModuleRule") == 0) {
                    rules = rule.rules;
                    break;
                }
                rule = rule.rules.get(0);
            }
        }

        // loop through the rule class to find the subModules.
        for (Rule r : rules) {
            if (r.type.compareTo("CourseUnitRule") == 0)
            {
                TreeModule temp = readTreeCourseUnit(r.courseUnitGroupId);
                subRoot.add(temp);
            }
            if (r.type.compareTo("ModuleRule") == 0) {
                // 
                //  BUG / ERROR: can't find a way to read Module with the moduleGroupId in different form than "otm-6ab4ce4a-4eb7-4c76-8ed7-9ab3a2faa5b6" ... 
                //  UPDATE LATER!
                //
                if (r.moduleGroupId.length() != 40) {
                    continue;
                }
                TreeModule temp = readTreeModule(r.moduleGroupId);
                subRoot.add(temp);
            }
        }

        return new TreeModule(root, subRoot);
    }

}
