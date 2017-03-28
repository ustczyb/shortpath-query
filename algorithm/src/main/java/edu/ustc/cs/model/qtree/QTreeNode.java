package edu.ustc.cs.model.qtree;

/**
 * Created by zyb on 2017/3/27.
 * 四叉树的数据结构
 */
public class QTreeNode {
    /*
    第一到第四象限
     */
    QTreeNode first;
    QTreeNode second;
    QTreeNode third;
    QTreeNode forth;
    /*
    是否为叶子节点
     */
    boolean isLeaf;

}
