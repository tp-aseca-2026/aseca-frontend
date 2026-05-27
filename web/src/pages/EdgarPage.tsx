import { FormEvent, useState } from "react";
import { Link } from "react-router";
import {
  edgarApi,
  EdgarCompany,
  EdgarFiling,
  EdgarHistoricalMetrics,
  EdgarMetricPoint,
  EdgarMetrics,
} from "../api/edgar";


type MetricKey = keyof EdgarMetrics;
type HistoricalMetricKey = keyof EdgarHistoricalMetrics;
type MetricFormat = "money" | "number";

const metricTabs: {
  key: HistoricalMetricKey;
  label: string;
  format: MetricFormat;
}[] = [
  { key: "revenue", label: "Revenue", format: "money" },
  { key: "netIncome", label: "Net Income", format: "money" },
  { key: "eps", label: "EPS", format: "number" },
  { key: "totalAssets", label: "Total Assets", format: "money" },
  { key: "totalLiabilities", label: "Total Liabilities", format: "money" },
];

const metricCards: {
  key: MetricKey;
  label: string;
  format: MetricFormat;
}[] = [
  { key: "revenue", label: "Revenue", format: "money" },
  { key: "netIncome", label: "Net Income", format: "money" },
  { key: "eps", label: "EPS", format: "number" },
  { key: "totalAssets", label: "Total Assets", format: "money" },
  { key: "totalLiabilities", label: "Total Liabilities", format: "money" },
];

export function EdgarPage() {
  const [query, setQuery] = useState("");
  const [companies, setCompanies] = useState<EdgarCompany[]>([]);
  const [selectedCompany, setSelectedCompany] = useState<EdgarCompany | null>(
    null,
  );

  const [metrics, setMetrics] = useState<EdgarMetrics | null>(null);
  const [filings, setFilings] = useState<EdgarFiling[]>([]);
  const [historical, setHistorical] = useState<EdgarHistoricalMetrics | null>(
    null,
  );

  const [activeMetric, setActiveMetric] =
    useState<HistoricalMetricKey>("revenue");

  const [loadingSearch, setLoadingSearch] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState(false);
  const [error, setError] = useState("");

  async function handleSearch(event: FormEvent) {
    event.preventDefault();

    if (!query.trim()) return;

    setError("");
    setLoadingSearch(true);
    setSelectedCompany(null);
    setMetrics(null);
    setFilings([]);
    setHistorical(null);

    try {
      const results = await edgarApi.searchCompanies(query);
      setCompanies(results);
    } catch (error) {
      console.log(error);
      setError("No pudimos buscar empresas en EDGAR.");
    } finally {
      setLoadingSearch(false);
    }
  }

  async function handleSelectCompany(company: EdgarCompany) {
    setError("");
    setLoadingDetails(true);
    setSelectedCompany(company);
    setMetrics(null);
    setFilings([]);
    setHistorical(null);
    setActiveMetric("revenue");

    try {
      const [metricsResult, filingsResult, historicalResult] =
        await Promise.all([
          edgarApi.getCompanyMetrics(company.ticker),
          edgarApi.getCompanyFilings(company.ticker),
          edgarApi.getHistoricalMetrics(company.ticker),
        ]);

      setMetrics(metricsResult);
      setFilings(filingsResult);
      setHistorical(historicalResult);
    } catch (error) {
      console.log(error);
      setError("No pudimos cargar los datos financieros de la empresa.");
    } finally {
      setLoadingDetails(false);
    }
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

  function formatMetric(value: number | undefined, format: MetricFormat) {
    return format === "money" ? formatMoney(value) : formatNumber(value);
  }

  const activeMetricInfo = metricTabs.find((tab) => tab.key === activeMetric);
  const activeHistoricalPoints = historical?.[activeMetric] ?? [];

  return (
    <main
      style={{
        minHeight: "100vh",
        background: "#080d14",
        color: "#e8edf3",
        padding: 48,
        fontFamily: "Inter, system-ui, sans-serif",
      }}
    >
      <section style={{ maxWidth: 1180, margin: "0 auto" }}>
        <Link
          to="/home"
          style={{
            color: "#6b7280",
            textDecoration: "none",
            fontSize: 14,
            display: "inline-block",
            marginBottom: 34,
            fontWeight: 700,
          }}
        >
          ← Volver al inicio
        </Link>

        <p
          style={{
            textTransform: "uppercase",
            letterSpacing: "0.28em",
            fontSize: 13,
            color: "#596579",
            marginBottom: 24,
            fontWeight: 700,
          }}
        >
          SEC EDGAR
        </p>

        <h1
          style={{
            fontFamily: "Georgia, serif",
            fontSize: 58,
            fontWeight: 400,
            lineHeight: 1,
            margin: "0 0 20px",
            color: "#f3f6fb",
          }}
        >
          Explorá datos financieros
        </h1>

        <p
          style={{
            fontSize: 20,
            lineHeight: 1.5,
            color: "#788293",
            maxWidth: 800,
            margin: "0 0 42px",
          }}
        >
          Buscá empresas cotizadas en Estados Unidos y consultá métricas,
          evolución histórica y filings reales reportados ante la SEC.
        </p>

        <form
          onSubmit={handleSearch}
          style={{
            display: "flex",
            gap: 14,
            marginBottom: 28,
          }}
        >
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Ej: AAPL, Apple, Microsoft, Tesla"
            style={{
              flex: 1,
              height: 62,
              borderRadius: 16,
              border: "1px solid #243044",
              background: "#060a0f",
              color: "#e8edf3",
              fontSize: 18,
              padding: "0 20px",
              outline: "none",
            }}
          />

          <button
            type="submit"
            disabled={loadingSearch}
            style={{
              minWidth: 150,
              height: 62,
              border: "none",
              borderRadius: 16,
              background: "#00e676",
              color: "#06100b",
              fontSize: 18,
              fontWeight: 800,
              cursor: loadingSearch ? "not-allowed" : "pointer",
              opacity: loadingSearch ? 0.65 : 1,
            }}
          >
            {loadingSearch ? "Buscando..." : "Buscar"}
          </button>
        </form>

        {error && (
          <div
            style={{
              marginBottom: 24,
              borderRadius: 14,
              padding: "16px 18px",
              fontSize: 15,
              background: "rgba(255,83,112,0.08)",
              border: "1px solid rgba(255,83,112,0.22)",
              color: "#ff6b86",
            }}
          >
            {error}
          </div>
        )}

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "350px 1fr",
            gap: 24,
            alignItems: "start",
          }}
        >
          <section
            style={{
              background: "#0d141f",
              border: "1px solid #1e2a3d",
              borderRadius: 22,
              padding: 22,
              position: "sticky",
              top: 24,
            }}
          >
            <h2 style={{ margin: "0 0 18px", fontSize: 22 }}>Resultados</h2>

            {companies.length === 0 && (
              <p style={{ color: "#788293", margin: 0, lineHeight: 1.5 }}>
                Buscá una empresa para ver resultados.
              </p>
            )}

            {companies.map((company) => (
              <button
                key={company.cik}
                onClick={() => handleSelectCompany(company)}
                style={{
                  width: "100%",
                  textAlign: "left",
                  border: "1px solid #243044",
                  background:
                    selectedCompany?.cik === company.cik
                      ? "rgba(0,230,118,0.09)"
                      : "#060a0f",
                  color: "#e8edf3",
                  borderRadius: 16,
                  padding: 16,
                  marginBottom: 12,
                  cursor: "pointer",
                }}
              >
                <strong style={{ fontSize: 16 }}>{company.companyName}</strong>
                <div
                  style={{
                    marginTop: 8,
                    color: "#788293",
                    fontSize: 14,
                  }}
                >
                  {company.ticker} · CIK {company.cik}
                </div>
              </button>
            ))}
          </section>

          <section
            style={{
              background: "#0d141f",
              border: "1px solid #1e2a3d",
              borderRadius: 22,
              padding: 26,
              minHeight: 520,
            }}
          >
            {!selectedCompany && (
              <EmptyState />
            )}

            {selectedCompany && (
              <>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 24,
                    alignItems: "flex-start",
                    marginBottom: 28,
                  }}
                >
                  <div>
                    <h2 style={{ margin: "0 0 8px", fontSize: 32 }}>
                      {selectedCompany.companyName}
                    </h2>

                    <p style={{ margin: 0, color: "#788293" }}>
                      Ticker {selectedCompany.ticker} · CIK{" "}
                      {selectedCompany.cik}
                    </p>
                  </div>

<div style={{ display: "flex", gap: 12, alignItems: "center" }}>
  <span
    style={{
      border: "1px solid rgba(0,230,118,0.22)",
      background: "rgba(0,230,118,0.08)",
      color: "#00e676",
      borderRadius: 999,
      padding: "10px 14px",
      fontSize: 13,
      fontWeight: 800,
    }}
  >
    Datos reales SEC
  </span>


</div>
                </div>

                {loadingDetails && (
                  <div
                    style={{
                      border: "1px solid #243044",
                      borderRadius: 18,
                      padding: 22,
                      background: "#060a0f",
                      color: "#788293",
                    }}
                  >
                    Cargando métricas, histórico y filings...
                  </div>
                )}

                {metrics && (
                  <>
                    <SectionTitle
                      title="Métricas financieras"
                      subtitle="Último dato reportado disponible por la empresa."
                    />

                    <div
                      style={{
                        display: "grid",
                        gridTemplateColumns: "repeat(5, minmax(0, 1fr))",
                        gap: 14,
                        marginBottom: 34,
                      }}
                    >
                      {metricCards.map((metric) => {
                        const point = metrics[metric.key];

                        return (
                          <MetricCard
                            key={metric.key}
                            title={metric.label}
                            value={formatMetric(point?.val, metric.format)}
                            period={point ? `${point.fp} ${point.fy}` : ""}
                            form={point?.form}
                          />
                        );
                      })}
                    </div>
                  </>
                )}

                {historical && (
                  <>
                    <SectionTitle
                      title="Evolución histórica"
                      subtitle="Últimos puntos reportados para cada métrica financiera."
                    />

                    <div
                      style={{
                        display: "flex",
                        flexWrap: "wrap",
                        gap: 10,
                        marginBottom: 20,
                      }}
                    >
                      {metricTabs.map((tab) => (
                        <button
                          key={tab.key}
                          onClick={() => setActiveMetric(tab.key)}
                          style={{
                            border:
                              activeMetric === tab.key
                                ? "1px solid rgba(0,230,118,0.45)"
                                : "1px solid #243044",
                            background:
                              activeMetric === tab.key
                                ? "rgba(0,230,118,0.1)"
                                : "#060a0f",
                            color:
                              activeMetric === tab.key ? "#00e676" : "#a1a9b7",
                            borderRadius: 999,
                            padding: "10px 14px",
                            cursor: "pointer",
                            fontWeight: 800,
                          }}
                        >
                          {tab.label}
                        </button>
                      ))}
                    </div>

                    <HistoricalTable
                      points={activeHistoricalPoints}
                      label={activeMetricInfo?.label ?? ""}
                      format={activeMetricInfo?.format ?? "money"}
                      formatMetric={formatMetric}
                    />
                  </>
                )}

                {filings.length > 0 && (
                  <>
                    <SectionTitle
                      title="Filings recientes"
                      subtitle="Reportes 10-K y 10-Q publicados por la empresa."
                    />

                    <div
                      style={{
                        display: "grid",
                        gap: 10,
                      }}
                    >
                      {filings.map((filing) => (
                        <a
                          key={filing.accessionNumber}
                          href={filing.link}
                          target="_blank"
                          rel="noreferrer"
                          style={{
                            display: "grid",
                            gridTemplateColumns: "90px 1fr auto",
                            alignItems: "center",
                            gap: 16,
                            textDecoration: "none",
                            color: "#e8edf3",
                            border: "1px solid #243044",
                            borderRadius: 14,
                            padding: "14px 16px",
                            background: "#060a0f",
                          }}
                        >
                          <strong
                            style={{
                              color: "#00e676",
                              fontSize: 15,
                            }}
                          >
                            {filing.form}
                          </strong>

                          <span style={{ color: "#a1a9b7" }}>
                            Filing date: {filing.filingDate}
                            {filing.reportDate
                              ? ` · Report date: ${filing.reportDate}`
                              : ""}
                          </span>

                          <span style={{ color: "#00e676", fontWeight: 800 }}>
                            Ver filing →
                          </span>
                        </a>
                      ))}
                    </div>
                  </>
                )}
              </>
            )}
          </section>
        </div>
      </section>
    </main>
  );
}

function EmptyState() {
  return (
    <div
      style={{
        minHeight: 430,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        color: "#788293",
      }}
    >
      <div>
        <div
          style={{
            width: 74,
            height: 74,
            borderRadius: 24,
            margin: "0 auto 20px",
            background: "rgba(0,230,118,0.08)",
            border: "1px solid rgba(0,230,118,0.2)",
            color: "#00e676",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 30,
            fontWeight: 900,
          }}
        >
          SEC
        </div>

        <h3 style={{ color: "#e8edf3", margin: "0 0 10px", fontSize: 24 }}>
          Seleccioná una empresa
        </h3>

        <p style={{ margin: 0, maxWidth: 420, lineHeight: 1.6 }}>
          Buscá por nombre o ticker para consultar métricas financieras,
          evolución histórica y filings oficiales.
        </p>
      </div>
    </div>
  );
}

function SectionTitle({
  title,
  subtitle,
}: {
  title: string;
  subtitle: string;
}) {
  return (
    <div style={{ margin: "0 0 18px" }}>
      <h3 style={{ margin: "0 0 6px", fontSize: 22, color: "#f3f6fb" }}>
        {title}
      </h3>
      <p style={{ margin: 0, color: "#788293", fontSize: 14 }}>
        {subtitle}
      </p>
    </div>
  );
}

type MetricCardProps = {
  title: string;
  value: string;
  period?: string;
  form?: string;
};

function MetricCard({ title, value, period, form }: MetricCardProps) {
  return (
    <div
      style={{
        border: "1px solid #243044",
        background: "#060a0f",
        borderRadius: 16,
        padding: 18,
        minHeight: 116,
      }}
    >
      <p
        style={{
          margin: "0 0 10px",
          color: "#788293",
          fontSize: 12,
          textTransform: "uppercase",
          letterSpacing: "0.12em",
          fontWeight: 800,
        }}
      >
        {title}
      </p>

      <strong
        style={{
          display: "block",
          fontSize: 22,
          color: "#f3f6fb",
          marginBottom: 10,
        }}
      >
        {value}
      </strong>

      {(period || form) && (
        <span style={{ color: "#536079", fontSize: 13 }}>
          {period}
          {form ? ` · ${form}` : ""}
        </span>
      )}
    </div>
  );
}

function HistoricalTable({
  points,
  label,
  format,
  formatMetric,
}: {
  points: EdgarMetricPoint[];
  label: string;
  format: MetricFormat;
  formatMetric: (value: number | undefined, format: MetricFormat) => string;
}) {
  const maxValue = Math.max(...points.map((point) => Math.abs(point.val)), 0);

  if (points.length === 0) {
    return (
      <div
        style={{
          border: "1px solid #243044",
          background: "#060a0f",
          borderRadius: 16,
          padding: 20,
          color: "#788293",
          marginBottom: 34,
        }}
      >
        No hay datos históricos disponibles para {label}.
      </div>
    );
  }

  return (
    <div
      style={{
        border: "1px solid #243044",
        background: "#060a0f",
        borderRadius: 16,
        overflow: "hidden",
        marginBottom: 34,
      }}
    >
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "130px 90px 1fr 170px",
          gap: 16,
          padding: "14px 18px",
          color: "#536079",
          fontSize: 12,
          textTransform: "uppercase",
          letterSpacing: "0.12em",
          fontWeight: 800,
          borderBottom: "1px solid #1e2a3d",
        }}
      >
        <span>Fecha</span>
        <span>Form</span>
        <span>{label}</span>
        <span>Filed</span>
      </div>

      {points.map((point) => {
        const width =
          maxValue > 0 ? Math.max((Math.abs(point.val) / maxValue) * 100, 4) : 0;

        return (
          <div
            key={`${point.end}-${point.filed}-${point.form}-${point.val}`}
            style={{
              display: "grid",
              gridTemplateColumns: "130px 90px 1fr 170px",
              gap: 16,
              alignItems: "center",
              padding: "15px 18px",
              borderBottom: "1px solid #1e2a3d",
            }}
          >
            <span style={{ color: "#a1a9b7", fontWeight: 700 }}>
              {point.end}
            </span>

            <span
              style={{
                color: "#00e676",
                fontWeight: 800,
              }}
            >
              {point.form}
            </span>

            <div>
              <strong style={{ color: "#f3f6fb" }}>
                {formatMetric(point.val, format)}
              </strong>

              <div
                style={{
                  width: "100%",
                  height: 7,
                  borderRadius: 999,
                  background: "#142033",
                  marginTop: 8,
                  overflow: "hidden",
                }}
              >
                <div
                  style={{
                    width: `${width}%`,
                    height: "100%",
                    borderRadius: 999,
                    background:
                      point.val >= 0
                        ? "rgba(0,230,118,0.85)"
                        : "rgba(255,83,112,0.85)",
                  }}
                />
              </div>
            </div>

            <span style={{ color: "#788293" }}>{point.filed}</span>
          </div>
        );
      })}
    </div>
  );
}