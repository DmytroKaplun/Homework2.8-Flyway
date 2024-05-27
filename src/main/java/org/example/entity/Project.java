package org.example.entity;

import lombok.Data;
import org.example.annotation.Column;
import org.example.annotation.Id;
import org.example.annotation.Table;

import java.time.LocalDateTime;

@Data
@Table(value = "project")
public class Project {
    @Id
    private long id;
    @Column(value = "client_id")
    private long clientId;
    @Column(value = "start_date")
    private LocalDateTime startDate;
    @Column(value = "finish_date")
    private LocalDateTime finishDate;
}
