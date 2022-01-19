package ru.innotech.education.rxjava.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Accessors(chain = true)
@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class RSS {
    private Channel channel;
}
