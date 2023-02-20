package com.example.springboot.batch.mapper.read;

import com.example.springboot.batch.model.ContractCompany;
import com.example.springboot.batch.model.Read;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReadMapper {
    List<ContractCompany> selectContractCompany(Map<String, Integer> map);
}
