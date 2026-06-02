import {
  buildTestUser,
  createTestUser,
  loginThroughUi,
  registerThroughUi,
} from "../support/api";

describe("Smoke", () => {
  it("muestra la pantalla de login", () => {
    cy.visit("/login");

    cy.get("[data-cy='email-input']").should("be.visible");
    cy.get("[data-cy='password-input']").should("be.visible");
    cy.get("[data-cy='submit-button']").should("be.visible");
    cy.get("[data-cy='go-to-register']").should("be.visible");
  });

  it("navega desde login a registro y vuelve", () => {
    cy.visit("/login");

    cy.get("[data-cy='go-to-register']").click();

    cy.url().should("include", "/register");
    cy.get("[data-cy='email-input']").should("be.visible");
    cy.get("[data-cy='password-input']").should("be.visible");
    cy.get("[data-cy='submit-button']").should("be.visible");

    cy.get("[data-cy='back-to-login']").click();

    cy.url().should("include", "/login");
    cy.get("[data-cy='email-input']").should("be.visible");
  });
});

describe("Register", () => {
  it("redirige a /home tras registro exitoso", () => {
    registerThroughUi(buildTestUser());
  });

  it("muestra el mensaje de error cuando el email ya está registrado", () => {
    createTestUser().then((user) => {
      cy.visit("/register");

      cy.get("[data-cy='email-input']").should("be.visible").type(user.email);
      cy.get("[data-cy='password-input']")
        .should("be.visible")
        .type(user.password);
      cy.get("[data-cy='submit-button']").should("be.visible").click();

      cy.get("[data-cy='error-message']")
        .should("be.visible")
        .invoke("text")
        .should("match", /email|registr|exist|already/i);

      cy.url().should("include", "/register");
    });
  });
});

describe("Login", () => {
  it("redirige a /home tras login exitoso", () => {
    createTestUser().then(loginThroughUi);
  });

  it("muestra el mensaje de error con credenciales incorrectas", () => {
    cy.visit("/login");

    cy.get("[data-cy='email-input']").type(`wrong${Date.now()}@aseca.com`);
    cy.get("[data-cy='password-input']").type("wrongpassword");
    cy.get("[data-cy='submit-button']").click();

    cy.get("[data-cy='error-message']").should("be.visible");
    cy.url().should("include", "/login");
  });
});

describe("Session persistence", () => {
  it("mantiene al usuario autenticado al recargar la app", () => {
    createTestUser().then((user) => {
      loginThroughUi(user);

      cy.reload();

      cy.get("[data-cy='home-dashboard']").should("be.visible");
      cy.url().should("include", "/home");
    });
  });
});
