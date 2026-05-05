package com.lilin.tcmqa.module.knowledge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.exception.BusinessException;
import com.lilin.tcmqa.module.knowledge.entity.KnowledgeCategory;
import com.lilin.tcmqa.module.knowledge.mapper.KnowledgeCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<KnowledgeCategory>> list() {
        List<KnowledgeCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getStatus, 1)
                        .orderByAsc(KnowledgeCategory::getSortOrder)
        );

        return Result.success(categories);
    }

    @PostMapping("/add")
    public Result<Long> add(@RequestBody KnowledgeCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        category.setStatus(1);

        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        categoryMapper.insert(category);

        return Result.success(category.getId());
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeCategory category) {
        KnowledgeCategory exist = categoryMapper.selectById(id);

        if (exist == null) {
            throw new BusinessException(404, "分类不存在");
        }

        category.setId(id);
        categoryMapper.updateById(category);

        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        KnowledgeCategory exist = categoryMapper.selectById(id);

        if (exist == null) {
            throw new BusinessException(404, "分类不存在");
        }

        categoryMapper.deleteById(id);

        return Result.success();
    }
}