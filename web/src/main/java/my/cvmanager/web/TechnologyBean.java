package my.cvmanager.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import my.cvmanager.domain.Technology;
import my.cvmanager.service.TechnologyService;

import java.io.Serializable;
import java.util.List;

/**
 * Managed bean for handling technology-related operations.
 * <p>
 * This class provides methods for saving, deleting, and retrieving technologies.
 *
 * @author marco.braun
 */
@Named("technologyBean")
@SessionScoped
public class TechnologyBean implements Serializable {

    /**
     * The name of the technology.
     */
    private String name;

    /**
     * The selected technology.
     */
    private Technology selected;

    /**
     * The technology service instance.
     */
    @Inject
    private TechnologyService technologyService;

    /**
     * Saves a new technology or updates an existing one.
     * <p>
     * If no technology is selected, a new one is created and saved.
     * Otherwise, the selected technology is updated.
     */
    public void save() {
        if (selected == null) {
            Technology tech = new Technology();
            tech.setName(name);
            technologyService.save(tech);
        } else {
            selected.setName(name);
            technologyService.update(selected);
        }
        clearForm();
    }

    /**
     * Deletes a technology.
     *
     * @param tech the technology to delete
     */
    public void delete(Technology tech) {
        technologyService.delete(tech.getId());
    }

    /**
     * Retrieves all technologies.
     *
     * @return a list of all technologies
     */
    public List<Technology> getTechnologies() {
        return technologyService.findAll();
    }

    /**
     * Clears the form fields.
     */
    private void clearForm() {
        name = null;
        selected = null;
    }

    // Getter & Setter

    /**
     * Gets the name of the technology.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the technology.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the selected technology.
     *
     * @return the selected technology
     */
    public Technology getSelected() {
        return selected;
    }

    /**
     * Sets the selected technology.
     *
     * @param selected the selected technology to set
     */
    public void setSelected(Technology selected) {
        this.selected = selected;
    }
}