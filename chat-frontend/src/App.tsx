import { ReactKeycloakProvider } from "@react-keycloak/web";
import React, { useEffect, useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import ChatWindow from "./components/ChatWindow/ChatWindow";
import JoinChatByLink from "./components/JoinChatByLink/JoinChatByLink";
import CustomNav from "./components/Nav/CustomNav";
import WelcomePage from "./components/WelcomePage/WelcomePage";
import keycloak from "./lib/auth/Keycloak";
import PrivateRoute from "./lib/auth/PrivateRoute";
import { ChatServerClient } from "./lib/chat-server-client/ChatServerClient";
import { RsocketContext } from "./lib/chat-server-client/RsocketContext";

function App() {
  const [navbarHeight, setNavbarHeight] = useState(0);
  const [rsocket, setRsocket] = useState<ChatServerClient | null>(null);

  async function getClient() {
    const client = await ChatServerClient.CreateAsync("localhost", 9090);
    setRsocket(client);
  }

  useEffect(() => {
    getClient();
    return () => {
      rsocket?.disconnect();
    };
  }, []);

  return (
    <div className="App">
      <ReactKeycloakProvider authClient={keycloak}>
        <RsocketContext.Provider value={rsocket}>
          <BrowserRouter>
            <CustomNav setHeight={setNavbarHeight} />
            <Routes>
              <Route path="/" element={<WelcomePage />} />
              <Route
                path="/chat"
                element={
                  <PrivateRoute
                    protectedComponent={
                      <ChatWindow navbarHeight={navbarHeight} />
                    }
                  />
                }
              />
              <Route path="/joinChat" element={<JoinChatByLink />} />
            </Routes>
          </BrowserRouter>
        </RsocketContext.Provider>
      </ReactKeycloakProvider>
    </div>
  );
}

export default App;
