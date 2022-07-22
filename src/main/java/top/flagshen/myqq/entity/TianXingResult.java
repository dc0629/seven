package top.flagshen.myqq.entity;

import lombok.Data;

import java.util.List;

@Data
public class TianXingResult {

    private Integer code;

    private String msg;

    private List<Content> newslist;
}
