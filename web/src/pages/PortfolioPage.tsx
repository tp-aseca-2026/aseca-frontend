import { useEffect, useState } from "react";
import { Link } from "react-router";
import { getPortfolio, type PortfolioResponse } from "../api/portfolio";
import { buyTransaction, sellTransaction } from "../api/transactions";
import { getStocks, type Stock } from "../api/stocks";
import { TransactionModal } from "../components/portfolio/TransactionModal";

function money(value: number) {
  return `USD ${value.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export function PortfolioPage() {
  const [portfolio, setPortfolio] = useState<PortfolioResponse | null>(null);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);

  const [transactionModalOpen, setTransactionModalOpen] = useState(false);
  const [transactionMode, setTransactionMode] = useState<"buy" | "sell">("buy");
  const [selectedTicker, setSelectedTicker] = useState("");
  const [transactionLoading, setTransactionLoading] = useState(false);
  const [transactionError, setTransactionError] = useState<string | null>(null);

  const positions = portfolio?.positions ?? [];
  const summary = portfolio?.summary;

  useEffect(() => {
    loadPortfolio();
  }, []);

  async function loadPortfolio() {
    try {
      const [portfolioResult, stocksResult] = await Promise.all([
        getPortfolio(),
        getStocks(),
      ]);

      setPortfolio(portfolioResult);
      setStocks(stocksResult);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  function openBuyModal(defaultTicker = "") {
    setTransactionMode("buy");
    setSelectedTicker(defaultTicker);
    setTransactionError(null);
    setTransactionModalOpen(true);
  }

  function openSellModal(defaultTicker = "") {
    setTransactionMode("sell");
    setSelectedTicker(defaultTicker);
    setTransactionError(null);
    setTransactionModalOpen(true);
  }

  function closeTransactionModal() {
    setTransactionModalOpen(false);
    setSelectedTicker("");
    setTransactionError(null);
  }

  async function handleTransactionSubmit(
    ticker: string,
    quantity: number,
  ) {
    try {
      setTransactionLoading(true);
      setTransactionError(null);

      if (transactionMode === "buy") {
        await buyTransaction(ticker, quantity);
      } else {
        await sellTransaction(ticker, quantity);
      }

      await loadPortfolio();
      closeTransactionModal();
    } catch (error) {
      console.error(error);
      setTransactionError("No se pudo registrar la operación.");
    } finally {
      setTransactionLoading(false);
    }
  }

  return (
    <>
      <main
        style={{
          minHeight: "100vh",
          background: "#060a0f",
          color: "#e8edf3",
          padding: 48,
          fontFamily: "Inter, system-ui, sans-serif",
        }}
      >
        <section style={{ maxWidth: 1200, margin: "0 auto" }}>
          <Link
            to="/home"
            style={{
              color: "#7b8495",
              textDecoration: "none",
              fontWeight: 800,
              display: "inline-block",
              marginBottom: 32,
            }}
          >
            ← Volver al inicio
          </Link>

          <h1
            data-cy="portfolio-title"
            style={{
              margin: "0 0 32px",
              fontSize: 52,
              fontFamily: "Georgia, serif",
              fontWeight: 400,
            }}
          >
            Portfolio completo
          </h1>

          <section
            data-cy="portfolio-summary"
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(4, 1fr)",
              gap: 18,
              marginBottom: 28,
            }}
          >
            <SummaryCard
              title="Costo total"
              value={money(summary?.totalCostBasis ?? 0)}
            />
            <SummaryCard
              title="Valor actual"
              value={money(summary?.currentValue ?? 0)}
            />
            <SummaryCard
              title="P&L total"
              value={money(summary?.totalProfitLoss ?? 0)}
            />
            <SummaryCard
              title="P&L no realizado"
              value={money(summary?.unrealizedProfitLoss ?? 0)}
            />
          </section>

          <section
            style={{
              border: "1px solid #162235",
              borderRadius: 24,
              background: "#0c1017",
              overflow: "hidden",
            }}
          >
            <div
              style={{
                padding: "22px 24px",
                borderBottom: "1px solid #162235",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <h2 style={{ margin: 0, fontSize: 22 }}>
                Mis posiciones
              </h2>

              <button
                style={primaryButton}
                onClick={() => openBuyModal()}
                data-cy="portfolio-open-buy-dialog"
              >
                + Registrar compra
              </button>
            </div>

            <div style={{ padding: 24 }}>
              {loading ? (
                <p style={{ color: "#7b8495" }}>
                  Cargando portfolio...
                </p>
              ) : positions.length === 0 ? (
                <p style={{ color: "#7b8495" }}>
                  Todavía no tenés posiciones activas.
                </p>
              ) : (
                <table
                  data-cy="portfolio-positions"
                  style={{
                    width: "100%",
                    borderCollapse: "collapse",
                  }}
                >
                  <thead>
                    <tr>
                      {[
                        "Ticker",
                        "Cantidad",
                        "Precio compra",
                        "Precio actual",
                        "P&L",
                        "Acciones",
                      ].map((header) => (
                        <th
                          key={header}
                          style={{
                            textAlign: "left",
                            color: "#536079",
                            fontSize: 12,
                            textTransform: "uppercase",
                            letterSpacing: "0.12em",
                            paddingBottom: 16,
                          }}
                        >
                          {header}
                        </th>
                      ))}
                    </tr>
                  </thead>

                  <tbody>
                    {positions.map((position) => {
                      const pnlPercent =
                        position.unrealizedProfitLossPercentage ?? 0;

                      const positive = pnlPercent >= 0;

                      return (
                        <tr key={position.stockId}>
                          <td style={tdStyle}>
                            <strong
                              style={{ color: "#e8edf3" }}
                              data-cy={`portfolio-position-${position.ticker}`}
                            >
                              {position.ticker}
                            </strong>

                            <p
                              style={{
                                margin: "4px 0 0",
                                color: "#536079",
                              }}
                            >
                              {position.companyName ??
                                "Sin nombre registrado"}
                            </p>
                          </td>

                          <td style={tdStyle}>
                            {position.quantity}
                          </td>

                          <td style={tdStyle}>
                            {money(position.averageBuyPrice)}
                          </td>

                          <td style={tdStyle}>
                            {position.latestPrice !== null
                              ? money(position.latestPrice)
                              : "Sin precio"}
                          </td>

                          <td
                            style={{
                              ...tdStyle,
                              color: positive
                                ? "#00e676"
                                : "#ff5370",
                              fontWeight: 800,
                            }}
                          >
                            {positive ? "+" : ""}
                            {pnlPercent.toFixed(2)}%
                          </td>

                          <td style={tdStyle}>
                            <button
                              style={primaryButton}
                              onClick={() =>
                                openBuyModal(position.ticker)
                              }
                              data-cy={`portfolio-buy-more-${position.ticker}`}
                            >
                              Comprar más
                            </button>

                            <button
                              style={ghostButton}
                              onClick={() =>
                                openSellModal(position.ticker)
                              }
                              data-cy={`portfolio-sell-${position.ticker}`}
                            >
                              Vender
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              )}
            </div>
          </section>
        </section>
      </main>

      <TransactionModal
        open={transactionModalOpen}
        mode={transactionMode}
        stocks={stocks}
        defaultTicker={selectedTicker}
        loading={transactionLoading}
        error={transactionError}
        onClose={closeTransactionModal}
        onSubmit={handleTransactionSubmit}
      />
    </>
  );
}

const tdStyle = {
  padding: "16px 10px",
  color: "#7b8495",
  borderTop: "1px solid #162235",
  fontSize: 14,
};

function SummaryCard({ title, value }: { title: string; value: string }) {
  return (
    <div
      style={{
        border: "1px solid #162235",
        borderRadius: 22,
        padding: 22,
        background: "#0c1017",
      }}
    >
      <p style={{ margin: "0 0 12px", color: "#536079", fontSize: 14 }}>
        {title}
      </p>
      <p
        style={{
          margin: 0,
          color: "#e8edf3",
          fontSize: 28,
          fontWeight: 800,
        }}
      >
        {value}
      </p>
    </div>
  );
}

const primaryButton = {
  border: "none",
  borderRadius: 10,
  background: "#00e676",
  color: "#060a0f",
  padding: "9px 12px",
  fontWeight: 800,
  cursor: "pointer",
  marginRight: 8,
};

const ghostButton = {
  border: "1px solid #243044",
  borderRadius: 10,
  background: "rgba(255,255,255,0.03)",
  color: "#e8edf3",
  padding: "9px 12px",
  fontWeight: 800,
  cursor: "pointer",
};
