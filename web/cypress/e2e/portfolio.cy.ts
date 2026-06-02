import { createTestUser, loginThroughUi } from "../support/api";

const ticker = "MSFT";
const apiTimeout = 30000;

function goToPortfolio() {
  cy.get("[data-cy='nav-portfolio']").click();
  cy.url().should("include", "/portfolio");
  cy.get("[data-cy='portfolio-title']").should("be.visible");
}

function buyStock(quantity: string) {
  cy.get("[data-cy='portfolio-open-buy-dialog']").should("be.visible").click();
  cy.get("[data-cy='transaction-modal']").should("be.visible");
  cy.get("[data-cy='modal-ticker-select']", { timeout: apiTimeout })
    .find(`option[value='${ticker}']`)
    .should("exist");
  cy.get("[data-cy='modal-ticker-select']").select(ticker);
  cy.get("[data-cy='modal-quantity-input']").clear().type(quantity);
  cy.get("[data-cy='modal-submit-button']").click();
  cy.get("[data-cy='transaction-modal']", { timeout: apiTimeout }).should(
    "not.exist",
  );
}

describe("Portfolio", () => {
  it("compra una acción y la muestra como posición activa", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);
      goToPortfolio();

      buyStock("10");

      cy.get(`[data-cy='portfolio-position-${ticker}']`, {
        timeout: apiTimeout,
      }).should("be.visible");
    });
  });

  it("vende parte de una posición activa", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);
      goToPortfolio();

      buyStock("10");

      cy.get(`[data-cy='portfolio-sell-${ticker}']`, {
        timeout: apiTimeout,
      })
        .should("be.visible")
        .click();
      cy.get("[data-cy='transaction-modal']").should("be.visible");
      cy.get("[data-cy='modal-ticker-select']").should("have.value", ticker);
      cy.get("[data-cy='modal-quantity-input']").clear().type("4");
      cy.get("[data-cy='modal-submit-button']").click();
      cy.get("[data-cy='transaction-modal']", { timeout: apiTimeout }).should(
        "not.exist",
      );

      cy.get(`[data-cy='portfolio-position-${ticker}']`, {
        timeout: apiTimeout,
      }).should("be.visible");
      cy.get("[data-cy='portfolio-positions']").should("contain", "6");
    });
  });

  it("muestra resumen y P&L después de comprar una acción", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);
      goToPortfolio();

      buyStock("10");

      cy.get("[data-cy='portfolio-summary']")
        .should("be.visible")
        .and("contain", "Costo total")
        .and("contain", "Valor actual")
        .and("contain", "P&L total")
        .and("contain", "P&L no realizado");
    });
  });
});
