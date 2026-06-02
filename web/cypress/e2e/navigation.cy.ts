import { createTestUser, loginThroughUi } from "../support/api";

describe("Main navigation", () => {
  it("navega por las secciones principales de la app", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);

      cy.get("[data-cy='nav-portfolio']").click();
      cy.url().should("include", "/portfolio");
      cy.get("[data-cy='portfolio-title']").should("be.visible");
      cy.contains("Volver al inicio").click();

      cy.get("[data-cy='nav-watchlist']").click();
      cy.url().should("include", "/watchlist");
      cy.get("[data-cy='watchlist-title']").should("be.visible");
      cy.contains("Volver al inicio").click();

      cy.get("[data-cy='nav-edgar']").click();
      cy.url().should("include", "/edgar");
      cy.get("[data-cy='edgar-title']").should("be.visible");
      cy.contains("Volver al inicio").click();

      cy.get("[data-cy='nav-transactions']").click();
      cy.url().should("include", "/transactions");
      cy.get("[data-cy='transactions-title']").should("be.visible");
      cy.contains("Volver al inicio").click();

      cy.get("[data-cy='home-dashboard']").should("be.visible");
    });
  });
});
