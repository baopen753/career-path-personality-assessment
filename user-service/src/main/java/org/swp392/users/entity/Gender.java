package org.swp392.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE, FEMALE, OTHER;

    @JsonCreator
    public static Gender fromString(String key) {
        return key == null ? null : Gender.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

}
