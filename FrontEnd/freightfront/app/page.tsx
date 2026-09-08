// import Image from "next/image";
import { BackgroundPaths } from "./components/ui/background-path";
import  DashboardPage from "./dashboard/page"

export default function Home() {
  return (
    <main className="w-full min-h-screen bg-black">
      {/*<BackgroundPaths  title="FreightGrid" />*/}
        <DashboardPage />
    </main>
  
  );
}
