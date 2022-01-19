package ru.innotech.education.rxjava.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("subscription")
@NoArgsConstructor
public class SubscriptionEntity {
    @Id
    private Integer id;
    @Column
    private String name;
    @Column
    private String link;
    @Column
    private String title;
    @Column
    private String description;


    //FixMe test constructor to find necessary data..
    public SubscriptionEntity(String name, String link) {
        this.link = link;
        this.name = name;
        title = "title";
    }
}
