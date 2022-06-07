import { createContext, useContext } from "react";
import { ChatServerClient } from "./ChatServerClient";

export const rsocketContext = createContext<ChatServerClient | null>(null)

