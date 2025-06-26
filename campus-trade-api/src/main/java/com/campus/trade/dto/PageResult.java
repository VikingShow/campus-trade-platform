package com.campus.trade.dto;

import com.github.pagehelper.Page;
import java.io.Serializable;
import java.util.List;

/**
 * 一个通用的分页结果封装类，用于向前端返回统一格式的分页数据
 * @param <T>
 */
public class PageResult<T> implements Serializable {

    private long total;     // 总记录数
    private List<T> list;   // 当前页的数据列表

    /**
     * 一个方便的构造方法，可以直接从 PageHelper 的 Page 对象转换
     * @param page mybatis-pagehelper查询后返回的page对象
     */
    public PageResult(Page<T> page) {
        this.total = page.getTotal();
        this.list = page.getResult();
    }

    // --- Getters and Setters ---
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}