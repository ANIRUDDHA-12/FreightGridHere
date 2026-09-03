"use client";

import { useRef } from "react";
import { motion, useScroll, useTransform } from "framer-motion";
import { MapPin, Truck, CloudLightning, Activity } from "lucide-react";
import Image from "next/image";

// Logistics Mock Data mapped directly to your FreightGrid concepts
const ASSET_DATA = [
    {
        id: "TRK-01",
        title: "HEAVY REEFER",
        desc: "Cold-chain active. Capacity: 40k lbs.",
        src: "https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?q=80&w=800&auto=format&fit=crop",
        icon: Truck,
        status: "active",
    },
    {
        id: "HUB-02",
        title: "MUMBAI DIST",
        desc: "Sector 4 Warehouse. Output: High.",
        src: "https://images.unsplash.com/photo-1586528116311-ad8ed745f43c?q=80&w=800&auto=format&fit=crop",
        icon: MapPin,
        status: "optimal",
    },
    {
        id: "INC-03",
        title: "WEATHER ALERT",
        desc: "Heavy rain on Route 66. Rerouting.",
        src: "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=800&auto=format&fit=crop",
        icon: CloudLightning,
        status: "warning",
    },
    {
        id: "TRK-04",
        title: "FLATBED UNIT",
        desc: "Oversized cargo. Driver HOS: 8.5",
        src: "https://images.unsplash.com/photo-1519003722824-194d4455a60c?q=80&w=800&auto=format&fit=crop",
        icon: Activity,
        status: "active",
    },
];

export function AssetSlider() {
    const scrollRef = useRef<HTMLDivElement>(null);

    // Connect Framer Motion to the native horizontal scroll of our container
    const { scrollXProgress } = useScroll({ container: scrollRef });

    // Parallax effect for the background gradient based on scroll position
    const bgX = useTransform(scrollXProgress, [0, 1], ["0%", "-20%"]);

    return (
        <section className="relative h-screen w-full overflow-hidden bg-slate-950 text-slate-50 font-sans flex flex-col">
            {/* Dynamic Background */}
            <motion.div
                className="absolute inset-0 z-0 opacity-20 pointer-events-none"
                style={{
                    background: "radial-gradient(circle at center, #1e293b 0%, #020617 100%)",
                    x: bgX
                }}
            />

            {/* Header Overlay */}
            <header className="relative z-20 flex flex-col items-center pt-16 pb-8 px-6 text-center shrink-0">
                <motion.div
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.8, ease: "easeOut" }}
                >
                    <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-cyan-200">
                        FreightGrid Asset Network
                    </h1>
                    <p className="mt-4 text-sm md:text-base tracking-[0.1em] text-slate-400 uppercase">
                        Scroll horizontally to explore live telemetry
                    </p>
                </motion.div>
            </header>

            {/* The Scrollable Slider */}
            <div
                ref={scrollRef}
                className="relative z-10 flex flex-1 w-full overflow-x-auto snap-x snap-mandatory hide-scrollbar items-center pb-12 pt-4 px-[10vw] md:px-[20vw] gap-8"
                style={{ scrollBehavior: 'smooth' }}
            >
                {ASSET_DATA.map((item, idx) => {
                    const Icon = item.icon;

                    return (
                        <motion.div
                            key={item.id}
                            initial={{ opacity: 0, scale: 0.9 }}
                            animate={{ opacity: 1, scale: 1 }}
                            transition={{ delay: idx * 0.15, duration: 0.6 }}
                            className="relative group shrink-0 w-[80vw] sm:w-[50vw] md:w-[400px] aspect-[3/4] snap-center cursor-grab active:cursor-grabbing rounded-2xl overflow-hidden border border-slate-800 bg-slate-900 shadow-2xl"
                        >
                            {/* Image with hover scale (Tailwind group-hover) */}
                            <div className="absolute inset-0 w-full h-full transition-transform duration-700 ease-out group-hover:scale-105">
                                <Image
                                    src={item.src}
                                    alt={item.title}
                                    fill
                                    className="object-cover opacity-60 group-hover:opacity-80 transition-opacity duration-500"
                                    sizes="(max-width: 768px) 80vw, 400px"
                                    priority={idx < 2}
                                />
                            </div>

                            {/* Persistent Top Badges */}
                            <div className="absolute top-4 left-4 right-4 flex justify-between items-center z-20">
                <span className="px-3 py-1 bg-slate-950/80 backdrop-blur-md rounded-full text-xs font-bold tracking-wider text-slate-300 border border-slate-700/50">
                  {item.id}
                </span>
                                <span className={`p-2 rounded-full backdrop-blur-md border ${
                                    item.status === 'warning' ? 'bg-red-500/20 border-red-500/50 text-red-400' :
                                        item.status === 'optimal' ? 'bg-green-500/20 border-green-500/50 text-green-400' :
                                            'bg-blue-500/20 border-blue-500/50 text-blue-400'
                                }`}>
                  <Icon size={16} />
                </span>
                            </div>

                            {/* Gradient overlay that slides up slightly on hover */}
                            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-900/50 to-transparent opacity-80 group-hover:opacity-90 transition-opacity duration-500" />

                            {/* Bottom Text Content (Animated with Framer Motion on hover) */}
                            <motion.div
                                className="absolute bottom-0 left-0 right-0 p-6 z-20 flex flex-col justify-end h-1/2"
                                initial={false}
                                whileHover={{ y: 0 }}
                                animate={{ y: 10 }}
                            >
                                <div className="transform transition-transform duration-500 ease-out group-hover:-translate-y-2">
                                    <h2 className="text-2xl font-bold text-slate-100 tracking-wide mb-2 drop-shadow-md">
                                        {item.title}
                                    </h2>
                                    <p className="text-sm text-slate-300 leading-relaxed font-medium opacity-0 translate-y-4 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-500 delay-75">
                                        {item.desc}
                                    </p>
                                </div>
                            </motion.div>
                        </motion.div>
                    );
                })}
            </div>

            {/* Global CSS to hide the ugly scrollbar but keep functionality */}
            <style>
                {`
          .hide-scrollbar::-webkit-scrollbar { display: none; }
          .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
        `}
            </style>
        </section>
    );
}