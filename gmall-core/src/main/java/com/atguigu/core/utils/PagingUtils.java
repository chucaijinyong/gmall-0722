package com.atguigu.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chucai
 * @Description : 自定义分页工具类
 * @Create on : 2020/7/23 18:25
 **/
public class PagingUtils {
    private Integer totalNum;//总条数
    private Integer totalPage;//总页数
    private Integer pageSize;//每页条数
    private Integer pageNum;//当前页码
    private Integer queryIndex;//当前页从第几条开始查
    private Object data;

    public static PagingUtils pagination(Integer totalNum, Integer pageSize, Integer pageNum){
        PagingUtils page = new PagingUtils();
        page.setTotalNum(totalNum);
        Integer totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        page.setTotalPage(totalPage);
        page.setPageSize(pageSize);
        page.setQueryIndex((pageNum-1)*pageSize+1);
        return page;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageNum;
    }

    public void setPageIndex(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Paging{" +
                "totalNum=" + totalNum +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", queryIndex=" + queryIndex +
                ", data=" + data +
                '}';
    }

    public static void main(String[] args) {


        //手动对集合进行分页
        List<String> strings = new ArrayList<>(4);
        strings.add("1111");
        strings.add("222");
        strings.add("333");
        strings.add("444");
        strings.add("5555");
        strings.add("666");
        strings.add("777");
        strings.add("888");
        PagingUtils paging = PagingUtils.pagination(strings.size(),5,2);
//        paging.setPageIndex(pageNum);
        // 从哪一个下标开始查
        int fromIndex = paging.getQueryIndex()-1;
        int toIndex = 0;
        if (fromIndex + paging.getPageSize() >= strings.size()){
            toIndex = strings.size();
        }else {
            toIndex = fromIndex + paging.getPageSize();
        }
//        if (fromIndex > toIndex){
//            return null;
//        }
        List<String> strings1 = strings.subList(fromIndex, toIndex);
        strings1.stream().forEach(e->
                        System.out.println(e)
                );
        paging.setData(strings1);
        System.out.println(paging.toString());
    }
}
