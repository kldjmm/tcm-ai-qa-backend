package com.lilin.tcmqa.util;

import java.util.ArrayList;
import java.util.List;

public class TextSplitUtil {

    /**
     * 文本切分工具
     *
     * @param text 原始文本
     * @param chunkSize 每个片段最大长度
     * @param overlap 相邻片段重叠长度
     * @return 切分后的文本片段列表
     */
    public static List<String> splitText(String text,int chunkSize,int overlap){
        List<String> chunks = new ArrayList<String>();
        if(text==null || text.trim().isEmpty()){
            return chunks;
        }
        if(chunkSize<=0){
            throw new IllegalArgumentException("chunkSize必须大于0");
        }
        if(overlap<=0||overlap>=chunkSize){
            throw new IllegalArgumentException("overlap必须大于等于0且小元chunksize");
        }
        String cleanText=text.trim();
        int length=cleanText.length();
        int start=0;
        while(start<length){
            int end=Math.min(start+chunkSize,length);
            String chunk=cleanText.substring(start,end);
            chunks.add(chunk);

            if(end==length){
                break;
            }
            start=end-overlap;
        }
        return chunks;
    }

}
