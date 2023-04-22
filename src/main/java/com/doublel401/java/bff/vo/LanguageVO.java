package com.doublel401.java.bff.vo;

import com.doublel401.java.bff.entity.Language;
import lombok.Data;

@Data
public class LanguageVO {
    private Long id;
    private String code;
    private String name;
    private Integer displayOrder;

    public LanguageVO(Language language) {
        this.id = language.getId();
        this.code = language.getCode();
        this.name = language.getName();
        this.displayOrder = language.getDisplayOrder();
    }
}
