import { useKeycloak } from '@react-keycloak/web';
import React, { FC, useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { Container, Navbar, Nav } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom'

interface NavProps { }

const CustomNav: FC<NavProps> = () => {
  const { keycloak, initialized } = useKeycloak();
  const navigate = useNavigate();

  useEffect(() => {
    keycloak.onAuthSuccess = () => {
      navigate("/chat")
    }
  }, [])


  return (
    <Navbar bg="light" expand="lg">
      <Container>
        <Navbar.Brand href="#home">Rsocket-Chat</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        {/* <Nav.Link href="/chat">Chat</Nav.Link> */}
        <Navbar.Collapse id="basic-navbar-nav" className="justify-content-end">
          <Nav>
            {!keycloak.authenticated && (
              <div>
                <Button onClick={() => keycloak.login()} className="ml-2">Login</Button>
                <Button onClick={() => keycloak.register()} className="ml-2">Register</Button>
              </div>
            )}
            {keycloak.authenticated && (
              <Button onClick={() => keycloak.logout()}>
                Logout ({keycloak.tokenParsed?.preferred_username})
              </Button>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default CustomNav;
