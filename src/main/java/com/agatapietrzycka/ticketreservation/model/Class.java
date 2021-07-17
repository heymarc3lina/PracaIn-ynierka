package com.agatapietrzycka.ticketreservation.model;

import com.agatapietrzycka.ticketreservation.model.enums.ClassType;
import com.agatapietrzycka.ticketreservation.util.ClassTypeEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    @Id
    @SequenceGenerator(
            name = "class_seq",
            sequenceName = "class_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_seq")
    private Long classId;
    @NotNull
    private Double calculatePrice;
    @Convert(converter = ClassTypeEnumConverter.class)
    private ClassType classType;

}
