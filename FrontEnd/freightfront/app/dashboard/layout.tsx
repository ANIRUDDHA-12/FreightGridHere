import Sidebar from "@/components/shell/sidebar";
import Topbar from "@/components/shell/topbar";

/* ────────────────────────────────────────────────────────
   Dashboard Layout — Strict Flex Shell
   Sidebar (in-flow flex child) + Topbar + Scrollable Main
   ──────────────────────────────────────────────────────── */

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen w-full bg-black text-white overflow-hidden">
      {/* Sidebar: Fixed width, doesn't shrink */}
      <div className="w-64 shrink-0 h-full border-r border-white/10 bg-[#0d0e10]">
        <Sidebar />
      </div>

      {/* Right Column: Topbar + Main Content */}
      <div className="flex-1 flex flex-col min-w-0 h-full">
        <div className="h-16 shrink-0 border-b border-white/10 bg-black z-10">
          <Topbar />
        </div>

        {/* Scrollable Main Content */}
        <main className="flex-1 overflow-y-auto p-8 space-y-8">
          {children}
        </main>
      </div>
    </div>
  );
}
