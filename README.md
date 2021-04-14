
# Army

本项目起源于一次公司关于app自升级基础能力需求的迭代，由于以前没有做弹窗优先级控制，或者是页面显示蒙层等管理，
从而导致强制升级的弹窗被遮挡，或是一次出现多个弹窗的情形，所以当时做了一套轻量级的优先级控制方案来解决这一问题，不过
当时因为考虑到改动所导致的不是本需求功能的关联改动，从而导致测试的工作量上升，及其项目稳定性的考虑，实现方式比较不优雅，
而且在项目迭代末期还发现了一个致命的问题，也因为其它解决方案改动过大不得不采用了折中的方案去解决这一问题。轻量级的方案可以大致简述为：
app维护一个单例，单例里面管理一个弹窗的数组，当没有弹窗显示时则直接弹窗，当有弹窗展示时则添加到数组，当弹窗消失的时候主动去移除弹窗数组的值，
并根据优先级触发下一个弹窗，这样就会产生一个问题，当因为异常或者没有主动调用移除，就会导致接下来的弹窗都弹不出来。

基于上面的原因就有了本项目，本项目加入了生命周期控制，当页面异常关掉会主动移除。
而且不仅仅可以用作弹窗的优先级管理，你可以把它当作一个任务优先级调度管理器


#### 功能概述

* 支持生命周期创建和消亡，传入context决定
* 支持任务执行时回调，
* 支持任务分组

#### 集成




#### 使用

```
 Army.with(this).load("low dialog")
                 .priority(TaskPriority.LOW.setPriorityExtra(10))
                 .listener(new RequestListener() {
                     @Override
                     public void startTask() {
                         showDialog("low dialog");
                     }
                 })
                 .post();
```
    
#### 打开日志

```
Army.openLog();
```

#### 自定义Army

```
 ArmyBuilder armyBuilder = new ArmyBuilder();
 armyBuilder.addGlobalRequestListener(new RequestListener() {
            @Override
            public void startTask(SingleTask newTask) {
                
            }
        });
 armyBuilder.setEngine(new IEngine() {
            @Override
            public void clearCache() {
                
            }

            @Override
            public boolean release(String taskName) {
                return false;
            }

            @Override
            public void post(String taskName, TaskPriority priority, ResourceCallback callback) {

            }
        });
 Army.init(this,armyBuilder);
```
   