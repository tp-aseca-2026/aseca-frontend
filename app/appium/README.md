# Appium mobile smoke tests

Tests E2E mínimos sobre la app Android real instalada en el emulador.

## Qué cubre

- Register con usuario random.
- Llegada al Home/Dashboard.
- Logout.
- Login con el usuario recién creado.

## Cómo correr

1. Levantar backend:

```bash
cd ../../../aseca-backend
docker compose up -d
```

2. Instalar app debug en el emulador:

```bash
cd ../aseca-frontend/app
./gradlew :app:installDebug
```

3. Levantar Appium:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
npx appium --port 4723
```

4. Ejecutar test:

```bash
cd appium
npm install
npm test
```

Si es la primera vez, instalar el driver de Android:

```bash
npx appium driver install uiautomator2
```
