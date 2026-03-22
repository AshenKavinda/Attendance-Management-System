package com.attendance.model;

public class ClassRoom {

    private int    id;
    private String className;
    private String classCode;
    private String section;
    private int    year;

    public ClassRoom() {}

    public ClassRoom(int id, String className, String classCode, String section, int year) {
        this.id        = id;
        this.className = className;
        this.classCode = classCode;
        this.section   = section;
        this.year      = year;
    }

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }

    public String getClassName()           { return className; }
    public void   setClassName(String v)   { this.className = v; }

    public String getClassCode()           { return classCode; }
    public void   setClassCode(String v)   { this.classCode = v; }

    public String getSection()             { return section; }
    public void   setSection(String v)     { this.section = v; }

    public int    getYear()                { return year; }
    public void   setYear(int v)           { this.year = v; }

    @Override
    public String toString() { return className + " (" + classCode + ")"; }
}
