package com.alibaba.ydt.portal.common;

import org.springframework.core.PriorityOrdered;

import java.util.Comparator;

/**
 * 优先级比较器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 15/1/13 上午11:19.
 */
public class PriorityComparator implements Comparator<PriorityOrdered> {

    public static final int ORDER_DIR_ASC = 1;
    public static final int ORDER_DIR_DESC = -1;

    private int dir = ORDER_DIR_ASC;

    public PriorityComparator(int dir) {
        if(dir != ORDER_DIR_ASC && dir != ORDER_DIR_DESC) {
            throw new RuntimeException("您指定的排序方向不合理，请传入 1 或者 -1");
        }
        this.dir = dir;
    }

    @Override
    public int compare(PriorityOrdered o1, PriorityOrdered o2) {
        return dir * (o1.getOrder() - o2.getOrder());
    }
}
