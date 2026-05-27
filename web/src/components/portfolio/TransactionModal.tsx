type Stock = {
  id: number;
  ticker: string;
  companyName: string | null;
};

type TransactionMode = "buy" | "sell";

type TransactionModalProps = {
  open: boolean;
  mode: TransactionMode;
  stocks: Stock[];
  defaultTicker?: string;
  loading?: boolean;
  error?: string | null;
  onClose: () => void;
  onSubmit: (ticker: string, quantity: number) => void;
};

export function TransactionModal({
  open,
  mode,
  stocks,
  defaultTicker = "",
  loading = false,
  error,
  onClose,
  onSubmit,
}: TransactionModalProps) {
  if (!open) return null;

  const title = mode === "buy" ? "Registrar compra" : "Registrar venta";

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);
    const ticker = String(formData.get("ticker") || "").trim().toUpperCase();
    const quantity = Number(formData.get("quantity"));

    onSubmit(ticker, quantity);
  }

  return (
    <div style={overlayStyle}>
      <form onSubmit={handleSubmit} style={modalStyle}>
        <h2 style={{ margin: 0, fontSize: 26 }}>{title}</h2>

        <p style={descriptionStyle}>
          Seleccioná una acción disponible y la cantidad que querés operar.
        </p>

        <label style={labelStyle}>Acción</label>
        <select name="ticker" defaultValue={defaultTicker} style={inputStyle}>
          <option value="">Seleccioná una acción</option>

          {stocks.map((stock) => (
            <option key={stock.id} value={stock.ticker}>
              {stock.ticker} — {stock.companyName ?? "Sin nombre"}
            </option>
          ))}
        </select>

        <label style={labelStyle}>Cantidad</label>
        <input
          name="quantity"
          type="number"
          min={1}
          step={1}
          placeholder="Ej: 10"
          style={inputStyle}
        />

        {error && <p style={errorStyle}>{error}</p>}

        <div style={actionsStyle}>
          <button type="button" onClick={onClose} style={cancelButton}>
            Cancelar
          </button>

          <button type="submit" disabled={loading} style={submitButton}>
            {loading ? "Guardando..." : title}
          </button>
        </div>
      </form>
    </div>
  );
}

const overlayStyle = {
  position: "fixed",
  inset: 0,
  background: "rgba(0,0,0,0.72)",
  zIndex: 100,
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: 24,
};

const modalStyle = {
  width: "100%",
  maxWidth: 460,
  borderRadius: 24,
  border: "1px solid #243044",
  background: "#0c1017",
  color: "#e8edf3",
  padding: 28,
  boxShadow: "0 24px 80px rgba(0,0,0,0.45)",
};

const descriptionStyle = {
  margin: "10px 0 24px",
  color: "#7b8495",
  lineHeight: 1.5,
};

const labelStyle = {
  display: "block",
  margin: "14px 0 8px",
  color: "#7b8495",
  fontSize: 14,
  fontWeight: 700,
};

const inputStyle = {
  width: "100%",
  boxSizing: "border-box" as const,
  border: "1px solid #243044",
  borderRadius: 14,
  background: "#060a0f",
  color: "#e8edf3",
  padding: "14px 16px",
  fontSize: 15,
  outline: "none",
};

const errorStyle = {
  margin: "12px 0 0",
  color: "#ff5370",
  fontWeight: 700,
  fontSize: 14,
};

const actionsStyle = {
  display: "flex",
  justifyContent: "flex-end",
  gap: 12,
  marginTop: 26,
};

const cancelButton = {
  border: "1px solid #243044",
  borderRadius: 12,
  background: "rgba(255,255,255,0.03)",
  color: "#e8edf3",
  padding: "11px 16px",
  fontWeight: 800,
  cursor: "pointer",
};

const submitButton = {
  border: "none",
  borderRadius: 12,
  background: "#00e676",
  color: "#060a0f",
  padding: "11px 16px",
  fontWeight: 900,
  cursor: "pointer",
};