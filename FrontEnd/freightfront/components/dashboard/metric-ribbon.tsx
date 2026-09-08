/* ────────────────────────────────────────────────────────
   MetricRibbon — 4-card KPI strip
   Server Component (no client interactivity)
   ──────────────────────────────────────────────────────── */

interface MetricCard {
  eyebrow: string;
  value: string;
  subtext: string;
  dotColor: string;
  subtextColor?: string;
}

const METRICS: MetricCard[] = [
  {
    eyebrow: "TOTAL NODES",
    value: "18 Active",
    subtext: "• 100% quorum health",
    dotColor: "bg-emerald-400",
    subtextColor: "text-emerald-400",
  },
  {
    eyebrow: "ACTIVE ASSETS",
    value: "142 In-Transit",
    subtext: "8 rerouted via telemetry",
    dotColor: "bg-cyan-400",
  },
  {
    eyebrow: "ACTIVE DISRUPTIONS",
    value: "1 Alert",
    subtext: "NH48 detour engaged",
    dotColor: "bg-red-400",
    subtextColor: "text-red-400",
  },
  {
    eyebrow: "AI SWARM STATUS",
    value: "Optimal",
    subtext: "Latency 14ms (p99)",
    dotColor: "bg-purple-400",
  },
];

export default function MetricRibbon() {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
      {METRICS.map((metric) => (
        <div
          key={metric.eyebrow}
          className="rounded-lg border border-white/10 bg-[#0d0e10] p-4"
        >
          {/* Eyebrow + Status Dot */}
          <div className="flex items-center justify-between">
            <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
              {metric.eyebrow}
            </p>
            <span className={`h-2 w-2 rounded-full ${metric.dotColor}`} />
          </div>

          {/* Big Metric Value */}
          <p className="mt-1 text-2xl font-bold tracking-tight text-white">
            {metric.value}
          </p>

          {/* Subtext */}
          <p
            className={`mt-1 text-xs ${
              metric.subtextColor ?? "text-zinc-400"
            }`}
          >
            {metric.subtext}
          </p>
        </div>
      ))}
    </div>
  );
}
