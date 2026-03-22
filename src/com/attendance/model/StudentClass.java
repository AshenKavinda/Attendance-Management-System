package com.attendance.model;

import java.sql.Date;

public class StudentClass {

    private int    id;
    private int    studentId;
    private int    classId;
    private int    indexNumber;
    private Date   enrollmentDate;
    private String status;

    // Extra fields populated by JOIN queries (display only)
    private String studentFirstName;
    private String studentLastName;
    private String className;
    private String classCode;

    public StudentClass() {}

    public StudentClass(int id, int studentId, int classId, int indexNumber,
                        Date enrollmentDate, String status) {
        this.id             = id;
        this.studentId      = studentId;
        this.classId        = classId;
        this.indexNumber    = indexNumber;
        this.enrollmentDate = enrollmentDate;
        this.status         = status;
    }

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }

    public int    getStudentId()               { return studentId; }
    public void   setStudentId(int v)          { this.studentId = v; }

    public int    getClassId()                 { return classId; }
    public void   setClassId(int v)            { this.classId = v; }

    public int    getIndexNumber()             { return indexNumber; }
    public void   setIndexNumber(int v)        { this.indexNumber = v; }

    public Date   getEnrollmentDate()          { return enrollmentDate; }
    public void   setEnrollmentDate(Date v)    { this.enrollmentDate = v; }

    public String getStatus()                  { return status; }
    public void   setStatus(String v)          { this.status = v; }

    public String getStudentFirstName()        { return studentFirstName; }
    public void   setStudentFirstName(String v){ this.studentFirstName = v; }

    public String getStudentLastName()         { return studentLastName; }
    public void   setStudentLastName(String v) { this.studentLastName = v; }

    public String getStudentFullName() {
        return (studentFirstName != null ? studentFirstName : "")
             + " "
             + (studentLastName  != null ? studentLastName  : "");
    }

    public String getClassName()               { return className; }
    public void   setClassName(String v)       { this.className = v; }

    public String getClassCode()               { return classCode; }
    public void   setClassCode(String v)       { this.classCode = v; }
}
