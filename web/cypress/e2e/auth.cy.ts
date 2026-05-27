describe("Register", () => {
  it("redirige a /home tras registro exitoso", () => {
    cy.intercept("POST", "http://localhost:3000/auth/register", {
      statusCode: 201,
      body: {},
    }).as("register");

    cy.intercept("POST", "http://localhost:3000/auth/login", {
      statusCode: 200,
      body: { accessToken: "test-token" },
    }).as("login");

    cy.visit("/register");

    cy.get("[data-cy='email-input']").should("be.visible").type("nuevo@test.com");
    cy.get("[data-cy='password-input']").should("be.visible").type("password123");
    cy.get("[data-cy='submit-button']").should("be.visible").click();

    cy.wait("@register");
    cy.wait("@login");

    cy.url().should("include", "/home");
  });

  it("muestra el mensaje de error cuando el email ya está registrado", () => {
    cy.intercept("POST", "http://localhost:3000/auth/register", {
      statusCode: 409,
      body: { message: "El email ya está registrado" },
    }).as("register");

    cy.visit("/register");

    cy.get("[data-cy='email-input']").should("be.visible").type("existente@test.com");
    cy.get("[data-cy='password-input']").should("be.visible").type("password123");
    cy.get("[data-cy='submit-button']").should("be.visible").click();

    cy.wait("@register");

    cy.get("[data-cy='error-message']")
      .should("be.visible")
      .and("contain", "El email ya está registrado");

    cy.url().should("include", "/register");
  });
});

describe("Login", () => {
  it("redirige a /home tras login exitoso", () => {
    cy.intercept("POST", "http://localhost:3000/auth/login", {
      statusCode: 200,
      body: { accessToken: "test-token" },
    }).as("login");

    cy.visit("/login");

    cy.get("[data-cy='email-input']").type("usuario@test.com");
    cy.get("[data-cy='password-input']").type("password123");
    cy.get("[data-cy='submit-button']").click();

    cy.wait("@login");

    cy.url().should("include", "/home");
  });

  it("muestra el mensaje de error con credenciales incorrectas", () => {
    cy.intercept("POST", "http://localhost:3000/auth/login", {
      statusCode: 401,
      body: { message: "Email o contraseña incorrectos" },
    }).as("login");

    cy.visit("/login");

    cy.get("[data-cy='email-input']").type("usuario@test.com");
    cy.get("[data-cy='password-input']").type("wrongpassword");
    cy.get("[data-cy='submit-button']").click();

    cy.wait("@login");

    cy.get("[data-cy='error-message']")
      .should("be.visible")
      .and("contain", "Email o contraseña incorrectos");

    cy.url().should("include", "/login");
  });
});
