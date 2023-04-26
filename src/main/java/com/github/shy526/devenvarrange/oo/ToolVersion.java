package com.github.shy526.devenvarrange.oo;

import lombok.Data;

import java.util.Date;

@Data
public class ToolVersion {
    private String version;
    private Date date;
    private String dateStr;

    public ToolVersion(String version, Date date, String dateStr) {
        this.version = version;
        this.date = date;
        this.dateStr = dateStr;
    }
}
