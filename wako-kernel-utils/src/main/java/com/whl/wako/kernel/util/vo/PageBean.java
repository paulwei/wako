package com.whl.wako.kernel.util.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paul.wei
 * @Description: 分页Bean
 * @date 2019/1/9
 */
public class PageBean<T> {

    private List<T> list;   // List集合
    private int totalCount;// 总的条数
    private int totalPage; // 总的条数
    private int pageNo=1;    // 当前的页数
    private int pageSize=15;  // 每页的条数

    public PageBean() {
    }

    public PageBean(List<T> list, int pageSize) {
        init(list,pageSize);
    }

    public PageBean(int totalPage, List<T> list) {
        initPage(list,totalPage);
    }

    public void  init(List<T> list, int pageSize) {
        this.list= list!=null?list:new ArrayList<T>();
        this.pageSize = pageSize>0?pageSize:1;
        this.totalCount = list!=null?list.size():0;
        if (totalCount % this.pageSize == 0) {
            totalPage = totalCount / this.pageSize;
        } else {
            totalPage =  totalCount / this.pageSize + 1;
        }
    }

    public void initPage(List<T> list,int totalPage){
        this.list= list!=null?list:new ArrayList<T>();
        this.totalCount = list!=null?list.size():0;
        this.totalPage = totalPage;
        if(totalCount % totalPage == 0){
            this.pageSize = totalCount/totalPage;
        }else{
            this.pageSize = totalCount/totalPage+1;
        }
        init(list, pageSize);
    }

    // 多写一个判断下一页的方法
    public boolean hasNextPage() {
        return pageNo < getTotalPage();
    }
    // 上一页的方法
    public boolean hasPrePage() {
        return pageNo > 1;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageStart(){
        return (pageNo-1)*pageSize;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if (totalCount % this.pageSize == 0) {
            totalPage = totalCount / this.pageSize;
        } else {
            totalPage =  totalCount / this.pageSize + 1;
        }
    }

    public List<T> getNextPage(int pageNo){
        int fromIndex= 0;
        int toIndex = 0;
        if(pageNo*pageSize<totalCount){
            toIndex=pageNo*pageSize;
            fromIndex=toIndex-pageSize;
        }else if(pageNo==totalPage){
            toIndex=totalCount;
            fromIndex=pageSize*(totalPage-1);
        }
        return list.subList(fromIndex, toIndex);
    }

    public PageBean(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    public int getPageNo() {
        return pageNo;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalPage() {
        return totalPage;
    }

}
