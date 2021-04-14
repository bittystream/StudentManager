package cn.edu.cqu.studentmanager.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.cqu.studentmanager.StudentInfo;

public class ListUtil {
    public static void sortByIdAscending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                System.out.println(t1.getId().compareTo(t2.getId()));
                return t1.getId().compareTo(t2.getId());
            }
        });
    }

    public static void sortByIdDescending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                return t2.getId().compareTo(t1.getId());
            }
        });
    }

    public static void sortByNameAscending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });
    }

    public static void sortByNameDescending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                return t2.getName().compareTo(t1.getName());
            }
        });
    }
    public static void sortByGpaAscending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                return (int)(t1.getGpa() * 100 - t2.getGpa() * 100);
            }
        });
    }

    public static void sortByGpaDescending(List<StudentInfo> list){
        Collections.sort(list, new Comparator<StudentInfo>() {
            @Override
            public int compare(StudentInfo t1, StudentInfo t2) {
                return (int)(t2.getGpa() * 100 - t1.getGpa() * 100);
            }
        });
    }
}
