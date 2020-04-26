package com.jsq.demo.pojo.po;

import java.time.LocalDateTime;
import java.util.Date;

public class PersonPO implements Comparable<PersonPO> {
    private String code;
    private String name;
    private Date date;

    public PersonPO(String code, String name,Date date) {
        this.code = code;
        this.name = name;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(PersonPO o) {
        return date.compareTo(o.getDate());
    }
}
