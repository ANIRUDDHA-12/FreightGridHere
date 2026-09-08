"use client";

import {
  LayoutDashboard,
  GitFork,
  Truck,
  AlertTriangle,
  Cpu,
  Sliders,
  ShieldCheck,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";

/* ────────────────────────────────────────────────────────
   Type Declarations
   ──────────────────────────────────────────────────────── */

interface NavItem {
  label: string;
  icon: LucideIcon;
  href: string;
  active?: boolean;
  /** Small numeric badge (e.g. incident count) */
  badge?: number;
  /** Colored status dot on the right edge */
  statusDot?: string;
}

interface NavGroup {
  label: string;
  items: NavItem[];
}

/* ────────────────────────────────────────────────────────
   Navigation Data
   ──────────────────────────────────────────────────────── */

const NAV_GROUPS: NavGroup[] = [
  {
    label: "NETWORK",
    items: [
      {
        label: "Overview",
        icon: LayoutDashboard,
        href: "/dashboard",
      },
      {
        label: "Node Matrix",
        icon: GitFork,
        href: "/dashboard/nodes",
        active: true,
      },
      {
        label: "Fleet Telemetry",
        icon: Truck,
        href: "/dashboard/fleet",
        statusDot: "bg-cyan-400",
      },
    ],
  },
  {
    label: "OPERATIONS",
    items: [
      {
        label: "Incident Stream",
        icon: AlertTriangle,
        href: "/dashboard/incidents",
        badge: 1,
      },
      {
        label: "AI Swarm Console",
        icon: Cpu,
        href: "/dashboard/ai-swarm",
        statusDot: "bg-purple-400",
      },
    ],
  },
  {
    label: "SYSTEM",
    items: [
      {
        label: "Settings",
        icon: Sliders,
        href: "/dashboard/settings",
      },
      {
        label: "Audit Logs",
        icon: ShieldCheck,
        href: "/dashboard/audit",
      },
    ],
  },
];

/* ────────────────────────────────────────────────────────
   Sidebar Component
   ──────────────────────────────────────────────────────── */

export default function Sidebar() {
  return (
    <aside className="flex h-full flex-col">
      {/* ── Brand Header ── */}
      <div className="flex h-16 items-center gap-3 border-b border-white/10 px-5">
        <div className="flex h-8 w-8 items-center justify-center rounded-md bg-white">
          <span className="text-xs font-bold text-black tracking-tight">
            FG
          </span>
        </div>
        <span className="text-sm font-bold tracking-tight text-white">
          FreightGrid
        </span>
      </div>

      {/* ── Navigation Groups ── */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
        {NAV_GROUPS.map((group) => (
          <div key={group.label}>
            {/* Group Eyebrow */}
            <p className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-zinc-500">
              {group.label}
            </p>

            {/* Group Items */}
            <ul className="space-y-0.5">
              {group.items.map((item) => {
                const Icon = item.icon;
                return (
                  <li key={item.label}>
                    <a
                      href={item.href}
                      className={`
                        group flex h-9 items-center gap-3 rounded-md px-3 text-xs font-medium transition-colors
                        ${
                          item.active
                            ? "bg-white/10 text-white"
                            : "text-zinc-400 hover:bg-white/5 hover:text-white"
                        }
                      `}
                    >
                      <Icon className="h-4 w-4 shrink-0" />
                      <span className="flex-1 truncate">{item.label}</span>

                      {/* Badge (numeric count) */}
                      {item.badge !== undefined && (
                        <span className="flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500/20 px-1.5 text-[10px] font-bold text-red-400">
                          {item.badge}
                        </span>
                      )}

                      {/* Status Dot */}
                      {item.statusDot && (
                        <span
                          className={`h-2 w-2 shrink-0 rounded-full ${item.statusDot}`}
                        />
                      )}
                    </a>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </nav>

      {/* ── Footer: Cluster Health Strip ── */}
      <div className="flex items-center justify-between border-t border-white/10 px-5 py-3">
        <div className="flex items-center gap-2">
          <span className="relative flex h-2 w-2">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-75" />
            <span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-400" />
          </span>
          <span className="text-[11px] text-zinc-400">Cluster 04: Online</span>
        </div>
        <span className="font-mono text-[11px] text-zinc-500">18ms</span>
      </div>
    </aside>
  );
}
