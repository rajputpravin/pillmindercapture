package utils;

public enum AlertStatus {
    SCHEDULED("scheduled"),
    TAKEN("taken"),
    MISSED("missed"),
    OVERDOSE("overdose");

    public final String value;

    AlertStatus(String value) {
        this.value = value;
    }
}
