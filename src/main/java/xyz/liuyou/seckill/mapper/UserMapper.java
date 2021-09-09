package xyz.liuyou.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.liuyou.seckill.pojo.User;

/**
 * <p>
 * 秒杀用户表 Mapper 接口
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-09
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

}
