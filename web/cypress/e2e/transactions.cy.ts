import { createTestUser, loginThroughUi } from "../support/api";

const ticker = "MSFT";
const apiTimeout = 30000;

function buyStock(quantity: string) {
  cy.get("[data-cy='portfolio-open-buy-dialog']").should("be.visible").click();
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

describe("Transactions", () => {
  it("muestra las operaciones de compra en el historial", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);

      cy.get("[data-cy='nav-portfolio']").click();
      cy.get("[data-cy='portfolio-title']").should("be.visible");

      buyStock("10");

      cy.contains("Volver al inicio").click();
      cy.get("[data-cy='nav-transactions']").click();

      cy.get("[data-cy='transactions-title']").should("be.visible");
      cy.get(`[data-cy='transaction-BUY-${ticker}']`, {
        timeout: apiTimeout,
      })
        .should("be.visible")
        .and("contain", "Compra")
        .and("contain", ticker);
    });
  });
});
