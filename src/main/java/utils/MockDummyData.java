package utils;

import data.BaseDataAccessObject;
import data.impl.BaseDataAccessObjectImpl;

import java.sql.Date;
import java.util.Calendar;

public class MockDummyData {

    public static void main(String[] args) {
        insertMockedData("Jack");
//        insertMockedData("Andy");
//        insertMockedData("Rock");
//        baseDao.initDailyData("Ram", date, "Scheduled at 08:30 AM", "Scheduled at 09:00 PM", "Doctor's advice. Take twice daily 8 hours apart.");
//        baseDao.initVirtualPillBox("Ram", date);
//        baseDao.updateStatus("Jack", date, "Scheduled at  AM", "Scheduled at  PM", "Doctor's advice. Take twice daily 8 hours apart.", AlertStatus.MISSED);
    }

    private static void insertMockedData(String patientName) {
        BaseDataAccessObject baseDao = new BaseDataAccessObjectImpl();

        Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 1; i < 31; i++) {
            baseDao.initDailyData(patientName, new Date(cal.getTime().getTime()), "Scheduled at 08:30 AM", "Scheduled at 09:00 PM", "Doctor's advice. Take twice daily 8 hours apart.");
            baseDao.initVirtualPillBox(patientName, new Date(cal.getTime().getTime()));
            cal.add(Calendar.DATE, 1);
        }
    }
}
