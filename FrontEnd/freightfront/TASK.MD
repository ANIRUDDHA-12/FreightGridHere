### 3. `task.md`

```markdown
# FreightGrid Implementation Task Roadmap

## Status Dashboard
- **Current Phase:** Sprint 1 — The Command Shell
- **Next Milestone:** Interactive Shell & Live Telemetry Wireframe
- **Target Stack:** Next.js App Router + TypeScript + Tailwind CSS

---

## Sprint 1: Persistent Command Shell & Navigation
- [ ] **Task 1.1: Base Token Setup**
  - Verify Tailwind configuration maps `#0d0e10` (Surface-1), `#141618` (Surface-2), and border opacity classes.
  - Set default body background to `#000000` and text color to `#ffffff` in `app/globals.css`.
- [ ] **Task 1.2: Shell Layout Assembly**
  - Create `components/shell/sidebar.tsx`: implement grouped navigation with eyebrows, status indicators, and footer cluster health.
  - Create `components/shell/topbar.tsx`: implement corridor breadcrumb, search input, network health badge, and "+ New Dispatch" primary CTA.
  - Create `components/shell/command-shell.tsx`: assemble Sidebar + Topbar + Scrollable Main Content area.
- [ ] **Task 1.3: Wire Shell into Layout**
  - Integrate `CommandShell` into `app/dashboard/layout.tsx` or `app/layout.tsx` without breaking existing hero routes.

---

## Sprint 2: High-Density Telemetry Grid
- [ ] **Task 2.1: Metric Ribbon Component**
  - Create `components/dashboard/metric-ribbon.tsx`.
  - Implement 4-card metric strip (Total Nodes, Active Assets, Active Disruptions, AI Swarm Status) with domain accent dots.
- [ ] **Task 2.2: Live Telemetry Matrix Cards**
  - Create `components/dashboard/asset-card.tsx` supporting Hubs, Reefers, and Incidents.
  - Implement tabular key-value metrics with borderless rows and technical monospace footers.
- [ ] **Task 2.3: Telemetry Audit Trail Feed**
  - Create `components/dashboard/audit-trail-table.tsx`.
  - Format live timestamps (`HH:mm:ss.SSS`), event type badges, payload descriptors, and consensus pills (`COMMITTED`, `RESOLVING`).

---

## Sprint 3: Mock Data & Interactive State
- [ ] **Task 3.1: Data Fixtures & Providers**
  - Create `lib/mock-telemetry.ts` containing the initial nodes (`HUB-02`), assets (`TRK-01`), and active incidents (`INC-03`).
- [ ] **Task 3.2: Simulated Feed Ingestion**
  - Write a lightweight polling/interval hook that periodically pushes simulated log items to the audit trail to confirm fluid rendering.

---

## Sprint 4: Spring Boot Backend Integration
- [ ] **Task 4.1: API Service Layer**
  - Create `lib/api/client.ts` with authenticated fetch utilities handling Auth0 JWT tokens.
- [ ] **Task 4.2: Vehicles & Node Endpoints**
  - Connect `GET /api/v1/vehicles/list` to populate asset metrics and cards.
  - Connect `GET /api/v1/nodes/list` to populate the distribution node metrics.
- [ ] **Task 4.3: Incident & RCA Feed**
  - Connect `GET /api/v1/incidents/active` to feed the Incident Detected card and Audit Trail.

---

## Sprint 5: Incident Inspection & AI Swarm Drawer
- [ ] **Task 5.1: Slide-Over Incident Drawer**
  - Implement a slide-over panel triggered by clicking "Inspect AI RCA".
- [ ] **Task 5.2: LangChain4j Markdown Report Rendering**
  - Render Root Cause Analysis markdown reports with suggested detour chips.