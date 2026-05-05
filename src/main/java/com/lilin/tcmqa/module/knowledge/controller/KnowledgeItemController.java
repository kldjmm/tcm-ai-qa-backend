package com.lilin.tcmqa.module.knowledge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.exception.BusinessException;
import com.lilin.tcmqa.module.knowledge.entity.KnowledgeItem;
import com.lilin.tcmqa.module.knowledge.mapper.KnowledgeItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeItemController {

    private final KnowledgeItemMapper knowledgeItemMapper;

    @PostMapping("/add")
    public Result<Long> add(@RequestBody KnowledgeItem knowledgeItem) {
        if (knowledgeItem.getTitle() == null || knowledgeItem.getTitle().trim().isEmpty()) {
            throw new BusinessException(400, "知识标题不能为空");
        }

        if (knowledgeItem.getCategoryId() == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }

        if (knowledgeItem.getContent() == null || knowledgeItem.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "知识内容不能为空");
        }

        if (knowledgeItem.getStatus() == null) {
            knowledgeItem.setStatus(1);
        }

        knowledgeItemMapper.insert(knowledgeItem);

        return Result.success(knowledgeItem.getId());
    }

    @GetMapping("/page")
    public Result<Page<KnowledgeItem>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId
    ) {
        LambdaQueryWrapper<KnowledgeItem> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(KnowledgeItem::getStatus, 1);

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(KnowledgeItem::getTitle, keyword)
                    .or()
                    .like(KnowledgeItem::getTags, keyword)
                    .or()
                    .like(KnowledgeItem::getContent, keyword)
            );
        }

        if (categoryId != null) {
            wrapper.eq(KnowledgeItem::getCategoryId, categoryId);
        }

        wrapper.orderByDesc(KnowledgeItem::getCreateTime);

        Page<KnowledgeItem> page = new Page<>(pageNum, pageSize);

        Page<KnowledgeItem> result = knowledgeItemMapper.selectPage(page, wrapper);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<KnowledgeItem> detail(@PathVariable Long id) {
        KnowledgeItem knowledgeItem = knowledgeItemMapper.selectById(id);

        if (knowledgeItem == null) {
            throw new BusinessException(404, "知识条目不存在");
        }

        return Result.success(knowledgeItem);
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeItem knowledgeItem) {
        KnowledgeItem exist = knowledgeItemMapper.selectById(id);

        if (exist == null) {
            throw new BusinessException(404, "知识条目不存在");
        }

        knowledgeItem.setId(id);

        knowledgeItemMapper.updateById(knowledgeItem);

        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        KnowledgeItem exist = knowledgeItemMapper.selectById(id);

        if (exist == null) {
            throw new BusinessException(404, "知识条目不存在");
        }

        knowledgeItemMapper.deleteById(id);

        return Result.success();
    }
}