package data;

import org.bson.Document;
import utils.AlertStatus;

import java.sql.Date;

public interface BaseDataAccessObject {

    /**
     * update patient data for a particular day as well as update virtual pill box statuses.
     * @param patientName
     * @param recordDate
     * @param amStatus
     * @param pmStatus
     * @param note
     * @param alertStatus
     */
    void updateStatus(String patientName, Date recordDate, String amStatus, String pmStatus, String note, AlertStatus alertStatus);

    /**
     * To initialize one patient data for a day. Usage is at start of each week for initializing all seven week days data.
     * @param patientName
     * @param recordDate
     * @param amStatus
     * @param pmStatus
     * @param note
     */
    void initDailyData(String patientName, Date recordDate, String amStatus, String pmStatus, String note);

    /**
     * To initialize virtual pill box at start of each week. All alert statuses will be set to SCHEDULED.
     * @param patientName
     * @param recordDate
     */
    void initVirtualPillBox(String patientName, Date recordDate);
}
