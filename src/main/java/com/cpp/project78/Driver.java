package com.cpp.project78;

import com.cpp.project78.main.Main;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException {
        Main main = new Main("src/main/resources/StackTest.vm");
        main.compile();
    }
}
