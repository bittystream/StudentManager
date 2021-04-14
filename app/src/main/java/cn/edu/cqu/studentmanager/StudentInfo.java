package cn.edu.cqu.studentmanager;

public class StudentInfo{
    private String _name;
    private String _id;
    private String _gender;
    private String _college;
    private String _nativeOf;
    private float _gpa;
    private byte [] _avatar;

    public StudentInfo(String name, String id, String gender, String college, String nativeOf, float gpa, byte[] avatar){
       _name = name;
       _id = id;
       _gender = gender;
       _college = college;
       _nativeOf = nativeOf;
       _gpa = gpa;
       _avatar = avatar;
    }

    public String getName() { return _name;}
    public String getId() { return _id;}
    public String getGender() { return _gender;}
    public String getNative() { return _nativeOf;}
    public String getCollege() {return _college;}
    public float getGpa() { return _gpa;}
    public byte [] getAvatar() {return _avatar;}
}
