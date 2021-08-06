package data.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import data.BaseDataAccessObject;
import org.bson.Document;
import utils.AlertStatus;
import utils.DbConnectionManager;
import utils.GenericCalendarUtils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class BaseDataAccessObjectImpl implements BaseDataAccessObject {

    public static final String COLLECTION_DAILY_PATIENT_DATA = "dailyPatientData";
    public static final String COLLECTION_WEEKLY_PATIENT_PILL_BOX = "weeklyPatientPillBox";
    public static final String PATIENT_NAME = "patientName";
    public static final String RECORD_DATE = "recordDate";
    public static final String RECORD_WEEK = "recordWeek";
    public static final String AM = "am";
    public static final String PM = "pm";
    public static final String NOTE = "note";
    public static final String DOSE_ALERTS = "alerts";
    public static final String ALERT_SCHEDULED = "scheduled";
    public static final String ALERT_TAKEN = "taken";
    public static final String ALERT_MISSED = "missed";
    public static final String ALERT_OVERDOSE = "overdose";

    public void updateStatus(String patientName, Date recordDate, String amStatus, String pmStatus, String note, AlertStatus alertStatus) {
        MongoDatabase database = DbConnectionManager.getMongoDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //update daily data
        BasicDBObject dailySearchQuery = new BasicDBObject();
        dailySearchQuery.append(PATIENT_NAME, patientName);
        dailySearchQuery.append(RECORD_DATE, dateFormat.format(recordDate));

        BasicDBObject dailyUpdateQuery = new BasicDBObject();
        BasicDBObject dailyUpdateObject = new BasicDBObject();
        if (amStatus != null && !amStatus.isEmpty()) {
            dailyUpdateObject.append(AM, amStatus);
        }
        if (pmStatus != null && !pmStatus.isEmpty()) {
            dailyUpdateObject.append(PM, pmStatus);
        }
        if (note != null && !note.isEmpty()) {
            dailyUpdateObject.append(NOTE, note);
        }

        dailyUpdateQuery.append("$set", dailyUpdateObject);

        database.getCollection(COLLECTION_DAILY_PATIENT_DATA).updateOne(dailySearchQuery, dailyUpdateQuery);

        //updateVirtualPillBox
        BasicDBObject weekSearchQuery = new BasicDBObject();
        weekSearchQuery.append(PATIENT_NAME, patientName);
        weekSearchQuery.append(RECORD_WEEK, GenericCalendarUtils.getWeek(recordDate));

        BasicDBObject weekUpdateQuery = new BasicDBObject();
        BasicDBObject weekUpdateObject = new BasicDBObject();

        if (amStatus != null && !amStatus.isEmpty()) {
            weekUpdateObject.append(DOSE_ALERTS + "." + GenericCalendarUtils.getIndexByWeekDayAmPm(recordDate), alertStatus.value);
        }

        if (pmStatus != null && !pmStatus.isEmpty()) {
            weekUpdateObject.append(DOSE_ALERTS + "." + GenericCalendarUtils.getIndexByWeekDayAmPm(recordDate) + 1, alertStatus.value);
        }
        weekUpdateQuery.append("$set", weekUpdateObject);

        database.getCollection(COLLECTION_WEEKLY_PATIENT_PILL_BOX).updateOne(weekSearchQuery, weekUpdateQuery);
    }

    public void initDailyData(String patientName, Date recordDate, String amStatus, String pmStatus, String note) {
        MongoDatabase database = DbConnectionManager.getMongoDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Document dailyDocument = new Document();
        dailyDocument.append(PATIENT_NAME, patientName);
        dailyDocument.append(RECORD_DATE, dateFormat.format(recordDate));
        dailyDocument.append(AM, amStatus);
        dailyDocument.append(PM, pmStatus);
        dailyDocument.append(NOTE, note);

        database.getCollection(COLLECTION_DAILY_PATIENT_DATA).insertOne(dailyDocument);
    }

    public void initVirtualPillBox(String patientName, Date recordDate) {
        MongoDatabase database = DbConnectionManager.getMongoDatabase();

        Document virtualPillBox = new Document();
        virtualPillBox.append(PATIENT_NAME, patientName);
        virtualPillBox.append(RECORD_WEEK, GenericCalendarUtils.getWeek(recordDate));
        virtualPillBox.append(DOSE_ALERTS, Arrays.asList(
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED,
                ALERT_SCHEDULED, ALERT_SCHEDULED).subList(0, 14));
        database.getCollection(COLLECTION_WEEKLY_PATIENT_PILL_BOX).insertOne(virtualPillBox);
    }
}
