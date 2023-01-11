package top.flagshen.myqq.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词字典树
 */
public class MgcTrie {
    private TrieNode root;

    MgcTrie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setEnd(true);
    }

    boolean search(String word) {
        TrieNode current = root;
        int depth = 0;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                // 如果查询深度大于0，就回退两步
                if (depth>0) {
                    i=i-depth;
                    depth=0;
                    current=root;
                }
                continue;
            }
            current = node;
            // 如果已经走到某个节点尽头，代表包含该词
            if (current.isEnd()) {
                return true;
            }
            depth++;
        }
        return current.isEnd();
    }
}

class TrieNode {
    private final Map<Character, TrieNode> children = new HashMap<>();
    private boolean end;

    Map<Character, TrieNode> getChildren() {
        return children;
    }

    boolean isEnd() {
        return end;
    }

    void setEnd(boolean end) {
        this.end = end;
    }
}
