package fi.partio.pajautin.randomizer;

public class Workshop implements Comparable<Workshop> {
    public boolean isSpeech;
    public int id;
    public String name;
    public int maxParticipants;
    public int minParticipants;
    public double desirability;

    public boolean[] availableSlots;

    public double personalPreference;

    public int category;

    public int popularity;

    public Workshop( int id, String name, int minParticipants,int maxParticipants, double desirability, boolean isSpeech, boolean[] availableSlots,int category) {
        this.isSpeech = isSpeech;
        this.id = id;
        this.name = name;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.desirability=desirability;
        this.availableSlots=availableSlots;
        this.category=category;
    }

    public boolean isSpeech() {
        return isSpeech;
    }

    public void setSpeech(boolean speech) {
        isSpeech = speech;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(int minParticipants) {
        this.minParticipants = minParticipants;
    }

    public double getDesirability() {
        return desirability;
    }

    public void setDesirability(double desirability) {
        this.desirability = desirability;
    }

    public boolean[] getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(boolean[] availableSlots) {
        this.availableSlots = availableSlots;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getPersonalPreference() {
        return personalPreference;
    }

    public void setPersonalPreference(double personalPreference) {
        this.personalPreference = personalPreference;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void addPopularity(int i) {
        popularity+=i;
    }

    @Override
    public int compareTo(Workshop o) {
        return -(Double.valueOf(desirability+personalPreference)).compareTo(Double.valueOf(o.getDesirability()+o.getPersonalPreference()));
    }
}
