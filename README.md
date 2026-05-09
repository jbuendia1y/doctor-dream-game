# 🧬 Doctor Dream: Guardianes del Cuerpo

Juego educativo 2D estilo pixel art desarrollado en Java con Swing, basado en combate por turnos con cartas. El jugador asume el rol de un doctor que combate enfermedades dentro del cuerpo humano utilizando conocimientos médicos representados como habilidades.

---

## 🎯 Objetivo del Proyecto

Aplicar conceptos de:

* Programación Orientada a Objetos (POO)
* Principios SOLID
* Estructuras de Datos (List, Stack, Queue)
* Arquitectura modular con separación de responsabilidades
* Patrón EventBus para desacoplar lógica de renderizado
* MVC (Modelo-Vista-Controlador) con Swing

---

## 🧩 Arquitectura del Sistema

El proyecto sigue una arquitectura en capas con EventBus como middleware de comunicación:

```
src/
 ├── juego/
 │   ├── eventbus/      → EventBus (pub/sub) + Eventos del sistema
 │   ├── controlador/   → Controladores (orquestan lógica de negocio)
 │   └── vista/         → Vistas Swing (JFrame, JPanels) — solo renderizan
 ├── combate/           → Lógica de combate por turnos
 ├── cartas/            → Tipos de cartas (ataque, defensa, efecto)
 ├── mazo/              → Gestión del mazo y estructuras de datos
 ├── entidades/         → Jugador, enemigo y abstracciones
 └── doctordream/       → Punto de entrada (main)
```

### Flujo de comunicación

```
Vista (Swing) ──llama──▶ Controlador ──modifica──▶ Modelo
                              │
                              ▼
                          EventBus ──notifica──▶ Vista (se actualiza sola)
```

Las vistas **nunca** modifican el modelo directamente. Los controladores orquestan la lógica y publican eventos. Las vistas se suscriben a eventos y se actualizan automáticamente.

---

## 🧠 Diseño basado en SOLID

### ✅ SRP (Responsabilidad Única)

Cada módulo tiene una responsabilidad clara:

* `GestorCombate` → lógica de combate
* `Mazo` → gestión de cartas
* `ControladorCombate` → orquestación del flujo de combate
* `PanelCombate` → solo renderizado de la UI de combate
* `EventBus` → comunicación desacoplada entre capas

### ✅ OCP (Abierto/Cerrado)

El sistema permite agregar nuevas cartas sin modificar código existente:

```java
class CartaVeneno extends Carta { ... }
```

### ✅ LSP (Sustitución de Liskov)

Se utiliza la abstracción `Combatiente`, permitiendo tratar jugadores y enemigos de forma uniforme.

### ✅ DIP (Inversión de Dependencias)

El combate depende de la interfaz `Combatiente` en lugar de clases concretas:

```java
private Combatiente jugador;
private Combatiente enemigo;
```

Las vistas dependen de controladores abstractos, no de implementaciones concretas del modelo.

---

## ⚔️ Sistema de Combate

El combate es por turnos:

1. El jugador roba cartas
2. Selecciona una carta de la mano (botón en UI)
3. El controlador aplica el efecto (daño, defensa, buff)
4. Publica eventos de actualización
5. El enemigo responde automáticamente
6. Se repite hasta que uno pierda

---

## 🃏 Sistema de Cartas

Las cartas utilizan polimorfismo:

```java
carta.usar(jugador, enemigo);
```

Tipos de cartas:

* Ataque → daño directo
* Defensa → escudo
* Efecto → buffs o debuffs

---

## 🧱 Sistema de Mazo

El mazo se divide en tres estados:

* **pilaRobo** → cartas disponibles (List)
* **mano** → cartas utilizables (Queue)
* **pilaDescarte** → cartas usadas (Stack)

Esto permite simular correctamente el flujo de un juego de cartas.

---

## 🧍 Entidades

Jerarquía:

* `Combatiente` (interfaz)
* `Entidad` (abstracta)
* `Jugador`
* `Enemigo`

Responsabilidades:

* Vida
* Daño
* Estado (vivo/muerto)

---

## 🎭 Patrón EventBus

El `EventBus` es un singleton que permite comunicación desacoplada:

```java
// Publicar evento
EventBus.getInstancia().publicar(Evento.VIDA_ACTUALIZADA, datos);

// Suscribirse desde la vista
EventBus.getInstancia().suscribir(Evento.VIDA_ACTUALIZADA, datos -> {
    lblVida.setText("❤️ " + jugador.obtenerVida());
});
```

Eventos del sistema:

| Evento              | Disparado por              | Escuchado por     |
|---------------------|----------------------------|-------------------|
| INICIAR_COMBATE     | ControladorMenu            | PanelCombate      |
| CARTA_USADA         | ControladorCombate         | PanelCombate      |
| TURNO_JUGADOR       | ControladorCombate         | PanelCombate      |
| TURNO_ENEMIGO       | ControladorCombate         | PanelCombate      |
| VIDA_ACTUALIZADA    | ControladorCombate         | PanelCombate      |
| COMBATE_TERMINADO   | ControladorCombate         | PanelCombate      |
| MOSTRAR_MENU        | PanelCombate/Controlador   | VentanaPrincipal  |
| MOSTRAR_COMBATE     | ControladorMenu            | VentanaPrincipal  |

---

## 🎮 Renderizado y Juego

Incluye:

* JFrame con CardLayout para navegar entre pantallas
* Panel de menú principal
* Panel de combate con HUD (vida, escudo, bonos)
* Botones de cartas interactivos
* Bitácora de combate
* Diálogos de victoria/derrota

---

## 👥 División del Trabajo

| Integrante | Módulo                        |
|------------|-------------------------------|
| Joaquín    | Combate + Cartas              |
| Paolo      | Mazo                          |
| Aldhair    | Vistas + EventBus             |
| Dens       | Entidades + Controladores     |

---

## 🚀 Estado del Proyecto

✅ En desarrollo funcional:

* ✅ Combate por turnos funcional
* ✅ Render con JFrame + JPanels
* ✅ Sistema de cartas (ataque, defensa, efecto)
* ✅ EventBus operativo
* ✅ Separación Vista-Controlador-Modelo

---

## 🧪 Ejecución

```bash
javac -d build src/**/*.java
java -cp build doctordream.DoctorDream
```

O desde NetBeans: abrir proyecto → Run (F6).

---

## 📌 Notas Finales

Este proyecto está diseñado para ser:

* Escalable
* Modular
* Fácil de mantener
* Didáctico para niños
* Extensible con nuevas cartas, enemigos y efectos

---

## 🏁 Autoría

Proyecto desarrollado en equipo para el curso de Algoritmos y Estructura de Datos.
