import { useEffect, useState } from "react";
import { Link } from "react-router";
import { api } from "../api/axios";
import { getStocks, type Stock } from "../api/stocks";

type Transaction = {
  id?: number;
  userId: number;
  stockId: number;
  quantity: number;
  type?: "BUY" | "SELL";
  executedAt?: string;
  price?: number | string;
};

function money(value: number | string) {
  const numericValue = Number(value);

  return `USD ${numericValue.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export function TransactionsPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTransactions();
  }, []);

  async function loadTransactions() {
    try {
      const [transactionsResponse, stocksResponse] = await Promise.all([
        api.get<Transaction[]>("/transactions"),
        getStocks(),
      ]);

      setTransactions(transactionsResponse.data);
      setStocks(stocksResponse);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

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
          data-cy="transactions-title"
          style={{
            margin: "0 0 32px",
            fontSize: 52,
            fontFamily: "Georgia, serif",
            fontWeight: 400,
          }}
        >
          Historial de transacciones
        </h1>

        <section
          style={{
            border: "1px solid #162235",
            borderRadius: 24,
            background: "#0c1017",
            padding: 24,
          }}
        >
          {loading ? (
            <p style={{ color: "#7b8495" }}>Cargando transacciones...</p>
          ) : transactions.length === 0 ? (
            <p style={{ color: "#7b8495" }}>
              Todavía no hay transacciones registradas.
            </p>
          ) : (
            <div style={{ display: "grid", gap: 12 }}>
              {transactions.map((transaction, index) => {
                const stock = stocks.find(
                  (stock) => stock.id === transaction.stockId,
                );

                const ticker = stock?.ticker ?? `Stock #${transaction.stockId}`;
                const companyName = stock?.companyName;

                return (
                  <div
                    key={transaction.id || index}
                    data-cy={`transaction-${transaction.type ?? "OPERATION"}-${ticker}`}
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

                    <p style={{ margin: 0, color: "#7b8495", fontSize: 14 }}>
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
        </section>
      </section>
    </main>
  );
}
