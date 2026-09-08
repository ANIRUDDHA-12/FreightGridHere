/* ────────────────────────────────────────────────────────
   AuditTrailTable — Real-time telemetry dispatches feed
   Server Component (no client interactivity)
   ──────────────────────────────────────────────────────── */

/* ── Type Declarations ── */

type ConsensusStatus = "COMMITTED" | "ACKNOWLEDGED" | "RESOLVING" | "QUORUM_OK";

interface AuditEntry {
  timestamp: string;
  eventType: string;
  eventColor: string;
  target: string;
  payload: string;
  consensus: ConsensusStatus;
}

/* ── Consensus Pill Styling Map ── */

const CONSENSUS_STYLES: Record<ConsensusStatus, string> = {
  COMMITTED:
    "border-emerald-500/40 text-emerald-400",
  ACKNOWLEDGED:
    "border-emerald-500/40 text-emerald-400",
  RESOLVING:
    "border-red-500/40 text-red-400",
  QUORUM_OK:
    "border-emerald-500/40 text-emerald-400",
};

/* ── Mock Audit Trail Data ── */

const AUDIT_ENTRIES: AuditEntry[] = [
  {
    timestamp: "14:02:18.409",
    eventType: "ROUTE_REROUTE",
    eventColor: "bg-cyan-400",
    target: "TRK-01 / REEFER",
    payload:
      "Rerouted via NH48 bypass (Flash flood incident INC-03 avoidance)",
    consensus: "COMMITTED",
  },
  {
    timestamp: "14:01:55.112",
    eventType: "CAPACITY_SPIKE",
    eventColor: "bg-purple-400",
    target: "HUB-02 / MUMBAI",
    payload:
      "Threshold crossed >85% utilization (88.4% current)",
    consensus: "ACKNOWLEDGED",
  },
  {
    timestamp: "13:58:30.884",
    eventType: "INCIDENT_TRIGGER",
    eventColor: "bg-red-400",
    target: "CORRIDOR_W_48",
    payload:
      "Sensors detect 280mm standing water (KM 61.2); Roadway shutdown broadcast",
    consensus: "RESOLVING",
  },
  {
    timestamp: "13:55:04.221",
    eventType: "TELEMETRY_HEARTBEAT",
    eventColor: "bg-emerald-400",
    target: "CLUSTER_04_INGEST",
    payload:
      "Quorum verified across 18 distributed Raft instances (45,020 evt/sec)",
    consensus: "QUORUM_OK",
  },
];

/* ── Table Header Columns ── */

const COLUMNS = [
  "TIMESTAMP",
  "EVENT TYPE",
  "TARGET UNIT / NODE",
  "PARAMETERS & PAYLOAD",
  "CONSENSUS STATUS",
] as const;

/* ── AuditTrailTable Component ── */

export default function AuditTrailTable() {
  return (
    <section>
      {/* Section Header */}
      <div className="mb-4 flex items-end justify-between">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
            Telemetry Audit Trail
          </p>
          <h2 className="mt-1 text-lg font-bold tracking-tight text-white">
            Real-Time Terminal Dispatches &amp; AI Rerouting Feeds
          </h2>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs text-zinc-400">Auto-sync: 1000ms</span>
          <span className="h-2 w-2 rounded-full bg-emerald-400" />
        </div>
      </div>

      {/* Table Container */}
      <div className="overflow-x-auto rounded-lg border border-white/10 bg-[#0d0e10] p-5">
        <table className="w-full min-w-[800px] text-left">
          {/* Table Head */}
          <thead>
            <tr>
              {COLUMNS.map((col) => (
                <th
                  key={col}
                  className="pb-4 text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-600"
                >
                  {col}
                </th>
              ))}
            </tr>
          </thead>

          {/* Table Body */}
          <tbody className="divide-y divide-white/5">
            {AUDIT_ENTRIES.map((entry) => (
              <tr key={entry.timestamp} className="group">
                {/* Timestamp */}
                <td className="py-3 pr-4 align-top font-mono text-xs tracking-tight text-zinc-300">
                  {entry.timestamp}
                </td>

                {/* Event Type */}
                <td className="py-3 pr-4 align-top">
                  <span className="flex items-center gap-2">
                    <span
                      className={`h-1.5 w-1.5 rounded-full ${entry.eventColor}`}
                    />
                    <span className="font-mono text-xs tracking-tight text-zinc-200">
                      {entry.eventType}
                    </span>
                  </span>
                </td>

                {/* Target Unit / Node */}
                <td className="py-3 pr-4 align-top font-mono text-xs tracking-tight text-zinc-300">
                  {entry.target}
                </td>

                {/* Parameters & Payload */}
                <td className="max-w-xs py-3 pr-4 align-top text-xs text-zinc-400">
                  {entry.payload}
                </td>

                {/* Consensus Status Pill */}
                <td className="py-3 align-top">
                  <span
                    className={`inline-flex rounded-md border px-2.5 py-1 font-mono text-[11px] font-medium ${
                      CONSENSUS_STYLES[entry.consensus]
                    }`}
                  >
                    {entry.consensus}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
