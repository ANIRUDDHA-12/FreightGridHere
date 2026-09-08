"use client";

import { ChevronRight } from "lucide-react";
import {AiSwarmDrawer} from "@/components/dashboard/ai-swarm-drawer";
import {useState} from "react"

/* ────────────────────────────────────────────────────────
   AssetMatrix — 3-card live telemetry network
   Accepts live vehicle data from Spring Boot backend.
   ──────────────────────────────────────────────────────── */

/* ── Props ── */

interface AssetMatrixProps {
  assets?: any[];
}

interface ActiveDisruptionCardProps {
    onInspectClick: () => void;
}

/* ── Data Row Helper ── */

interface DataRowProps {
  label: string;
  children: React.ReactNode;
}

function DataRow({ label, children }: DataRowProps) {
  return (
    <div className="flex items-center justify-between py-2.5 border-b border-white/5 last:border-b-0">
      <span className="text-xs font-medium text-zinc-400">{label}</span>
      <span className="text-sm font-semibold text-white">{children}</span>
    </div>
  );
}

/* ── Card Footer Helper ── */

interface CardFooterProps {
  left: string;
  right: string;
}

function CardFooter({ left, right }: CardFooterProps) {
  return (
    <div className="flex items-center justify-between border-t border-white/5 px-5 py-3 mt-auto">
      <span className="font-mono text-[11px] text-zinc-500">{left}</span>
      <span className="font-mono text-[11px] text-zinc-600">{right}</span>
    </div>
  );
}

/* ── Distribution Hub Card ── */

function DistributionHubCard() {
  return (
    <div className="flex flex-col rounded-lg border border-white/10 bg-[#0d0e10]">
      {/* Header */}
      <div className="flex items-start justify-between p-5 pb-0">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
            Distribution Node • Region 4
          </p>
          <h3 className="mt-1.5 mb-4 text-base font-semibold tracking-tight text-white">
            Mumbai Distribution Hub
          </h3>
        </div>
        <span className="rounded-md border border-purple-500/30 bg-purple-500/10 px-2 py-1 font-mono text-[11px] font-bold text-purple-400">
          HUB-02
        </span>
      </div>

      {/* Data Rows */}
      <div className="flex-1 px-5">
        {/* Capacity with progress bar */}
        <div className="py-2.5 border-b border-white/5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-zinc-400">
              Capacity Utilization
            </span>
            <span className="font-mono text-xs font-semibold text-white">
              88%
            </span>
          </div>
          <div className="mt-2 h-1.5 w-full overflow-hidden rounded-full bg-white/5">
            <div
              className="h-full rounded-full bg-purple-500"
              style={{ width: "88%" }}
            />
          </div>
        </div>

        <DataRow label="Daily Throughput">
          <span className="font-mono text-xs">420 t/day</span>
        </DataRow>

        <DataRow label="Node Status">
          <span className="flex items-center gap-1.5 text-xs">
            <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
            Operational (Nominal)
          </span>
        </DataRow>

        <DataRow label="Linked Assets">
          <span className="text-xs">12 active reefers docked / queued</span>
        </DataRow>
      </div>

      {/* Footer */}
      <CardFooter left="Synced 2s ago" right="#H-4029" />
    </div>
  );
}

/* ── In-Transit Asset Card (Live Data) ── */

function InTransitAssetCard({ vehicle }: { vehicle?: any }) {
  const v = vehicle;
  const id = v?.id ?? "TRK-01";
  const name = v?.name ?? "Heavy Reefer Unit 01";
  const route = v?.currentRoute ?? "Mumbai → Pune Express";
  const coreTemp = v?.telemetry?.coreTempCelsius ?? -18.2;
  const payloadLbs = v?.telemetry?.payloadLbs ?? 38400;
  const hos = v?.telemetry?.hosRemainingHours ?? 6.2;
  const gpsAccuracy = v?.telemetry?.gpsAccuracyMeters ?? 0.8;

  return (
    <div className="flex flex-col rounded-lg border border-white/10 bg-[#0d0e10]">
      {/* Header */}
      <div className="flex items-start justify-between p-5 pb-0">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
            Cold-Chain Asset • In Transit
          </p>
          <h3 className="mt-1.5 mb-4 text-base font-semibold tracking-tight text-white">
            {name}
          </h3>
        </div>
        <span className="rounded-md border border-cyan-500/30 bg-cyan-500/10 px-2 py-1 font-mono text-[11px] font-bold text-cyan-400">
          {id}
        </span>
      </div>

      {/* Data Rows */}
      <div className="flex-1 px-5">
        <DataRow label="Active Route">
          <span className="text-xs">
            {route}{" "}
            <span className="text-zinc-500">(KM 42.4)</span>
          </span>
        </DataRow>

        <DataRow label="Cargo Core Temp">
          <span className="inline-flex items-center rounded-full border border-cyan-500/30 bg-cyan-500/10 px-2 py-0.5 font-mono text-[11px] text-cyan-400">
            {coreTemp}°C (Optimal)
          </span>
        </DataRow>

        <DataRow label="Manifest Payload">
          <span className="text-xs">
            {payloadLbs.toLocaleString()} lbs{" "}
            <span className="text-zinc-500">(Pharma Grade)</span>
          </span>
        </DataRow>

        <DataRow label="Driver HOS">
          <span className="text-xs">
            <span className="font-medium">{hos}h remaining</span>{" "}
            <span className="text-zinc-500">(Compliant)</span>
          </span>
        </DataRow>
      </div>

      {/* Footer */}
      <CardFooter
        left={`GPS Lock: ${gpsAccuracy}m accuracy`}
        right="#CR-881"
      />
    </div>
  );
}

/* ── Active Disruption Card ── */

function ActiveDisruptionCard({onInspectClick}:ActiveDisruptionCardProps) {
  return (
    <div className="flex flex-col rounded-lg border border-white/10 bg-[#0d0e10]">
      {/* Header */}
      <div className="flex items-start justify-between p-5 pb-0">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
            Incident Detected • Route 66
          </p>
          <h3 className="mt-1.5 mb-4 text-base font-semibold tracking-tight text-white">
            Weather Flash Flood Alert
          </h3>
        </div>
        <span className="rounded-md border border-red-500/30 bg-red-500/10 px-2 py-1 font-mono text-[11px] font-bold text-red-400">
          INC-03
        </span>
      </div>

      {/* Data Rows — now using DataRow for consistency */}
      <div className="flex-1 px-5">
        <DataRow label="Direct Impact">
          <span className="text-xs">3 Units (TRK-01, 04, 09)</span>
        </DataRow>

        <DataRow label="Incident Severity">
          <span className="text-xs font-medium text-red-400">
            High (Roadway Blockade)
          </span>
        </DataRow>

        <DataRow label="AI Action">
          <span className="text-xs">Detour via NH48 (+24m ETA)</span>
        </DataRow>
      </div>

      {/* Inspect AI RCA Button */}
      <div className="px-5 pb-4">
        <button
          type="button"
          className="flex w-full mt-4 items-center justify-between rounded-md border border-white/10 bg-[#18191b] px-4 py-2 text-xs text-white transition-colors hover:bg-[#202226]"
          onClick={onInspectClick}
        >
          <span>Inspect AI RCA</span>
          <ChevronRight className="h-3.5 w-3.5 text-zinc-500" />
        </button>
      </div>

      {/* Footer */}
      <CardFooter
        left="Auto-Reroute Engine: Triggered 4m ago"
        right="#AI-RT-09"
      />
    </div>
  );
}

/* ── Main AssetMatrix Export ── */

export default function AssetMatrix({ assets }: AssetMatrixProps) {
    const [drawerOpen,isDrawerOpen]=useState(false)
  const activeVehicle =
    assets && assets.length > 0 ? assets[0] : undefined;

  return (
    <section>
      {/* Section Header */}
      <div className="mb-4 flex items-end justify-between">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
            Distributed Topology &amp; Asset Stream
          </p>
          <h2 className="mt-1 text-lg font-bold tracking-tight text-white">
            Live Telemetry Network
          </h2>
        </div>
        <div className="flex items-center gap-2">
          <span className="h-2 w-2 rounded-full bg-emerald-400" />
          <span className="text-xs text-zinc-400">
            Telemetry Ingestion Active
          </span>
        </div>
      </div>

      {/* 3-Card Grid */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-3">
        <DistributionHubCard />
        <InTransitAssetCard vehicle={activeVehicle} />
        <ActiveDisruptionCard  onInspectClick={()=>isDrawerOpen(true)}/>
      </div>
        <AiSwarmDrawer
        isOpen={drawerOpen}
        onClose={()=>isDrawerOpen(false)}
        />
    </section>
  );
}
