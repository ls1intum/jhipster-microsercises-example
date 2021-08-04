package de.tum.in.www1.artemis.repository;

import de.tum.in.www1.artemis.domain.Lecture;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lecture entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {}
