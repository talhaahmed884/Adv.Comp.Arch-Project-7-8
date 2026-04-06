package com.cpp.project78.codeWriter;

public enum Segment {
    local("local", "LCL"),
    argument("argument", "ARG"),
    _this("this", "THIS"),
    that("that", "THAT"),
    constant("constant", ""),
    temp("temp", ""),
    pointer("pointer", ""),
    _static("static", "");

    private final String code;
    private final String value;

    Segment(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public static Segment fromValue(String value) {
        for (Segment segment : values()) {
            if (segment.value.equals(value)) {
                return segment;
            }
        }
        throw new IllegalArgumentException("invalid segment value: " + value);
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
