import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import JoinChatByLink from "./JoinChatByLink";
import { MemoryRouter, Router } from "react-router-dom";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import Keycloak from "keycloak-js";

describe("<JoinChatByLink />", () => {
  test("it should mount", () => {

    const createKeycloakStub = () => ({
      init: jest.fn().mockResolvedValue(true),
      updateToken: jest.fn(),
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
      accountManagement: jest.fn(),
      createLoginUrl: jest.fn()
      
    });
    let mockInitialized = false;

    jest.mock("Keycloak")
    // jest.mock("@react-keycloak/web", () => {
    //   const originalModule = jest.requireActual("@react-keycloak/web");
    //   return {
    //     ...originalModule,
    //     useKeycloak: () => [createKeycloakStub(), mockInitialized],
    //   };
    // });

    // render(
    //   <ReactKeycloakProvider authClient={createKeycloakStub()}>
    //     <MemoryRouter>
    //       <JoinChatByLink addChat={() => {}} />
    //     </MemoryRouter>
    //   </ReactKeycloakProvider>
    // );

    const joinChatByLink = screen.getByTestId("JoinChatByLink");

    expect(joinChatByLink).toBeInTheDocument();
  });
});
