import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { api } from "../api/axios";
import { getPortfolio, type PortfolioResponse } from "../api/portfolio";
import { buyTransaction, sellTransaction } from "../api/transactions";
import { TransactionModal } from "../components/portfolio/TransactionModal";
import { getStocks, type Stock } from "../api/stocks";
import { getWatchlist, type WatchlistItem } from "../api/watchlist";
import {
  getLatestPriceSnapshots,
  updatePriceSnapshots,
  type PriceSnapshot,
} from "../api/priceSnapshots";

type Transaction = {
  id?: number;
  stockId: number;
  ticker?: string;
  quantity: number;
  type?: "BUY" | "SELL";
  executedAt?: string;
  createdAt?: string;
  price?: number | string;
};

function money(value: number | string) {
  const numericValue = Number(value);

  return `USD ${numericValue.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

function getSnapshotDate(snapshots: PriceSnapshot[]) {
  if (!snapshots.length) return "Sin actualización registrada";

  const first = snapshots[0];
  const rawDate = first.updatedAt || first.createdAt || first.timestamp;

  if (!rawDate) return "Sin timestamp disponible";

  return new Date(rawDate).toLocaleString("es-AR");
}

export function HomePage() {
  const navigate = useNavigate();

  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [snapshots, setSnapshots] = useState<PriceSnapshot[]>([]);
  const [portfolio, setPortfolio] = useState<PortfolioResponse | null>(null);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [watchlist, setWatchlist] = useState<WatchlistItem[]>([]);

  const [loading, setLoading] = useState(true);
  const [transactionModalOpen, setTransactionModalOpen] = useState(false);
  const [transactionMode, setTransactionMode] = useState<"buy" | "sell">("buy");
  const [selectedTicker, setSelectedTicker] = useState("");
  const [transactionLoading, setTransactionLoading] = useState(false);
  const [updatingPrices, setUpdatingPrices] = useState(false);
  const [transactionError, setTransactionError] = useState<string | null>(null);

  const summary = portfolio?.summary;
  const positions = portfolio?.positions ?? [];

  const totalValue = summary?.currentValue ?? 0;
  const totalPnL = summary?.totalProfitLoss ?? 0;
  const totalPnLPercent = summary?.unrealizedProfitLossPercentage ?? 0;
  const positiveTotalPnL = totalPnL >= 0;

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    if (!token) {
      navigate("/login");
      return;
    }

    loadDashboardData();
  }, []);

  async function loadDashboardData() {
    try {
      const [
        transactionsResponse,
        snapshotsResponse,
        portfolioResponse,
        stocksResponse,
        watchlistResponse,
      ] = await Promise.allSettled([
        api.get<Transaction[]>("/transactions"),
        getLatestPriceSnapshots(),
        getPortfolio(),
        getStocks(),
        getWatchlist(),
      ]);

      if (transactionsResponse.status === "fulfilled") {
        setTransactions(transactionsResponse.value.data);
      }

      if (snapshotsResponse.status === "fulfilled") {
        setSnapshots(snapshotsResponse.value.prices);
      }

      if (portfolioResponse.status === "fulfilled") {
        setPortfolio(portfolioResponse.value);
      }

      if (stocksResponse.status === "fulfilled") {
        setStocks(stocksResponse.value);
      }
      if (watchlistResponse.status === "fulfilled") {
        setWatchlist(watchlistResponse.value);
      }
    } catch (error) {
      console.log("Dashboard error:", error);
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem("accessToken");
    navigate("/login");
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

  async function handleTransactionSubmit(ticker: string, quantity: number) {
    if (!ticker) {
      setTransactionError("Ingresá un ticker válido.");
      return;
    }

    if (!Number.isInteger(quantity) || quantity <= 0) {
      setTransactionError("La cantidad debe ser un número entero positivo.");
      return;
    }

    try {
      setTransactionLoading(true);
      setTransactionError(null);

      if (transactionMode === "buy") {
        await buyTransaction(ticker, quantity);
      } else {
        await sellTransaction(ticker, quantity);
      }

      await loadDashboardData();
      closeTransactionModal();
    } catch (error) {
      console.error(error);
      setTransactionError(
        transactionMode === "buy"
          ? "No se pudo registrar la compra. Verificá que el ticker exista y tenga precio actualizado."
          : "No se pudo registrar la venta. Verificá que tengas acciones suficientes.",
      );
    } finally {
      setTransactionLoading(false);
    }
  }

  async function handleUpdatePrices() {
    try {
      setUpdatingPrices(true);

      const tickers = stocks.map((stock) => stock.ticker);

      const result = await updatePriceSnapshots(tickers);

      if (result.saved === 0 && result.failed.length > 0) {
        alert(`No se pudieron actualizar los precios: ${result.failed[0].error}`);
        return;
      }

      await loadDashboardData();
    } catch (error) {
      console.error(error);

      alert("No se pudieron actualizar los precios.");
    } finally {
      setUpdatingPrices(false);
    }
  }

  return (
    <>
      <style>
        {`
          @keyframes fadeUp {
            from {
              opacity: 0;
              transform: translateY(14px);
            }
            to {
              opacity: 1;
              transform: translateY(0);
            }
          }

          @keyframes tickerMove {
            from { transform: translateX(0); }
            to { transform: translateX(-50%); }
          }
        `}
      </style>

      <main
        style={{
          width: "100vw",
          minHeight: "100vh",
          background: "#060a0f",
          color: "#e8edf3",
          fontFamily: "Inter, system-ui, sans-serif",
          position: "relative",
          overflowX: "hidden",
        }}
      >
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundImage:
              "linear-gradient(rgba(0,230,118,0.035) 1px, transparent 1px), linear-gradient(90deg, rgba(0,230,118,0.035) 1px, transparent 1px)",
            backgroundSize: "56px 56px",
            pointerEvents: "none",
          }}
        />

        <div
          style={{
            position: "fixed",
            width: 640,
            height: 640,
            borderRadius: "50%",
            background: "rgba(0,230,118,0.08)",
            filter: "blur(120px)",
            top: -240,
            left: -180,
            pointerEvents: "none",
          }}
        />

        <section
          style={{
            position: "relative",
            zIndex: 1,
            padding: "36px 56px 56px",
          }}
        >
          <header
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: 42,
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <div
                style={{
                  width: 42,
                  height: 42,
                  borderRadius: 12,
                  background: "#00e676",
                  color: "#060a0f",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontWeight: 800,
                  fontSize: 14,
                }}
              >
                $P
              </div>

              <p
                style={{
                  margin: 0,
                  fontSize: 25,
                  fontFamily: "Georgia, serif",
                  color: "#e8edf3",
                }}
              >
                Stock
                <span style={{ color: "#00e676", fontWeight: 700 }}>Folio</span>
              </p>
            </div>

            <nav
              style={{
                display: "flex",
                alignItems: "center",
                gap: 22,
                color: "#7b8495",
                fontSize: 14,
                fontWeight: 600,
              }}
            >
              <span>Portfolio</span>
              <span>Transacciones</span>
              <span>Buscar empresa</span>
              <span>Watchlist</span>

              <button
                onClick={logout}
                style={{
                  border: "1px solid #243044",
                  borderRadius: 12,
                  background: "rgba(255,255,255,0.03)",
                  color: "#e8edf3",
                  padding: "10px 14px",
                  fontWeight: 700,
                  cursor: "pointer",
                }}
              >
                Cerrar sesión
              </button>
            </nav>
          </header>

          <section
            style={{
              display: "grid",
              gridTemplateColumns: "1.25fr 0.75fr",
              gap: 28,
              alignItems: "stretch",
              marginBottom: 28,
              animation: "fadeUp 0.45s ease both",
            }}
          >
            <div
              style={{
                border: "1px solid #162235",
                borderRadius: 28,
                padding: 34,
                background:
                  "linear-gradient(135deg, rgba(0,230,118,0.09), rgba(255,255,255,0.025))",
                minHeight: 260,
              }}
            >
              <p
                style={{
                  margin: "0 0 18px",
                  color: "#00e676",
                  textTransform: "uppercase",
                  letterSpacing: "0.2em",
                  fontSize: 12,
                  fontWeight: 800,
                }}
              >
                Dashboard
              </p>

              <h1
                style={{
                  margin: 0,
                  fontSize: 54,
                  lineHeight: 1.05,
                  fontFamily: "Georgia, serif",
                  fontWeight: 400,
                  color: "#f3f6fb",
                  maxWidth: 720,
                }}
              >
                Estado actual de tu{" "}
                <span style={{ color: "#00e676", fontWeight: 700 }}>
                  portfolio
                </span>
              </h1>

              <p
                style={{
                  margin: "22px 0 0",
                  color: "#7b8495",
                  fontSize: 17,
                  lineHeight: 1.7,
                  maxWidth: 620,
                }}
              >
                Resumen de posiciones, precios almacenados, P&L y últimas
                operaciones registradas.
              </p>

              <p
                style={{
                  margin: "28px 0 0",
                  color: "#536079",
                  fontSize: 14,
                }}
              >
                Última actualización de precios:{" "}
                <span style={{ color: "#e8edf3", fontWeight: 700 }}>
                  {summary?.lastPriceUpdatedAt
                    ? new Date(summary.lastPriceUpdatedAt).toLocaleString(
                        "es-AR",
                      )
                    : getSnapshotDate(snapshots)}
                </span>
              </p>
            </div>

            <div
              style={{
                border: "1px solid #162235",
                borderRadius: 28,
                padding: 28,
                background: "#0c1017",
              }}
            >
              <p
                style={{
                  margin: "0 0 10px",
                  color: "#7b8495",
                  fontSize: 14,
                }}
              >
                Valor total del portfolio
              </p>

              <h2
                style={{
                  margin: 0,
                  fontSize: 42,
                  color: "#f3f6fb",
                }}
              >
                {money(totalValue)}
              </h2>

              <div
                style={{
                  marginTop: 22,
                  padding: 18,
                  borderRadius: 18,
                  background: positiveTotalPnL
                    ? "rgba(0,230,118,0.08)"
                    : "rgba(255,83,112,0.08)",
                  border: positiveTotalPnL
                    ? "1px solid rgba(0,230,118,0.18)"
                    : "1px solid rgba(255,83,112,0.18)",
                }}
              >
                <p
                  style={{
                    margin: 0,
                    color: positiveTotalPnL ? "#00e676" : "#ff5370",
                    fontWeight: 800,
                  }}
                >
                  {positiveTotalPnL ? "+" : ""}
                  {money(totalPnL)}
                </p>
                <p style={{ margin: "6px 0 0", color: "#7b8495" }}>
                  {positiveTotalPnL ? "+" : ""}
                  {totalPnLPercent.toFixed(2)}% total
                </p>
              </div>
            </div>
          </section>

          <section
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(4, 1fr)",
              gap: 18,
              marginBottom: 28,
            }}
          >
            <MetricCard title="Valor actual" value={money(totalValue)} />
            <MetricCard
              title="Ganancia / pérdida"
              value={`${positiveTotalPnL ? "+" : ""}${totalPnLPercent.toFixed(
                2,
              )}%`}
              positive={positiveTotalPnL}
            />
            <MetricCard
              title="Posiciones"
              value={`${positions.length}`}
              subtitle="acciones activas"
            />
            <MetricCard
              title="Snapshots"
              value={`${snapshots.length || 0}`}
              subtitle="precios guardados"
            />
          </section>

          <section
            style={{
              display: "grid",
              gridTemplateColumns: "1.35fr 0.65fr",
              gap: 28,
              alignItems: "start",
            }}
          >
            <div
              style={{
                border: "1px solid #162235",
                borderRadius: 28,
                background: "#0c1017",
                overflow: "hidden",
              }}
            >
              <SectionHeader
                title="Mis posiciones"
                actionText="Ver portfolio completo"
                onAction={() => navigate("/portfolio")}
              />

              <div style={{ padding: 24 }} data-cy="positions-list">
                {loading ? (
                  <p style={{ color: "#7b8495" }}>Cargando portfolio...</p>
                ) : positions.length === 0 ? (
                  <p style={{ color: "#7b8495" }}>
                    Todavía no tenés posiciones activas.
                  </p>
                ) : (
                  <table
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
                      {positions.slice(0, 3).map((position) => {
                        const pnlPercent =
                          position.unrealizedProfitLossPercentage ?? 0;
                        const positive = pnlPercent >= 0;

                        return (
                          <tr key={position.stockId}>
                            <td style={tdStyle}>
                              <strong style={{ color: "#e8edf3" }}>
                                {position.ticker}
                              </strong>
                              <p
                                style={{
                                  margin: "4px 0 0",
                                  color: "#536079",
                                }}
                              >
                                {position.companyName ||
                                  "Sin nombre registrado"}
                              </p>
                            </td>

                            <td style={tdStyle}>{position.quantity}</td>

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
                                color: positive ? "#00e676" : "#ff5370",
                                fontWeight: 800,
                              }}
                            >
                              {positive ? "+" : ""}
                              {pnlPercent.toFixed(2)}%
                            </td>

                            <td style={tdStyle}>
                              <button
                                style={smallButton}
                                onClick={() => openBuyModal(position.ticker)}
                              >
                                Comprar más
                              </button>
                              <button
                                style={smallGhostButton}
                                onClick={() => openSellModal(position.ticker)}
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
            </div>

            <div style={{ display: "grid", gap: 22 }}>
              <div
                style={{
                  border: "1px solid #162235",
                  borderRadius: 28,
                  background: "#0c1017",
                  padding: 24,
                }}
              >
                <p
                  style={{
                    margin: "0 0 18px",
                    color: "#e8edf3",
                    fontSize: 20,
                    fontWeight: 800,
                  }}
                >
                  Acciones rápidas
                </p>

                <div style={{ display: "grid", gap: 12 }}>
                  <button
                    style={quickButton}
                    onClick={() => openBuyModal()}
                    data-cy="buy-button"
                  >
                    + Registrar compra
                  </button>

                  <button style={quickButton} onClick={() => openSellModal()}>
                    − Registrar venta
                  </button>

                  <button
                    style={quickButton}
                    onClick={() => navigate("/edgar")}
                  >
                    Buscar empresa en EDGAR
                  </button>
                  <button
                    style={quickButton}
                    onClick={handleUpdatePrices}
                    disabled={updatingPrices}
                  >
                    {updatingPrices
                      ? "Actualizando precios..."
                      : "Actualizar precios"}
                  </button>
                </div>
              </div>

              <div
                onClick={() => navigate("/watchlist")}
                style={{
                  border: "1px solid #162235",
                  borderRadius: 28,
                  background: "#0c1017",
                  padding: 24,
                  cursor: "pointer",
                }}
              >
                <p
                  style={{
                    margin: "0 0 8px",
                    color: "#e8edf3",
                    fontSize: 20,
                    fontWeight: 800,
                  }}
                >
                  Watchlist
                </p>

                <p
                  style={{
                    margin: "0 0 18px",
                    color: "#536079",
                    fontSize: 14,
                    lineHeight: 1.5,
                  }}
                >
                  Empresas guardadas para seguimiento.
                </p>

                {watchlist.length === 0 ? (
                  <p style={{ margin: 0, color: "#7b8495", fontSize: 14 }}>
                    Todavía no agregaste empresas.
                  </p>
                ) : (
                  <div style={{ display: "flex", flexWrap: "wrap", gap: 10 }}>
                    {watchlist.map((item) => (
                      <span
                        key={item.id}
                        style={{
                          padding: "8px 12px",
                          borderRadius: 999,
                          background: "rgba(0,230,118,0.08)",
                          border: "1px solid rgba(0,230,118,0.18)",
                          color: "#00e676",
                          fontWeight: 800,
                          fontSize: 13,
                        }}
                      >
                        {item.stock.ticker}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </section>

          <section
            style={{
              marginTop: 28,
              border: "1px solid #162235",
              borderRadius: 28,
              background: "#0c1017",
              overflow: "hidden",
            }}
          >
            <SectionHeader
              title="Últimas transacciones"
              actionText="Ver más"
              onAction={() => navigate("/transactions")}
            />

            <div style={{ padding: 24 }}>
              {loading ? (
                <p style={{ color: "#7b8495" }}>Cargando operaciones...</p>
              ) : transactions.length === 0 ? (
                <p style={{ color: "#7b8495" }}>
                  Todavía no hay transacciones registradas.
                </p>
              ) : (
                <div style={{ display: "grid", gap: 12 }}>
                  {transactions.slice(0, 3).map((transaction, index) => {
                    const stock = stocks.find(
                      (stock) => stock.id === transaction.stockId,
                    );
                    const ticker = stock?.ticker ?? transaction.ticker ?? "";

                    return (
                      <div
                        key={transaction.id || index}
                        style={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          padding: 18,
                          borderRadius: 18,
                          background: "#060a0f",
                          border: "1px solid #162235",
                        }}
                      >
                        <div>
                          <p
                            style={{
                              margin: 0,
                              color: "#e8edf3",
                              fontWeight: 800,
                            }}
                          >
                            {transaction.type === "BUY"
                              ? "Compra"
                              : transaction.type === "SELL"
                                ? "Venta"
                                : "Operación"}{" "}
                            {ticker}
                          </p>

                          <p
                            style={{
                              margin: "6px 0 0",
                              color: "#536079",
                              fontSize: 14,
                            }}
                          >
                            {transaction.quantity} acciones
                            {transaction.price
                              ? ` · ${money(transaction.price)}`
                              : ""}
                          </p>
                        </div>

                        <p
                          style={{ margin: 0, color: "#7b8495", fontSize: 14 }}
                        >
                          {transaction.executedAt
                            ? new Date(transaction.executedAt).toLocaleString(
                                "es-AR",
                              )
                            : "Sin fecha"}
                        </p>
                      </div>
                    );
                  })}
                </div>
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

function MetricCard({
  title,
  value,
  subtitle,
  positive = false,
}: {
  title: string;
  value: string;
  subtitle?: string;
  positive?: boolean;
}) {
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
          color: positive ? "#00e676" : "#e8edf3",
          fontSize: 30,
          fontWeight: 800,
        }}
      >
        {value}
      </p>

      {subtitle && (
        <p style={{ margin: "8px 0 0", color: "#4a5568", fontSize: 13 }}>
          {subtitle}
        </p>
      )}
    </div>
  );
}

function SectionHeader({
  title,
  actionText,
  onAction,
}: {
  title: string;
  actionText?: string;
  onAction?: () => void;
}) {
  return (
    <div
      style={{
        padding: "22px 24px",
        borderBottom: "1px solid #162235",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <h2 style={{ margin: 0, color: "#e8edf3", fontSize: 22 }}>{title}</h2>

      {actionText && (
        <button style={smallButton} onClick={onAction}>
          {actionText}
        </button>
      )}
    </div>
  );
}

const tdStyle = {
  padding: "16px 10px",
  color: "#7b8495",
  borderTop: "1px solid #162235",
  fontSize: 14,
};

const smallButton = {
  border: "none",
  borderRadius: 10,
  background: "#00e676",
  color: "#060a0f",
  padding: "9px 12px",
  fontWeight: 800,
  cursor: "pointer",
  marginRight: 8,
};

const smallGhostButton = {
  border: "1px solid #243044",
  borderRadius: 10,
  background: "rgba(255,255,255,0.03)",
  color: "#e8edf3",
  padding: "9px 12px",
  fontWeight: 800,
  cursor: "pointer",
};

const quickButton = {
  width: "100%",
  border: "1px solid #243044",
  borderRadius: 14,
  background: "rgba(255,255,255,0.03)",
  color: "#e8edf3",
  padding: "14px 16px",
  fontWeight: 800,
  cursor: "pointer",
  textAlign: "left" as const,
};
