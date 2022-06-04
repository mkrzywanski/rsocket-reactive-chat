import { useKeycloak } from '@react-keycloak/web';
import React, { Dispatch, FC, SetStateAction, useEffect, useRef, useState } from 'react';
import { Button } from 'react-bootstrap';
import { Container, Navbar, Nav } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom'

export interface NavProps { 
  setHeight : Dispatch<SetStateAction<number>>
}

const CustomNav: FC<NavProps> = (props : NavProps) => {
  const { keycloak, initialized } = useKeycloak();
  const navigate = useNavigate();
  const navbarRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    keycloak.onAuthSuccess = () => {
      navigate("/chat")
    }
  }, [])

  useEffect(
    () => {
      if(navbarRef.current) {
        props.setHeight(navbarRef.current.clientHeight)
      }
    }
  )

  return (
    <Navbar bg="light" expand="lg" ref={navbarRef}>
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
