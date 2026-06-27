# AGENTS.md

# Physics Simulation Suite

## Project Overview

Physics Simulation Suite is a modular JavaFX application for building interactive physics simulations, calculators, and educational visualization tools.

The project is designed around strict separation of:

* Physics logic
* User interface
* Rendering
* Navigation
* Feature registration

The architecture must remain scalable so new simulation modules can be added without modifying existing ones.

---

# Technology Stack

* Java
* JavaFX
* Canvas API
* AnimationTimer
* CSS Styling
* Component-Based UI Architecture

---

# Project Structure

```text
src/
├── com/
│   └── physicssim/
│
│       ├── app/
│       │   ├── PhysicsSimulatorApp.java
│       │   └── AppShell.java
│
│       ├── navigation/
│       │   ├── NavigationController.java
│       │   └── ViewType.java
│
│       ├── views/
│       │   ├── HomeView.java
│       │   ├── SimulationsView.java
│       │   ├── HelpView.java
│       │   └── AboutView.java
│
│       ├── model/
│       │   ├── SimulationCatalog.java
│       │   ├── SimulationItem.java
│       │   └── SimulationType.java
│
│       ├── components/
│       │   ├── AppHeader.java
│       │   ├── AppFooter.java
│       │   ├── PhysicsButton.java
│       │   ├── SimulationCard.java
│       │   ├── SimulationIconFactory.java
│       │   └── ...
│
│       ├── features/
│       │
│       │   ├── pendulum/
│       │   ├── mechanics/
│       │   └── future-modules/
│
│       └── theme/
│           └── AppTheme.java
│
└── resources/
    └── style/
        └── css/
```

---

# Folder Responsibilities

## app/

Application bootstrap layer.

Responsibilities:

* Application startup
* Scene creation
* Root layout management

Must not contain:

* Physics calculations
* Feature-specific logic

---

## navigation/

Application routing layer.

Responsibilities:

* Navigation state
* View switching
* Menu routing

Must not contain:

* Physics calculations
* Rendering logic

---

## views/

Top-level application pages.

Examples:

* HomeView
* SimulationsView
* AboutView
* HelpView

These are application screens, not simulation engines.

---

## model/

Global application models and registries.

Examples:

* SimulationCatalog
* SimulationItem
* SimulationType

Responsibilities:

* Simulation metadata
* Simulation registration
* Feature discovery

Physics engines may also exist in feature-specific folders when tightly coupled to a feature.

---

## components/

Reusable UI building blocks.

Examples:

* Buttons
* Cards
* Headers
* Footers
* Icons

Rules:

* Reusable across multiple features.
* No simulation-specific calculations.
* Avoid feature coupling.

---

## features/

Contains all simulation and calculator modules.

Each feature should be self-contained.

Examples:

```text
features/
├── pendulum/
├── mechanics/
├── optics/
├── thermodynamics/
├── waves/
└── electricity/
```

A feature folder owns:

* UI
* Rendering
* Simulation coordination
* Feature-specific models

A feature should be removable with minimal impact on the rest of the system.

---

## theme/

Global styling and design tokens.

Responsibilities:

* Colors
* Typography
* Layout constants
* Shared visual rules

Avoid hardcoded styling when a theme value exists.

---

## resources/

External resources.

Examples:

* CSS
* Icons
* Images
* Fonts

---

# Feature Architecture Pattern

Every simulation feature should follow a coordinator pattern.

Example:

```text
Feature Input
      ↓
Control Panel
      ↓
Model
      ↓
Feature View
      ↓
Canvas / Charts
```

The View acts as the coordinator.

---

# Recommended Feature Structure

For simulation-heavy modules:

```text
FeatureName/

├── FeatureModel.java
├── FeatureSimulationView.java
├── FeatureControlPanel.java
├── FeatureCanvas.java
└── FeatureChartCard.java
```

Not every feature requires all components.

Calculator-style tools may only need:

```text
CalculatorView.java
CalculatorModel.java
```

---

# Core Architectural Rules

## Rule 1: Models Are Headless

Models must not know about:

* JavaFX
* Canvas
* CSS
* UI Controls

Models only manage state and calculations.

---

## Rule 2: Rendering Is Passive

Canvas classes:

* Draw data
* Do not calculate physics

Rendering consumes state.

Rendering never owns state.

---

## Rule 3: Views Coordinate

Views manage:

* AnimationTimer
* Lifecycle
* User actions
* Data synchronization

Views connect models to UI.

---

## Rule 4: Components Stay Reusable

If a component can be reused by another feature, place it in:

```text
components/
```

Avoid duplicating UI controls.

---

## Rule 5: Feature Isolation

A feature should not directly depend on another feature's internal classes.

Communication should happen through:

* Registries
* Shared components
* Shared models

---

# Data Ownership

## Model Owns

* Physical variables
* Equations
* Simulation state

Examples:

* velocity
* acceleration
* angle
* mass
* force

---

## View Owns

* Animation lifecycle
* History collections
* State synchronization

Examples:

* angleHistory
* velocityHistory
* graph datasets

---

## Canvas Owns

* Drawing operations

Only rendering.

---

## Control Panels Own

* User interaction widgets

Only input handling.

---

# Simulation Registration

All discoverable simulations must be registered through:

```java
SimulationCatalog
SimulationItem
SimulationType
```

Navigation and menus should rely on the registry rather than hardcoded feature lists.

---

# Development Guidelines

When modifying code:

1. Preserve folder boundaries.
2. Keep physics logic separate from UI.
3. Keep rendering separate from simulation calculations.
4. Prefer reusable components.
5. Follow existing naming conventions.
6. Register new simulations through the catalog system.
7. Design new features to be self-contained.
8. Minimize cross-feature dependencies.
9. Keep classes focused on a single responsibility.
10. Favor extensibility over feature-specific shortcuts.

---

# Long-Term Goal

The codebase should evolve into a collection of independent physics modules that share:

* Navigation
* Theme
* Components
* Registry infrastructure

while remaining isolated in implementation.
