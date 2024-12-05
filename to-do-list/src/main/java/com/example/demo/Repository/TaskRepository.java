package com.example.demo.Repository;


import com.example.demo.model.Task;
import com.example.demo.model.TaskState;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.category.id =:id and t.state.id =:state and t.client.id =:clientID")
    public List<Task> findByCategory(@Param("id") Long id,
                                     @Param("state") Long state,
                                     @Param("clientID") Long clientID);

    @Query("select t from Task  t where t.category.id= :id")
    public List<Task> findByCategoryId(@Param("id") Long id);

    @Query("SELECT t from Task t WHERE t.id= :id AND t.client.id=:clientID")
    public Task findTask(@Param("id") Long id, @Param("clientID") Long clientID);

    @Query("SELECT t FROM Task t WHERE t.state.id = :state AND t.client.id = :clientID")
    Page<Task> findByStateAndClientId(@Param("state") Long state,
                                      @Param("clientID") Long clientID,
                                      Pageable pageable);

    @Query("SELECT t FROM Task t WHERE (t.state.id = :state OR t.state.id = :state2) AND t.client.id = :clientID ORDER BY t.donedate DESC")
    Page<Task> findByStateOpenOrLateOrderByDonedate(
            @Param("state") Long state,
            @Param("state2") Long state2,
            @Param("clientID") Long clientID,
            Pageable pageable);

    public List<Task> findByClientId(Long clientID);

    @Query("SELECT t FROM Task t WHERE (t.state.id = :state OR t.state.id = :state2) AND t.client.id = :clientID")
    Page<Task> findByStateOpenOrLate(@Param("state") Long state, @Param("state2") Long state2,
                                     @Param("clientID") Long clientID, Pageable pageable);


    public default Task findTaskById(Long id){
        return findById(id).orElse(null);
    };

    @Query("SELECT t FROM Task t WHERE t.client.id = :userID")
    public List<Task> findByClient(@Param("userID") Long userID);

    Page<Task> findByClient(User client, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.client.id = :userId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :taskName, '%'))")
    List<Task> findTasksByNameLike(@Param("userId") Long userId, @Param("taskName") String taskName);



}
