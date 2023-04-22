package com.doublel401.java.bff.vo;

import com.doublel401.java.bff.entity.Gender;
import com.doublel401.java.bff.enums.GenderEnum;
import lombok.Data;

@Data
public class GenderVO {
    private Long id;
    private GenderEnum name;
    private String displayName;

    public GenderVO(Gender gender) {
        this.id = gender.getId();
        this.name = gender.getName();
        this.displayName = gender.getDisplayName();
    }
}
