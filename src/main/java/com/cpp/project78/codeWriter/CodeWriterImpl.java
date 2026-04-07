package com.cpp.project78.codeWriter;

import com.cpp.project78.parser.Command;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriterImpl implements CodeWriter {
    private final BufferedWriter writer;
    private String currentFileName;
    private int comparisonLabelCounter;
    private String currentHackFunctionName;
    private int currentHackFunctionCallCounter;

    public CodeWriterImpl(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
        currentFileName = "";
        comparisonLabelCounter = 0;
        currentHackFunctionName = "";
        currentHackFunctionCallCounter = 0;
    }

    @Override
    public void setFileName(String fileName) {
        this.currentFileName = fileName;
    }

    @Override
    public void writeArithmetic(String command) throws IOException {
        writer.write(AssemblyCode.SP.toString());

        Command cmd = Command.fromValue(command.toLowerCase());
        switch (cmd) {
            case Command.add, Command.sub, Command.and, Command.or, Command.eq, Command.gt, Command.lt -> {
                writer.write(AssemblyCode.M_EQ_M_SUB_1.toString());
                writer.write(AssemblyCode.A_EQ_M.toString());
                writer.write(AssemblyCode.D_EQ_M.toString());
                writer.write(AssemblyCode.A_EQ_A_SUB_1.toString());
            }
            case Command.neg, Command.not -> writer.write(AssemblyCode.A_EQ_M_SUB_1.toString());
        }

        switch (cmd) {
            case Command.add -> writer.write(AssemblyCode.M_EQ_D_ADD_M.toString());
            case Command.sub -> writer.write(AssemblyCode.M_EQ_M_SUB_D.toString());
            case Command.and -> writer.write(AssemblyCode.M_EQ_D_AND_M.toString());
            case Command.or -> writer.write(AssemblyCode.M_EQ_D_OR_M.toString());
            case Command.neg -> writer.write(AssemblyCode.M_EQ_NEG_M.toString());
            case Command.not -> writer.write(AssemblyCode.M_EQ_NOT_M.toString());
            case Command.eq -> this.writeComparisonOperators(AssemblyCode.EQ.toString());
            case Command.gt -> this.writeComparisonOperators(AssemblyCode.GT.toString());
            case Command.lt -> this.writeComparisonOperators(AssemblyCode.LT.toString());
            default -> throw new IllegalStateException("Unexpected value: " + cmd);
        }
    }

    @Override
    public void writePushPop(String command, String segment, int index) throws IOException {
        Command cmd = Command.fromValue(command.toLowerCase());
        Segment seg = Segment.fromValue(segment.toLowerCase());

        switch (cmd) {
            case Command.push -> this.writePush(seg, index);
            case Command.pop -> this.writePop(seg, index);
            default -> throw new IllegalStateException("Unexpected value: " + cmd);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            writer.close();
        } catch (Exception e) {
            throw new IOException("Unable to complete writing to file", e);
        }
    }

    @Override
    public void writeInit() throws IOException {
        writer.write(AssemblyCode.CONST_256.toString());
        writer.write(AssemblyCode.D_EQ_A.toString());
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        this.writeCall("Sys.init", 0);
    }

    @Override
    public void writeLabel(String label) throws IOException {
        String labelName = "(" + this.currentHackFunctionName + "$" + label + ")\n";
        writer.write(labelName);
    }

    @Override
    public void writeGoto(String label) throws IOException {
        String labelName = "@" + this.currentHackFunctionName + "$" + label + "\n";
        writer.write(labelName);
        writer.write(AssemblyCode.JMP_IF_EQ_0.toString());
    }

    @Override
    public void writeIf(String label) throws IOException {
        String labelName = "@" + this.currentHackFunctionName + "$" + label + "\n";
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.M_EQ_M_SUB_1.toString());
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write(labelName);
        writer.write(AssemblyCode.JNE_IF_NE_D.toString());
    }

    @Override
    public void writeCall(String functionName, int numArgs) throws IOException {
        String label = functionName + "$ret." + this.currentHackFunctionCallCounter;
        writer.write("@" + label + "\n");
        this.currentHackFunctionCallCounter++;
        writer.write(AssemblyCode.D_EQ_A.toString());
        this.writeSegmentPushToDReg();

        Segment[] seg = new Segment[]{Segment.local, Segment.argument, Segment._this, Segment.that};
        for (Segment s : seg) {
            writer.write("@" + s.getCode() + "\n");
            writer.write(AssemblyCode.D_EQ_M.toString());
            this.writeSegmentPushToDReg();
        }

        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write("@" + (5 + numArgs) + "\n");
        writer.write(AssemblyCode.D_EQ_D_SUB_A.toString());
        writer.write("@" + Segment.argument.getCode() + "\n");
        writer.write(AssemblyCode.M_EQ_D.toString());
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write("@" + Segment.local.getCode() + "\n");
        writer.write(AssemblyCode.M_EQ_D.toString());
        writer.write("@" + functionName + "\n");
        writer.write(AssemblyCode.JMP_IF_EQ_0.toString());
        writer.write("(" + label + ")\n");
    }

    @Override
    public void writeReturn() throws IOException {
        writer.write("@" + Segment.local.getCode() + "\n");
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write(AssemblyCode.R13.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        writer.write(AssemblyCode.CONST_5.toString());
        writer.write(AssemblyCode.A_EQ_D_SUB_A.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write(AssemblyCode.R14.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        this.writeSegmentPopToDReg();
        writer.write("@" + Segment.argument.getCode() + "\n");
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        writer.write("@" + Segment.argument.getCode() + "\n");
        writer.write(AssemblyCode.D_EQ_M_ADD_1.toString());
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());

        Segment[] seg = new Segment[]{Segment.that, Segment._this, Segment.argument, Segment.local};
        for (int a = 0; a < seg.length; a++) {
            writer.write(AssemblyCode.R13.toString());
            writer.write(AssemblyCode.D_EQ_M.toString());
            writer.write("@" + (a + 1) + "\n");
            writer.write(AssemblyCode.A_EQ_D_SUB_A.toString());
            writer.write(AssemblyCode.D_EQ_M.toString());
            writer.write("@" + seg[a].getCode() + "\n");
            writer.write(AssemblyCode.M_EQ_D.toString());
        }

        writer.write(AssemblyCode.R14.toString());
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.JMP_IF_EQ_0.toString());
    }

    @Override
    public void writeFunction(String functionName, int numLocals) throws IOException {
        this.currentHackFunctionName = functionName;
        String labelName = "(" + functionName + ")\n";
        writer.write(labelName);

        for (int i = 0; i < numLocals; i++) {
            this.writePush(Segment.constant, 0);
        }
    }

    private void writeComparisonOperators(String command) throws IOException {
        String comparisonLabelStart = "COMPARISON_LABEL_START_" + comparisonLabelCounter;
        String comparisonLabelEnd = "COMPARISON_LABEL_END_" + comparisonLabelCounter;
        comparisonLabelCounter++;

        writer.write(AssemblyCode.D_EQ_M_SUB_D.toString());

        writer.write("@" + comparisonLabelStart + "\n");
        writer.write("D;" + command);

        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.A_EQ_M_SUB_1.toString());
        writer.write(AssemblyCode.M_EQ_0.toString());
        writer.write("@" + comparisonLabelEnd + "\n");
        writer.write(AssemblyCode.JMP_IF_EQ_0.toString());

        writer.write("(" + comparisonLabelStart + ")\n");
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.A_EQ_M_SUB_1.toString());
        writer.write(AssemblyCode.M_EQ_NEG_1.toString());

        writer.write("(" + comparisonLabelEnd + ")\n");
    }

    private void writePush(Segment seg, int index) throws IOException {
        switch (seg) {
            case Segment.local, Segment.argument, Segment._this, Segment.that ->
                    this.writeSegmentPush(seg.getCode(), index);
            case Segment.constant -> {
                writer.write("@" + index + "\n");
                writer.write(AssemblyCode.D_EQ_A.toString());
            }
            case Segment.temp -> {
                writer.write("@" + (5 + index) + "\n");
                writer.write(AssemblyCode.D_EQ_M.toString());
            }
            case Segment.pointer -> {
                if (index == 0) {
                    writer.write(AssemblyCode.THIS.toString());
                } else if (index == 1) {
                    writer.write(AssemblyCode.THAT.toString());
                } else {
                    throw new IllegalStateException("Unexpected value of Segment: " + seg + ", Value: " + index);
                }
                writer.write(AssemblyCode.D_EQ_M.toString());
            }
            case Segment._static -> {
                writer.write("@" + currentFileName + "." + index + "\n");
                writer.write(AssemblyCode.D_EQ_M.toString());
            }
            default -> throw new IllegalStateException("Unexpected value: " + seg);
        }

        this.writeSegmentPushToDReg();
    }

    private void writeSegmentPush(String segmentCode, int index) throws IOException {
        writer.write("@" + segmentCode + "\n");
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write("@" + index + "\n");
        writer.write(AssemblyCode.A_EQ_D_ADD_A.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
    }

    private void writePop(Segment seg, int index) throws IOException {
        switch (seg) {
            case Segment.local, Segment.argument, Segment._this, Segment.that ->
                    this.writeSegmentPop(seg.getCode(), index);
            case Segment.temp -> {
                this.writeSegmentPopToDReg();
                writer.write("@" + (5 + index) + "\n");
                writer.write(AssemblyCode.M_EQ_D.toString());
            }
            case Segment.pointer -> {
                this.writeSegmentPopToDReg();
                if (index == 0) {
                    writer.write(AssemblyCode.THIS.toString());
                } else if (index == 1) {
                    writer.write(AssemblyCode.THAT.toString());
                } else {
                    throw new IllegalStateException("Unexpected value of Segment: " + seg + ", Value: " + index);
                }
                writer.write(AssemblyCode.M_EQ_D.toString());
            }
            case Segment._static -> {
                this.writeSegmentPopToDReg();
                writer.write("@" + currentFileName + "." + index + "\n");
                writer.write(AssemblyCode.M_EQ_D.toString());
            }
            default -> throw new IllegalStateException("Unexpected value: " + seg);
        }
    }

    private void writeSegmentPop(String segmentCode, int index) throws IOException {
        writer.write("@" + segmentCode + "\n");
        writer.write(AssemblyCode.D_EQ_M.toString());
        writer.write("@" + index + "\n");
        writer.write(AssemblyCode.D_EQ_D_ADD_A.toString());
        writer.write(AssemblyCode.R13.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        this.writeSegmentPopToDReg();
        writer.write(AssemblyCode.R13.toString());
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
    }

    private void writeSegmentPopToDReg() throws IOException {
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.M_EQ_M_SUB_1.toString());
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.D_EQ_M.toString());
    }

    private void writeSegmentPushToDReg() throws IOException {
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.A_EQ_M.toString());
        writer.write(AssemblyCode.M_EQ_D.toString());
        writer.write(AssemblyCode.SP.toString());
        writer.write(AssemblyCode.M_EQ_M_ADD_1.toString());
    }
}
