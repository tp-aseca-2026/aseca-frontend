import { useNavigate } from "react-router";

const tickers = [
  { symbol: "AAPL", change: "+1.24%", positive: true },
  { symbol: "MSFT", change: "+0.87%", positive: true },
  { symbol: "GOOGL", change: "-0.43%", positive: false },
  { symbol: "AMZN", change: "+2.11%", positive: true },
  { symbol: "TSLA", change: "-1.78%", positive: false },
  { symbol: "NVDA", change: "+3.56%", positive: true },
  { symbol: "META", change: "+1.02%", positive: true },
  { symbol: "JPM", change: "-0.21%", positive: false },
  { symbol: "V", change: "+0.66%", positive: true },
  { symbol: "WMT", change: "+0.33%", positive: true },
];

export function LandingPage() {
  const navigate = useNavigate();

  return (
    <>
      <style>
        {`
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
          overflow: "hidden",
        }}
      >
        <div
          style={{
            position: "absolute",
            inset: 0,
            backgroundImage:
              "linear-gradient(rgba(0,230,118,0.045) 1px, transparent 1px), linear-gradient(90deg, rgba(0,230,118,0.045) 1px, transparent 1px)",
            backgroundSize: "56px 56px",
          }}
        />

        <div
          style={{
            position: "absolute",
            width: 650,
            height: 650,
            borderRadius: "50%",
            background: "rgba(0,230,118,0.08)",
            filter: "blur(110px)",
            top: -180,
            left: -160,
          }}
        />

        <section
          style={{
            position: "relative",
            zIndex: 1,
            minHeight: "100vh",
            padding: "56px",
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
          }}
        >
          <header
            style={{
              display: "flex",
              alignItems: "center",
              gap: 12,
            }}
          >
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
              <span style={{ color: "#00e676", fontWeight: 700 }}>
                Folio
              </span>
            </p>
          </header>

          <div>
            <p
              style={{
                color: "#536079",
                textTransform: "uppercase",
                letterSpacing: "0.22em",
                fontSize: 12,
                fontWeight: 700,
                marginBottom: 22,
              }}
            >
              Mercado en tiempo real
            </p>

            <div
              style={{
                overflow: "hidden",
                width: "100%",
                marginBottom: 90,
                WebkitMaskImage:
                  "linear-gradient(90deg, transparent 0%, black 8%, black 92%, transparent 100%)",
                maskImage:
                  "linear-gradient(90deg, transparent 0%, black 8%, black 92%, transparent 100%)",
              }}
            >
              <div
                style={{
                  display: "flex",
                  width: "max-content",
                  animation: "tickerMove 28s linear infinite",
                }}
              >
                {[...tickers, ...tickers].map((ticker, index) => (
                  <div
                    key={index}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: 10,
                      padding: "10px 32px",
                      borderRight: "1px solid #111827",
                      whiteSpace: "nowrap",
                    }}
                  >
                    <span
                      style={{
                        color: "#e8edf3",
                        fontWeight: 700,
                        fontSize: 14,
                      }}
                    >
                      {ticker.symbol}
                    </span>

                    <span
                      style={{
                        color: ticker.positive ? "#00e676" : "#ff5370",
                        fontWeight: 700,
                        fontSize: 13,
                      }}
                    >
                      {ticker.positive ? "▲" : "▼"} {ticker.change}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div style={{ maxWidth: 780 }}>
              <p
                style={{
                  color: "#00e676",
                  textTransform: "uppercase",
                  letterSpacing: "0.2em",
                  fontSize: 12,
                  fontWeight: 800,
                  marginBottom: 24,
                }}
              >
                Portfolio tracker
              </p>

              <h1
                style={{
                  fontFamily: "Georgia, serif",
                  fontSize: "clamp(64px, 7vw, 104px)",
                  lineHeight: 0.98,
                  fontWeight: 400,
                  letterSpacing: "-0.05em",
                  margin: 0,
                  color: "#f3f6fb",
                }}
              >
                Invertí con <br />
                <span style={{ color: "#00e676", fontWeight: 700 }}>
                  datos reales,
                </span>
                <br />
                decisiones claras.
              </h1>

              <p
                style={{
                  maxWidth: 560,
                  marginTop: 30,
                  color: "#7b8495",
                  fontSize: 18,
                  lineHeight: 1.7,
                }}
              >
                Armá tu portfolio, seguí tus acciones favoritas y consultá
                información financiera desde una plataforma simple, moderna y
                segura.
              </p>

              <div
                style={{
                  display: "flex",
                  gap: 16,
                  marginTop: 44,
                }}
              >
                <button
                  onClick={() => navigate("/register")}
                  style={{
                    border: "none",
                    borderRadius: 16,
                    padding: "18px 28px",
                    background: "#00e676",
                    color: "#060a0f",
                    fontSize: 16,
                    fontWeight: 800,
                    cursor: "pointer",
                    minWidth: 230,
                  }}
                >
                  Quiero empezar a invertir
                </button>

                <button
                  onClick={() => navigate("/login")}
                  style={{
                    border: "1px solid #243044",
                    borderRadius: 16,
                    padding: "18px 28px",
                    background: "rgba(255,255,255,0.03)",
                    color: "#e8edf3",
                    fontSize: 16,
                    fontWeight: 800,
                    cursor: "pointer",
                    minWidth: 170,
                  }}
                >
                  Ya tengo cuenta
                </button>
              </div>
            </div>
          </div>

          <div style={{ display: "flex", gap: 72 }}>
            <div>
              <p style={{ margin: 0, fontSize: 30, fontWeight: 800 }}>
                <span style={{ color: "#00e676" }}>$</span>2.4B
              </p>
              <p style={{ marginTop: 8, color: "#4a5568", fontSize: 13 }}>
                Volumen gestionado
              </p>
            </div>

            <div>
              <p style={{ margin: 0, fontSize: 30, fontWeight: 800 }}>
                8K<span style={{ color: "#00e676" }}>+</span>
              </p>
              <p style={{ marginTop: 8, color: "#4a5568", fontSize: 13 }}>
                Tickers disponibles
              </p>
            </div>

            <div>
              <p style={{ margin: 0, fontSize: 30, fontWeight: 800 }}>
                SEC<span style={{ color: "#00e676" }}>.</span>
              </p>
              <p style={{ marginTop: 8, color: "#4a5568", fontSize: 13 }}>
                Datos EDGAR en vivo
              </p>
            </div>
          </div>
        </section>
      </main>
    </>
  );
}