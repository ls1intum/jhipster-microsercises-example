package de.tum.in.www1.artemis.web.rest;

import de.tum.in.www1.artemis.domain.Lecture;
import de.tum.in.www1.artemis.repository.LectureRepository;
import de.tum.in.www1.artemis.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.tum.in.www1.artemis.domain.Lecture}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LectureResource {

    private final Logger log = LoggerFactory.getLogger(LectureResource.class);

    private static final String ENTITY_NAME = "lectureLecture";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LectureRepository lectureRepository;

    public LectureResource(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    /**
     * {@code POST  /lectures} : Create a new lecture.
     *
     * @param lecture the lecture to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lecture, or with status {@code 400 (Bad Request)} if the lecture has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lectures")
    public ResponseEntity<Lecture> createLecture(@RequestBody Lecture lecture) throws URISyntaxException {
        log.debug("REST request to save Lecture : {}", lecture);
        if (lecture.getId() != null) {
            throw new BadRequestAlertException("A new lecture cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Lecture result = lectureRepository.save(lecture);
        return ResponseEntity
            .created(new URI("/api/lectures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lectures/:id} : Updates an existing lecture.
     *
     * @param id the id of the lecture to save.
     * @param lecture the lecture to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lecture,
     * or with status {@code 400 (Bad Request)} if the lecture is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lecture couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lectures/{id}")
    public ResponseEntity<Lecture> updateLecture(@PathVariable(value = "id", required = false) final Long id, @RequestBody Lecture lecture)
        throws URISyntaxException {
        log.debug("REST request to update Lecture : {}, {}", id, lecture);
        if (lecture.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lecture.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lectureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Lecture result = lectureRepository.save(lecture);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lecture.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lectures/:id} : Partial updates given fields of an existing lecture, field will ignore if it is null
     *
     * @param id the id of the lecture to save.
     * @param lecture the lecture to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lecture,
     * or with status {@code 400 (Bad Request)} if the lecture is not valid,
     * or with status {@code 404 (Not Found)} if the lecture is not found,
     * or with status {@code 500 (Internal Server Error)} if the lecture couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lectures/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Lecture> partialUpdateLecture(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Lecture lecture
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lecture partially : {}, {}", id, lecture);
        if (lecture.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lecture.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lectureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Lecture> result = lectureRepository
            .findById(lecture.getId())
            .map(
                existingLecture -> {
                    if (lecture.getTitle() != null) {
                        existingLecture.setTitle(lecture.getTitle());
                    }
                    if (lecture.getDescription() != null) {
                        existingLecture.setDescription(lecture.getDescription());
                    }
                    if (lecture.getStartDate() != null) {
                        existingLecture.setStartDate(lecture.getStartDate());
                    }
                    if (lecture.getEndDate() != null) {
                        existingLecture.setEndDate(lecture.getEndDate());
                    }

                    return existingLecture;
                }
            )
            .map(lectureRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lecture.getId().toString())
        );
    }

    /**
     * {@code GET  /lectures} : get all the lectures.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lectures in body.
     */
    @GetMapping("/lectures")
    public List<Lecture> getAllLectures() {
        log.debug("REST request to get all Lectures");
        return lectureRepository.findAll();
    }

    /**
     * {@code GET  /lectures/:id} : get the "id" lecture.
     *
     * @param id the id of the lecture to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lecture, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lectures/{id}")
    public ResponseEntity<Lecture> getLecture(@PathVariable Long id) {
        log.debug("REST request to get Lecture : {}", id);
        Optional<Lecture> lecture = lectureRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lecture);
    }

    /**
     * {@code DELETE  /lectures/:id} : delete the "id" lecture.
     *
     * @param id the id of the lecture to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lectures/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        log.debug("REST request to delete Lecture : {}", id);
        lectureRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
