"use client";

import React, { useState, useEffect, useRef } from "react";
import { X, Zap, Hexagon, Send } from "lucide-react";
import ReactMarkdown from "react-markdown";
import { streamAiSwarm } from "@/lib/api/client";

export interface Message {
  id: string;
  role: "user" | "ai";
  type: "rca_card" | "kpi_card" | "markdown";
  content?: string;
  timestamp: string;
  trajectory?: string[];
  cardPayload?: {
    incidentId?: string;
    confidence?: string;
    suggestedAction?: string;
    metrics?: { label: string; value: string; color: string }[];
  };
}

interface AiSwarmDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  sessionId?: string;
}

const INITIAL_MESSAGES: Message[] = [
  {
    id: "m1",
    role: "ai",
    type: "rca_card",
    timestamp: "14:22:24",
    content: `Autonomous multi-agent synthesis detects severe congestion on **Corridor Sector 7 (NH48)** due to an unannounced bridge weigh-station inspection. Vehicle \`TK-8942-AX\` current idle duration exceeds threshold by 34 minutes with active cold-chain freight payload.

**IDENTIFIED ROOT CAUSES:**
- **Telemetry Discrepancy:** Primary toll gateway node experienced API timeout (HTTP 504), causing localized queue backup of 3.8 km.
- **Reefer Power Draw:** Aux refrigeration unit switched to high-cycle compressor mode to compensate for prolonged idling ambient heat (+38°C).`,
    cardPayload: {
      incidentId: "#INC-4409",
      confidence: "97.4%",
      suggestedAction: "Bypass via Sector 9",
    },
  },
  {
    id: "m2",
    role: "user",
    type: "markdown",
    content: "What is the ETA penalty if we reroute through NH48 Bypass West, and has the driver been notified of reefer fuel burn?",
    timestamp: "14:23:05",
  },
  {
    id: "m3",
    role: "ai",
    type: "kpi_card",
    timestamp: "14:23:12",
    content: "Bypass West reduces net transit delay from <span class=\"text-[#f87171]\">+47 min</span> down to <span class=\"text-[#10b981]\">+11 min</span>. Fuel burn variance is estimated at **+4.2 Liters**, which remains well within the ±8% cold-chain SLA safety margin.",
    cardPayload: {
      metrics: [
        { label: "NET TIME SAVED", value: "36 mins", color: "text-[#10b981]" },
        { label: "DRIVER NOTIFICATION", value: "Pending Approval", color: "text-amber-500" },
      ],
    },
  },
];

export function AiSwarmDrawer({ isOpen, onClose, sessionId = "swn-8912b" }: AiSwarmDrawerProps) {
  const [messages, setMessages] = useState<Message[]>(INITIAL_MESSAGES);
  const [input, setInput] = useState("");
  const [isThinking, setIsThinking] = useState(false);
  const [currentStream, setCurrentStream] = useState("");
  const [currentThoughts, setCurrentThoughts] = useState<string[]>([]);
  const scrollRef = useRef<HTMLDivElement>(null);

  // We need refs to hold the latest stream state for the onComplete callback
  const streamRef = useRef("");
  const thoughtsRef = useRef<string[]>([]);

  useEffect(() => {
    streamRef.current = currentStream;
    thoughtsRef.current = currentThoughts;
  }, [currentStream, currentThoughts]);

  // Auto-scroll to bottom when new content arrives
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, currentStream, currentThoughts, isThinking]);

  const handleSend = async (e?: React.FormEvent, overrideInput?: string) => {
    if (e) e.preventDefault();
    const query = overrideInput || input;
    if (!query.trim() || isThinking) return;

    const userMessage: Message = {
      id: Date.now().toString() + "-user",
      role: "user",
      type: "markdown",
      content: query,
      timestamp: new Date().toISOString().substring(11, 19),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsThinking(true);
    setCurrentStream("");
    setCurrentThoughts([]);
    streamRef.current = "";
    thoughtsRef.current = [];

    await streamAiSwarm(
      query,
      sessionId,
      (thought) => setCurrentThoughts((prev) => [...prev, thought]),
      (token) => setCurrentStream((prev) => prev + token),
      () => {
        setIsThinking(false);
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now().toString() + "-ai",
            role: "ai",
            type: "markdown",
            content: streamRef.current,
            timestamp: new Date().toISOString().substring(11, 19),
            trajectory: [...thoughtsRef.current],
          },
        ]);
        setCurrentStream("");
        setCurrentThoughts([]);
      },
      (err) => {
        console.error("Stream error:", err);
        setIsThinking(false);
      }
    );
  };

  // Do not render drawer until client hydration to avoid hydration mismatch 
  // since we check window for escape key. However, Next.js 'use client' handles this.
  
  // Close on Escape key
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [isOpen, onClose]);

  return (
    <>
      {/* Backdrop */}
      <div
        className={`fixed inset-0 bg-[#0d0e10]/80 backdrop-blur-sm z-40 transition-opacity duration-300 ${
          isOpen ? "opacity-100" : "opacity-0 pointer-events-none"
        }`}
        onClick={onClose}
      />

      {/* Drawer */}
      <div
        className={`fixed top-0 right-0 bottom-0 w-full max-w-[500px] bg-[#0d0e10] border-l border-white/10 shadow-2xl z-50 flex flex-col transition-transform duration-300 ease-out transform ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {/* Header */}
        <div className="flex-none">
          <div className="h-16 px-6 border-b border-white/10 flex items-center justify-between">
            <div>
              <div className="flex items-center gap-2 mb-1">
                <div className="w-2 h-2 rounded-full bg-purple-500 animate-pulse" />
                <span className="text-[10px] font-mono text-purple-500 tracking-wider font-semibold">AI SWARM CONSOLE</span>
                <span className="bg-[#1c1e22] text-zinc-400 font-mono text-[10px] px-2 py-0.5 rounded-full">NODE-04</span>
              </div>
              <h2 className="text-white text-base font-semibold">Incident RCA & Resolution</h2>
            </div>
            <button onClick={onClose} className="text-zinc-400 hover:text-white transition-colors">
              <X size={20} />
            </button>
          </div>
          <div className="py-2 px-6 border-b border-white/5 flex justify-between items-center bg-[#0d0e10]">
            <div className="font-mono text-[11px] text-zinc-500">
              <span className="mr-4">SESSION: #{sessionId}</span>
              <span>CORRIDOR: NH48-South</span>
            </div>
            <div className="flex items-center gap-1.5 text-purple-500">
              <Hexagon size={12} className="fill-purple-500/20" />
              <span className="font-mono text-[10px] tracking-wider font-semibold">TERRAFORM SWARM ENGINE</span>
            </div>
          </div>
        </div>

        {/* Scrollable Feed */}
        <div className="flex-1 overflow-y-auto p-6 scroll-smooth" ref={scrollRef}>
          <div className="flex justify-center mb-6">
            <span className="bg-[#1c1e22] text-zinc-500 font-mono text-[10px] px-3 py-1 rounded-full">
              TODAY • 14:22:10 UTC
            </span>
          </div>

          <div className="space-y-6">
            {messages.map((msg) => (
              <div key={msg.id} className={msg.role === "user" ? "flex flex-col items-end" : "flex flex-col items-start"}>
                
                {/* Sender Header */}
                <div className="flex items-center gap-2 mb-2 w-full">
                  {msg.role === "ai" ? (
                    <>
                      <Zap size={14} className="text-purple-500 fill-purple-500/20" />
                      <span className="text-purple-500 font-medium text-sm">FleetChatbotAgent</span>
                      <span className="bg-purple-500/10 text-purple-400 font-mono text-[9px] px-1.5 py-0.5 rounded uppercase border border-purple-500/20">
                        {msg.type === "rca_card" ? "AGENT_AUTONOMOUS" : "SWARM_SYNC"}
                      </span>
                      <span className="font-mono text-[10px] text-zinc-600 ml-auto">{msg.timestamp}</span>
                    </>
                  ) : (
                    <div className="flex justify-end w-full gap-2">
                      <span className="font-mono text-[11px] text-zinc-500">DispatchOperator_01</span>
                      <span className="font-mono text-[10px] text-zinc-600">{msg.timestamp}</span>
                    </div>
                  )}
                </div>

                {/* Message Body */}
                {msg.role === "user" ? (
                  <div className="max-w-[85%] text-zinc-300 text-sm leading-relaxed text-right">
                    {msg.content}
                  </div>
                ) : (
                  <div className={`w-full ${msg.type === "markdown" ? "" : "bg-[#111213] border border-white/10 rounded-lg p-4"}`}>
                    
                    {msg.type === "rca_card" && msg.cardPayload && (
                      <>
                        <div className="flex justify-between items-center mb-4">
                          <span className="text-rose-500 text-sm font-semibold tracking-wide flex items-center gap-2">
                            <span className="text-[#f43f5e]">•</span> ROOT CAUSE ANALYSIS (RCA)
                          </span>
                          <span className="font-mono text-[10px] text-zinc-500">INCIDENT ID: {msg.cardPayload.incidentId}</span>
                        </div>
                        <div className="prose prose-invert prose-sm max-w-none text-zinc-300 marker:text-zinc-500">
                          <ReactMarkdown>{msg.content || ""}</ReactMarkdown>
                        </div>
                        <div className="mt-4 pt-4 border-t border-white/5 flex justify-between items-center font-mono text-[11px]">
                          <span className="text-zinc-400">Confidence Index: <span className="text-[#10b981]">{msg.cardPayload.confidence}</span></span>
                          <span className="text-zinc-400">Suggested Action: <span className="text-purple-400">{msg.cardPayload.suggestedAction}</span></span>
                        </div>
                      </>
                    )}

                    {msg.type === "kpi_card" && msg.cardPayload && (
                      <>
                        <div className="text-zinc-300 text-sm leading-relaxed mb-4" dangerouslySetInnerHTML={{ __html: msg.content || "" }} />
                        <div className="grid grid-cols-2 gap-3">
                          {msg.cardPayload.metrics?.map((m, i) => (
                            <div key={i} className="bg-[#1c1e22] rounded p-3 border border-white/5">
                              <div className="font-mono text-[9px] text-zinc-500 mb-1">{m.label}</div>
                              <div className={`font-mono text-xs font-semibold ${m.color}`}>{m.value}</div>
                            </div>
                          ))}
                        </div>
                      </>
                    )}

                    {msg.type === "markdown" && (
                      <div className="prose prose-invert prose-sm max-w-none text-zinc-300">
                        {msg.trajectory && msg.trajectory.length > 0 && (
                          <details className="mb-3 group">
                            <summary className="font-mono text-[10px] text-zinc-500 cursor-pointer list-none flex items-center hover:text-zinc-400">
                              <span className="mr-2 transition-transform group-open:rotate-90">›</span>
                              View Agent Trajectory
                            </summary>
                            <div className="mt-2 bg-[#0d0e10] border-l-2 border-purple-500 pl-4 py-2 font-mono text-[11px] text-zinc-500 space-y-1">
                              {msg.trajectory.map((t, i) => (
                                <div key={i}>{t}</div>
                              ))}
                            </div>
                          </details>
                        )}
                        <ReactMarkdown>{msg.content || ""}</ReactMarkdown>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}

            {/* Active Streaming State */}
            {isThinking && (
              <div className="flex flex-col items-start w-full">
                <div className="flex items-center gap-2 mb-2 w-full">
                  <Zap size={14} className="text-purple-500 fill-purple-500/20" />
                  <span className="text-purple-500 font-medium text-sm">FleetChatbotAgent</span>
                  <span className="bg-purple-500/10 text-purple-400 font-mono text-[9px] px-1.5 py-0.5 rounded uppercase border border-purple-500/20 animate-pulse">
                    THINKING...
                  </span>
                </div>
                
                {currentThoughts.length > 0 && (
                  <div className="w-full bg-[#0d0e10] border-l-2 border-purple-500 pl-4 py-2 mb-3 font-mono text-[11px] text-zinc-500 space-y-1">
                    {currentThoughts.map((t, i) => (
                      <div key={i}>{t}</div>
                    ))}
                  </div>
                )}

                {currentStream && (
                  <div className="prose prose-invert prose-sm max-w-none text-zinc-300 w-full">
                    <ReactMarkdown>{currentStream}</ReactMarkdown>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="flex-none p-4 border-t border-white/10 bg-[#0d0e10]">
          <div className="mb-3">
            <span className="font-mono text-[10px] text-zinc-500 mb-2 block">RECOMMENDED SWARM OPERATIONS:</span>
            <div className="flex gap-2">
              {["Reroute via NH48", "Ping Driver", "Authorize Aux Power"].map((op) => (
                <button
                  key={op}
                  onClick={() => handleSend(undefined, op)}
                  disabled={isThinking}
                  className="px-3 py-1.5 border border-white/10 rounded-md font-mono text-[10px] text-zinc-400 hover:text-purple-400 hover:border-purple-500 hover:bg-purple-500/10 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {op}
                </button>
              ))}
            </div>
          </div>
          
          <form onSubmit={handleSend} className="flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={isThinking}
              placeholder="Query swarm intelligence..."
              className="flex-1 bg-[#141618] border border-white/10 rounded-md px-4 py-2 text-sm text-white placeholder-zinc-600 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={isThinking || !input.trim()}
              className="w-10 h-10 flex items-center justify-center bg-white text-black rounded-md hover:bg-zinc-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Send size={16} />
            </button>
          </form>
          
          <div className="mt-3 flex justify-between items-center font-mono text-[10px] text-zinc-600">
            <span>MODEL: logistics-swarm-v3 <span className="ml-2">LATENCY: 142ms</span></span>
            <span>↵ Enter to execute</span>
          </div>
        </div>
      </div>
    </>
  );
}
