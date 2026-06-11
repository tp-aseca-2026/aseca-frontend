# ASECA Frontend Monorepo

Frontend del TP Final de Aseguramiento de la Calidad de Software 2026. Portfolio tracker de acciones del mercado estadounidense con integración a datos financieros de SEC EDGAR.

## Estructura

| Directorio | Descripción | README |
|---|---|---|
| `web/` | SPA en React + Vite (frontend web) | [web/README.md](web/README.md) |
| `app/` | App Android nativa en Kotlin + Jetpack Compose | [app/README.md](app/README.md) |
| `mobile-tests/` | Tests E2E con WebdriverIO + Appium para la app Android | [app/README.md](app/README.md) |

Ambas interfaces consumen el mismo backend REST. Ver [aseca-backend](https://github.com/tp-aseca-2026/aseca-backend) para instrucciones de levantarlo.
