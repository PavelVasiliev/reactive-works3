package ru.innotech.education.rxjava.models;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

public class ZonedDateTimeAdapter
        extends XmlAdapter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime unmarshal(String v) {
        return ZonedDateTime.from(RFC_1123_DATE_TIME.parse(v));
    }

    @Override
    public String marshal(ZonedDateTime v) {
        return RFC_1123_DATE_TIME.format(v);
    }
}
