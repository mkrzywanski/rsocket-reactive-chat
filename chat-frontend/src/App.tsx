import { ReactKeycloakProvider } from '@react-keycloak/web';
import React, { useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import ChatWindow from './components/ChatWindow/ChatWindow';
import JoinChatByLink from './components/JoinChatByLink/JoinChatByLink';
import CustomNav from './components/Nav/CustomNav';
import WelcomePage from './components/WelcomePage/WelcomePage';
import keycloak from './lib/auth/Keycloak';
import PrivateRoute from './lib/auth/PrivateRoute';

function App() {
  const [navbarHeight, setNavbarHeight] = useState(0)

  return (
    <div className="App">
      <ReactKeycloakProvider authClient={keycloak}>
        <BrowserRouter>
          <CustomNav setHeight={setNavbarHeight}/>
          <Routes>
            <Route path="/" element={<WelcomePage />} />
            <Route path="/chat" element={<PrivateRoute protectedComponent={<ChatWindow navbarHeight={navbarHeight}/>} />} />
            <Route path="/joinChat" element={<JoinChatByLink />} />
          </Routes>
        </BrowserRouter>
      </ReactKeycloakProvider>
    </div>
  );
}

export default App;
