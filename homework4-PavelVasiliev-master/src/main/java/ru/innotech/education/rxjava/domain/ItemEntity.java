package ru.innotech.education.rxjava.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Accessors(chain = true)
@Table("items")
public class ItemEntity {
    @Id
    private Integer id;
    private String title;
    private String link;
    private String description;
    private ZonedDateTime publication;
    @Column("subscription_id")
    private Integer subscriptionId;
}
