# ASECA Frontend

Frontend del TP Final de Aseguramiento de la Calidad de Software 2026. SPA que permite a un usuario registrado gestionar un portfolio de inversiones en acciones del mercado estadounidense, con integración a datos financieros reales de SEC EDGAR.

## Stack

- React 19
- Vite 6
- TypeScript 6
- React Router 7
- Axios
- Cypress 15 (tests E2E)
- ESLint + Husky + commitlint

## Ejecución Local Sin Docker

Desde el directorio `web/`:

```bash
npm install
npm run dev
```

La app queda disponible en `http://localhost:5173`.

Requiere el backend corriendo en `http://localhost:3000` (o el valor configurado en `VITE_API_URL`).

## Ejecución Local Con Docker

El `docker-compose.yml` levanta el servidor de desarrollo con hot reload:

```bash
docker compose up --build
```

La app queda disponible en `http://localhost:5173`. El volumen de código fuente está montado, por lo que los cambios se reflejan sin reconstruir la imagen.

## Variables De Entorno

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `VITE_API_URL` | URL base del backend REST | `http://localhost:3000` |

Para sobreescribir el valor, crear un archivo `.env.local` en `web/`:

```env
VITE_API_URL=http://mi-backend:3000
```

En Docker, la variable se inyecta desde `docker-compose.yml`. No se commitean archivos `.env` al repositorio.

## Integración Con El Backend

Todas las llamadas HTTP pasan por el cliente Axios centralizado en `src/api/axios.ts`:

```typescript
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:3000",
});
```

Un interceptor de request lee el JWT desde `localStorage` y lo agrega automáticamente como header `Authorization: Bearer {token}` en cada llamada. Las páginas y servicios no necesitan conocer el mecanismo de autenticación para hacer requests protegidos.

## Pantallas Y Rutas

| Ruta | Página | Acceso |
|---|---|---|
| `/` | Landing page pública | Público |
| `/login` | Inicio de sesión | Público |
| `/register` | Registro de cuenta | Público |
| `/home` | Dashboard principal | Autenticado |
| `/portfolio` | Portfolio completo | Autenticado |
| `/edgar` | Búsqueda de empresas en SEC EDGAR | Autenticado |
| `/watchlist` | Watchlist con comparación de métricas | Autenticado |
| `/transactions` | Historial de transacciones | Autenticado |

La protección de rutas es por componente. `HomePage` verifica el token en `localStorage` al montar y redirige a `/login` si no existe. Las demás rutas autenticadas asumen que el usuario llegó a través del flujo normal de login.

## Funcionalidades Implementadas

**Autenticación:**
- Registro con email y contraseña (`POST /auth/register`).
- Login con email y contraseña (`POST /auth/login`).
- El registro hace auto-login automáticamente.
- Token JWT almacenado en `localStorage`. Persiste entre recargas de página.
- Logout limpia el token y redirige a `/login`.

**Dashboard (`/home`):**
- Resumen del portfolio: valor actual, P&L no realizado, cantidad de posiciones.
- Top 3 posiciones con filas expandibles para ver detalle y acciones rápidas de compra y venta.
- Card de watchlist resumida.
- Últimas 3 transacciones.
- Botón para actualizar precios de posiciones activas (`POST /price-snapshots/update`).

**Portfolio (`/portfolio`):**
- Tabla completa de posiciones abiertas con costo promedio, valor actual, P&L realizado y no realizado.
- Resumen total del portfolio.
- Compra y venta desde esta pantalla.

**Compra y venta:**
- Modal reutilizable `TransactionModal` con selector de ticker y campo de cantidad.
- Disponible desde `/home` y `/portfolio`.
- Endpoints: `POST /transactions/buy`, `POST /transactions/sell`.

**Historial de transacciones (`/transactions`):**
- Lista completa de operaciones del usuario con tipo, ticker, cantidad, precio y fecha.

**Watchlist (`/watchlist`):**
- Agregar y eliminar empresas de la watchlist.
- Tabla de comparación de métricas EDGAR entre todas las empresas de la watchlist: revenue, net income, EPS, activos totales, pasivos totales.

**Búsqueda EDGAR (`/edgar`):**
- Búsqueda de empresas por ticker o nombre.
- Métricas financieras actuales de la empresa seleccionada.
- Evolución histórica de métricas con tabla y barras comparativas.
- Listado de filings con links a documentos oficiales de SEC.

## Organización Del Código

```
web/src/
├── api/
│   ├── axios.ts            # Cliente Axios con interceptor de auth
│   ├── edgar.ts            # Endpoints de SEC EDGAR
│   ├── portfolio.ts        # Endpoint GET /portfolio
│   ├── priceSnapshots.ts   # Endpoints de precios
│   ├── stocks.ts           # Endpoint GET /stocks
│   ├── transactions.ts     # Endpoints de compra y venta
│   └── watchlist.ts        # Endpoints de watchlist
├── auth/
│   └── authService.ts      # Login y registro
├── components/
│   └── portfolio/
│       └── TransactionModal.tsx  # Modal reutilizable de compra/venta
└── pages/
    ├── LandingPage.tsx
    ├── LoginPage.tsx
    ├── RegisterPage.tsx
    ├── HomePage.tsx
    ├── PortfolioPage.tsx
    ├── EdgarPage.tsx
    ├── WatchlistPage.tsx
    └── TransactionsPage.tsx
```

Cada módulo en `api/` encapsula las llamadas HTTP de un dominio. Las páginas llaman a esos módulos directamente. El estado es local a cada página con `useState`. No se usa Context API, Redux ni Zustand.

## Testing Con Cypress

Los tests son E2E y corren contra el frontend en `localhost:5173` y el backend real en `localhost:3000`.

### Requisitos

El frontend y el backend deben estar corriendo antes de ejecutar los tests:

```bash
# Terminal 1 — backend (desde aseca-backend/)
docker compose up

# Terminal 2 — frontend (desde web/)
npm run dev
```

### Comandos

Abrir Cypress en modo interactivo:

```bash
npm run cy:open
```

Ejecutar todos los tests en modo headless:

```bash
npm run cy:run
```

### Suites Disponibles

| Archivo | Qué cubre |
|---|---|
| `auth.cy.ts` | Registro exitoso, email duplicado, login exitoso, credenciales incorrectas, persistencia de sesión |
| `portfolio.cy.ts` | Compra de acciones, venta parcial, resumen y P&L |
| `transactions.cy.ts` | Compra y verificación en historial |
| `watchlist.cy.ts` | Agregar y eliminar items, verificación de status codes |
| `edgar.cy.ts` | Búsqueda de empresa, métricas y evolución histórica |
| `navigation.cy.ts` | Navegación completa entre todas las secciones |
| `update-prices.cy.ts` | Botón de actualización de precios desde home |

Cada test crea su propio usuario con email único via `POST /auth/register` antes de ejecutar el flujo. No depende de datos preexistentes.

### Configuración

`cypress.config.ts` apunta a:

```typescript
baseUrl: "http://localhost:5173"
env: { apiUrl: "http://localhost:3000" }
```

Para usar URLs distintas, sobreescribir con variables de entorno de Cypress:

```bash
CYPRESS_apiUrl=http://otro-backend:3000 npm run cy:run
```

## Comandos Útiles

Instalar dependencias:

```bash
npm install
```

Servidor de desarrollo:

```bash
npm run dev
```

Build de producción:

```bash
npm run build
```

Preview del build:

```bash
npm run preview
```

Lint:

```bash
npm run lint
```

## CI

El workflow `.github/workflows/ci.yml` corre en cada push a ramas distintas de `main` y en cada PR a `main`:

- **Lint:** `npm run lint`
- **Build:** `npm run build`

Los tests E2E de Cypress no corren en CI ya que requieren el backend levantado. La validación funcional E2E se ejecuta manualmente en local.

El workflow `.github/workflows/release.yml` corre en cada push a `main` que incluya cambios en `web/`. Si lint y build pasan, ejecuta semantic-release y publica un release en GitHub con las notas generadas desde los commits convencionales.

## Decisiones Técnicas

### Separación De La Capa API

Todas las llamadas HTTP están centralizadas en `src/api/`, un módulo por dominio. Las páginas no construyen URLs ni manejan headers directamente. Esto hace que el contrato con el backend sea visible en un solo lugar y facilita localizar y reemplazar llamadas cuando el backend cambia.

### JWT En `localStorage` Con Interceptor

El token se guarda en `localStorage` y el interceptor de Axios lo inyecta en cada request sin que cada página lo gestione. La alternativa de cookies httpOnly sería más segura ante XSS pero requeriría soporte explícito del backend para el flujo de cookies. Para el alcance del proyecto, `localStorage` con Bearer token es suficiente.

### Conventional Commits Y Semantic Release

Husky valida que los commits sigan el formato Conventional Commits antes de aceptarlos. En `main`, semantic-release analiza los commits y genera releases con changelog automático. Esto hace el historial legible y los releases reproducibles.
