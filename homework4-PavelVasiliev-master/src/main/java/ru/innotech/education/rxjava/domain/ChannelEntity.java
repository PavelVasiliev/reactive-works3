package ru.innotech.education.rxjava.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Accessors(chain = true)
@Table("channel")
@NoArgsConstructor
public class ChannelEntity {
    @Id
    private Integer id;
    @Column
    private String title;
    @Column
    private String link;
    @Column
    private String description;
    @Column
    private List<String> items;
}
