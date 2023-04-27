package com.github.shy526.devenvarrange.oo;

import lombok.Data;

import java.util.Date;

@Data
public class ToolVersion {
    private String version;
    private Date date;
    private String dateStr;
    private String url;

    public ToolVersion(String version, Date date, String dateStr, String url) {
        this.version = version;
        this.date = date;
        this.dateStr = dateStr;
        this.url = url;
    }
}
