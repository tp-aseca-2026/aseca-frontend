import { useEffect, useState } from "react";
import { Link } from "react-router";
import {
  addToWatchlist,
  getWatchlist,
  getWatchlistComparison,
  removeFromWatchlist,
  type WatchlistComparisonItem,
  type WatchlistItem,
} from "../api/watchlist";
import { getStocks, type Stock } from "../api/stocks";

export function WatchlistPage() {
  const [items, setItems] = useState<WatchlistItem[]>([]);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [comparison, setComparison] = useState<WatchlistComparisonItem[]>([]);
  const [selectedTicker, setSelectedTicker] = useState("");
  const [loading, setLoading] = useState(true);
  const [loadingComparison, setLoadingComparison] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadWatchlist();
  }, []);

  async function loadWatchlist() {
    try {
      setLoading(true);
      setError("");

      const [watchlistResult, stocksResult] = await Promise.all([
        getWatchlist(),
        getStocks(),
      ]);

      setItems(watchlistResult);
      setStocks(stocksResult);

      await loadComparison();
    } catch (error) {
      console.error(error);
      setError("No pudimos cargar la watchlist.");
    } finally {
      setLoading(false);
    }
  }

  async function loadComparison() {
    try {
      setLoadingComparison(true);

      const result = await getWatchlistComparison();
      setComparison(result);
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingComparison(false);
    }
  }

  async function handleAdd() {
    if (!selectedTicker) {
      setError("Seleccioná una acción.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");

      await addToWatchlist(selectedTicker);
      setSelectedTicker("");
      await loadWatchlist();
    } catch (error) {
      console.error(error);
      setError("No se pudo agregar. Puede que ya esté en tu watchlist.");
    } finally {
      setActionLoading(false);
    }
  }

  async function handleRemove(ticker: string) {
    try {
      setActionLoading(true);
      setError("");

      await removeFromWatchlist(ticker);
      await loadWatchlist();
    } catch (error) {
      console.error(error);
      setError("No se pudo eliminar de la watchlist.");
    } finally {
      setActionLoading(false);
    }
  }

  const tickersInWatchlist = new Set(items.map((item) => item.stock.ticker));
  const availableStocks = stocks.filter(
    (stock) => !tickersInWatchlist.has(stock.ticker),
  );

  return (
    <main
      style={{
        minHeight: "100vh",
        background: "#060a0f",
        color: "#e8edf3",
        padding: 48,
        fontFamily: "Inter, system-ui, sans-serif",
      }}
    >
      <section style={{ maxWidth: 1100, margin: "0 auto" }}>
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
          style={{
            margin: "0 0 12px",
            fontSize: 52,
            fontFamily: "Georgia, serif",
            fontWeight: 400,
          }}
        >
          Watchlist
        </h1>

        <p
          style={{
            margin: "0 0 32px",
            color: "#7b8495",
            fontSize: 18,
            lineHeight: 1.6,
          }}
        >
          Guardá empresas que querés seguir sin necesidad de tener posiciones
          abiertas.
        </p>

        <section
          style={{
            border: "1px solid #162235",
            borderRadius: 24,
            background: "#0c1017",
            padding: 24,
            marginBottom: 28,
          }}
        >
          <h2 style={{ margin: "0 0 18px", fontSize: 22 }}>Agregar empresa</h2>

          <div style={{ display: "flex", gap: 12 }}>
            <select
              value={selectedTicker}
              onChange={(event) => setSelectedTicker(event.target.value)}
              style={inputStyle}
            >
              <option value="">Seleccioná una acción</option>

              {availableStocks.map((stock) => (
                <option key={stock.id} value={stock.ticker}>
                  {stock.ticker} — {stock.companyName ?? "Sin nombre"}
                </option>
              ))}
            </select>

            <button
              onClick={handleAdd}
              disabled={actionLoading}
              style={primaryButton}
            >
              {actionLoading ? "Guardando..." : "+ Agregar"}
            </button>
          </div>

          {error && <p style={errorStyle}>{error}</p>}
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
            }}
          >
            <h2 style={{ margin: 0, fontSize: 22 }}>Empresas guardadas</h2>
          </div>

          <div style={{ padding: 24 }}>
            {loading ? (
              <p style={{ color: "#7b8495" }}>Cargando watchlist...</p>
            ) : items.length === 0 ? (
              <p style={{ color: "#7b8495" }}>
                Todavía no agregaste empresas a tu watchlist.
              </p>
            ) : (
              <div style={{ display: "grid", gap: 14 }}>
                {items.map((item) => (
                  <div
                    key={item.id}
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      gap: 18,
                      padding: 18,
                      borderRadius: 18,
                      background: "#060a0f",
                      border: "1px solid #162235",
                    }}
                  >
                    <div>
                      <strong style={{ fontSize: 18 }}>
                        {item.stock.ticker}
                      </strong>
                      <p
                        style={{
                          margin: "6px 0 0",
                          color: "#7b8495",
                          fontSize: 14,
                        }}
                      >
                        {item.stock.companyName ?? "Sin nombre registrado"}
                      </p>
                    </div>

                    <button
                      onClick={() => handleRemove(item.stock.ticker)}
                      disabled={actionLoading}
                      style={dangerButton}
                    >
                      Eliminar
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>

        <section
          style={{
            border: "1px solid #162235",
            borderRadius: 24,
            background: "#0c1017",
            overflow: "hidden",
            marginTop: 28,
          }}
        >
          <div
            style={{
              padding: "22px 24px",
              borderBottom: "1px solid #162235",
            }}
          >
            <h2 style={{ margin: 0, fontSize: 22 }}>Comparación financiera</h2>
            <p style={{ margin: "8px 0 0", color: "#7b8495", fontSize: 14 }}>
              Métricas principales de las empresas guardadas en tu watchlist.
            </p>
          </div>

          <div style={{ padding: 24 }}>
            {loadingComparison ? (
              <p style={{ color: "#7b8495" }}>Cargando comparación...</p>
            ) : comparison.length === 0 ? (
              <p style={{ color: "#7b8495" }}>
                Agregá empresas a tu watchlist para compararlas.
              </p>
            ) : (
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                  <tr>
                    {[
                      "Ticker",
                      "Revenue",
                      "Net Income",
                      "EPS",
                      "Assets",
                      "Liabilities",
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
                  {comparison.map((item) => (
                    <tr key={item.ticker}>
                      <td style={tdStyle}>
                        <strong style={{ color: "#e8edf3" }}>
                          {item.ticker}
                        </strong>
                        <p style={{ margin: "4px 0 0", color: "#536079" }}>
                          {item.companyName ?? "Sin nombre"}
                        </p>
                      </td>
                      <td style={tdStyle}>{formatMoney(item.revenue?.val)}</td>
                      <td style={tdStyle}>
                        {formatMoney(item.netIncome?.val)}
                      </td>
                      <td style={tdStyle}>{formatNumber(item.eps?.val)}</td>
                      <td style={tdStyle}>
                        {formatMoney(item.totalAssets?.val)}
                      </td>
                      <td style={tdStyle}>
                        {formatMoney(item.totalLiabilities?.val)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </section>
      </section>
    </main>
  );
}

function formatMoney(value?: number) {
  if (value == null) return "No disponible";

  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    notation: "compact",
    maximumFractionDigits: 2,
  }).format(value);
}

function formatNumber(value?: number) {
  if (value == null) return "No disponible";

  return new Intl.NumberFormat("en-US", {
    maximumFractionDigits: 2,
  }).format(value);
}

const inputStyle = {
  flex: 1,
  border: "1px solid #243044",
  borderRadius: 14,
  background: "#060a0f",
  color: "#e8edf3",
  padding: "14px 16px",
  fontSize: 15,
  outline: "none",
};

const primaryButton = {
  border: "none",
  borderRadius: 14,
  background: "#00e676",
  color: "#060a0f",
  padding: "14px 18px",
  fontWeight: 900,
  cursor: "pointer",
};

const dangerButton = {
  border: "1px solid rgba(255,83,112,0.3)",
  borderRadius: 12,
  background: "rgba(255,83,112,0.08)",
  color: "#ff6b86",
  padding: "10px 14px",
  fontWeight: 800,
  cursor: "pointer",
};

const errorStyle = {
  margin: "14px 0 0",
  color: "#ff5370",
  fontWeight: 700,
  fontSize: 14,
};

const tdStyle = {
  padding: "16px 10px",
  color: "#7b8495",
  borderTop: "1px solid #162235",
  fontSize: 14,
};
