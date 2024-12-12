package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;



    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId") //删除某个key对应的缓存数据
    public Result save(@RequestBody SetmealDTO setmealDTO ) {
        log.info("新增套餐{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐{}",setmealPageQueryDTO);
        PageResult res = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(res);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//删除setmealCache下所有的缓存数据
    public Result changeStatus(@PathVariable("status") Integer status, Long id){
        log.info("启用/禁用套餐，status:{},id:{}",status,id);
        setmealService.changeStatus(status , id);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable("id") Long id){
        log.info("根据id查询套餐，id:{}",id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//删除setmealCache下所有的缓存数据
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐，id:{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success() ;
    }


    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐信息")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//删除setmealCache下所有的缓存数据
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐信息{}",setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

}
