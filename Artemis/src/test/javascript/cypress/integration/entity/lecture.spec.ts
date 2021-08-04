import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Lecture e2e test', () => {
  const lecturePageUrl = '/lecture';
  const lecturePageUrlPattern = new RegExp('/lecture(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/lecture/api/lectures+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/lecture/api/lectures').as('postEntityRequest');
    cy.intercept('DELETE', '/services/lecture/api/lectures/*').as('deleteEntityRequest');
  });

  it('should load Lectures', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('lecture');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Lecture').should('exist');
    cy.url().should('match', lecturePageUrlPattern);
  });

  it('should load details Lecture page', function () {
    cy.visit(lecturePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('lecture');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', lecturePageUrlPattern);
  });

  it('should load create Lecture page', () => {
    cy.visit(lecturePageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Lecture');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', lecturePageUrlPattern);
  });

  it('should load edit Lecture page', function () {
    cy.visit(lecturePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('Lecture');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', lecturePageUrlPattern);
  });

  it('should create an instance of Lecture', () => {
    cy.visit(lecturePageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Lecture');

    cy.get(`[data-cy="title"]`).type('Iowa Paradigm').should('have.value', 'Iowa Paradigm');

    cy.get(`[data-cy="description"]`).type('synthesize').should('have.value', 'synthesize');

    cy.get(`[data-cy="startDate"]`).type('2021-08-04T00:03').should('have.value', '2021-08-04T00:03');

    cy.get(`[data-cy="endDate"]`).type('2021-08-04T01:23').should('have.value', '2021-08-04T01:23');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', lecturePageUrlPattern);
  });

  it('should delete last instance of Lecture', function () {
    cy.visit(lecturePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.getEntityDeleteDialogHeading('lecture').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', lecturePageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
