package com.ai.PathFinder.repositories;

import com.ai.PathFinder.entities.CommonRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonRouteRepository extends JpaRepository<CommonRoute, Long> {

}
