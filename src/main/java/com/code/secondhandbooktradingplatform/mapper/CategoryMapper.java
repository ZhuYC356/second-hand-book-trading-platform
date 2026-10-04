package com.code.secondhandbooktradingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.code.secondhandbooktradingplatform.entity.Category;
import com.code.secondhandbooktradingplatform.vo.NameValue;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT c.name AS name, COUNT(b.id) AS value FROM category c " +
            "LEFT JOIN book b ON b.category_id = c.id " +
            "GROUP BY c.id, c.name ORDER BY c.id")
    List<NameValue> selectCategoryDist();
}
