package com.chen.acl.utils;

import com.chen.search.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class BuildPermissionList {
    public static List<Permission> buildPermissionHelper(List<Permission> permissions) {
        //因为其中依旧是树形结构，所以依旧需要通过递归的方式
        //首先创造一个集合，目的是将最后的所有数据都放在集合中进行返回
        List<Permission> trees = new ArrayList<>();
        //进行循环遍历，pid=0 则这是第一层
        for (Permission p : permissions) {
            //如果pid==0  则设置成第一层
            if (p.getPid() == 0) {
                //设置这个节点，就说明从这个节点开始
                p.setLevel(1);
                //找到第一次层开始调用，从第一层开始往下找
                //将找到的数据存储到trees集合中去
                trees.add(findTrees(p, permissions));
            }
        }
        return trees;
    }

    private static Permission findTrees(Permission p, List<Permission> permissions) {

        //此时的p就是当前节点
        p.setChildren(new ArrayList<Permission>());
        //遍历下层id
        for (Permission it : permissions) {
            //判断当前节点的id和pid是否一样
            if (p.getId().longValue() == it.getPid().longValue()) {
                //节点增加
                int level = p.getLevel() + 1;
                //将新的节点设置进去
                it.setLevel(level);
                if(p.getChildren()==null){
                    p.setChildren(new ArrayList<>());

                }
                //封装下一层数据
                p.getChildren().add(findTrees(it, permissions));

            }


        }

        return p;
    }

}
