package com.rolan.army;

/**
 * Created by Rolan.Wang on 2/3/21.11:06 AM
 * 描述：
 */
public enum  TaskPriority {
    /**
     * 请自主在以下几种类型区间里选择，
     * 1000~2000 弱弹窗(运营弹窗)
     * 2000~3000 普通弹窗
     * 3000~4000 重要弹窗
     * 4000~5000 非常重要弹窗
     * 5000~6000 管理员级别弹窗 (强制升级弹窗)强弹，不管前面是否已经有弹窗,如果有则会盖在上面
     */
    LOW(1000),
    NORMAL(2000),
    IMPORTANCE(3000),
    ADMIN(4000),
    FORCE(5000);


    private int priority;

    TaskPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public TaskPriority setPriorityExtra(int priority) {
        if(priority>0){
            if(priority>=1000){//保证不随机加优先级
                this.priority=priority%1000;
            }else {
                this.priority+=priority;
            }
        }
        return this;
    }
}
