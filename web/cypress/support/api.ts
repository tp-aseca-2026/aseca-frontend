export const API_URL = Cypress.env("apiUrl") ?? "http://localhost:3000";

export type TestUser = {
  email: string;
  password: string;
};

export function createTestUser(): Cypress.Chainable<TestUser> {
  const user = {
    email: `cypress${Date.now()}${Math.floor(Math.random() * 100000)}@aseca.com`,
    password: "Password123",
  };

  return cy
    .request("POST", `${API_URL}/auth/register`, user)
    .then(() => user);
}

export function loginThroughUi(user: TestUser) {
  cy.visit("/login");
  cy.get("[data-cy='email-input']").should("be.visible").type(user.email);
  cy.get("[data-cy='password-input']").should("be.visible").type(user.password);
  cy.get("[data-cy='submit-button']").should("be.visible").click();
  cy.url().should("include", "/home");
  cy.get("[data-cy='home-dashboard']").should("be.visible");
}

export function registerThroughUi(user: TestUser) {
  cy.visit("/register");
  cy.get("[data-cy='email-input']").should("be.visible").type(user.email);
  cy.get("[data-cy='password-input']").should("be.visible").type(user.password);
  cy.get("[data-cy='submit-button']").should("be.visible").click();
  cy.url().should("include", "/home");
  cy.get("[data-cy='home-dashboard']").should("be.visible");
}

export function buildTestUser(): TestUser {
  return {
    email: `cypress${Date.now()}${Math.floor(Math.random() * 100000)}@aseca.com`,
    password: "Password123",
  };
}
