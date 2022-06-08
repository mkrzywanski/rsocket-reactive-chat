import { createContext, useContext } from "react";
import { ChatServerClient } from "./ChatServerClient";

export const RsocketContext = createContext<ChatServerClient | null>(null);
