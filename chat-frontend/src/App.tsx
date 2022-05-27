import { ReactKeycloakProvider } from '@react-keycloak/web';
import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import ChatWindow from './components/ChatWindow/ChatWindow';
import Nav from './components/Nav/Nav';
import WelcomePage from './components/WelcomePage/WelcomePage';
import keycloak from './lib/chat-server-client/Keycloak';
import PrivateRoute from './lib/chat-server-client/PrivateRoute';

function App() {
  return (
    <div className="App">
      {/* <ChatWindow/> */}
      <ReactKeycloakProvider authClient={keycloak}>
        <Nav />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<WelcomePage />} />
            <Route path="/chat" element={<PrivateRoute protectedComponent={<ChatWindow />} /> } />
          </Routes>
        </BrowserRouter>
      </ReactKeycloakProvider>
    </div>
  );
}

export default App;
