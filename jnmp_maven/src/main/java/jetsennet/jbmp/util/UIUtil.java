/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @author Guo
 */
public class UIUtil
{

    /**
     * 设置Dialog居中
     * @param dialog 参数
     */
    public static void setLocation(JDialog dialog)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dis = tk.getScreenSize();
        int width = (int) dis.getWidth();
        int height = (int) dis.getHeight();
        int w = dialog.getWidth();
        int h = dialog.getHeight();
        dialog.setLocation((width - w) / 2, (height - h) / 2);
    }

    /**
     * 完全展开树
     * @param tree 参数
     * @param parent 参数
     */
    public static void expandTree(JTree tree, TreePath parent)
    {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0)
        {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();)
            {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandTree(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    /**
     * @param tree 参数
     * @param path 参数
     */
    public static void expandTo(JTree tree, TreePath path)
    {
        tree.expandPath(path);
        TreePath parentPath = path.getParentPath();
        if (parentPath != null)
        {
            expandTo(tree, parentPath);
        }
    }

    /**
     * 设置组件大小的最大，最小，最佳值为一个值
     * @param component 组件
     * @param width 组件宽度
     * @param height 组件长度
     */
    public static void setInvariantSize(JComponent component, int width, int height)
    {
        Dimension dimension = new Dimension(width, height);
        component.setPreferredSize(dimension);
        component.setMaximumSize(dimension);
        component.setMinimumSize(dimension);
    }

    /**
     * 设置组件的高度固定
     * @param j 组件
     * @param width 组件宽度
     * @param height 组件长度
     */
    public static void setInvariantHeight(JComponent j, int width, int height)
    {
        j.setPreferredSize(new Dimension(width, height));
        j.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        j.setMinimumSize(new Dimension(0, height));
    }
}
