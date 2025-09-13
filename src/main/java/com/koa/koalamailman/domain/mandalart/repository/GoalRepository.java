package com.koa.koalamailman.domain.mandalart.repository;

import com.koa.koalamailman.domain.mandalart.repository.entity.GoalEntity;
import com.koa.koalamailman.domain.mandalart.repository.entity.GoalLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {
    List<GoalEntity> findGoalsByMandalartId(@Param("mandalartId") Long mandalartId);
    List<GoalEntity> findByMandalartIdAndLevelIn(Long mandalartId, List<GoalLevel> levels);

    /**
     select g.*
     from goal g
     join mandalart m on g.mandalart_id = m.mandalart_id
     where m.user_id = ?
    **/
    @Query("""
        select g
        from GoalEntity g
        join fetch g.mandalart m
        where m.userId = :userId
    """)
    List<GoalEntity> findGoalsByUserId(@Param("userId") Long userId);
}
