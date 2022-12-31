package com.heima.utils.common;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @Author peelsannaw
 * @create 18/12/2022 下午3:59
 */
public class Huffman {

    /**
     * sym: 左右
     * freq: 频率
     * ch: 表示是0/1  用来连接,返回字符串
     * left: 左节点
     * right: 右节点
     */
    @Data
    private static class TreeNode {
        private int sym;
        private int freq;
        private Character ch;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(int sym, int freq, Character ch, TreeNode left, TreeNode right) {
            this.sym = sym;
            this.freq = freq;
            this.ch = ch;
            this.left = left;
            this.right = right;
        }
    }


    /**
     * 构造哈夫曼树
     *
     * @return
     */
    @SuppressWarnings("all")
    private static TreeNode buildTree(String code) {
        if (code == null || code.length() == 0) {
            return null;
        }
        //记录频率
        Map<Character, Integer> freq = new HashMap<>();
        //小根堆
        PriorityQueue<TreeNode> queue = new PriorityQueue<>((o1, o2) -> o1.getFreq() - o2.getFreq());

        for (int i = 0; i < code.length(); i++) {
            Character c = code.charAt(i);
            if (freq.containsKey(c)) {
                freq.put(c, freq.get(c) + 1);
            } else {
                freq.put(c, 1);
            }
        }
        freq.forEach((key, value) -> {
            TreeNode treeNode = new TreeNode(0, value, key, null, null);
            queue.add(treeNode);
        });

        TreeNode root = null;
        if(queue.size()==1){
            TreeNode t = queue.poll();
            root = new TreeNode(0,t.getFreq(),t.getCh(),null,null);
            return root;
        }
        while (queue.size() > 0) {
            TreeNode left = null;
            TreeNode right = null;
            left = queue.poll();
            right = queue.poll();

            if (queue.size() == 0) {
                assert right != null;
                int val = left.getFreq()+right.getFreq();
                root = new TreeNode(0, val, null, left, right);
                return root;
            }else{
                int val = left.getFreq()+right.getFreq();
                TreeNode temp = new TreeNode(0,val,null,left,right);
                //重新加入
                queue.add(temp);
            }
        }
        return root;
    }

    /**
     * 便利,得到编码
     */
    private static void dfs(TreeNode root, Map<Character,String>result,StringBuilder path) {
       if(root!=null){
           if(root.getCh()!=null){
               path.append(root.getCh());
               result.put(root.getCh(),path.toString());
               return;
           }
           if(root.getLeft()!=null){
               dfs(root.getLeft(),result,path.append(root.getCh()));
           }
           if(root.getRight()!=null){
               dfs(root.getRight(),result,path.append(root.getCh()));
           }
       }
    }

    @SuppressWarnings("all")
    public static Map encode(String s){
        Map<Character,String>map = new HashMap<>();
        TreeNode treeNode = buildTree(s);
        dfs(treeNode,map,new StringBuilder());
        Map resultMap = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<s.length();i++){
            stringBuilder.append(map.get(s.charAt(i)));
        }
        resultMap.put("result",stringBuilder.toString());
        resultMap.put("mapping",map);
        return resultMap;
    }

    public static String decode(String encodeStr,Map<Character,String> result) {
        StringBuilder decodeStr = new StringBuilder();
        while(encodeStr.length()>0){
            for(Map.Entry<Character,String> e: result.entrySet()){
                String charEncodeStr = e.getValue();
                if(encodeStr.startsWith(charEncodeStr)){
                    decodeStr.append(e.getKey());
                    encodeStr = encodeStr.substring(charEncodeStr.length());
                    break;
                }
            }
        }
        return decodeStr.toString();
    }


    public static void main(String[] args) {

    }

}

