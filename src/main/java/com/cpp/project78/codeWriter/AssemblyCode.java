package com.cpp.project78.codeWriter;

public enum AssemblyCode {
    A_EQ_A_SUB_1("A=A-1"),
    A_EQ_D_ADD_A("A=D+A"),
    A_EQ_D_SUB_A("A=D-A"),
    A_EQ_M("A=M"),
    A_EQ_M_SUB_1("A=M-1"),

    CONST_5("@5"),
    CONST_256("@256"),

    D_EQ_A("D=A"),
    D_EQ_D_ADD_A("D=D+A"),
    D_EQ_D_SUB_A("D=D-A"),
    D_EQ_M("D=M"),
    D_EQ_M_ADD_1("D=M+1"),
    D_EQ_M_SUB_D("D=M-D"),

    EQ("JEQ"),

    GT("JGT"),

    JMP_IF_EQ_0("0;JMP"),
    JNE_IF_NE_D("D;JNE"),

    LT("JLT"),

    M_EQ_0("M=0"),
    M_EQ_D("M=D"),
    M_EQ_D_ADD_M("M=D+M"),
    M_EQ_D_AND_M("M=D&M"),
    M_EQ_D_OR_M("M=D|M"),
    M_EQ_M_SUB_D("M=M-D"),
    M_EQ_M_ADD_1("M=M+1"),
    M_EQ_M_SUB_1("M=M-1"),
    M_EQ_NEG_M("M=-M"),
    M_EQ_NOT_M("M=!M"),
    M_EQ_NEG_1("M=-1"),

    R13("@R13"),
    R14("@R14"),

    SP("@SP"),

    THAT("@THAT"),
    THIS("@THIS");

    private final String value;

    AssemblyCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value + "\n";
    }
}
