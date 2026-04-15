package com.mochu.business.mapper;

import com.mochu.business.entity.BizNoSeed;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BizNoSeedMapper {

    @Select("SELECT prefix, date_part, current_seq FROM biz_no_seed WHERE prefix = #{prefix} AND date_part = #{datePart} FOR UPDATE")
    BizNoSeed selectForUpdate(@Param("prefix") String prefix, @Param("datePart") String datePart);

    @Select("SELECT prefix, date_part, current_seq FROM biz_no_seed")
    List<BizNoSeed> selectAll();

    @Insert("INSERT INTO biz_no_seed (prefix, date_part, current_seq) VALUES (#{prefix}, #{datePart}, #{currentSeq})")
    int insert(BizNoSeed seed);

    @Update("UPDATE biz_no_seed SET current_seq = #{currentSeq} WHERE prefix = #{prefix} AND date_part = #{datePart}")
    int updateSeq(BizNoSeed seed);
}
