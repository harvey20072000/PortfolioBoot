package ga.workshop.com.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ga.workshop.com.model.Target;



public interface DemoRepository /*extends JpaRepository<Target, Integer>*/ {
	/*
	@Query(value = "FROM Target WHERE actorName like :actorName")
	List<Target> findByActorName(@Param(value = "actorName") String actorName);
	*/
}
