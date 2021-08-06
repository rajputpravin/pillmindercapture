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
        Date firstDayOfMonth = new Date(cal.getTime().getTime());
        //Day Iterator starting first day of month
        Calendar calIteratorForDays = (Calendar) cal.clone();
        //Week Iterator starting first day of month
        Calendar calIteratorForWeeks = (Calendar) cal.clone();

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date lastDayOfMonthPlusOne = new Date(cal.getTime().getTime());

        while (new Date(calIteratorForDays.getTime().getTime()).before(lastDayOfMonthPlusOne)) {
            baseDao.initDailyData(patientName, new Date(calIteratorForDays.getTime().getTime()), "Scheduled at 08:30 AM", "Scheduled at 09:00 PM", "Doctor's advice. Take twice daily 8 hours apart.");
            calIteratorForDays.add(Calendar.DATE, 1);
        }

        while (new Date(calIteratorForWeeks.getTime().getTime()).before(lastDayOfMonthPlusOne)) {
            baseDao.initVirtualPillBox(patientName, new Date(calIteratorForWeeks.getTime().getTime()));
            calIteratorForWeeks.add(Calendar.DATE, 7);
        }
    }
}
