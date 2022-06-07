import { useKeycloak } from "@react-keycloak/web";
import React, { FC } from "react";
import { useSearchParams } from "react-router-dom";
import { ChatServerClient } from "../../lib/chat-server-client/ChatServerClient";
import styles from "./JoinChatByLink.module.css";

interface JoinChatByLinkProps {
  // rsocket?: ChatServerClient
}

const JoinChatByLink: FC<JoinChatByLinkProps> = (props : JoinChatByLinkProps) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { keycloak } = useKeycloak();

  const chatId = searchParams.get("chatId");
  const redirectUrl = window.location.host + "/joinChat?chatId=" + chatId
  if (!keycloak.authenticated) {
    keycloak.login({redirectUri: redirectUrl})
  } 

  return null;
};

export default JoinChatByLink;
