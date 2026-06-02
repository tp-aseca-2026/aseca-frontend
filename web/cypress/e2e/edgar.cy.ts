import { createTestUser, loginThroughUi } from "../support/api";

const ticker = "MSFT";

describe("EDGAR search", () => {
  it("busca una empresa y muestra métricas financieras", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);

      cy.get("[data-cy='nav-edgar']").click();
      cy.url().should("include", "/edgar");
      cy.get("[data-cy='edgar-title']").should("be.visible");

      cy.get("[data-cy='edgar-search-input']").type(ticker);
      cy.get("[data-cy='edgar-search-button']").click();

      cy.get(`[data-cy='edgar-company-${ticker}']`, { timeout: 20000 })
        .should("be.visible")
        .click();

      cy.get("[data-cy='edgar-metrics-title']", { timeout: 30000 }).should(
        "be.visible",
      );
      cy.get("[data-cy='edgar-historical-title']").should("be.visible");
      cy.contains("Métricas financieras").should("be.visible");
      cy.contains("Evolución histórica").should("be.visible");
    });
  });
});
