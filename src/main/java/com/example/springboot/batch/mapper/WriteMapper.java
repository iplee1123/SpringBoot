package com.example.springboot.batch.mapper;

import com.example.springboot.batch.model.Write;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WriteMapper {

    @Insert("insert into write_table (write_id, write_name) values (#{writeId}, #{writeName})")
    int save(@Param("write") Write write);
}
