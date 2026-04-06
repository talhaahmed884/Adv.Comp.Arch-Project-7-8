package com.cpp.project78.parser;

public enum Command {
    add("add"),
    sub("sub"),
    neg("neg"),
    eq("eq"),
    gt("gt"),
    lt("lt"),
    and("and"),
    or("or"),
    not("not"),
    push("push"),
    pop("pop");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
