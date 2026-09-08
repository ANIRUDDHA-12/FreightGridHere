# FreightGrid UI Design System & Token Specifications

## 1. Design Philosophy
- **Canvas-First Ground:** The base canvas is absolute pure black (`#000000`).
- **Surface Lift Elevation:** No drop shadows. Elevation is achieved solely through stepped charcoal surfaces (`canvas` → `surface-1` → `surface-2`).
- **Hairline Precision:** All containers and dividers use 1px translucent borders (`rgba(255, 255, 255, 0.08)` to `rgba(255, 255, 255, 0.12)`).
- **Geometric Geometry:** No pill buttons. Interactive inputs and buttons use `rounded-md` (8px). Cards use `rounded-lg` (12px). Badges/dots use `rounded-full`.
- **Domain Accent Semantics:** Colors are functional identity signals, never decorative backgrounds:
  - Hubs & Topology: Terraform Purple (`#a855f7` / `#c084fc`)
  - Fleet & Telemetry: Waypoint Cyan (`#06b6d4` / `#22d3ee`)
  - Disruptions & Incidents: Consul Red / Coral (`#ef4444` / `#f87171`)
  - Nominal States & Consensus: Nomad Green / Emerald (`#10b981` / `#34d399`)

---

## 2. Color Palette & Token Mapping

| Token Name | Hex / Tailwind | Semantic Usage |
|---|---|---|
| `colors.canvas` | `#000000` / `bg-black` | Page base background, table row bases |
| `colors.surface-1` | `#0d0e10` / `bg-[#0d0e10]` | Sidebar, top metric cards, telemetry cards |
| `colors.surface-2` | `#141618` / `bg-[#141618]` | Input fields, active row hovers, sub-badges |
| `colors.surface-3` | `#1c1e22` / `bg-[#1c1e22]` | Inner chips, ID badges (`HUB-02`, `TRK-01`) |
| `colors.hairline` | `border-white/10` | 1px card outlines, horizontal dividers |
| `colors.hairline-subtle` | `border-white/5` | Subtle table row borders |
| `colors.ink` | `#ffffff` / `text-white` | Primary headlines, critical values, card titles |
| `colors.ink-muted` | `#9ca3af` / `text-zinc-400` | Secondary labels, body copy, descriptions |
| `colors.ink-subtle` | `#52525b` / `text-zinc-600` | Micro timestamps, table header keys |
| `colors.accent-purple` | `#c084fc` / `text-purple-400` | Hub indicators, AI swarm stats |
| `colors.accent-cyan` | `#22d3ee` / `text-cyan-400` | In-transit assets, speed/temperature telemetry |
| `colors.accent-red` | `#f87171` / `text-red-400` | Blockades, disruption alerts, incident counters |
| `colors.accent-green` | `#34d399` / `text-emerald-400` | Quorum health, nominal indicators, committed logs |

---

## 3. Typography Hierarchy

- **Font Family:** Inter / System Sans-Serif (Geist Sans compatible) for all UI labels; JetBrains Mono / Geist Mono for technical telemetry, IDs, timestamps, and coordinates.
- **Eyebrow (Mandatory for sections/cards):**
  `text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500`
- **Metric Big Numbers:**
  `text-2xl font-bold tracking-tight text-white`
- **Card Titles:**
  `text-base font-semibold tracking-tight text-white`
- **Telemetry Keys:**
  `text-xs font-normal text-zinc-400`
- **Telemetry Monospace Values:**
  `font-mono text-xs tracking-tight text-zinc-200`

---

## 4. Shell & Layout Grid Specs

### A. Sidebar (`w-60` or `w-64`)
- Fixed left layout, `bg-[#0d0e10]`, `border-r border-white/10`.
- **Header:** `h-16 flex items-center px-5 gap-3 border-b border-white/10`. Box icon with `FG` badge + bold `FreightGrid` wordmark.
- **Navigation Groups:** Categorized under uppercase eyebrows (`NETWORK`, `OPERATIONS`, `SYSTEM`).
- **Links:** `h-9 px-3 text-xs font-medium rounded-md text-zinc-400 hover:text-white hover:bg-white/5`.
- **Footer:** Micro-telemetry strip (`Cluster 04: Online`, `18ms` ping, green pulse indicator).

### B. Topbar (`h-16`)
- Fixed top layout, `bg-black`, `border-b border-white/10 px-8 flex items-center justify-between`.
- **Breadcrumb Area:** Eyebrow `REGION: WESTERN CORRIDOR` stacked above `Telemetry Grid` title (`text-lg font-bold text-white`).
- **Filter Bar:** Centered input, `bg-[#141618] border border-white/10 rounded-md px-3 py-1.5 text-xs text-zinc-300 w-80`.
- **Action Group:**
  - Health Pill: `bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 px-2.5 py-1 rounded-full text-[11px] font-mono`.
  - Primary CTA: `bg-white hover:bg-zinc-200 text-black font-semibold text-xs px-3.5 py-1.5 rounded-md flex items-center gap-1.5`.
  - User Badge: `bg-[#1c1e22] text-zinc-300 border border-white/10 rounded-md w-8 h-8 flex items-center justify-center text-xs font-bold`.

### C. Metric Ribbon (4-Card Row)
- Grid layout: `grid grid-cols-1 md:grid-cols-4 gap-4`.
- Card container: `bg-[#0d0e10] border border-white/10 rounded-lg p-4`.
- Header: Micro eyebrow + status dot.
- Metric: Big stat number + secondary micro-copy underneath (e.g., `• 100% quorum health`).

### D. Asset & Node Telemetry Matrix (3-Card Row)
- Grid layout: `grid grid-cols-1 md:grid-cols-3 gap-4`.
- Container: `bg-[#0d0e10] border border-white/10 rounded-lg p-5 flex flex-col justify-between`.
- Header: Eyebrow stacked with square badge tag (`HUB-02`, `TRK-01`, `INC-03`).
- Spec Table: Borderless key-value rows with hairline divider lines.
- Footer: Monospace sync state and device IDs (`#H-4029`, `#CR-881`, `#AI-RT-09`).

### E. Telemetry Audit Trail (Tabular Feed)
- Container: `bg-[#0d0e10] border border-white/10 rounded-lg p-5`.
- Header: Eyebrow + Title stacked on left; `Auto-sync: 1000ms` green indicator on right.
- Columns: `TIMESTAMP`, `EVENT TYPE`, `TARGET UNIT / NODE`, `PARAMETERS & PAYLOAD`, `CONSENSUS STATUS`.
- Monospace rows with colored semantic badges (`COMMITTED`, `ACKNOWLEDGED`, `RESOLVING`, `QUORUM_OK`).