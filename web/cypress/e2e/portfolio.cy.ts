const BASE_URL = "http://localhost:3000";

const STOCKS_MOCK = [
  { id: 1, ticker: "AAPL", companyName: "Apple Inc.", cik: null },
];

const EMPTY_PORTFOLIO = {
  positions: [],
  summary: {
    totalCostBasis: 0,
    currentValue: 0,
    unrealizedProfitLoss: 0,
    unrealizedProfitLossPercentage: 0,
    realizedProfitLoss: 0,
    totalProfitLoss: 0,
    lastPriceUpdatedAt: null,
  },
};

const PORTFOLIO_WITH_AAPL = {
  positions: [
    {
      stockId: 1,
      ticker: "AAPL",
      companyName: "Apple Inc.",
      quantity: 10,
      averageBuyPrice: 150,
      costBasis: 1500,
      latestPrice: 155,
      currentValue: 1550,
      unrealizedProfitLoss: 50,
      unrealizedProfitLossPercentage: 3.33,
      realizedProfitLoss: 0,
      totalProfitLoss: 50,
      lastPriceUpdatedAt: null,
    },
  ],
  summary: {
    totalCostBasis: 1500,
    currentValue: 1550,
    unrealizedProfitLoss: 50,
    unrealizedProfitLossPercentage: 3.33,
    realizedProfitLoss: 0,
    totalProfitLoss: 50,
    lastPriceUpdatedAt: null,
  },
};

function interceptDashboard() {
  cy.intercept("GET", `${BASE_URL}/transactions`, { body: [] }).as(
    "transactions"
  );
  cy.intercept("GET", `${BASE_URL}/price-snapshots/latest`, {
    body: { lastUpdatedAt: null, prices: [] },
  }).as("snapshots");
  cy.intercept("GET", `${BASE_URL}/stocks`, { body: STOCKS_MOCK }).as("stocks");
  cy.intercept("GET", `${BASE_URL}/watchlist`, { body: [] }).as("watchlist");
}

describe("Portfolio", () => {
  it("registra una compra exitosa y muestra la posición en el portfolio", () => {
    interceptDashboard();

    cy.intercept("GET", `${BASE_URL}/portfolio`, {
      body: PORTFOLIO_WITH_AAPL,
    }).as("portfolioWithPosition");

    cy.intercept(
      { method: "GET", url: `${BASE_URL}/portfolio`, times: 1 },
      { body: EMPTY_PORTFOLIO }
    ).as("portfolioEmpty");

    cy.intercept("POST", `${BASE_URL}/transactions/buy`, {
      statusCode: 201,
      body: {
        id: 1,
        stockId: 1,
        type: "BUY",
        quantity: 10,
        price: 150,
        executedAt: "2026-05-27T00:00:00.000Z",
      },
    }).as("buyTransaction");

    cy.visit("/home", {
      onBeforeLoad(win) {
        win.localStorage.setItem("accessToken", "test-token");
      },
    });

    cy.wait("@portfolioEmpty");

    cy.get("[data-cy='buy-button']").should("be.visible").click();
    cy.get("[data-cy='transaction-modal']").should("be.visible");
    cy.get("[data-cy='modal-ticker-select']").select("AAPL");
    cy.get("[data-cy='modal-quantity-input']").type("10");
    cy.get("[data-cy='modal-submit-button']").click();

    cy.wait("@buyTransaction");
    cy.wait("@portfolioWithPosition");

    cy.get("[data-cy='positions-list']").should("contain", "AAPL");
  });

  it("muestra el error del modal cuando la compra falla en el backend", () => {
    interceptDashboard();

    cy.intercept("GET", `${BASE_URL}/portfolio`, {
      body: EMPTY_PORTFOLIO,
    }).as("portfolio");

    cy.intercept("POST", `${BASE_URL}/transactions/buy`, {
      statusCode: 400,
      body: { message: "No se encontró precio para el ticker indicado" },
    }).as("buyTransaction");

    cy.visit("/home", {
      onBeforeLoad(win) {
        win.localStorage.setItem("accessToken", "test-token");
      },
    });

    cy.wait("@portfolio");

    cy.get("[data-cy='buy-button']").should("be.visible").click();
    cy.get("[data-cy='modal-ticker-select']").select("AAPL");
    cy.get("[data-cy='modal-quantity-input']").type("10");
    cy.get("[data-cy='modal-submit-button']").click();

    cy.wait("@buyTransaction");

    cy.get("[data-cy='modal-error']")
      .should("be.visible")
      .and("contain", "No se pudo registrar la compra");

    cy.get("[data-cy='transaction-modal']").should("be.visible");
  });
});
