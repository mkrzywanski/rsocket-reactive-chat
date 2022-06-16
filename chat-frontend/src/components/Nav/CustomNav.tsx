import { useKeycloak } from "@react-keycloak/web";
import React, {
  Dispatch,
  FC,
  SetStateAction,
  useEffect,
  useRef
} from "react";
import { Button, Container, Nav, Navbar } from "react-bootstrap";

export interface NavProps {
  setHeight: Dispatch<SetStateAction<number>>;
}

const CustomNav: FC<NavProps> = (props: NavProps) => {
  const { keycloak } = useKeycloak();
  const navbarRef = useRef<HTMLDivElement>(null);
  const redirectUri = "http://" + window.location.host + "/chat";

  useEffect(() => {
    if (navbarRef.current) {
      props.setHeight(navbarRef.current.clientHeight);
    }
  });

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
                <Button
                  onClick={() => keycloak.login({ redirectUri: redirectUri })}
                  className="ml-2"
                >
                  Login
                </Button>
                <Button onClick={() => keycloak.register()} className="ml-2">
                  Register
                </Button>
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
};

export default CustomNav;
