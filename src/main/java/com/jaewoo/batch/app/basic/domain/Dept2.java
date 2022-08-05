package com.jaewoo.batch.app.basic.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Dept2 {
    @Id
    Integer deptId;
    String deptName;
    String deptLocation;
}
