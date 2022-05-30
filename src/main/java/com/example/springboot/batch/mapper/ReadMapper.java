package com.example.springboot.batch.mapper;

import com.example.springboot.batch.model.Read;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReadMapper {

    @Select("select read_id as readId, read_name as readName from read_table order by read_id limit #{_skiprows}, #{_pagesize}")
    List<Read> findAll();

    @Select("select * from read_table where read_id = #{readId}")
    Read findByReadId(@Param("readId") int readId);
}
