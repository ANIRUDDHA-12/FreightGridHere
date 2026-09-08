<!-- BEGIN:nextjs-agent-rules -->

# AGENTS.md — Full-Stack Technical Architecture & Engineering Directives

## 1. Project Overview & Scope
FreightGrid is a real-time logistics control tower application.
- **Frontend:** Next.js 16 (App Router, Turbopack, Tailwind CSS, TypeScript strict mode, Framer Motion, lucide-react).
- **Backend:** Spring Boot (Java 26, Spring Security OAuth2 / Auth0, JPA/Hibernate, LangChain4j AI Swarm).
- **Workspace Architecture:** Monorepo root tracking both `/frontend` (or `/freightfront`) and `/backend`.

---

## 2. Frontend Development Directives

### A. Rendering Strategy & Hydration Boundaries
- Keep `page.tsx` as React Server Components (RSC) where possible for initial layout metadata.
- Wrap interactive panels (dynamic telemetry tables, sliders, modals) in dedicated Client Components using `"use client"`.
- **Hydration Determinism:** Never call `Math.random()`, `new Date()`, or browser-dependent globals directly during render passes. Pre-calculate values in `useMemo` or initialize via `useEffect` to prevent SSR mismatches.

### B. Hardware & Runtime Optimization
- Force GPU acceleration on continuous animations via Tailwind (`will-change-transform` or `will-change-[stroke-dashoffset,opacity]`).
- Do not add heavy external client packages without necessity. Use native Tailwind transitions and hardware-accelerated CSS over heavy JS animation lifecycles.

### C. State Management & Live Ingestion
- UI state (sidebar collapse, active tab) lives in lightweight React context or local component state.
- Live telemetry data streams should be buffered using custom hooks (`useTelemetryFeed`, `useAuditStream`).
- For polling mock states, implement an interval-driven tick hook that pushes entries into the Audit Trail without triggering full re-renders of parent wrappers.

---

## 3. Full-Stack Data Contracts

### A. Vehicle Asset Model (`VehicleEntity`)
```typescript
interface VehicleAsset {
  id: string;              // e.g., "TRK-01"
  name: string;            // e.g., "Heavy Reefer Unit 01"
  type: "REEFER" | "DRY_VAN" | "FLATBED";
  status: "IN_TRANSIT" | "MAINTENANCE" | "IDLE" | "REROUTING";
  currentRoute: string;    // e.g., "Mumbai -> Pune Express"
  telemetry: {
    speedKmh: number;
    coreTempCelsius?: number;
    payloadLbs: number;
    hosRemainingHours: number;
    gpsAccuracyMeters: number;
  };
}

<!-- END:nextjs-agent-rules -->
