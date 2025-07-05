package com.campus.trade.dto;

import com.github.pagehelper.Page;
import java.io.Serializable;
import java.util.List;

/**
 * 【最终修正】一个更健壮、通用的分页结果封装类
 * @param <T>
 */
public class PageResult<T> implements Serializable {

    private long total;     // 总记录数
    private List<T> list;   // 当前页的数据列表

    /**
     * 这个构造函数现在可以接收一个 List。
     * 它会自动检查这个 List 是否是 PageHelper 返回的 Page 对象。
     * 如果是，它会提取出总数和数据列表。
     * 如果不是，它会把列表的大小作为总数。
     * 这种设计使得调用方无需再进行强制类型转换，代码更简洁、更安全。
     */
    public PageResult(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.total = page.getTotal();
            this.list = page.getResult();
        } else {
            this.total = list.size();
            this.list = list;
        }
    }

    // --- Getters and Setters ---
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}