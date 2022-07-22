package top.flagshen.myqq.entity;

import lombok.Data;

@Data
public class NovelAttribute {

    private String id; // id
    private String FictionName; // 小说名
    private String FictionChapter; // 小说章节以及章节名
    private String FictionUrl; // 章节链接

    public String getFictionNumber() {
        return FictionNumber;
    }

    public void setFictionNumber(String fictionNumber) {
        FictionNumber = fictionNumber;
    }

    private String FictionNumber; // 章节字数

}
