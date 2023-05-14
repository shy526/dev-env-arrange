package com.github.shy526.devenvarrange.download;

import com.github.shy526.date.DateFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * @author shy526
 */
@Slf4j
public class DateConverter {
    private final static String US_DATE_FORMAT = "dd-MMM-yyyy";

    public static ConverterResult to(String dateStr) {
        String[] split = dateStr.split("-");
        if (split[1].length() == 3 && split[2].length() == 4) {
            try {
                Date date = new SimpleDateFormat("dd-MMM-yyyy", Locale.US).parse(dateStr);
                return new ConverterResult(DateFormat.dateFormat(date), date);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            return new ConverterResult(dateStr, DateFormat.dateParse(dateStr));
        }
        return null;
    }

    @Data
    public static class ConverterResult {

        public ConverterResult(String dateStr, Date date) {
            this.dateStr = dateStr;
            this.date = date;
        }

        private String dateStr;
        private Date date;
    }
}
