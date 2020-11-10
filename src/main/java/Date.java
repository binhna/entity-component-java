import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class Date {
    public int day;
    public int month;
    public int year;
    public int weekday;

    public Date(LocalDate date) {
        day = date.getDayOfMonth();
        month = date.getMonthValue();
        year = date.getYear();
        weekday = date.getDayOfWeek().getValue();
    }
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        Calendar c = Calendar.getInstance();
        c.set(day, month-1, year, 0, 0);
        this.weekday = c.get(Calendar.DAY_OF_WEEK);
    }

    public static Date now(String zoneId) {
        return new Date(LocalDate.now(ZoneId.of(zoneId)));
    }

    public Date() { }

    public Date setValue(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        return this;
    }
}
