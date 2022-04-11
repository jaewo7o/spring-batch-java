package com.jaewoo.batch.app.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Dept {
    @Id
    Integer deptId;
    String deptName;
    String deptLocation;
}
