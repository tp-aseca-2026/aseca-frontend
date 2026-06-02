import { createTestUser, loginThroughUi } from "../support/api";

describe("Update prices", () => {
  it("permite actualizar los últimos precios desde home", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);

      cy.contains("Snapshots").should("be.visible");
      cy.get("[data-cy='update-prices-button']")
        .should("be.visible")
        .and("contain", "Actualizar precios")
        .click();

      cy.get("[data-cy='update-prices-button']", { timeout: 60000 })
        .should("be.visible")
        .and("contain", "Actualizar precios");
      cy.contains("Snapshots").should("be.visible");
    });
  });
});
