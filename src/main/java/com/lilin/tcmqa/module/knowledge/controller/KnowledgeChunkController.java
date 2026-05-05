package com.lilin.tcmqa.module.knowledge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.exception.BusinessException;
import com.lilin.tcmqa.module.knowledge.entity.KnowledgeChunk;
import com.lilin.tcmqa.module.knowledge.entity.KnowledgeItem;
import com.lilin.tcmqa.module.knowledge.mapper.KnowledgeChunkMapper;
import com.lilin.tcmqa.module.knowledge.mapper.KnowledgeItemMapper;
import com.lilin.tcmqa.util.TextSplitUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chunk")
@RequiredArgsConstructor
public class KnowledgeChunkController {

    private final KnowledgeItemMapper knowledgeItemMapper;

    private final KnowledgeChunkMapper knowledgeChunkMapper;

    /**
     * 把某个知识条目的 content 切分成多个 chunk
     */
    @PostMapping("/split/{itemId}")
    public Result<Integer> splitByItemId(@PathVariable Long itemId) {
        KnowledgeItem item = knowledgeItemMapper.selectById(itemId);

        if (item == null) {
            throw new BusinessException(404, "知识条目不存在");
        }

        if (item.getContent() == null || item.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "知识内容为空，无法切分");
        }

        // 删除旧片段，防止重复切分产生重复数据
        knowledgeChunkMapper.delete(
                new LambdaQueryWrapper<KnowledgeChunk>()
                        .eq(KnowledgeChunk::getItemId, itemId)
        );

        // 每 300 字切一段，相邻片段重叠 50 字
        List<String> chunks = TextSplitUtil.splitText(item.getContent(), 300, 50);

        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.setItemId(itemId);
            chunk.setDocumentId(null);
            chunk.setChunkIndex(i + 1);
            chunk.setChunkText(chunks.get(i));
            chunk.setStatus(1);

            knowledgeChunkMapper.insert(chunk);
        }

        return Result.success(chunks.size());
    }

    /**
     * 查询某个知识条目的所有 chunk
     */
    @GetMapping("/item/{itemId}")
    public Result<List<KnowledgeChunk>> listByItemId(@PathVariable Long itemId) {
        List<KnowledgeChunk> chunks = knowledgeChunkMapper.selectList(
                new LambdaQueryWrapper<KnowledgeChunk>()
                        .eq(KnowledgeChunk::getItemId, itemId)
                        .eq(KnowledgeChunk::getStatus, 1)
                        .orderByAsc(KnowledgeChunk::getChunkIndex)
        );

        return Result.success(chunks);
    }

    /**
     * 根据关键词检索 chunk
     */
    @GetMapping("/search")
    public Result<List<KnowledgeChunk>> search(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(400, "关键词不能为空");
        }

        List<KnowledgeChunk> chunks = knowledgeChunkMapper.selectList(
                new LambdaQueryWrapper<KnowledgeChunk>()
                        .eq(KnowledgeChunk::getStatus, 1)
                        .like(KnowledgeChunk::getChunkText, keyword)
                        .orderByDesc(KnowledgeChunk::getCreateTime)
                        .last("LIMIT 10")
        );

        return Result.success(chunks);
    }
}

























