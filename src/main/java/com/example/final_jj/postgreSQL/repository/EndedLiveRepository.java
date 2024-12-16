package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.EndedLive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EndedLiveRepository extends CrudRepository<EndedLive, Integer> {
//
//    // 누적 방송시간 계산 (end_time - start_time 합계)
//    @Query(value = "SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (el.actual_end_time - el.actual_start_time))), 0) " +
//            "FROM ended_live el WHERE el.channel_id = :channelId", nativeQuery = true)
//    Long getTotalBroadcastTimeByChannelId(String channelId);
//
//    @Query(value = "SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (e.actual_end_time - e.actual_start_time))), 0) " +
//            "FROM ended_live e " +
//            "WHERE e.channel_id = :channelId " +
//            "AND EXTRACT(YEAR FROM e.actual_end_time) = :year " +
//            "AND EXTRACT(MONTH FROM e.actual_end_time) = :month",
//            nativeQuery = true)
//    Long getTotalBroadcastTime(@Param("channelId") String channelId,
//                               @Param("year") int year,
//                               @Param("month") int month);

}
