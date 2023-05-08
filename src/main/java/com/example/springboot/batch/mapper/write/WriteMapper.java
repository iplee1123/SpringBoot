package com.example.springboot.batch.mapper.write;

import com.example.springboot.batch.model.ContractCompany;
import com.example.springboot.batch.model.Write;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WriteMapper {

    void insertContractCompany(ContractCompany contractCompany);

    void insert_ESZCTDT_IF(List<Map<String, Object>> list);

    void insert_ESZCTHD_IF(List<Map<String, Object>> list);
}
