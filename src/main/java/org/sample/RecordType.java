package org.sample;

import java.io.Serializable;

public class RecordType implements Serializable {
    private String str1;
    private String str2;
    private int int1;

    public String getStr1() {
        return str1;
    }
    public void setStr1(String str1) {
        this.str1 = str1;
    }
    public String getStr2() {
        return str2;
    }
    public void setStr2(String str2) {
        this.str2 = str2;
    }
    public int getInt1() {
        return int1;
    }
    public void setInt1(int int1) {
        this.int1 = int1;
    }
}
