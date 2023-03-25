package fi.partio.pajautin.randomizer;

import java.util.List;

public class Participant {
    String id;
    String firstName;
    String lastName;
    List<Integer> workshopPreference;
    List<Integer> categoryPreference;
    boolean[] availability;

    public Participant(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Integer> getWorkshopPreference() {
        return workshopPreference;
    }

    public void setWorkshopPreference(List<Integer> workshopPreference) {
        this.workshopPreference = workshopPreference;
    }

    public List<Integer> getCategoryPreference() {
        return categoryPreference;
    }

    public void setCategoryPreference(List<Integer> categoryPreference) {
        this.categoryPreference = categoryPreference;
    }

    public boolean[] getAvailability() {
        return availability;
    }

    public void setAvailability(boolean[] availability) {
        this.availability = availability;
    }
}
