package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;


    @Autowired
    SetmealDishMapper setmealDishMapper;
    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 使用分页插件 pagehelper
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 启用/禁用菜品
     * @param status
     * @param id
     */
    public void changeStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
//        dish.setUpdateUser(BaseContext.getCurrentId());
//        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.update(dish);
    }

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改菜品基本信息
        dishMapper.update(dish);
        // 删除菜品原有口味信息，重新插入
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    /**
     * 根据类型获取菜品列表
     * @param categoryId
     * @return
     */
    public List<Dish> list(Integer categoryId) {
        List<Dish> dishList = dishMapper.list(categoryId);
        return dishList;
    }

    /**
     * 新增菜品
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO , dish);
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.insert(dish);
        // dish插入数据库后会返回id
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            // 向dishFlavor插入多条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 批量删除菜品 （涉及dish表。dishFlavor表、 setmealDish表）
     * @param ids
     */
    @Transactional // 开启事务
    public void deleteBatch(List<Long> ids) {
        for(Long id : ids){
            // 如果当前菜品正在售卖，不允许删除
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            // 如果当前菜品关联了套餐，不允许删除
            List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
            if(setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        // 删除菜品数据
        for(Long id : ids){
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }
}
