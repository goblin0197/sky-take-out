package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;



    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO ) {
        log.info("新增分类{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类{}",categoryPageQueryDTO);
        PageResult res = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(res);
    }

    /**
         * 启用/禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用分类")
    public Result changeStatus(@PathVariable("status") Integer status, Long id){
        log.info("启用/禁用分类，status:{},id:{}",status,id);
        categoryService.changeStatus(status , id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询分类")
    public Result<Category> getById(@PathVariable("id") Long id){
        log.info("根据id查询分类，id:{}",id);
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result delete(@RequestBody Long id){
        log.info("删除分类，id:{}",id);
        categoryService.deleteById(id);
        return Result.success() ;
    }


    /**
     * 修改分类信息
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类信息")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        log.info("根据类型查询分类，type:{}",type);
        List<Category> categoryList = categoryService.list(type);
        return Result.success(categoryList);
    }
}
