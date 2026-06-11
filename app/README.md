# ASECA App Mobile

App Android nativa del TP Final de Aseguramiento de la Calidad de Software 2026. Permite gestionar un portfolio de inversiones en acciones del mercado estadounidense desde un dispositivo Android, consumiendo el mismo backend REST que el frontend web.

## Stack

- Kotlin 2.2.10
- Jetpack Compose (Compose BOM 2026.02.01)
- Material3
- Lifecycle ViewModel KTX 2.6.1
- `HttpURLConnection` nativo para llamadas HTTP
- `SharedPreferences` para persistencia del token JWT
- Min SDK 24 (Android 7.0+), Target SDK 36

## Arquitectura

La app sigue el patrón **MVVM** con cuatro capas:

```
UI (Screens / Composables)
        ↕
ViewModel  (estado con UiState + coroutines)
        ↕
Repository (traducción JSON → modelos Kotlin)
        ↕
ApiClient  (HttpURLConnection, Bearer token)
```

La navegación entre pantallas es un `when` sobre un enum `AuthScreen` gestionado con `rememberSaveable` en `MainActivity`. Las pantallas autenticadas comparten un `Scaffold` con barra de navegación inferior.

## Pantallas

| Pantalla | Descripción |
|---|---|
| `LoginScreen` | Inicio de sesión |
| `RegisterScreen` | Registro de cuenta |
| `HomeScreen` | Dashboard: resumen de portfolio, precios, top posiciones |
| `PortfolioScreen` | Tabla completa de posiciones con P&L |
| `TransactionsScreen` | Historial de transacciones |
| `WatchlistScreen` | Watchlist con comparación de métricas EDGAR |
| `EdgarScreen` | Búsqueda de empresas, métricas históricas y filings de SEC |

## Ejecución Local

### Requisitos

- Android Studio (Meerkat o superior recomendado)
- JDK 17
- Emulador Android o dispositivo físico con Android 7.0+ (API 24+)
- Backend corriendo en `localhost:3000`

### Pasos

1. Abrir el directorio `app/` en Android Studio.
2. Sincronizar Gradle.
3. Levantar el backend (ver [aseca-backend](https://github.com/tp-aseca-2026/aseca-backend)).
4. Correr la app en el emulador o dispositivo.

La app apunta a `http://10.0.2.2:3000`, que es la dirección que el emulador Android usa para acceder a `localhost` del host. En un dispositivo físico en la misma red, reemplazar por la IP local del host en `ApiClient.kt`.

## Configuración De La URL Del Backend

La URL base está definida en `app/src/main/java/com/aseca/mobile/network/ApiClient.kt`:

```kotlin
class ApiClient(
    private val baseUrl: String = "http://10.0.2.2:3000",
)
```

Para apuntar a otro servidor, modificar ese valor antes de compilar. No hay mecanismo de configuración por environment en runtime.

## Autenticación

El token JWT se guarda en `SharedPreferences` (archivo `aseca_auth`, clave `access_token`) a través de `TokenStore`. Persiste entre reinicios de la app.

Al iniciar, `MainActivity` lee el token almacenado. Si existe, la app navega directamente a `HomeScreen` sin pasar por login. El logout borra el token y vuelve a `LoginScreen`.

Cada request al backend incluye el token como header `Authorization: Bearer {token}` cuando está disponible.

## Endpoints Consumidos

| Endpoint | Método | Pantalla |
|---|---|---|
| `/auth/login` | POST | LoginScreen |
| `/auth/register` | POST | RegisterScreen |
| `/portfolio` | GET | HomeScreen, PortfolioScreen |
| `/stocks` | GET | HomeScreen, PortfolioScreen, WatchlistScreen |
| `/transactions` | GET | TransactionsScreen |
| `/transactions/buy` | POST | HomeScreen, PortfolioScreen |
| `/transactions/sell` | POST | HomeScreen, PortfolioScreen |
| `/price-snapshots/latest` | GET | HomeScreen |
| `/price-snapshots/update` | POST | HomeScreen |
| `/watchlist` | GET | WatchlistScreen |
| `/watchlist` | POST | WatchlistScreen |
| `/watchlist/:ticker` | DELETE | WatchlistScreen |
| `/watchlist/comparison` | GET | WatchlistScreen |
| `/edgar/companies/search` | GET | EdgarScreen |
| `/edgar/companies/:ticker/metrics` | GET | EdgarScreen |
| `/edgar/companies/:ticker/filings` | GET | EdgarScreen |
| `/edgar/companies/:ticker/historical-metrics` | GET | EdgarScreen |

## Comandos Útiles

Compilar APK de debug:

```bash
cd app
./gradlew :app:assembleDebug
```

Ejecutar tests unitarios:

```bash
./gradlew :app:testDebugUnitTest
```

Limpiar build:

```bash
./gradlew clean
```

El APK de debug se genera en `app/app/build/outputs/apk/debug/`.

## Docker

Docker se usa para compilar la app y correr tests unitarios en un entorno reproducible. No reemplaza Android Studio, el emulador ni Appium, porque los E2E mobile necesitan controlar un dispositivo Android mediante ADB. Por eso se ejecuta como comando puntual de build/test, no como un servicio con `docker compose up`.

Compilar APK de debug:

```bash
cd app
docker compose run --rm app
```

Ejecutar tests unitarios:

```bash
docker compose run --rm app ./gradlew :app:testDebugUnitTest
```

Limpiar build:

```bash
docker compose run --rm app ./gradlew clean
```

El APK generado puede instalarse luego en el emulador local para ejecutar los tests de Appium.

## CI

El workflow `.github/workflows/app-release.yml` corre en cada push a `main` que incluya cambios en `app/`:

1. **Android Checks:** ejecuta `testDebugUnitTest` y compila el APK de debug.
2. **Semantic Release:** si los checks pasan, genera un release en GitHub con las notas de los commits convencionales.

## Tests E2E Con Appium (`mobile-tests/`)

Los tests E2E están en el directorio `mobile-tests/` y automatizan la app instalada en un emulador Android.

### Stack De Tests

- WebdriverIO 9 + Appium 7
- Driver: UiAutomator2
- Framework: Mocha (BDD)
- TypeScript

### Requisitos

- Node.js 22
- Appium instalado globalmente: `npm install -g appium`
- Driver UiAutomator2: `appium driver install uiautomator2`
- Emulador Android `Pixel_8` corriendo con la app instalada
- Backend corriendo en `localhost:3000`

### Instalación

```bash
cd mobile-tests
npm install
```

### Ejecución

```bash
npm run wdio
```

### Suites Disponibles

| Archivo | Qué cubre |
|---|---|
| `smoke.e2e.ts` | Pantalla de login visible, navegación login ↔ registro |
| `auth-login.e2e.ts` | Login completo → llega a Home |
| `auth-register.e2e.ts` | Registro → auto-login → llega a Home |
| `navigation.e2e.ts` | Recorre todas las tabs: Portfolio, Watchlist, Edgar, Transactions |
| `portfolio-buy.e2e.ts` | Compra acciones → verifica posición en portfolio |
| `portfolio-sell.e2e.ts` | Compra y venta parcial → verifica posición actualizada |
| `portfolio-profit-loss.e2e.ts` | Compra → verifica cards de resumen y P&L |
| `transactions.e2e.ts` | Compra → verifica transacción en historial |
| `watchlist.e2e.ts` | Agregar y eliminar item de watchlist |
| `edgar-search.e2e.ts` | Buscar empresa → seleccionar → verificar métricas e histórico |
| `update-prices.e2e.ts` | Botón de actualización de precios desde Home |
| `session-persistence.e2e.ts` | Login → cierre de app → reapertura → sigue autenticado |

Cada test crea su propio usuario vía `POST /auth/register` con email único. No depende de datos preexistentes.

Los tests usan **Page Object Model**: cada pantalla tiene su propio archivo en `test/pageobjects/` con selectores encapsulados y métodos reutilizables.

### Configuración

`wdio.conf.ts` apunta al emulador `Pixel_8` con el paquete `com.aseca.mobile`. Para cambiar el dispositivo o la URL del backend:

```typescript
// wdio.conf.ts
capabilities: [{
  'appium:deviceName': 'Pixel_8',       // nombre del emulador
  'appium:appPackage': 'com.aseca.mobile',
}]
```

```bash
# URL del backend para los helpers de creación de usuarios
API_BASE_URL=http://otro-backend:3000 npm test
```
