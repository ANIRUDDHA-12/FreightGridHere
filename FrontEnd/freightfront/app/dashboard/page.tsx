import MetricRibbon from "@/components/dashboard/metric-ribbon";
import AssetMatrix from "@/components/dashboard/asset-matrix";
import AuditTrailTable from "@/components/dashboard/audit-trail-table";
import {fetchLiveVehicles} from "@/lib/api/client";

/* ────────────────────────────────────────────────────────
   Dashboard Page — Telemetry Grid view
   Server Component (RSC) — assembles client sub-trees.
   force-dynamic: fetch vehicle data at request time, not
   during `next build` (backend may be unreachable at build).
   ──────────────────────────────────────────────────────── */

export const dynamic = "force-dynamic";


export default async function DashboardPage() {
    const liveVehicles=await fetchLiveVehicles()
  return (
    <div className="flex flex-col gap-8">
      {/* ── KPI Metric Strip ── */}
      <MetricRibbon />

      {/* ── Live Telemetry Network (3-Card Matrix) ── */}
      <AssetMatrix assets={liveVehicles} />

      {/* ── Telemetry Audit Trail (Data Table) ── */}
      <AuditTrailTable />
    </div>
  );
}
