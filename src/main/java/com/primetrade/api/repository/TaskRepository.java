// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/repository/TaskRepository.java
package com.primetrade.api.repository;

import com.primetrade.api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // tried findByUserId first but needed descending order so used @Query
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<Task> findTasksForUserOrdered(@Param("userId") Long userId);

    List<Task> findAllByOrderByCreatedAtDesc();
}
