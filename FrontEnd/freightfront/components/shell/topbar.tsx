"use client";

import { Search, Plus } from "lucide-react";

/* ────────────────────────────────────────────────────────
   Topbar Component
   ──────────────────────────────────────────────────────── */

export default function Topbar() {
  return (
    <header className="flex h-full items-center justify-between px-8">
      {/* ── Left: Breadcrumb + Title ── */}
      <div className="flex flex-col justify-center">
        <p className="text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
          Region: Western Corridor
        </p>
        <h1 className="text-lg font-bold tracking-tight text-white">
          Telemetry Grid
        </h1>
      </div>

      {/* ── Center: Filter Input ── */}
      <div className="relative hidden md:block">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-zinc-500" />
        <input
          type="text"
          placeholder="Filter nodes, assets, route tags..."
          className="w-80 rounded-md border border-white/10 bg-[#141618] py-1.5 pl-9 pr-3 text-xs text-zinc-300 placeholder:text-zinc-600 outline-none focus:border-white/20 transition-colors"
        />
      </div>

      {/* ── Right: Actions ── */}
      <div className="flex items-center gap-3">
        {/* Network Health Pill */}
        <div className="hidden items-center gap-2 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-3 py-1 lg:flex">
          <span className="relative flex h-2 w-2">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-75" />
            <span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-400" />
          </span>
          <span className="font-mono text-[11px] font-medium text-emerald-400">
            NETWORK NOMINAL
          </span>
        </div>

        {/* Primary CTA */}
        <button
          type="button"
          className="flex items-center gap-1.5 rounded-md bg-white px-3.5 py-2 text-xs font-semibold text-black transition-colors hover:bg-zinc-200"
        >
          <Plus className="h-3.5 w-3.5" />
          New Dispatch
        </button>

        {/* User Profile Badge */}
        <div className="flex h-8 w-8 items-center justify-center rounded-md border border-white/10 bg-[#1c1e22] text-xs font-bold text-zinc-300">
          OP
        </div>
      </div>
    </header>
  );
}
