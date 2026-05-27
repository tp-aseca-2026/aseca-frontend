import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router";
import { authService } from "../auth/authService";

export function LoginPage() {
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
      const response = await authService.login({ email, password });

      localStorage.setItem("accessToken", response.accessToken);

      navigate("/home");
    } catch (error: any) {
      console.log("Login error:", error.response?.data);

      const message = error.response?.data?.message;

      if (message) {
        setError(message);
      } else {
        setError("Email o contraseña incorrectos.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <main
      style={{
        width: "100vw",
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
          Acceso seguro
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
          Iniciá sesión
        </h1>

        <p
          style={{
            fontSize: 23,
            lineHeight: 1.5,
            color: "#788293",
            margin: "0 0 58px",
          }}
        >
          Ingresá con tu email y contraseña para acceder a tu portfolio.
        </p>

        <form onSubmit={handleSubmit}>
          <AuthInput
            label="Email"
            type="email"
            placeholder="tu@email.com"
            value={email}
            icon="@"
            dataCy="email-input"
            onChange={setEmail}
          />

          <AuthInput
            label="Contraseña"
            type={showPassword ? "text" : "password"}
            placeholder="••••••••"
            value={password}
            icon={showPassword ? "🙈" : "👁"}
            dataCy="password-input"
            onIconClick={() => setShowPassword(!showPassword)}
            onChange={setPassword}
          />

          {error && <Message text={error} />}

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
            {loading ? "Ingresando..." : "Ingresar al portfolio"}
          </button>
        </form>

        <AuthDivider text="¿No tenés cuenta?" />

        <div style={{ textAlign: "center" }}>
          <Link
            to="/register"
            style={{
              color: "#00e676",
              fontSize: 20,
              fontWeight: 700,
              textDecoration: "none",
            }}
          >
            Crear cuenta gratuita →
          </Link>
        </div>
      </section>
    </main>
  );
}

type AuthInputProps = {
  label: string;
  type: string;
  placeholder: string;
  value: string;
  icon: string;
  dataCy?: string;
  onChange: (value: string) => void;
  onIconClick?: () => void;
};

function AuthInput({
  label,
  type,
  placeholder,
  value,
  icon,
  dataCy,
  onChange,
  onIconClick,
}: AuthInputProps) {
  return (
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
        {label}
      </label>

      <div style={{ position: "relative" }}>
        <input
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          required
          data-cy={dataCy}
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
          onClick={onIconClick}
          style={{
            position: "absolute",
            right: 24,
            top: "50%",
            transform: "translateY(-50%)",
            color: "#536079",
            fontSize: 24,
            cursor: onIconClick ? "pointer" : "default",
          }}
        >
          {icon}
        </span>
      </div>
    </div>
  );
}

function Message({ text }: { text: string }) {
  return (
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
      {text}
    </div>
  );
}

function AuthDivider({ text }: { text: string }) {
  return (
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
        {text}
      </span>
      <div style={{ flex: 1, height: 1, background: "#253044" }} />
    </div>
  );
}