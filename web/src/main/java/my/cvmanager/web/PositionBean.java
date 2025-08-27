package my.cvmanager.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import my.cvmanager.domain.Position;
import my.cvmanager.service.PositionService;
import my.cvmanager.util.CSVReader;
import org.primefaces.model.file.UploadedFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Backing bean for managing {@link Position} entities in the UI.
 * <p>
 * This bean provides fields for creating and editing positions as well as
 * methods to save, update, and delete positions. It is view-scoped, meaning
 * that the bean will live as long as the user stays on the same JSF view.
 * </p>
 */
@Named("positionBean")
@SessionScoped
public class PositionBean implements Serializable {
    private final Logger logger = Logger.getLogger(PositionBean.class.getCanonicalName());

    private String title;
    private String company;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    // updload CSV
    private UploadedFile csvFile;

    private Position selectedPosition;

    @Inject
    private PositionService positionService;

    /**
     * Saves a new position or updates an existing one.
     *
     * @return null (stays on the same page after AJAX update)
     */
    public String save() {
        if (selectedPosition == null) {
            Position pos = new Position();
            pos.setTitle(title);
            pos.setCompany(company);
            pos.setLocation(location);
            pos.setStartDate(startDate);
            pos.setEndDate(endDate);
            pos.setDescription(description);
            positionService.save(pos);
        } else {
            selectedPosition.setTitle(title);
            selectedPosition.setCompany(company);
            selectedPosition.setLocation(location);
            selectedPosition.setStartDate(startDate);
            selectedPosition.setEndDate(endDate);
            selectedPosition.setDescription(description);
            positionService.update(selectedPosition);
        }
        clearForm();

        return null;
    }

    public void csvImport() {
        logger.info("csvFile: " + csvFile.getFileName());
        //TODO: read csv and add postions
        CSVReader<Position> csvReader = new CSVReader<>();
        List<Position> positions = null;
        try {
            positions = csvReader.readCSV(csvFile.getInputStream(), Position.class);
            for (Position pos : positions) {
                if (pos != null) {
                    logger.info("position: " + pos.getTitle());
                }
            }
        } catch (IOException e) {
            logger.info("cannot import csv file: " + e.getMessage());
        }
    }

    /**
     * Deletes the given position.
     *
     * @param pos the position to delete
     * @return null (stays on the same page after AJAX update)
     */
    public String delete(Position pos) {
        positionService.delete(pos.getId());

        return null;
    }

    /**
     * Loads the selected position into the form for editing.
     *
     * @param pos the position to edit
     * @return null (stays on the same page after AJAX update)
     */
    public String edit(Position pos) {
        this.selectedPosition = pos;
        this.title = pos.getTitle();
        this.company = pos.getCompany();
        this.location = pos.getLocation();
        this.startDate = pos.getStartDate();
        this.endDate = pos.getEndDate();
        this.description = pos.getDescription();

        return null;
    }

    /**
     * Clears the input form fields.
     */
    private void clearForm() {
        selectedPosition = null;
        title = null;
        company = null;
        location = null;
        startDate = null;
        endDate = null;
        description = null;
    }

    /**
     * Returns a list of all positions.
     *
     * @return list of positions
     */
    public List<Position> getPositions() {
        return positionService.findAll();
    }

    // ---------------- Getters & Setters ---------------- //

    public UploadedFile getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(UploadedFile csvFile) {
        this.csvFile = csvFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(Position selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
