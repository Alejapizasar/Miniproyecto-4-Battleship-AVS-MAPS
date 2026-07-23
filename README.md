# Batalla Naval — Miniproyecto #4

Juego de Batalla Naval (Battleship) contra la máquina, desarrollado en **Java 17 + JavaFX** siguiendo arquitectura **MVC**, con interfaz construida en **FXML/Scene Builder**, persistencia en archivos planos y serializables, concurrencia con hilos, manejo de excepciones propias y pruebas unitarias con JUnit.

## Grupo

- Alejandro Valencia Sandoval — 2515411
- Maria Alejandra Pizarro Sarria — 2519474

## Descripción del juego

Dos jugadores (humano y máquina) compiten por hundir la flota del oponente en tableros de 10x10. Cada jugador cuenta con una flota de 10 barcos:

| Barco | Cantidad | Casillas c/u |
|---|---|---|
| Portaaviones | 1 | 4 |
| Submarino | 2 | 3 |
| Destructor | 3 | 2 |
| Fragata | 4 | 1 |

El jugador humano coloca su flota en el **tablero de posición** (solo observación una vez inicia la partida) y dispara sobre el **tablero principal**, que representa el territorio de la máquina y se actualiza en tiempo real con los resultados: **agua** (X, pasa el turno), **tocado** (vuelve a disparar) y **hundido** (barco eliminado; si acierta puede seguir disparando, salvo que haya ganado). La máquina coloca su flota y dispara de forma autónoma y aleatoria, respetando las mismas reglas.

## Funcionalidades (historias de usuario)

- **HU-1 — Colocación de barcos:** colocación manual (horizontal/vertical) con validación de superposición y límites del tablero, más colocación aleatoria.
- **HU-2 — Disparos:** el jugador dispara sobre el tablero principal; el sistema resuelve agua/tocado/hundido y gestiona el cambio de turno.
- **HU-3 — Visualización del tablero del oponente:** opción para revisar la flota completa de la máquina con fines de verificación.
- **HU-4 — IA de la máquina:** colocación y disparo aleatorios, respetando las reglas del juego y sin repetir celdas ya jugadas.
- **HU-5 — Guardado automático:** el estado del juego (tablero serializado, nickname y barcos hundidos) se guarda tras cada jugada y se restaura automáticamente al continuar.

## Arquitectura y patrones

- **MVC:** `model` (entidades del juego), `view` (componentes gráficos y helpers de UI), `controller` (uno por vista FXML: Home, Player, Game, End).
- **Strategy:** `ShipPlacementStrategy` (`ManualPlacementStrategy` / `RandomPlacementStrategy`) y `ShotStrategy` (`RandomShotStrategy`), para intercambiar el algoritmo de colocación y disparo sin modificar los controladores.
- **Adapter:** `BoardInteractionAdapter` adapta eventos de la vista (`BoardInteractionListener`) hacia la lógica de tablero.
- **Principios SOLID** aplicados en la separación entre modelo, persistencia, estrategias y vista.

## Estructura del proyecto

```
src/main/java/com/example/miniproyecto4/
├── Main.java                  # Punto de entrada (Application)
├── concurrency/                # Hilos: turno de la máquina y temporizador de partida
├── controller/                  # HomeController, PlayerController, GameController, EndController
├── exceptions/                  # Excepciones propias del dominio
├── model/                       # Board, Ship, Fleet, Coordinate, HumanPlayer, MachinePlayer, PlayerData...
│   └── interfaces/               # Opponent, ShipPlacementStrategy, ShotStrategy
├── persistence/                  # Archivos planos y serializables (guardado/carga de partida)
├── strategy/                     # Implementaciones de las estrategias de colocación y disparo
└── view/                         # BoardCellView, DialogHelper, SceneNavigator, ShipShapeFactory...

src/main/resources/com/example/miniproyecto4/
├── Views/                        # HomeView.fxml, PlayerView.fxml, GameView.fxml, EndView.fxml
├── css/                          # battleship2game.css
└── cssguide/                     # battleship.css

src/test/java/model/              # Pruebas unitarias JUnit
```

## Concurrencia

- `MachineTurnThread`: ejecuta el turno de la máquina (colocación/disparo) sin bloquear la interfaz.
- `GameTimerThread`: temporizador de la partida, sincronizado con la UI mediante `Platform.runLater`.

## Persistencia

- **Archivos serializables:** estado completo del tablero (`SerializableGameState`, `GameStateSerializer`), para reanudar la partida exactamente donde quedó.
- **Archivos planos:** nickname del jugador y cantidad de barcos hundidos (`FlatFilePlayerRepository`), gestionados según la ubicación definida en `SaveLocation`.

## Excepciones

- `PlacementException` — colocación de barco inválida (fuera de tablero o superposición).
- `InvalidShotException` — disparo inválido (celda ya jugada, fuera de rango).
- `PersistenceException` — error de guardado/carga de la partida.
- `InvalidShipSizeException` — tamaño de barco inválido (no verificada / *runtime*).

## Pruebas unitarias

Ubicadas en `src/test/java/model`, con JUnit 5 (Jupiter):

- `BoardTest`
- `ShipTest`
- `PlayerDataTest`

## Tecnologías

- Java 17
- JavaFX (`javafx-controls`, `javafx-fxml`) 17.0.1
- FXML + Scene Builder
- Maven (`javafx-maven-plugin`)
- JUnit 5 (Jupiter)

## Requisitos previos

- JDK 17 o superior
- Maven 3.8+

## Cómo ejecutar

```bash
git clone https://github.com/Alejapizasar/Miniproyecto-4-Battleship-AVS-MAPS.git
cd Miniproyecto-4-Battleship-AVS-MAPS-development
mvn clean javafx:run
```

## Cómo correr las pruebas

```bash
mvn test
```

## Flujo de pantallas

1. **Home:** menú principal (nueva partida / continuar).
2. **Player (colocación):** el jugador ubica su flota en el tablero de posición (manual o aleatoria).
3. **Game:** tablero principal con los disparos, marcador de partida y guardado automático.
4. **End:** resultado final de la partida y estadísticas.

## Control de versiones

Repositorio en GitHub: [Miniproyecto-4-Battleship-AVS-MAPS](https://github.com/Alejapizasar/Miniproyecto-4-Battleship-AVS-MAPS), con historial de commits por funcionalidad y tag de versión final del entregable.
