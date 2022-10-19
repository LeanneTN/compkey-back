package com.example.compkeyback.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.compkeyback.domain.Index;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexMapper extends BaseMapper<Index> {
}
