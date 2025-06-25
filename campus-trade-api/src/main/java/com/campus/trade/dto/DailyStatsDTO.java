package com.campus.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class DailyStatsDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    private long count;

    // Getters and Setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}