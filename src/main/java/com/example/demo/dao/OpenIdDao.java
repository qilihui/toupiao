package com.example.demo.dao;

import com.example.demo.entity.OpenId;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author qilihui
 * @date 2021/6/15 20:14
 */
@Mapper
public interface OpenIdDao {
    @Select("select id, open_id from open_id")
    List<OpenId> getAllOpenId();

    @Insert("insert into open_id (open_id)  values(#{openId})")
    int insert(String openId);
}
