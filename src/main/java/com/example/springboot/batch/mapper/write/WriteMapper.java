package com.example.springboot.batch.mapper.write;

import com.example.springboot.batch.model.ContractCompany;
import com.example.springboot.batch.model.Write;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WriteMapper {

    void insertContractCompany(ContractCompany contractCompany);
}
