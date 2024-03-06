package ru.hse.config.entity;

public class EventDescription {

    public EventDescription(String name, String category, String description, String isIncluded) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.isIncluded = castStringToBoolean(isIncluded);
    }

    private Boolean castStringToBoolean(String isIncluded) {
        return isIncluded.equals("TRUE");
    }

    private String name;

    private String category;

    private String description;

    private Boolean isIncluded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIncluded() {
        return isIncluded;
    }

    public void setIncluded(Boolean included) {
        isIncluded = included;
    }
}
