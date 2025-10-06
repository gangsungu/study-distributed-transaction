package org.example.point.infrastructure;

import org.example.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Integer> {

    Point findByUserId(Long userId);
}
