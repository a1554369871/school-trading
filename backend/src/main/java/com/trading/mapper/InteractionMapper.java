package com.trading.mapper;

import com.trading.entity.Interaction;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface InteractionMapper {

    Interaction findByUserAndProduct(@Param("userId") Integer userId,
                                     @Param("productId") Integer productId,
                                     @Param("type") String type);

    int insert(Interaction interaction);

    int deleteByUserAndProduct(@Param("userId") Integer userId,
                               @Param("productId") Integer productId,
                               @Param("type") String type);

    /**
     * GROUP BY + COUNT：统计商品的互动数据（点赞/收藏/想要数量）
     */
    Map<String, Object> countByProduct(@Param("productId") Integer productId);

    /**
     * 检查用户是否已互动
     */
    int checkInteraction(@Param("userId") Integer userId,
                         @Param("productId") Integer productId,
                         @Param("type") String type);

    /**
     * 查询用户指定类型互动的商品ID列表
     */
    List<Integer> findProductIdsByUserAndType(@Param("userId") Integer userId,
                                              @Param("type") String type);
}
