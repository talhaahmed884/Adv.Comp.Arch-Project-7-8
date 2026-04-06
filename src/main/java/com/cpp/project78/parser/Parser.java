package com.cpp.project78.parser;

public interface Parser {
    boolean hasMoreCommands();

    void advance();

    String commandType();

    String arg1();

    int arg2();
}
