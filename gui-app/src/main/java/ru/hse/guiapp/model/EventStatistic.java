package ru.hse.guiapp.model;

public class EventStatistic {

    private String name;
    private Integer frequency;
    private String description;


    public EventStatistic(String name, Integer frequency, String description) {
        this.name = name;
        this.frequency = frequency;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public String getDescription() {
        return description;
    }
}
