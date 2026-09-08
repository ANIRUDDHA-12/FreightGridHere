import { fetchEventSource } from '@microsoft/fetch-event-source';

const BACKEND_URL=process.env.NEXT_PUBLIC_URL || "http://localhost:8080"

export async function fetchLiveVehicles(){
    try{
        const response=await fetch(`${BACKEND_URL}/api/v1/vehicles/list`,{
            cache:'no-store'
        })
        if(!response.ok) throw new Error("Data fetch failed")
        return await response.json()
    }catch(error) {
        console.log(`Backend connection failed`,error);
        return []
    }
}

export async function fetchLiveNodes(){
    try{
        const res=await fetch(`${BACKEND_URL}/api/v1/nodes/list`,{cache:'no-store'})
        if(!res.ok) throw new Error(`Data Not Ready`)
    }catch (error) {
        console.log(`Database fetch failed`,error)
        return [
            {
                id: "INC-03",
                name: "Weather Flash Flood Alert",
                route: "ROUTE 66",
                impact: "3 Units (TRK-01, 04, 09)",
                severity: "High (Roadway Blockade)",
                aiAction: "Detour via NH48 (+24m ETA)",
                engineId: "#AI-RT-09",
            },
        ];
    }
}
export async function fetchActiveNode(){
    try{
        const res=await fetch(`${BACKEND_URL}/api/v1/nodes/list`,{
            cache:'no-cache'
        })
        if(!res.ok) throw new Error(`Still fetching`)
    }catch (e) {
        console.log(`Fetch failed`,e)
        return[
            
        ]
    }
}
export async function streamAiSwarm(
  query: string,
  sessionId: string,
  onThought: (thought: string) => void,
  onToken: (token: string) => void,
  onComplete: () => void,
  onError: (err: any) => void
) {
  class FatalError extends Error { }

  try {
    await fetchEventSource(`${BACKEND_URL}/api/v1/chatbot/ask`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Session-ID": sessionId,
      },
      body: JSON.stringify({ query }),
      onopen(response) {
        if (response.ok) {
          return Promise.resolve();
        } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
          throw new FatalError();
        } else {
          throw new Error(`Server returned ${response.status}`);
        }
      },
      onmessage(msg) {
        if (!msg.data) return;

        try {
          const parsed = JSON.parse(msg.data);
          if (msg.event === "thought") onThought(parsed.content);
          if (msg.event === "token") onToken(parsed.content);
          if (msg.event === "done") onComplete();
        } catch {
          // Fallback if parsing fails
          if (msg.event === "token") onToken(msg.data);
        }
      },
      onerror(err) {
        if (err instanceof FatalError) {
          throw err; 
        }
        onError(err);
      },
      onclose() {
        onComplete();
      }
    });
  } catch (err) {
    onError(err);
  }
}