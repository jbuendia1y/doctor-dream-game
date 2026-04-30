# 🧬 Doctor Dream: Guardianes del Cuerpo

Juego educativo 2D estilo pixel art desarrollado en Java, basado en combate por turnos con cartas. El jugador asume el rol de un doctor que combate enfermedades dentro del cuerpo humano utilizando conocimientos médicos representados como habilidades.

---

## 🎯 Objetivo del Proyecto

Aplicar conceptos de:

* Programación Orientada a Objetos (POO)
* Principios SOLID
* Estructuras de Datos (List, Stack)
* Arquitectura modular

Todo dentro de un entorno interactivo tipo videojuego.

---

## 🧩 Arquitectura del Sistema

El proyecto está dividido en módulos independientes para facilitar el desarrollo en equipo y reducir el acoplamiento.

```
src/
 ├── juego/        → Renderizado, game loop, mapa
 ├── combate/      → Lógica de combate por turnos
 ├── cartas/       → Tipos de cartas (ataque, defensa, efecto)
 ├── mazo/         → Gestión del mazo y estructuras de datos
 ├── entidades/    → Jugador, enemigo y abstracciones
```

---

## 🧠 Diseño basado en SOLID

### ✅ SRP (Responsabilidad Única)

Cada módulo tiene una responsabilidad clara:

* `GestorCombate` → combate
* `Mazo` → gestión de cartas
* `Juego` → flujo general
* `Entidad` → estado de personajes

---

### ✅ OCP (Abierto/Cerrado)

El sistema permite agregar nuevas cartas sin modificar código existente:

```java
class CartaVeneno extends Carta { ... }
```

---

### ✅ LSP (Sustitución de Liskov)

Se utiliza la abstracción `Combatiente`, permitiendo tratar jugadores y enemigos de forma uniforme.

---

### ✅ DIP (Inversión de Dependencias)

El combate depende de la interfaz `Combatiente` en lugar de clases concretas:

```java
private Combatiente jugador;
private Combatiente enemigo;
```

---

## ⚔️ Sistema de Combate

El combate es por turnos:

1. El jugador roba cartas
2. Usa una carta
3. Se aplica el efecto (daño, defensa, buff)
4. El enemigo responde
5. Se repite hasta que uno pierda

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

* **pilaRobo** → cartas disponibles
* **mano** → cartas utilizables
* **pilaDescarte** → cartas usadas

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

## 🎮 Renderizado y Juego

Incluye:

* Game loop
* Render con `JPanel`
* Animaciones por frames
* Sistema de tiles para el mapa
* Interfaz de cartas

---

## 👥 División del Trabajo

| Integrante | Módulo           |
| ---------- | ---------------- |
| Joaquín    | Combate + Cartas |
| Paolo      | Mazo             |
| Aldhair    | Render + Juego   |
| Dens       | Entidades        |

---

## 🚀 Estado del Proyecto

En desarrollo. Se implementarán progresivamente:

* Combate funcional
* Render básico
* Sistema completo de cartas

---

## 🧪 Ejecución (futuro)

```bash
javac Main.java
java Main
```

---

## 📌 Notas Finales

Este proyecto está diseñado para ser:

* Escalable
* Modular
* Fácil de mantener
* Didáctico para niños

---

## 🏁 Autoría

Proyecto desarrollado en equipo para el curso de Algoritmos y Estructura de Datos.
