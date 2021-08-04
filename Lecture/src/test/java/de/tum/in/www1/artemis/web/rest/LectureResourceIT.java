package de.tum.in.www1.artemis.web.rest;

import static de.tum.in.www1.artemis.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.tum.in.www1.artemis.IntegrationTest;
import de.tum.in.www1.artemis.domain.Lecture;
import de.tum.in.www1.artemis.repository.LectureRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LectureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LectureResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/lectures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLectureMockMvc;

    private Lecture lecture;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lecture createEntity(EntityManager em) {
        Lecture lecture = new Lecture()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return lecture;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lecture createUpdatedEntity(EntityManager em) {
        Lecture lecture = new Lecture()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return lecture;
    }

    @BeforeEach
    public void initTest() {
        lecture = createEntity(em);
    }

    @Test
    @Transactional
    void createLecture() throws Exception {
        int databaseSizeBeforeCreate = lectureRepository.findAll().size();
        // Create the Lecture
        restLectureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lecture)))
            .andExpect(status().isCreated());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeCreate + 1);
        Lecture testLecture = lectureList.get(lectureList.size() - 1);
        assertThat(testLecture.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLecture.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLecture.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testLecture.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void createLectureWithExistingId() throws Exception {
        // Create the Lecture with an existing ID
        lecture.setId(1L);

        int databaseSizeBeforeCreate = lectureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLectureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lecture)))
            .andExpect(status().isBadRequest());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLectures() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        // Get all the lectureList
        restLectureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lecture.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    @Test
    @Transactional
    void getLecture() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        // Get the lecture
        restLectureMockMvc
            .perform(get(ENTITY_API_URL_ID, lecture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lecture.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingLecture() throws Exception {
        // Get the lecture
        restLectureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLecture() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();

        // Update the lecture
        Lecture updatedLecture = lectureRepository.findById(lecture.getId()).get();
        // Disconnect from session so that the updates on updatedLecture are not directly saved in db
        em.detach(updatedLecture);
        updatedLecture.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restLectureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLecture.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLecture))
            )
            .andExpect(status().isOk());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
        Lecture testLecture = lectureList.get(lectureList.size() - 1);
        assertThat(testLecture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLecture.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLecture.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testLecture.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void putNonExistingLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lecture.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lecture))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lecture))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lecture)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLectureWithPatch() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();

        // Update the lecture using partial update
        Lecture partialUpdatedLecture = new Lecture();
        partialUpdatedLecture.setId(lecture.getId());

        partialUpdatedLecture.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).endDate(UPDATED_END_DATE);

        restLectureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLecture.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLecture))
            )
            .andExpect(status().isOk());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
        Lecture testLecture = lectureList.get(lectureList.size() - 1);
        assertThat(testLecture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLecture.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLecture.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testLecture.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateLectureWithPatch() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();

        // Update the lecture using partial update
        Lecture partialUpdatedLecture = new Lecture();
        partialUpdatedLecture.setId(lecture.getId());

        partialUpdatedLecture.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restLectureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLecture.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLecture))
            )
            .andExpect(status().isOk());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
        Lecture testLecture = lectureList.get(lectureList.size() - 1);
        assertThat(testLecture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLecture.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLecture.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testLecture.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lecture.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lecture))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lecture))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLecture() throws Exception {
        int databaseSizeBeforeUpdate = lectureRepository.findAll().size();
        lecture.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLectureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lecture)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lecture in the database
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLecture() throws Exception {
        // Initialize the database
        lectureRepository.saveAndFlush(lecture);

        int databaseSizeBeforeDelete = lectureRepository.findAll().size();

        // Delete the lecture
        restLectureMockMvc
            .perform(delete(ENTITY_API_URL_ID, lecture.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lecture> lectureList = lectureRepository.findAll();
        assertThat(lectureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
