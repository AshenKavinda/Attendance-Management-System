package com.attendance.model;

import java.sql.Date;

public class Attendance {

    private int    id;
    private int    studentClassId;
    private Date   date;
    private String status;
    private String remarks;

    // Extra fields populated by JOIN queries (display only)
    private String studentFirstName;
    private String studentLastName;
    private int    indexNumber;
    private String className;

    public Attendance() {}

    public Attendance(int id, int studentClassId, Date date, String status, String remarks) {
        this.id             = id;
        this.studentClassId = studentClassId;
        this.date           = date;
        this.status         = status;
        this.remarks        = remarks;
    }

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }

    public int    getStudentClassId()          { return studentClassId; }
    public void   setStudentClassId(int v)     { this.studentClassId = v; }

    public Date   getDate()                    { return date; }
    public void   setDate(Date v)              { this.date = v; }

    public String getStatus()                  { return status; }
    public void   setStatus(String v)          { this.status = v; }

    public String getRemarks()                 { return remarks; }
    public void   setRemarks(String v)         { this.remarks = v; }

    public String getStudentFirstName()        { return studentFirstName; }
    public void   setStudentFirstName(String v){ this.studentFirstName = v; }

    public String getStudentLastName()         { return studentLastName; }
    public void   setStudentLastName(String v) { this.studentLastName = v; }

    public String getStudentFullName() {
        return (studentFirstName != null ? studentFirstName : "")
             + " "
             + (studentLastName  != null ? studentLastName  : "");
    }

    public int    getIndexNumber()             { return indexNumber; }
    public void   setIndexNumber(int v)        { this.indexNumber = v; }

    public String getClassName()               { return className; }
    public void   setClassName(String v)       { this.className = v; }
}
