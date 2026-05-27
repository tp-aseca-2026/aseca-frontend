import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router";
import { authService } from "../auth/authService";

export function RegisterPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

async function handleSubmit(event: FormEvent) {
  event.preventDefault();

  setError("");
  setLoading(true);

  try {
    await authService.register({ email, password });

    const loginResponse = await authService.login({ email, password });

    localStorage.setItem("accessToken", loginResponse.accessToken);

    navigate("/home");
  } catch (error: any) {
    console.log("Register error:", error.response?.data);

    const message = error.response?.data?.message;

    if (Array.isArray(message)) {
      setError(message.join(" - "));
    } else if (message) {
      setError(message);
    } else {
      setError("No se pudo crear la cuenta.");
    }
  } finally {
    setLoading(false);
  }
}

  return (
    <main
      style={{
        minHeight: "100vh",
        background: "#080d14",
        color: "#e8edf3",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: 48,
        fontFamily: "Inter, system-ui, sans-serif",
      }}
    >
      <section style={{ width: "100%", maxWidth: 760 }}>
        <Link
          to="/"
          style={{
            color: "#6b7280",
            textDecoration: "none",
            fontSize: 14,
            display: "inline-block",
            marginBottom: 34,
          }}
        >
          ← Volver
        </Link>

        <p
          style={{
            textTransform: "uppercase",
            letterSpacing: "0.28em",
            fontSize: 14,
            color: "#596579",
            marginBottom: 28,
            fontWeight: 700,
          }}
        >
          Registro seguro
        </p>

        <h1
          style={{
            fontFamily: "Georgia, serif",
            fontSize: 64,
            fontWeight: 400,
            lineHeight: 1,
            margin: "0 0 28px",
            color: "#f3f6fb",
          }}
        >
          Creá tu cuenta
        </h1>

        <p
          style={{
            fontSize: 23,
            lineHeight: 1.5,
            color: "#788293",
            margin: "0 0 58px",
          }}
        >
          Registrate con tu email y contraseña para empezar a usar tu portfolio.
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 34 }}>
            <label
              style={{
                display: "block",
                textTransform: "uppercase",
                letterSpacing: "0.18em",
                fontSize: 15,
                fontWeight: 700,
                color: "#a1a9b7",
                marginBottom: 18,
              }}
            >
              Email
            </label>

            <div style={{ position: "relative" }}>
              <input
                type="email"
                placeholder="tu@email.com"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
                autoComplete="off"
                data-cy="email-input"
                style={{
                  width: "100%",
                  height: 76,
                  borderRadius: 18,
                  border: "1px solid #243044",
                  background: "#060a0f",
                  color: "#e8edf3",
                  fontSize: 22,
                  padding: "0 64px 0 24px",
                  outline: "none",
                }}
              />

              <span
                style={{
                  position: "absolute",
                  right: 24,
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "#536079",
                  fontSize: 24,
                }}
              >
                @
              </span>
            </div>
          </div>

          <div style={{ marginBottom: 34 }}>
            <label
              style={{
                display: "block",
                textTransform: "uppercase",
                letterSpacing: "0.18em",
                fontSize: 15,
                fontWeight: 700,
                color: "#a1a9b7",
                marginBottom: 18,
              }}
            >
              Contraseña
            </label>

            <div style={{ position: "relative" }}>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
                autoComplete="new-password"
                data-cy="password-input"
                style={{
                  width: "100%",
                  height: 76,
                  borderRadius: 18,
                  border: "1px solid #243044",
                  background: "#060a0f",
                  color: "#e8edf3",
                  fontSize: 22,
                  padding: "0 64px 0 24px",
                  outline: "none",
                }}
              />

              <span
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: "absolute",
                  right: 24,
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "#536079",
                  fontSize: 24,
                  cursor: "pointer",
                }}
              >
                {showPassword ? "🙈" : "👁"}
              </span>
            </div>
          </div>

          {error && (
            <div
              data-cy="error-message"
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

          <button
            type="submit"
            disabled={loading}
            data-cy="submit-button"
            style={{
              width: "100%",
              height: 76,
              border: "none",
              borderRadius: 18,
              background: "#00e676",
              color: "#06100b",
              fontSize: 22,
              fontWeight: 800,
              cursor: loading ? "not-allowed" : "pointer",
              opacity: loading ? 0.65 : 1,
              marginTop: 10,
            }}
          >
            {loading ? "Creando cuenta..." : "Crear cuenta gratuita"}
          </button>
        </form>

        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 28,
            margin: "48px 0 34px",
          }}
        >
          <div style={{ flex: 1, height: 1, background: "#253044" }} />
          <span
            style={{
              letterSpacing: "0.16em",
              fontSize: 15,
              color: "#4d5870",
              whiteSpace: "nowrap",
            }}
          >
            ¿Ya tenés cuenta?
          </span>
          <div style={{ flex: 1, height: 1, background: "#253044" }} />
        </div>

        <div style={{ textAlign: "center" }}>
          <Link
            to="/login"
            style={{
              color: "#00e676",
              fontSize: 20,
              fontWeight: 700,
              textDecoration: "none",
            }}
          >
            Iniciar sesión →
          </Link>
        </div>
      </section>
    </main>
  );
}