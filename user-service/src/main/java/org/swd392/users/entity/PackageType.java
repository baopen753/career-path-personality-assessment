package org.swd392.users.entity;

import lombok.Getter;

@Getter
public enum PackageType {
    STANDARD(1),
    PREMIUM(2);

    private final int code;

    PackageType(int code) {
        this.code = code;
    }

}

