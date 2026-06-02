import { API_URL, createTestUser, loginThroughUi } from "../support/api";

const ticker = "MSFT";
const apiTimeout = 30000;

describe("Watchlist", () => {
  it("agrega y elimina una acción de la watchlist", () => {
    createTestUser().then((user) => {
      cy.intercept("GET", `${API_URL}/watchlist`).as("getWatchlist");
      cy.intercept("POST", `${API_URL}/watchlist`).as("addToWatchlist");
      cy.intercept("DELETE", `${API_URL}/watchlist/${ticker}`).as(
        "removeFromWatchlist",
      );

      loginThroughUi(user);

      cy.get("[data-cy='nav-watchlist']").click();
      cy.url().should("include", "/watchlist");
      cy.get("[data-cy='watchlist-title']").should("be.visible");
      cy.wait("@getWatchlist", { timeout: apiTimeout })
        .its("response.statusCode")
        .should("be.oneOf", [200, 304]);

      cy.get("[data-cy='watchlist-stock-select']", { timeout: apiTimeout })
        .find(`option[value='${ticker}']`)
        .should("exist");
      cy.get("[data-cy='watchlist-stock-select']").select(ticker);
      cy.get("[data-cy='watchlist-add-button']").click();
      cy.wait("@addToWatchlist", { timeout: apiTimeout })
        .its("response.statusCode")
        .should("be.oneOf", [200, 201]);
      cy.wait("@getWatchlist", { timeout: apiTimeout })
        .its("response.statusCode")
        .should("be.oneOf", [200, 304]);

      cy.get(`[data-cy='watchlist-item-${ticker}']`, {
        timeout: apiTimeout,
      }).should("be.visible");
      cy.get("[data-cy='watchlist-error']").should("not.exist");

      cy.get(`[data-cy='watchlist-remove-${ticker}']`).click();
      cy.wait("@removeFromWatchlist", { timeout: apiTimeout })
        .its("response.statusCode")
        .should("be.oneOf", [200, 204]);
      cy.wait("@getWatchlist", { timeout: apiTimeout })
        .its("response.statusCode")
        .should("be.oneOf", [200, 304]);

      cy.get(`[data-cy='watchlist-item-${ticker}']`, {
        timeout: apiTimeout,
      }).should("not.exist");
    });
  });
});
